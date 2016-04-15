package me.kirimin.kotlin_osero.model

class OseroGame() {

    val BOARD_SIZE = 8
    // 初期配置石
    private val CENTER_LEFT_UP = Place(BOARD_SIZE / 2 - 1, BOARD_SIZE / 2 - 1, Stone.BLACK)
    private val CENTER_LEFT_UNDER = Place(BOARD_SIZE / 2 - 1, BOARD_SIZE / 2, Stone.WHITE)
    private val CENTER_RIGHT_UP = Place(BOARD_SIZE / 2, BOARD_SIZE / 2 - 1, Stone.WHITE)
    private val CENTER_RIGHT_UNDER = Place(BOARD_SIZE / 2, BOARD_SIZE / 2, Stone.BLACK)

    /** 盤の状態を2次元配列で保持 */
    val boardStatus = arrayOfNulls<List<Place>>(BOARD_SIZE).mapIndexed { x, list -> arrayOfNulls<Place>(BOARD_SIZE).mapIndexed { y, place -> Place(x, y, Stone.NONE) } }
    /** 現在どちらのターンか **/
    var currentPlayer = Stone.BLACK

    fun getInitialPlaces() = listOf(CENTER_LEFT_UP, CENTER_LEFT_UNDER, CENTER_RIGHT_UP, CENTER_RIGHT_UNDER)

    /** 指定された場所に石を置けるか */
    fun canPut(place: Place) = boardStatus[place.x][place.y].stone == Stone.NONE && getCanChangePlaces(place).isNotEmpty()

    /** 石を置ける全ての場所 */
    fun getAllCanPutPlaces(color: Stone) = boardStatus.flatMap { it }.filter { canPut(Place(it.x, it.y, color)) }

    /** 次のターンで置ける場所がまだ存在するか */
    fun canNext(color: Stone): Boolean = getAllCanPutPlaces(color).isNotEmpty()

    /** 石の数を数える */
    fun countStones(color: Stone) = boardStatus.flatMap { it }.count { it.stone == color }

    /** ゲームが終了しているか */
    fun isGameOver() = !canNext(Stone.BLACK) && !canNext(Stone.WHITE)

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