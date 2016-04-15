package me.kirimin.kotlin_osero

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import me.kirimin.kotlin_osero.game.GameActivity
import kotlinx.android.synthetic.main.activity_top.*
import me.kirimin.kotlin_osero.model.ai.AINone
import me.kirimin.kotlin_osero.model.ai.AIWeak
import me.kirimin.kotlin_osero.model.ai.OseroAI

class TopActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top)
        topModeManualButton.setOnClickListener { startGameActivity(AINone()) }
        topModeAIWeakButton.setOnClickListener { startGameActivity(AIWeak()) }
        topModeAIStrongButton.setOnClickListener { startGameActivity(AIWeak()) }
    }

    private fun startGameActivity(ai: OseroAI) {
        startActivity(GameActivity.createIntent(this, ai))
    }
}
