package me.kirimin.kotlin_osero.game

import android.support.annotation.VisibleForTesting
import me.kirimin.kotlin_osero.model.*
import me.kirimin.kotlin_osero.model.ai.AINone
import me.kirimin.kotlin_osero.model.ai.OseroAI

class GamePresenter {

    private val game: OseroGame = OseroGame()
    private lateinit var ai: OseroAI
    private var view: GameView? = null
    val boardSize = game.boardSize

    fun onCreate(view: GameView, ai: OseroAI = AINone()) {
        this.view = view
        this.ai = ai
        view.setCurrentPlayerText(Stone.BLACK)
        game.getInitialPlaces().forEach { putStone(it) }
    }

    fun onClickPlace(x: Int, y: Int) {
        val view = view ?: return
        val clickPlace = Place(x, y, game.currentPlayer)
        if (!game.canPut(clickPlace)) {
            return
        }
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
        // パス
        if (!game.canNext(game.currentPlayer)) {
            changePlayer()
            return
        }
        // AI
        if (ai !is AINone && game.currentPlayer == Stone.WHITE) {
            val choseByAI = ai.computeNext(game)
            onClickPlace(choseByAI.x, choseByAI.y)
        }
    }

    /** ターンを変更する */
    @VisibleForTesting
    fun changePlayer() {
        game.currentPlayer = game.currentPlayer.other()
        view?.setCurrentPlayerText(game.currentPlayer)
    }

    /** 指定した位置に石を置く。ターンは切り替わらない */
    @VisibleForTesting
    fun putStone(place: Place) {
        game.boardStatus[place.x][place.y].stone = place.stone
        view?.putStone(place)
    }
}