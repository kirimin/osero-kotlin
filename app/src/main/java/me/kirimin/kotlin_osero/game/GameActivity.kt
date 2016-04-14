package me.kirimin.kotlin_osero.game

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import android.widget.Toast
import me.kirimin.kotlin_osero.R

import kotlinx.android.synthetic.main.activity_game.*
import me.kirimin.kotlin_osero.TopActivity
import me.kirimin.kotlin_osero.model.AIRandom
import me.kirimin.kotlin_osero.model.Stone
import me.kirimin.kotlin_osero.model.Place

class GameActivity : AppCompatActivity(), GameView {

    lateinit var placeList: List<List<ImageView>>
    val presenter = GamePresenter()
    val BOARD_SIZE = presenter.BOARD_SIZE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        // 二次元配列にマッピングしながらGridLayoutにマスを設定
        placeList = arrayOfNulls<List<ImageView>>(BOARD_SIZE)
                .mapIndexed { x, list ->
                    arrayOfNulls<ImageView>(BOARD_SIZE).mapIndexed { y, imageView ->
                        val place = layoutInflater.inflate(R.layout.grid_place, null)
                        place.setOnClickListener { presenter.onClickPlace(x, y) }
                        gamePlacesGrid.addView(place)
                        place.findViewById(R.id.gamePlaceImageView) as ImageView
                    }
                }
        presenter.onCreate(this, AIRandom())
    }

    override fun putStone(place: Place) {
        val imageRes = when (place.stone) {
            Stone.BLACK -> R.drawable.black_stone
            Stone.WHITE -> R.drawable.white_stone
            Stone.NONE -> throw IllegalArgumentException()
        }
        placeList[place.x][place.y].setImageResource(imageRes)
    }

    override fun setCurrentPlayerText(player: Stone) {
        val color = when (player) {
            Stone.BLACK -> "黒"
            Stone.WHITE -> "白"
            Stone.NONE -> throw IllegalArgumentException()
        }
        gameCurrentPlayerText.text = getString(R.string.textGameCurrentPlayer, color)
    }

    override fun showWinner(player: Stone, blackCount: Int, whiteCount: Int) {
        val color = when (player) {
            Stone.BLACK -> "黒"
            Stone.WHITE -> "白"
            Stone.NONE -> throw IllegalArgumentException()
        }
        Toast.makeText(this, getString(R.string.textGameWinner, blackCount, whiteCount, color), Toast.LENGTH_SHORT).show()
    }

    override fun finishGame() {
        finish()
        startActivity(Intent(this, TopActivity::class.java))
    }
}
