package me.kirimin.kotlin_osero.model.ai

import me.kirimin.kotlin_osero.model.OseroGame
import me.kirimin.kotlin_osero.model.Place

interface OseroAI {

    fun computeNext(game: OseroGame): Place
}
