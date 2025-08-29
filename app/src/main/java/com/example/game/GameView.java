package com.example.game;

// Imports LevelSelectActivity variables.

import static com.example.game.LevelSelectActivity.levelAccess;
import static com.example.game.LevelSelectActivity.levelChosen;
import static com.example.game.LevelSelectActivity.levelComplete;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.util.ArrayList;

public class GameView extends SurfaceView implements Runnable
{
    private Thread thread;
    private boolean isPlaying = false;
    private final int screenX, screenY;
    public static float screenRatioX, screenRatioY;
    private GameActivity activity;

    // Current level the user is playing, its gravitational acceleration,
    // Platforms, gravity pads, time machines, enemies, power-ups,
    // and Background Object
    private Level levelPlaying;
    private Background levelBG;
    private ArrayList<Platform> platformsPresent;
    private ArrayList<Platform> platformsFuture;
    private ArrayList<GravityPad> gravPads;
    private ArrayList<TimeMachine> timeMachines;
    private ArrayList<Enemy> enemies;
    private ArrayList<PowerUp> powerUps;
    private Goal goalPost;

    // Boolean to indicate if the time period is in the present or future
    // True = present, False = future
    private boolean presentTime;

    // Holds color and styles for drawing to the screen Canvas
    private Paint paintInfo;

    // Player character shown on-screen
    private Player playerChar;

    // TimeState activation and time-machine interaction buttons
    private CanvasButton timeStateButton;
    private CanvasButton timeChangeButton;

    // Modified walkthrough version of constructor
    // screenX = width of screen
    // screenY = height of screen
    // startX = starting x-position of player character
    // startY = starting y-position of player character
    public GameView(GameActivity activity, int screenX, int screenY)
    {
        super(activity);

        // Initializes the current GameActivity
        this.activity = activity;

        // Initializes device screen width and height info
        this.screenX = screenX;
        this.screenY = screenY;

        // Calculates ratio of Google Pixel 4 screen size to user's device
        // screen size. Used to adapt visuals and movement to different
        // screen sizes for consistency.
        // *** MUST SWAP THE CONSTANTS IF USING LANDSCAPE ORIENTATION ***
        screenRatioX = screenX / 1080f;
        screenRatioY = screenY / 2280f;

        // Initializes the player and level states according to the gravity and
        // start positions specified in the Level object. Saves the Background, platforms,
        // gravity pads, time machines, power-ups, enemies, and goals of the level
        // for quicker access and later modification.
        levelPlaying = new Level(getResources(), screenX, screenY);
        levelBG = levelPlaying.getLvlBackground();
        platformsPresent = levelPlaying.getPlatformsPresent();
        platformsFuture = levelPlaying.getPlatformsFuture();
        gravPads = levelPlaying.getGravPads();
        enemies = levelPlaying.getEnemies();
        goalPost = levelPlaying.getGoalPost();
        powerUps = levelPlaying.getPowerUps();
        timeMachines = levelPlaying.getTimeMachines();
        playerChar = new Player(this, getResources(), levelPlaying.getStartX(), levelPlaying.getStartY(), levelPlaying.getGravityAccel());

        // Links the GameView to all enemies of the level to allow them to interact with
        // the level's entities and the player
        for(Enemy e : enemies)
        {
            e.linkGV(this);
        }

        // Creates the time-saving state button
        timeStateButton = new CanvasButton(15, 2100, "SAVESTATE", playerChar, this, getResources());

        // Creates the time-machine interaction button, positioning it relative to the time-saving
        // state button
        int adjustX = timeStateButton.getX() + timeStateButton.getWidth() + 20;
        timeChangeButton = new CanvasButton(adjustX, 2100, "TIMECHANGE", playerChar, this, getResources());

        // Sets the time period to present
        presentTime = true;

        // Initializes the Canvas painting info for the level display
        paintInfo = new Paint();
    }

    /* === Custom constructor for one Activity per level idea ===
    public GameView(GameActivity activity, int screenX, int screenY)
    {
        super(activity);

        this.activity = activity;

        pref = activity.getSharedPreferences("game", Context.MODE_PRIVATE);

        // Finding Screen Size and Making screenRatio
        this.screenX = screenX;
        this.screenY = screenY;
        screenRatioX = 1080f / screenX; //Unsure what values to use as the ratio part
        screenRatioY = 2280f / screenY;

    }
     */

    // === GETTERS ===

    // Returns the player object in the level
    public Player getPlayerChar()
    {
        return playerChar;
    }

    // Returns a boolean indicating the time period of the level
    // True = present, False = future
    public boolean isPresentTime()
    {
        return presentTime;
    }

    // Returns a reference to the time machine activation button so
    // the button can be shown when the player touches a time machine
    public CanvasButton getTimeChangeButton()
    {
        return timeChangeButton;
    }

    // === SETTERS ===

    // Sets time period of the level
    public void setPresentTime(boolean p)
    {
        presentTime = p;
    }

    // === OTHER METHODS ===

    //Extremely Basic run loop, logic should be in update() mostly
    @Override
    public void run()
    {
        isPlaying = true;
        while(isPlaying)
        {
            update();
            draw();
            sleep();
        }
    }

    // Updates physics of the level, (constantly running).
    public void update()
    {
        // Checks to see if playerChar's levelComplete is true.
        // If it is update the available levels, and then set levelComplete to true.
        if(playerChar.getComplete())
        {
            levelComplete = true;
            // If the level chosen is a made level.
            if(levelChosen < 10)
            {
                // Sets the next level as available.
                // Because of how indexes work, the levelChosen int variable
                // holds the next level's index value.
                levelAccess[levelChosen] = true;
            }
            // Ends the current instance of GameActivity and returns to
            // LevelSelectActivity.
            activity.finish();
        }

        // Moves the player and enemies vertically based on their gravitational accelerations
        playerChar.moveVertical();
        for(int i = 0; i < enemies.size(); i++)
        {
            enemies.get(i).moveVertical();
        }

        // Controls physics of player and enemies hitting floors, ceilings, and gravity pads
        if(presentTime)
        {
            playerChar.touchPlatforms(platformsPresent);
            for(int i = 0; i < enemies.size(); i++)
            {
                enemies.get(i).touchPlatforms(platformsPresent);
            }
        }
        else
        {
            playerChar.touchPlatforms(platformsFuture);
            for(int i = 0; i < enemies.size(); i++)
            {
                enemies.get(i).touchPlatforms(platformsFuture);
            }
        }
        playerChar.touchGravityPads(gravPads);
        for(int i = 0; i < enemies.size(); i++)
        {
            enemies.get(i).touchGravityPads(gravPads);
        }

        // Handles collision events for when the player touches goals, time machines,
        // power-ups, and enemies
        playerChar.touchGoal(goalPost);
        playerChar.touchTimeMachines(timeMachines);
        playerChar.touchPowerUps(powerUps);
        playerChar.touchEnemies(enemies);

        // Controls horizontal movement of player, including wall collision
        if(presentTime)
        {
            // Player moves while collidiing with walls and platforms of present time
            playerChar.moveHorizontal(platformsPresent);

            // Makes enemies attack if they're within a given proximity to the player
            for(int i = 0; i < enemies.size(); i++)
            {
                enemies.get(i).moveHorizontal(platformsPresent);
            }
        }
        else
        {
            // Player moves while colliding with walls and platforms of future time
            playerChar.moveHorizontal(platformsFuture);

            // Makes enemies attack if they're within a given proximity to the player
            for(int i = 0; i < enemies.size(); i++)
            {
                enemies.get(i).moveHorizontal(platformsPresent);
            }
        }

        // Keeps player and enemies from going off-screen horizontally
        if(playerChar.getXPos() < 0)
        {
            playerChar.setXPos(0);
        }
        if(playerChar.getXPos() + playerChar.getWidth() > screenX)
        {
            playerChar.setXPos(screenX - playerChar.getWidth());
        }
        for(int i = 0; i < enemies.size(); i++)
        {
            if(enemies.get(i).getX() < 0)
            {
                enemies.get(i).setX(0);
            }
            if(enemies.get(i).getX() + enemies.get(i).getWidth() > screenX)
            {
                enemies.get(i).setX(screenX - enemies.get(i).getWidth());
            }
        }

        // Sends player back to start if they fall down to bottom of screen
        // with downwards gravity or rise up to top of screen with upwards
        // gravity
        if(playerChar.getGravAccel() >= 0)
        {
            if(playerChar.getYPos() + playerChar.getHeight() > screenY)
            {
                resetPlayer();
            }
        }
        else
        {
            if(playerChar.getYPos() < 1)
            {
                resetPlayer();
            }
        }

        // Removes enemies from the list if they fall off-screen
        cleanEnemyList();
    }

    // Updates the level display
    public void draw()
    {
        // Checks if the SurfaceView is accessible, returns NULL if not
        if(getHolder().getSurface().isValid())
        {
            // Retrieves the current Canvas to allow drawing to the background
            Canvas currentCanvas = getHolder().lockCanvas();

            // Draws background onto Canvas
            levelBG.drawBackground(currentCanvas, paintInfo);

            // Draws platforms, enemies, power-ups, goals, time machines, and gravity pads
            // of the Level onto the Canvas. Platforms are moved prior to drawing them.
            levelPlaying.drawPads(currentCanvas, paintInfo);
            levelPlaying.drawEnemies(currentCanvas, paintInfo);
            levelPlaying.drawGoal(currentCanvas, paintInfo);
            levelPlaying.drawMachines(currentCanvas, paintInfo, this);
            levelPlaying.drawPowerUps(currentCanvas, paintInfo);
            // === PUT THIS BELOW THE PLAYER CHARACTER DRAWING LATER ===
            levelPlaying.drawPlatforms(currentCanvas, paintInfo, this);

            // Draws saved TimeState of the player onto the Canvas
            if(playerChar.getSavedState() != null)
            {
                playerChar.getSavedState().drawSavedState(currentCanvas);
            }

            // Draws player character on the Canvas
            currentCanvas.drawBitmap(playerChar.getImage(), playerChar.getXPos(), playerChar.getYPos(), paintInfo);

            // Draws TimeState saving and time period changing buttons on the Canvas
            timeStateButton.drawButton(currentCanvas);
            timeChangeButton.drawButton(currentCanvas);

            // Shows updated Canvas on the screen
            getHolder().unlockCanvasAndPost(currentCanvas);
        }
    }

    public void sleep()
    {
        try
        {
            Thread.sleep(10);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    //Simple Thread Pause and Unpause Systems
    public void resume()
    {
        isPlaying = true;
        thread = new Thread(this);
        thread.start();
    }

    // Pauses the game
    public void pause()
    {
        try
        {
            isPlaying = false;
            thread.join();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    // User controls for moving the player character on the screen
    // ===== MUST CHANGE TO ACCOMMODATE MULTIPLE FINGERS ON THE SCREEN =====
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction())
        {
            // Movement actions for when finger touches the screen without moving.
            // This does NOT have a break statement, in case the user just simply
            // tapped the button upon their first contact rather than with a second
            // finger.
            case MotionEvent.ACTION_DOWN:

                movePlayer(event);

                // Checks if the user tapped the TimeState or time changing buttons with a separate
                // finger, activates it if they're visible
            case MotionEvent.ACTION_POINTER_DOWN:

                timeStateButton.buttonClick(event);
                timeChangeButton.buttonClick(event);
                break;

            // Movement actions for when user slides finger across the screen
            case MotionEvent.ACTION_MOVE:

                movePlayer(event);
                break;

            // Movement actions for when finger is not touching the screen
            case MotionEvent.ACTION_UP:

                playerChar.setJumping(false);
                playerChar.setXSpeed(0);

                break;
        }

        return true;
    }

    // Moves player according to user control input
    public void movePlayer(MotionEvent event)
    {
        // If user touches right half of screen, move player right
        if(event.getX() >= screenX / 2)
        {
            // Move player to the right 1.5x as fast if they have the speed power-up,
            // or go at normal speed if they don't
            PowerUp currentPow = playerChar.getCurrentPower();
            if(currentPow != null && currentPow.getType().equals("SPEED"))
            {
                playerChar.setXSpeed(10*1.5*screenRatioX);
            }
            else
            {
                playerChar.setXSpeed(10*screenRatioX);
            }
        }
        // If user touches left side of screen, move player left
        if(event.getX() < screenX / 2)
        {
            // Move player to the left 1.5x as fast if they have the speed power-up,
            // or go at normal speed if they don't
            PowerUp currentPow = playerChar.getCurrentPower();
            if(currentPow != null && currentPow.getType().equals("SPEED"))
            {
                playerChar.setXSpeed(-10*1.5*screenRatioX);
            }
            else
            {
                playerChar.setXSpeed(-10*screenRatioX);
            }
        }
        // If user touches top half of screen AND gravity pulls downwards,
        // make player jump
        if(event.getY() <= screenY / 2 && playerChar.getGravAccel() >= 0)
        {
            playerChar.jump();
        }
        // If user touches bottom half of screen AND gravity pulls upwards,
        // make player jump
        else if(event.getY() > screenY / 2 && playerChar.getGravAccel() < 0)
        {
            playerChar.jump();
        }
        // If user stops touching top half of screen, indicate
        // that the jump button is no longer held down
        else
        {
            playerChar.setJumping(false);
        }
    }

    // Resets player to starting position upon dying
    public void resetPlayer()
    {
        // Reset time period back to present
        presentTime = true;

        // Resets player x-y location to level's start.
        // y-position accounts for player height
        playerChar.setXPos(levelPlaying.getStartX());
        playerChar.setYPos(levelPlaying.getStartY() - playerChar.getHeight());

        // Sets speed to zero and current state to IDLE
        playerChar.setXSpeed(0);
        playerChar.setYSpeed(0);
        playerChar.setPlayerState(Player.PlayerState.IDLE);
        if(playerChar.isFlipped())
        {
            playerChar.flipImages();
        }

        // Resets gravity back to original direction
        playerChar.setGravAccel(levelPlaying.getGravityAccel());

        // Resets the player back to normal state without powers
        playerChar.setPowTimer(0);
    }

    // Removes enemies from the level's list if they go off-screen
    public void cleanEnemyList()
    {
        for(int i = 0; i < enemies.size(); i++)
        {
            // If the enemy is dead and their existence timer is zero,
            // remove them from the list
            if(!enemies.get(i).isAlive() && enemies.get(i).getExistTimer() == 0)
            {
                enemies.remove(i);
                i--;
            }
            // If the enemy is alive but off-screen, remove them
            else
            {
                int enemyY = enemies.get(i).getY();
                double enemyGrav = enemies.get(i).getGravAccel();

                if(enemyGrav >= 0)
                {
                    if(enemyY + enemies.get(i).getHeight() > screenY)
                    {
                        enemies.remove(i);
                        i--;
                    }
                }
                else
                {
                    if(enemyY < 1)
                    {
                        enemies.remove(i);
                        i--;
                    }
                }
            }
        }
    }
}
