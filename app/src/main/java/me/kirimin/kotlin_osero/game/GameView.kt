package me.kirimin.kotlin_osero.game

import me.kirimin.kotlin_osero.model.Place
import me.kirimin.kotlin_osero.model.Stone

interface GameView {
    open fun putStone(place: Place)
    open fun setCurrentPlayerText(player: Stone)
    open fun showWinner(player: Stone, blackCount: Int, whiteCount: Int)
    open fun finishGame()
}