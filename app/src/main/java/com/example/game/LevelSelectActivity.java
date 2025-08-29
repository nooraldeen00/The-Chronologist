package com.example.game;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.logindemo.R;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

public class LevelSelectActivity extends AppCompatActivity
{
    // Holds saved and loaded data from previous sessions
    private SharedPreferences prefs;

    // Message display for indicating if a level is locked or unlocked and
    // for indicating when the user saves progress
    private TextView menu_message;

    // Icons for choosing each level
    private ImageButton level_1_tile;
    private ImageButton level_2_tile;
    private ImageButton level_3_tile;
    private ImageButton level_4_tile;
    private ImageButton level_5_tile;
    private ImageButton level_6_tile;
    private ImageButton level_7_tile;
    private ImageButton level_8_tile;
    private ImageButton level_9_tile;
    private ImageButton level_10_tile;

    // Icon for saving progress locally
    private ImageButton save_button;

    // Indicates what level is clicked on
    public static int levelChosen;

    // Indicates locked/unlocked status of each level
    public static boolean[] levelAccess;
    public static boolean levelComplete = false;
    //Timer Values
    public Date levelStartTime = new Date();
    private long[] bestTimes = new long[10];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Renders "activity_main" layout, initializes UI elements the user interacts with
        // on that layout. MUST have same name as xml file in the layout folder.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.level_select);

        // Initializes display message TextView variable for notifications of
        // locked and unlocked levels
        menu_message = findViewById(R.id.menu_message);

        // Level icons for picking a level
        level_1_tile = findViewById(R.id.level_1_tile);
        level_2_tile = findViewById(R.id.level_2_tile);
        level_3_tile = findViewById(R.id.level_3_tile);
        level_4_tile = findViewById(R.id.level_4_tile);
        level_5_tile = findViewById(R.id.level_5_tile);
        level_6_tile = findViewById(R.id.level_6_tile);
        level_7_tile = findViewById(R.id.level_7_tile);
        level_8_tile = findViewById(R.id.level_8_tile);
        level_9_tile = findViewById(R.id.level_9_tile);
        level_10_tile = findViewById(R.id.level_10_tile);

        // Save icon for saving progress locally
        save_button = findViewById(R.id.save_button);

        //Loading Best Times


        // Retrieves saved progress from previous sessions, defaults to only having
        // level 1 unlocked if no previous progress was saved
        prefs = getSharedPreferences(getString(R.string.saveData), Context.MODE_PRIVATE);
        int lastLvlFin = prefs.getInt(getString(R.string.saveData), 1);

        bestTimes[0] = prefs.getLong(getString(R.string.best1), 0);
        bestTimes[1] = prefs.getLong(getString(R.string.best2), 0);
        bestTimes[2] = prefs.getLong(getString(R.string.best3), 0);
        bestTimes[3] = prefs.getLong(getString(R.string.best4), 0);
        bestTimes[4] = prefs.getLong(getString(R.string.best5), 0);
        bestTimes[5] = prefs.getLong(getString(R.string.best6), 0);
        bestTimes[6] = prefs.getLong(getString(R.string.best7), 0);
        bestTimes[7] = prefs.getLong(getString(R.string.best8), 0);
        bestTimes[8] = prefs.getLong(getString(R.string.best9), 0);
        bestTimes[9] = prefs.getLong(getString(R.string.best10), 0);
        // Creates a new boolean array for indicating which levels are locked and
        // unlocked. Initializes all their values to False before setting any of
        // them to True based on the last level finished, taken from previously
        // saved data.
        levelAccess = new boolean[10];
        Arrays.fill(levelAccess, false);
        for(int i = 0; i < levelAccess.length && i < lastLvlFin; i++)
        {
            levelAccess[i] = true;
        }

        // Locks screen in Portrait view
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Saves data up to the last level finished upon clicking the save icon
        save_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Iterates through the booleans in the levelAccess array, which
                // should be any number of True values from 1-10 followed by any
                // number of False values from 1-9.
                for(int i = 0; i < levelAccess.length; i++)
                {
                    // On finding the first False value (which marks the level
                    // immediately after the last finished level), save the number
                    // of the last finished level into the local saveData file.
                    if(!levelAccess[i])
                    {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt(getString(R.string.saveData), i);
                        editor.apply();
                        break;
                    }
                }
                SharedPreferences.Editor editor = prefs.edit();
                editor.putLong(getString(R.string.best1), bestTimes[0]);
                editor.putLong(getString(R.string.best2), bestTimes[1]);
                editor.putLong(getString(R.string.best3), bestTimes[2]);
                editor.putLong(getString(R.string.best4), bestTimes[3]);
                editor.putLong(getString(R.string.best5), bestTimes[4]);
                editor.putLong(getString(R.string.best6), bestTimes[5]);
                editor.putLong(getString(R.string.best7), bestTimes[6]);
                editor.putLong(getString(R.string.best8), bestTimes[7]);
                editor.putLong(getString(R.string.best9), bestTimes[8]);
                editor.putLong(getString(R.string.best10), bestTimes[9]);
                editor.apply();

                // Indicates data was successfully saved
                displaySaveMsg();

            }
        });

        // ===== LEVEL SELECTION AND SWITCH-TO FUNCTIONS =====

        // Sends user to Level 1 upon clicking the Level 1 button
        level_1_tile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // If the level is unlocked...
                if(levelAccess[0])
                {
                    // Set level chosen to Level 1, clear the "locked level" message if it's
                    // present, and switch to GameActivity
                    levelChosen = 1;
                    menu_message.setText("");
                    Intent intent = new Intent(LevelSelectActivity.this, GameActivity.class);
                    levelStartTime = new Date();
                    startActivity(intent);

                }
                else
                {
                    displayLockMsg();
                }
            }
        });

        // Sends user to Level 2 upon clicking the Level 2 button
        level_2_tile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(levelAccess[1])
                {
                    levelChosen = 2;
                    menu_message.setText("");
                    Intent intent = new Intent(LevelSelectActivity.this, GameActivity.class);
                    levelStartTime = new Date();
                    startActivity(intent);
                }
                else
                {
                    displayLockMsg();
                }
            }
        });

        // Sends user to Level 3 upon clicking the Level 3 button
        level_3_tile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(levelAccess[2])
                {
                    levelChosen = 3;
                    menu_message.setText("");
                    Intent intent = new Intent(LevelSelectActivity.this, GameActivity.class);
                    levelStartTime = new Date();
                    startActivity(intent);
                }
                else
                {
                    displayLockMsg();
                }
            }
        });

        // Sends user to Level 3 upon clicking the Level 4 button
        level_4_tile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(levelAccess[3])
                {
                    levelChosen = 4;
                    menu_message.setText("");
                    Intent intent = new Intent(LevelSelectActivity.this, GameActivity.class);
                    levelStartTime = new Date();
                    startActivity(intent);
                }
                else
                {
                    displayLockMsg();
                }
            }
        });
    }

    // === OTHER METHODS ===

    // Message displayer for when user clicks on a locked level
    public void displayLockMsg()
    {
        menu_message.setText(R.string.accessMessage);
    }

    //This Runs anytime you RE-NAVIGATE to this level and as well as the first creation
    public void onResume()
    {
        //Shows Time Taken in a level to be used by the specific level before returning from the activity
        if(levelChosen != 0 && levelComplete) {
            levelComplete = false;
            Date temp = new Date();
            long longTime = (temp.getTime() - levelStartTime.getTime());
            double timeTaken = (double) longTime;
            String menuMessage = "Level completed in " + timeTaken / 1000 + " seconds";
            if (longTime < bestTimes[levelChosen - 1] || bestTimes[levelChosen - 1] == 0) {
                menuMessage += "\nThat's a new best!";
                bestTimes[levelChosen - 1] = longTime;
            } else if (bestTimes[levelChosen - 1] != 0) {
                menuMessage += "\nBest Time is " + (double) (bestTimes[levelChosen - 1]) / 1000 + " seconds";
            }
            menu_message.setText(menuMessage);


        }
        super.onResume();
    }

    // Message displayer for when user clicks on the save icon to save progress
    public void displaySaveMsg()
    {
        menu_message.setText(R.string.saveMessage);
    }

    // Deletes all saved progress
    public void deleteSaveData()
    {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(getString(R.string.saveData), 1);
        editor.apply();
    }

}