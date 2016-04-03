//package me.kirimin.kotlin_osero.game
//
//import me.kirimin.kotlin_osero.model.Piece
//import me.kirimin.kotlin_osero.model.Place
//
//class GameUseCase() {
//
//    val BOARD_SIZE = 8
//
//    val CENTER_LEFT_UP = Place(BOARD_SIZE / 2 - 1, BOARD_SIZE / 2 - 1, Piece.BLACK)
//    val CENTER_LEFT_UNDER = Place(BOARD_SIZE / 2 - 1, BOARD_SIZE / 2, Piece.WHITE)
//    val CENTER_RIGHT_UP = Place(BOARD_SIZE / 2, BOARD_SIZE / 2 - 1, Piece.BLACK)
//    val CENTER_RIGHT_UNDER = Place(BOARD_SIZE / 2, BOARD_SIZE / 2, Piece.WHITE)
//
//    private val boardStatus = Array(BOARD_SIZE) { arrayOfNulls<Place>(BOARD_SIZE) }.mapIndexed { y, list -> list.mapIndexed { x, Place -> Place(y, x, Piece.NONE) } }
//
//    /** どちらのターンか **/
//    var currentPiece = Piece.A
//        private set
//
//    /** 既に石が置かれている位置か */
//    fun isNone(y: Int, x: Int) = boardStatus[y][x].Piece == Piece.NONE
//
//    /**
//     * 石を置ける場所が残っているか
//     */
//    fun canNext(Piece: Piece): Boolean {
//        boardStatus.flatMap { it }.forEach {
//            val next = Place(it.y, it.x, Piece)
//            if (isNone(next.y, next.x) && !getCanChangePlaces(next).isEmpty()) return true
//        }
//        return false
//    }
//
//    /** 石の数が多い方のプレイヤーを返す */
//    fun getWinner() = if (countPlaces(Piece.A) > countPlaces(Piece.B)) Piece.A else Piece.B
//
//    /** 単純に指定した場所に石を配置する(他の状態は変わらない) */
//    fun putPlace(Place: Place) {
//        boardStatus[Place.y][Place.x].Piece = Place.Piece
//    }
//
//    /** currentPieceを入れ替える */
//    fun changePiece() {
//        currentPiece = currentPiece.other()
//    }
//
//    /** ひっくり返せる石のリストを取得 */
//    fun getCanChangePlaces(target: Place): List<Place> {
//        return searchChangePlacesRight(target)
//                .plus(searchChangePlacesLeft(target))
//                .plus(searchChangePlacesUp(target))
//                .plus(searchChangePlacesUnder(target))
//    }
//
//    /** AIが次に置く手を計算 */
//    fun computeNext(Piece: Piece): Place {
//        boardStatus.flatMap { it }.forEach {
//            val next = Place(it.y, it.x, Piece)
//            if (isNone(next.y, next.x) && !getCanChangePlaces(next).isEmpty()) return next
//        }
//        return Place(-1, -1, Piece.NONE)
//    }
//
//    /** 置かれている石の数を取得 */
//    private fun countPlaces(Piece: Piece): Int = boardStatus.flatMap { it }.filter { it.Piece == Piece }.count()
//
//    /** 置いた石から右方向にひっくり返せる石のリストを返す */
//    private fun searchChangePlacesRight(target: Place): List<Place> {
//        if (target.x + 1 > BOARD_SIZE - 1) return emptyList()
//
//        val rightPlaces = boardStatus[target.y].drop(target.x + 1) // targetより右側だけ抽出
//        return getInsidePlaces(target, rightPlaces)
//    }
//
//    /** 置いた石から左方向にひっくり返せる石のリストを返す */
//    private fun searchChangePlacesLeft(target: Place): List<Place> {
//        if (target.x == 0) return emptyList()
//
//        val leftPlaces = boardStatus[target.y]
//                .take(target.x) // targetより左側だけを抽出
//                .reversed() // 左方向だと辿りにくいのでリスト反転
//        return getInsidePlaces(target, leftPlaces)
//    }
//
//    /** 置いた石から下方向にひっくり返せる石のリストを返す */
//    private fun searchChangePlacesUnder(target: Place): List<Place> {
//        if (target.y + 1 > BOARD_SIZE - 1) return emptyList()
//
//        val underPlaces = boardStatus
//                .drop(target.y + 1) // targetより下の行だけ抽出
//                .map { it[target.x] } // targetの列だけ取り出す
//        return getInsidePlaces(target, underPlaces)
//    }
//
//    /** 置いた石から上方向にひっくり返せる石のリストを返す */
//    private fun searchChangePlacesUp(target: Place): List<Place> {
//        if (target.y == 0) return emptyList()
//
//        val upPlaces = boardStatus
//                .take(target.y) // targetより上の行だけ抽出
//                .map { it[target.x] } // targetの列だけ取り出す
//                .reversed() // 上方向だと辿りにくいのでリスト反転
//        return getInsidePlaces(target, upPlaces)
//    }
//
//    /** targetを始点として挟めている範囲を判定して返す */
//    private fun getInsidePlaces(target: Place, Places: List<Place>): List<Place> {
//        // 最初に見つかった自分の石
//        val endPoint = Places.indexOfFirst { it.Piece == target.Piece }
//        // 挟めていなければ終わり
//        if (endPoint == -1) return emptyList()
//        //挟んでいる範囲を抽出
//        val insidePlaces = Places.take(endPoint)
//        // 挟んでいる範囲が全て相手の石なら反転範囲として返す
//        if (insidePlaces.all { it.Piece == target.Piece.other() }) {
//            return insidePlaces.map { Place(it.y, it.x, target.Piece) }
//        }
//        return emptyList()
//    }
//}