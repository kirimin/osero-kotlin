package me.kirimin.kotlin_osero.model

import me.kirimin.kotlin_osero.game.GamePresenter

interface OseroAI {

    fun computeNext(game: GamePresenter): Place
}
