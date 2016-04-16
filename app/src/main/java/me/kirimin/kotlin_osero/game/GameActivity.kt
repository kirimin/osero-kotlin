package me.kirimin.kotlin_osero.game

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import android.widget.Toast
import me.kirimin.kotlin_osero.R

import kotlinx.android.synthetic.main.activity_game.*
import me.kirimin.kotlin_osero.model.Stone
import me.kirimin.kotlin_osero.model.Place
import me.kirimin.kotlin_osero.model.ai.AINone
import me.kirimin.kotlin_osero.model.ai.OseroAI

class GameActivity : AppCompatActivity(), GameView {

    lateinit var placeList: List<List<ImageView>>
    val presenter = GamePresenter()
    val boardSize = presenter.boardSize

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        // 二次元配列にマッピングしながらGridLayoutにマスを設定
        placeList = arrayOfNulls<List<ImageView>>(boardSize)
                .mapIndexed { x, list ->
                    arrayOfNulls<ImageView>(boardSize).mapIndexed { y, imageView ->
                        val place = layoutInflater.inflate(R.layout.grid_place, null)
                        place.setOnClickListener { presenter.onClickPlace(x, y) }
                        gamePlacesGrid.addView(place)
                        place.findViewById(R.id.gamePlaceImageView) as ImageView
                    }
                }
        val ai = intent.getSerializableExtra(EXTRA_NAME_AI) as? OseroAI ?: AINone()
        presenter.onCreate(this, ai)
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
    }

    override fun markCanPutPlaces(places: List<Place>) {
        places.forEach { placeList[it.x][it.y].setBackgroundColor(ContextCompat.getColor(this, R.color.green_light)) }
    }

    override fun clearAllMarkPlaces() {
        placeList.flatMap { it }.forEach { it.setBackgroundColor(ContextCompat.getColor(this, R.color.green)) }
    }

    companion object {
        val EXTRA_NAME_AI = "extra_ai"

        fun createIntent(context: Context, ai: OseroAI = AINone()): Intent {
            val intent = Intent(context, GameActivity::class.java)
            intent.putExtra(EXTRA_NAME_AI, ai)
            return intent
        }
    }
}
