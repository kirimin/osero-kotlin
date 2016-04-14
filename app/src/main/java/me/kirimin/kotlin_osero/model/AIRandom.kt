package me.kirimin.kotlin_osero.model

import me.kirimin.kotlin_osero.game.GamePresenter
import java.util.*

class AIRandom : OseroAI {

    override fun computeNext(game: GamePresenter): Place {
        val canPlaces = game.boardStatus.flatMap { it }.filter { game.canPut(Place(it.x, it.y, game.currentPlayer)) }
        return canPlaces[Random().nextInt(canPlaces.size)]
    }
}