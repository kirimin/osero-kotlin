package me.kirimin.kotlin_osero.model.ai

import me.kirimin.kotlin_osero.model.OseroGame
import me.kirimin.kotlin_osero.model.Place
import java.util.*

class AIRandom : OseroAI {

    override fun computeNext(game: OseroGame): Place {
        val canPlaces = game.boardStatus.flatMap { it }.filter { game.canPut(Place(it.x, it.y, game.currentPlayer)) }
        return canPlaces[Random().nextInt(canPlaces.size)]
    }
}