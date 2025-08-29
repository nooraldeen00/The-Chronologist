package com.example.game;

import static com.example.game.GameView.screenRatioX;
import static com.example.game.GameView.screenRatioY;
import static com.example.game.LevelSelectActivity.levelChosen;
import static com.example.game.Platform.MAX_HEIGHT;
import static com.example.game.Platform.MAX_WIDTH;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;

// === ADD import com.example.logindemo.R; AS NEEDED ===

public class Level
{
    // Gravitational acceleration of level
    private double gravityAccel;

    // Starting x and y coordinates of player in level
    private int startX;
    private int startY;

    // Width and height of the device
    private int screenX;
    private int screenY;

    // Background Object of the level
    private Background lvlBackground;

    // Resources Object to access platform images
    private Resources res;

    // ArrayList of platforms for "present" and "future" time periods
    private ArrayList<Platform> platformsPresent;
    private ArrayList<Platform> platformsFuture;

    // ArrayList of gravity pads
    private ArrayList<GravityPad> gravPads;

    // ArrayList of time machines in the level
    private ArrayList<TimeMachine> timeMachines;

    // ArrayList of enemies in the level
    private ArrayList<Enemy> enemies;

    // ArrayList of powerups in the level
    private ArrayList<PowerUp> powerUps;

    // Goal post of the level
    private Goal goalPost;

    // Constructor for the level.
    public Level(Resources r, int screenX, int screenY)
    {
        // Initializes the device screen width and height
        this.screenX = screenX;
        this.screenY = screenY;

        // Initializes the Canvas paint style, platform lists, time machine list,
        // gravity pad list, power-up list, and enemy list
        res = r;
        platformsPresent = new ArrayList<>();
        platformsFuture = new ArrayList<>();
        timeMachines = new ArrayList<>();
        gravPads = new ArrayList<>();
        powerUps = new ArrayList<>();
        enemies = new ArrayList<>();

        createLevel(levelChosen);
    }

    // === GETTERS ===

    // Returns gravitational acceleration of level
    public double getGravityAccel()
    {
        return gravityAccel;
    }

    // Returns player's starting x-coordinate in the level
    public int getStartX()
    {
        return startX;
    }

    // Returns player's starting y-coordinate in the level
    public int getStartY()
    {
        return startY;
    }

    // Returns level background Object
    public Background getLvlBackground()
    {
        return lvlBackground;
    }

    // Returns level's platforms for "present" time period
    public ArrayList<Platform> getPlatformsPresent()
    {
        return platformsPresent;
    }

    // Returns level's platforms for "future" time period
    public ArrayList<Platform> getPlatformsFuture()
    {
        return platformsFuture;
    }

    // Returns the level's list of time machines
    public ArrayList<TimeMachine> getTimeMachines()
    {
        return timeMachines;
    }

    // Returns the level's list of power ups
    public ArrayList<PowerUp> getPowerUps()
    {
        return powerUps;
    }

    // Returns level's gravity pads
    public ArrayList<GravityPad> getGravPads()
    {
        return gravPads;
    }

    // Returns level's enemies
    public ArrayList<Enemy> getEnemies()
    {
        return enemies;
    }

    // Returns level's goalpost.
    public Goal getGoalPost() { return goalPost; }

    // === OTHER METHODS ===

    // Creates the level's platforms and other entities based on the level
    // number given.
    // LIMITS:
    // x-coordinates of platforms can't be > (1080 - platform width) pixels
    // y-coordinates of platforms can't be > (2280 - platform height) pixels
    public void createLevel(int levelNum)
    {
        switch(levelNum)
        {
            // Level 1 start data
            case 1:

                // Sets starting location of player and gravitational acceleration
                startX = (int)(10*screenRatioX);
                startY = (int)(2180*screenRatioY);
                gravityAccel = 3;

                // Sets background to "facility", initializes background image
                lvlBackground = new Background(screenX, screenY, res, "FACILITY");

                // === LEVEL 1 PLATFORMS ===

                // Left and right walls
                platformsPresent.add(new Platform(0, 0, 50, MAX_HEIGHT-200, res, "METAL"));
                platformsPresent.add(new Platform(880, 750, 200, MAX_HEIGHT, res, "METAL"));

                // Other platforms
                platformsPresent.add(new Platform(680, 1515, 200, 50, res, "METAL"));
                platformsPresent.add(new Platform(300, 1300, 200, 50, res, "METAL"));
                platformsPresent.add(new Platform(480, 1100, 200, 50, res, "METAL"));
                platformsPresent.add(new Platform(380, 950, 100, 50, res, "METAL"));
                platformsPresent.add(new Platform(680, 800, 200, 50, res, "METAL"));

                // Gravity pads
                gravPads.add(new GravityPad(690, 2180, false, res));
                gravPads.add(new GravityPad(540, 1150, true, res));

                // Goalpost
                goalPost = new Goal(980, 750, res);

                // Main floor platforms
                platformsPresent.add(new Platform(0, 2180, 390, MAX_HEIGHT, res, "DARK_METAL"));
                platformsPresent.add(new Platform(590, 2180, 490, MAX_HEIGHT, res, "DARK_METAL"));

                break;

            // Level 2 start data
            case 2:

                // Sets starting location of player and gravitational acceleration
                startX = (int)(10*screenRatioX);
                startY = (int)(800*screenRatioY);
                gravityAccel = 1;

                // Sets background to "space"
                lvlBackground = new Background(screenX, screenY, res, "SPACE");

                // Adds goal post to Level 2
                goalPost = new Goal(1015, 2180, res);

                // Adds present time platforms to Level 2
                platformsPresent.add(new Platform(0, 800, 300, MAX_HEIGHT, res, "DIRT"));
                platformsPresent.add(new Platform(500, 200, 75, 1800, res, "STONE"));
                platformsPresent.add(new Platform(125, 0, MAX_WIDTH, 200, res, "STONE"));
                platformsPresent.add(new Platform(880, 2180, MAX_WIDTH, MAX_HEIGHT, res, "DIRT"));

                // Adds future time platforms to Level 2
                platformsFuture.add(new Platform(0, 800, 300, MAX_HEIGHT, res, "DIRT"));
                platformsFuture.add(new Platform(575, 0, MAX_WIDTH, 200, res, "STONE"));
                platformsFuture.add(new Platform(880, 2180, MAX_WIDTH, MAX_HEIGHT, res, "DIRT"));
                platformsFuture.add(new Platform(300, 800, 1080, 800, 10, 0, 200, 50, res, "STONE"));
                platformsFuture.add(new Platform(1080, 1400, 300, 1600, 10, 0, 200, 50, res, "STONE"));

                // Adds gravity pads to Level 2
                gravPads.add(new GravityPad(150, 800, false, res));

                // Add power-ups to Level 2
                powerUps.add(new PowerUp(700, 200, "JUMP", res));

                // Adds time machine to Level 2
                timeMachines.add(new TimeMachine(1000, 200, true, res));

                break;

            // Level 3 start data
            case 3:

                // Sets starting location of player and gravitational acceleration
                startX = (int)(10*screenRatioX);
                startY = (int)(1200*screenRatioY);
                gravityAccel = 3;

                // Sets background to "night"
                lvlBackground = new Background(screenX, screenY, res, "NIGHT");

                // Adds goal post to Level 3
                goalPost = new Goal(50, 400, res);

                // Adds present time platforms to Level 3
                platformsPresent.add(new Platform(200, 1500, MAX_WIDTH, 75, res, "WOOD"));
                platformsPresent.add(new Platform(200, 2180, MAX_WIDTH, MAX_HEIGHT, res, "STONE"));
                platformsPresent.add(new Platform(0, 400, 300, 100, res, "STONE"));
                platformsPresent.add(new Platform(0, 1500, 200, MAX_HEIGHT, res, "DIRT"));
                platformsPresent.add(new Platform(0, 1500, 200, 50, res, "GRASS"));
                platformsPresent.add(new Platform(0, 1250, 125, MAX_HEIGHT, res, "DIRT"));
                platformsPresent.add(new Platform(0, 1250, 125, 50, res, "GRASS"));
                platformsPresent.add(new Platform(900, 1500, 180, MAX_HEIGHT, res, "DIRT"));
                platformsPresent.add(new Platform(900, 1500, 180, 50, res, "GRASS"));

                // Adds future time platforms to Level 3
                platformsFuture.add(new Platform(200, 2180, MAX_WIDTH, MAX_HEIGHT, res, "STONE"));
                platformsFuture.add(new Platform(0, 1500, 200, MAX_HEIGHT, res, "DIRT"));
                platformsFuture.add(new Platform(0, 1500, 200, 50, res, "GRASS"));
                platformsFuture.add(new Platform(0, 1250, 125, MAX_HEIGHT, res, "DIRT"));
                platformsFuture.add(new Platform(0, 1250, 125, 50, res, "GRASS"));
                platformsFuture.add(new Platform(900, 1500, 180, MAX_HEIGHT, res, "DIRT"));
                platformsFuture.add(new Platform(900, 1500, 180, 50, res, "GRASS"));
                platformsFuture.add(new Platform(0, 400, 300, 100, res, "STONE"));

                // Adds gravity pads to Level 3
                gravPads.add(new GravityPad(250, 2180, false, res));
                gravPads.add(new GravityPad(500, 2180, false, res));
                gravPads.add(new GravityPad(750, 2180, false, res));

                // Adds time machines to Level 3
                timeMachines.add(new TimeMachine(990, 1500, false, res));

                // Add enemies to Level 3
                enemies.add(new Enemy(600, 1500, gravityAccel, res));
                enemies.add(new Enemy(820, 1500, gravityAccel, res));

                // Add shield power up to Level 3
                powerUps.add(new PowerUp(10, 1000, "SHIELD", res));

                break;

            // Level 4 start data
            default:

                // Sets starting location of player and gravitational acceleration
                startX = (int)(500*screenRatioX);
                startY = (int)(1980*screenRatioY);
                gravityAccel = 3;

                // Sets background to "night"
                lvlBackground = new Background(screenX, screenY, res, "NIGHT");

                // Adds goal post to Level 4
                goalPost = new Goal(1030, 1780, res);

                // Adds platforms to Level 3
                platformsPresent.add(new Platform(0, 1000, 200, 1280, res, "STONE"));
                platformsPresent.add(new Platform(200, 1200, 100, 1080, res, "WOOD"));
                platformsPresent.add(new Platform(980, 1780, 100, 500, res, "WOOD"));

                // Main floor of the level
                platformsPresent.add(new Platform(0, 1980, MAX_WIDTH, 300, res, "DIRT"));
        }
    }

    // Moves and draws the platforms of the Level to a given Canvas (in GameView)
    public void drawPlatforms(Canvas levelMap, Paint paintInfo, GameView currentGV)
    {
        if(currentGV.isPresentTime())
        {
            for(Platform p: platformsPresent)
            {
                p.movePlatform();
                p.drawPlatform(levelMap, paintInfo);
            }
        }
        else
        {
            for(Platform p: platformsFuture)
            {
                p.movePlatform();
                p.drawPlatform(levelMap, paintInfo);
            }
        }
    }

    // Draws the gravity pads of the Level to a given Canvas (in GameView)
    public void drawPads(Canvas levelMap, Paint paintInfo)
    {
        for(GravityPad g : gravPads)
        {
            g.drawPad(levelMap, paintInfo);
        }
    }

    // Draws the goalposts of the Level to a given Canvas (in GameView)
    public void drawGoal(Canvas levelMap, Paint paintInfo)
    {
        goalPost.drawGoal(levelMap, paintInfo);
    }

    // Draws the time machines of the Level to a given Canvas (in GameView)
    public void drawMachines(Canvas levelMap, Paint paintInfo, GameView currentGV)
    {
        for(TimeMachine m : timeMachines)
        {
            m.drawTimeMachine(levelMap, paintInfo, currentGV);
        }
    }

    // Draws the enemies of the level to a given Canvas (in GameView)
    public void drawEnemies(Canvas levelMap, Paint paintInfo)
    {
        for(int i = 0; i < enemies.size(); i++)
        {
            enemies.get(i).drawEnemy(levelMap, paintInfo);
        }
    }

    // Draws the power-ups of the level to a given Canvas (in GameView)
    public void drawPowerUps(Canvas levelMap, Paint paintInfo)
    {
        for(PowerUp p : powerUps)
        {
            p.drawPowerUp(levelMap, paintInfo);
        }
    }
}
