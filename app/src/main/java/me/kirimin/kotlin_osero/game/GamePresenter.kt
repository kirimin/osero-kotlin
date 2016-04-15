package me.kirimin.kotlin_osero.game

import android.support.annotation.VisibleForTesting
import me.kirimin.kotlin_osero.model.*
import me.kirimin.kotlin_osero.model.ai.AINone
import me.kirimin.kotlin_osero.model.ai.OseroAI

class GamePresenter {

    private val game: OseroGame = OseroGame()
    private lateinit var ai: OseroAI
    private var view: GameView? = null
    val boardSize = game.BOARD_SIZE

    /** 現在どちらのターンか **/
    var currentPlayer = Stone.BLACK

    fun onCreate(view: GameView, ai: OseroAI = AINone()) {
        this.view = view
        this.ai = ai
        view.setCurrentPlayerText(Stone.BLACK)
        game.getInitialPlaces().forEach { putStone(it) }
        view.markCanPutPlaces(game.getAllCanPutPlaces(currentPlayer))
    }

    fun onClickPlace(x: Int, y: Int) {
        val view = view ?: return
        val clickPlace = Place(x, y, currentPlayer)
        if (!game.canPut(clickPlace)) {
            return
        }
        view.clearAllMarkPlaces()
        putStone(clickPlace)
        game.getCanChangePlaces(clickPlace).forEach { putStone(it) }

        // 終了処理
        if (game.isGameOver()) {
            val blackCount = game.countStones(Stone.BLACK)
            val whiteCount = game.countStones(Stone.WHITE)
            view.showWinner(if (blackCount > whiteCount) Stone.BLACK else Stone.WHITE, blackCount, whiteCount)
            view.finishGame()
        }
        // ターン切替
        changePlayer()
        view.markCanPutPlaces(game.getAllCanPutPlaces(currentPlayer))
        // パス
        if (!game.canNext(currentPlayer)) {
            changePlayer()
            return
        }
        // AI
        if (ai !is AINone && currentPlayer == Stone.WHITE) {
            val choseByAI = ai.computeNext(game, currentPlayer)
            onClickPlace(choseByAI.x, choseByAI.y)
        }
    }

    /** ターンを変更する */
    @VisibleForTesting
    fun changePlayer() {
        currentPlayer = currentPlayer.other()
        view?.setCurrentPlayerText(currentPlayer)
    }

    /** 指定した位置に石を置く。ターンは切り替わらない */
    @VisibleForTesting
    fun putStone(place: Place) {
        game.boardStatus[place.x][place.y].stone = place.stone
        view?.putStone(place)
    }
}