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
        putStone(CENTER_LEFT_UP)
        putStone(CENTER_LEFT_UNDER)
        putStone(CENTER_RIGHT_UP)
        putStone(CENTER_RIGHT_UNDER)
    }

    fun onClickPlace(x: Int, y: Int) {
        if (!canPut(x, y)) {
            return
        }
        val clickPlace = Place(x, y, currentPlayer)
        putStone(clickPlace)
        getCanChangePlaces(clickPlace).forEach { putStone(it) }
        changePlayer()
    }

    @VisibleForTesting
    fun changePlayer() {
        currentPlayer = currentPlayer.other()
    }

    private fun canPut(x: Int, y: Int) = boardStatus[x][y].stone == Stone.NONE && getCanChangePlaces(Place(x, y, currentPlayer)).isNotEmpty()

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