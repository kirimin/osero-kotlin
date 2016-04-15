package me.kirimin.kotlin_osero.model.ai

import me.kirimin.kotlin_osero.model.OseroGame
import me.kirimin.kotlin_osero.model.Place
import me.kirimin.kotlin_osero.model.Stone

/**
 * AIを使用しない場合
 */
class AINone : OseroAI {

    override fun computeNext(game: OseroGame, color: Stone): Place {
        throw IllegalAccessException()
    }

}
