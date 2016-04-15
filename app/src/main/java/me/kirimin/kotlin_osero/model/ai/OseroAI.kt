package me.kirimin.kotlin_osero.model.ai

import me.kirimin.kotlin_osero.model.OseroGame
import me.kirimin.kotlin_osero.model.Place
import java.io.Serializable

interface OseroAI : Serializable {

    fun computeNext(game: OseroGame): Place
}
