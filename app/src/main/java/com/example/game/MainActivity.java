package com.example.game;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.example.logindemo.R;

import androidx.appcompat.app.AppCompatActivity;

// "import com.example.logindemo.R;" must be used in GitHub version

public class MainActivity extends AppCompatActivity
{
    private ImageView titleArt;
    private ImageButton titleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // Renders "activity_main" layout, initializes UI elements the user interacts with
        // on that layout. MUST have same name as xml file in the layout folder.
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        // Assign elements of "activity_main" to variables we can use
        titleArt = findViewById(R.id.titleArt);
        titleButton = findViewById(R.id.titleButton);

        // Locks screen in Portrait view
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Sends user to level select screen upon clicking the start button
        titleButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, LevelSelectActivity.class);
                startActivity(intent);
            }
        });
    }

    // === May need to move to the Activity handling level selection ===
    public void play(View v)
    {
        Intent intent = new Intent(MainActivity.this, LevelSelectActivity.class);
        startActivity(intent);
        finish();
    }
}
