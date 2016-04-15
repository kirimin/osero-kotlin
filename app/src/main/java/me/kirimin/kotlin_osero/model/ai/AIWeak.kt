package me.kirimin.kotlin_osero.model.ai

import me.kirimin.kotlin_osero.model.OseroGame
import me.kirimin.kotlin_osero.model.Place

/**
 * 先読みせず一番多く石が取れる手を選ぶ
 */
class AIWeak : OseroAI {
    override fun computeNext(game: OseroGame): Place {
        return game.boardStatus.flatMap { it }
                .filter { game.canPut(Place(it.x, it.y, game.currentPlayer)) }
                .maxBy { game.getCanChangePlaces(it).count() }!!
    }
}