package me.kirimin.kotlin_osero;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import me.kirimin.kotlin_osero.game.GameActivity;

public class TopActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top);
        startActivity(new Intent(this, GameActivity.class));
        finish();
    }
}
