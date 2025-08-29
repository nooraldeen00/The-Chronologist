package com.example.game;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity
{
    private GameView gameView;
    static GameActivity gameActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Saves the instance of this GameActivity.
        gameActivity = this;

        // Puts app in fullscreen mode
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Locks screen in Portrait view
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Gets the size of the device's screen for visual compatibility.
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);

        // Generates and displays the level being played, adjusted for screen size
        gameView = new GameView(this, point.x, point.y);
        setContentView(gameView);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        gameView.pause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        gameView.resume();
    }
    // Returns the current instance of GameActivity.
    public static GameActivity getInstance()
    {
        return gameActivity;
    }


}