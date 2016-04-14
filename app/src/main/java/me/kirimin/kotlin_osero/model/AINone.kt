package me.kirimin.kotlin_osero.model

import me.kirimin.kotlin_osero.game.GamePresenter

class AINone : OseroAI {

    override fun computeNext(game: GamePresenter): Place {
        throw UnsupportedOperationException()
    }

}
