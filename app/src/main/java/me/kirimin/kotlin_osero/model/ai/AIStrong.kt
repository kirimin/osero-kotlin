package me.kirimin.kotlin_osero.model.ai

import me.kirimin.kotlin_osero.model.OseroGame
import me.kirimin.kotlin_osero.model.Place
import me.kirimin.kotlin_osero.model.Stone

/**
 * 盤面に重み付けをして最適手を評価する
 */
class AIStrong : OseroAI {

    val boardRatings = arrayOf(
            arrayOf(30, -12, 0, -1, -1, 0, -12, 30),
            arrayOf(-12, -15, -3, -3, -3, -3, -15, -12),
            arrayOf(0, -3, 0, -1, -1, 0, -3, -1),
            arrayOf(-1, -3, -1, -1, -1, -1, -3, -1),
            arrayOf(-1, -3, -1, -1, -1, -1, -3, -1),
            arrayOf(0, -3, 0, -1, -1, 0, -3, -1),
            arrayOf(-12, -15, -3, -3, -3, -3, -15, -12),
            arrayOf(30, -12, 0, -1, -1, 0, -12, 30)
    )

    override fun computeNext(game: OseroGame, color: Stone): Place {
        return game.boardStatus.flatMap { it }
                .filter { game.canPut(Place(it.x, it.y, color)) }
                .maxBy { checkScore(it) + game.getCanChangePlaces(it).map { checkScore(it) }.sum() }!!
    }

    private fun checkScore(place: Place) = boardRatings[place.x][place.y]
}