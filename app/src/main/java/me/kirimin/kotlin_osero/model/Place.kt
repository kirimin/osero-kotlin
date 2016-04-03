package me.kirimin.kotlin_osero.model

/**
 * マスの座標と状態を保持する
 */
data class Place(val x: Int, val y: Int, var stone: Stone) {

    override fun equals(other: Any?): Boolean {
        val other = other as? Place ?: return false
        return x == other.x && y == other.y && stone == other.stone
    }

    override fun hashCode(): Int {
        var result = x
        result += 31 * result + y
        result += 31 * result + stone.hashCode()
        return result
    }
}