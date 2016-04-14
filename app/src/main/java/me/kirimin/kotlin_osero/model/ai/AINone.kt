package me.kirimin.kotlin_osero.model.ai

import me.kirimin.kotlin_osero.model.OseroGame
import me.kirimin.kotlin_osero.model.Place

class AINone : OseroAI {

    override fun computeNext(game: OseroGame): Place {
        throw IllegalAccessException()
    }

}
