package me.kirimin.kotlin_osero.game

import me.kirimin.kotlin_osero.model.Place

interface GameView {
    open fun putStone(place: Place)
}