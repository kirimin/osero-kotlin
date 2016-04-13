package me.kirimin.kotlin_osero.game

import android.support.annotation.VisibleForTesting
import me.kirimin.kotlin_osero.model.Stone
import me.kirimin.kotlin_osero.model.Place

class GamePresenter {

    val BOARD_SIZE = 8
    private val CENTER_LEFT_UP = Place(BOARD_SIZE / 2 - 1, BOARD_SIZE / 2 - 1, Stone.BLACK)
    private val CENTER_LEFT_UNDER = Place(BOARD_SIZE / 2 - 1, BOARD_SIZE / 2, Stone.WHITE)
    private val CENTER_RIGHT_UP = Place(BOARD_SIZE / 2, BOARD_SIZE / 2 - 1, Stone.WHITE)
    private val CENTER_RIGHT_UNDER = Place(BOARD_SIZE / 2, BOARD_SIZE / 2, Stone.BLACK)

    private var view: GameView? = null
    /** 盤の状態を2次元配列で保持 */
    private val boardStatus = arrayOfNulls<List<Place>>(BOARD_SIZE).mapIndexed { x, list -> arrayOfNulls<Place>(BOARD_SIZE).mapIndexed { y, place -> Place(x, y, Stone.NONE) } }
    /** 現在どちらのターンか **/
    private var currentPlayer = Stone.BLACK

    fun onCreate(view: GameView) {
        this.view = view
        view.setCurrentPlayerText(Stone.BLACK)
        putStone(CENTER_LEFT_UP)
        putStone(CENTER_LEFT_UNDER)
        putStone(CENTER_RIGHT_UP)
        putStone(CENTER_RIGHT_UNDER)
    }

    fun onClickPlace(x: Int, y: Int) {
        val view = view ?: return
        val clickPlace = Place(x, y, currentPlayer)
        if (!canPut(clickPlace)) {
            return
        }
        putStone(clickPlace)
        getCanChangePlaces(clickPlace).forEach { putStone(it) }

        if (isGameOver()) {
            val blackCount = countStones(Stone.BLACK)
            val whiteCount = countStones(Stone.WHITE)
            view.showWinner(if (blackCount > whiteCount) Stone.BLACK else Stone.WHITE, blackCount, whiteCount)
            view.finishGame()
        }
        changePlayer()
        // パス
        if (!canNext(currentPlayer)) changePlayer()
    }

    @VisibleForTesting
    fun changePlayer() {
        currentPlayer = currentPlayer.other()
        view?.setCurrentPlayerText(currentPlayer)
    }

    /** 次のターンで置ける場所がまだ存在するか */
    private fun canNext(color: Stone): Boolean {
        boardStatus.flatMap { it }.forEach { if (canPut(Place(it.x, it.y, color))) return true }
        return false
    }

    /** 指定された場所に石を置けるか */
    private fun canPut(place: Place) = boardStatus[place.x][place.y].stone == Stone.NONE && getCanChangePlaces(place).isNotEmpty()

    /** 石の数を数える */
    private fun countStones(color: Stone) = boardStatus.flatMap { it }.count { it.stone == color }

    /** ゲームが終了しているか */
    private fun isGameOver() = !canNext(Stone.BLACK) && !canNext(Stone.WHITE)

    /** 指定した位置に石を置く */
    @VisibleForTesting
    fun putStone(place: Place) {
        boardStatus[place.x][place.y].stone = place.stone
        view?.putStone(place)
    }

    /** ひっくり返せる石のリストを取得 */
    fun getCanChangePlaces(target: Place): List<Place> {
        return searchChangePlacesRight(target)
                .plus(searchChangePlacesLeft(target))
                .plus(searchChangePlacesUp(target))
                .plus(searchChangePlacesUnder(target))
                .plus(searchChangePlacesUpperLeft(target))
                .plus(searchChangePlacesDownRight(target))
                .plus(searchChangePlacesUpperRight(target))
                .plus(searchChangePlacesDownLeft(target))
    }

    /** 置いた石から右方向にひっくり返せる石のリストを返す */
    private fun searchChangePlacesRight(target: Place): List<Place> {
        if (target.x + 1 > BOARD_SIZE - 1) return emptyList()

        val rightPlaces = boardStatus.drop(target.x + 1) // targetより右の列だけ抽出
                .map { it[target.y] } // targetの行だけ取り出す
        return getInsidePlaces(target, rightPlaces)
    }

    /** 置いた石から左方向にひっくり返せる石のリストを返す */
    private fun searchChangePlacesLeft(target: Place): List<Place> {
        if (target.x == 0) return emptyList()

        val leftPlaces = boardStatus
                .take(target.x) // targetより左の列だけを抽出
                .map { it[target.y] } // targetの行だけ取り出す
                .reversed() // 左方向だと辿りにくいのでリスト反転
        return getInsidePlaces(target, leftPlaces)
    }

    /** 置いた石から下方向にひっくり返せる石のリストを返す */
    private fun searchChangePlacesUnder(target: Place): List<Place> {
        if (target.y + 1 > BOARD_SIZE - 1) return emptyList()

        val underPlaces = boardStatus[target.x].drop(target.y + 1) // targetより下の行だけ抽出
        return getInsidePlaces(target, underPlaces)
    }

    /** 置いた石から上方向にひっくり返せる石のリストを返す */
    private fun searchChangePlacesUp(target: Place): List<Place> {
        if (target.y == 0) return emptyList()

        val upPlaces = boardStatus[target.x]
                .take(target.y) // targetより上の行だけ抽出
                .reversed() // 上方向だと辿りにくいのでリスト反転
        return getInsidePlaces(target, upPlaces)
    }

    /** 置いた石から左上方向にひっくり返せる石のリストを返す */
    private fun searchChangePlacesUpperLeft(target: Place): List<Place> {
        if (target.x == 0 || target.y == 0) return emptyList()

        val upperLeftPlaces = boardStatus.flatMap { it }
                .filter { it.x < target.x && it.y < target.y } // targetより左上だけ抽出
                .filter { it.x - it.y == target.x - target.y } // 斜めのライン上だけ抽出
                .reversed()
        return getInsidePlaces(target, upperLeftPlaces)
    }

    /** 置いた石から右下方向にひっくり返せる石のリストを返す */
    private fun searchChangePlacesDownRight(target: Place): List<Place> {
        if (target.x + 1 > BOARD_SIZE - 1 || target.y + 1 > BOARD_SIZE - 1) return emptyList()

        val downRightPlaces = boardStatus.flatMap { it }
                .filter { it.x > target.x && it.y > target.y }
                .filter { it.x - it.y == target.x - target.y } // 斜めのライン上だけ抽出
        return getInsidePlaces(target, downRightPlaces)
    }

    /** 置いた石から右上方向にひっくり返せる石のリストを返す */
    private fun searchChangePlacesUpperRight(target: Place): List<Place> {
        if (target.x + 1 > BOARD_SIZE || target.y == 0) return emptyList()

        val upperRightPlaces = boardStatus.flatMap { it }
                .filter { it.x > target.x && it.y < target.y }
                .filter { it.x + it.y == target.x + target.y }
        return getInsidePlaces(target, upperRightPlaces)
    }

    /** 置いた石から左下方向にひっくり返せる石のリストを返す */
    private fun searchChangePlacesDownLeft(target: Place): List<Place> {
        if (target.x == 0 || target.y + 1 > BOARD_SIZE - 1) return emptyList()

        val downLeftPlaces = boardStatus.flatMap { it }
                .filter { it.x < target.x && it.y > target.y }
                .filter { it.x + it.y == target.x + target.y }
                .reversed()
        return getInsidePlaces(target, downLeftPlaces)
    }

    /** targetを始点として挟めている範囲を判定して返す */
    private fun getInsidePlaces(target: Place, places: List<Place>): List<Place> {
        // 最初に見つかった自分の石
        val endPoint = places.indexOfFirst { it.stone == target.stone }
        // 挟めていなければ終わり
        if (endPoint == -1) return emptyList()
        //挟んでいる範囲を抽出
        val insidePlaces = places.take(endPoint)
        // 挟んでいる範囲が全て相手の石なら反転範囲として返す
        if (insidePlaces.all { it.stone == target.stone.other() }) {
            return insidePlaces.map { Place(it.x, it.y, target.stone) }
        }
        return emptyList()
    }
}