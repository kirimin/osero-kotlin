package me.kirimin.kotlin_osero.game

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import android.widget.Toast
import me.kirimin.kotlin_osero.R

import kotlinx.android.synthetic.main.activity_game.*
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
        presenter.onCreate(this)
    }

    override fun putStone(place: Place) {
        val imageRes = when (place.stone) {
            Stone.BLACK -> Color.BLACK
            Stone.WHITE -> Color.WHITE
            Stone.NONE -> throw IllegalArgumentException()
        }
        placeList[place.x][place.y].setBackgroundColor(imageRes)
    }
}