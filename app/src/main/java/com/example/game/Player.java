package com.example.game;

// Change these to use com.example.logindemo

import static com.example.game.GameView.screenRatioX;
import static com.example.game.GameView.screenRatioY;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import com.example.logindemo.R;

import java.util.ArrayList;

public class Player
{
    // Different states indicating what the player is doing and what they can do
    // in those states.
    // === WILL CONTROL WHAT ANIMATIONS THE PLAYER IS GOING THROUGH ===
    public enum PlayerState
    {
        IDLE,
        WALKING,
        FALLING,
        JUMPING
    }

    // Boolean to show if Player has finished level yet.
    private boolean levelComplete = false;

    // State the player is in, controls what they can do and what they are doing
    private PlayerState currentState;

    // Stores current level GameView the player character is in
    private GameView gameView;

    // Toggle variables for player movement
    private boolean jumping = false;

    // Location coordinates and speeds in horizontal & vertical
    // directions
    private int xPos, yPos;
    private double xSpeed, ySpeed;
    private static final double jumpSpeed = 40;

    // Gravitational acceleration on the player
    private double gravAccel;

    // "Slope" detector to keep player from walking into walls
    private int slope;

    // Power-up effect the player currently has and its duration before ending
    private int powTimer;
    private PowerUp currentPower;

    // Timer for player audio like footsteps. Any value greater than zero keeps
    // audio from playing to avoid playing too much audio at once.
    private int soundTimer;

    // TimeState for when the player uses time manipulation to save a previous state
    // they were in
    private TimeState savedState;

    // Width and height of the player character Bitmap
    private int width, height;

    // Integer counter to determine if player is falling or on ground
    private int falling;

    // Boolean variable to indicate whether the player is right-side up or
    // upside down
    private boolean flip;

    // Bitmap image array and sprite number for player sprites
    private Bitmap images[];
    private int spriteNum;

    // Main Player Object constructor
    public Player(GameView gv, Resources res, int xPos, int yPos, double gravAccel) {

        // Stores current GameView
        gameView = gv;

        // Decodes and stores player's different sprites
        images = new Bitmap[4];
        images[0] = BitmapFactory.decodeResource(res, R.drawable.player_char_og);
        images[1] = BitmapFactory.decodeResource(res, R.drawable.player_char_speed);
        images[2] = BitmapFactory.decodeResource(res, R.drawable.player_char_jump);
        images[3] = BitmapFactory.decodeResource(res, R.drawable.player_char_shield);

        // Scales the player character images to fit device size
        width = images[0].getWidth();
        height = images[0].getHeight();
        width /= 4;
        height /= 4;
        width = (int)(width*screenRatioX);
        height = (int)(height*screenRatioY);
        for(int i = 0; i < images.length; i++)
        {
            images[i] = Bitmap.createScaledBitmap(images[i], width, height, false);
        }

        // Sets player character's start location, speeds, states,
        // and "slope". yPos is adjusted to account for player's height.
        // Neither coordinate is adjusted for screen size since the Level
        // automatically does itself.
        this.xPos = xPos;
        this.yPos = yPos - height;
        this.xSpeed = 0;
        this.ySpeed = 0;
        currentState = PlayerState.IDLE;
        falling = 0;
        slope = 0;

        // Sets the gravitational acceleration on the player
        this.gravAccel = gravAccel;

        // Starts the player with no power-up, sets image to default
        spriteNum = 0;
        powTimer = 0;
        currentPower = null;

        // Sets the player to being right-side up on creation
        flip = false;

        // Sets sound timer to zero, player sounds will occur at the next
        // available moment
        soundTimer = 0;
    }

    // ===== GETTER METHODS =====

    // Returns current sprite image of player
    public Bitmap getImage()
    {
        return images[spriteNum];
    }

    // Returns the boolean of if the player is completed or not.
    public boolean getComplete()
    {
        return levelComplete;
    }

    // Returns width of player character image
    public int getWidth()
    {
        return width;
    }

    // Returns height of player character image
    public int getHeight()
    {
        return height;
    }

    // Returns x-position of player character
    public int getXPos()
    {
        return xPos;
    }

    // Returns y-position of player character
    public int getYPos()
    {
        return yPos;
    }

    // Returns x-speed of player character
    public double getXSpeed()
    {
        return xSpeed;
    }

    // Returns y-speed of player character
    public double getYSpeed()
    {
        return ySpeed;
    }

    // Returns gravitational acceleration on the player character
    public double getGravAccel()
    {
        return gravAccel;
    }

    // Returns saved TimeState of player character
    public TimeState getSavedState()
    {
        return savedState;
    }

    // Returns the current power-up the player has (if they do have it)
    public PowerUp getCurrentPower()
    {
        return currentPower;
    }

    // Returns orientation boolean of the player
    public boolean isFlipped()
    {
        return flip;
    }

    // ===== SETTER METHODS =====

    // Sets x-location of player
    public void setXPos(int x)
    {
        xPos = x;
    }

    // Sets y-location of player
    public void setYPos(int y)
    {
        yPos = y;
    }

    // Sets "jumping" condition of player
    public void setJumping(boolean j)
    {
        jumping = j;
    }

    // Sets x-speed of player
    public void setXSpeed(double s)
    {
        xSpeed = s;
    }

    // Sets y-speed of player
    public void setYSpeed(double s)
    {
        ySpeed = s;
    }

    // Sets gravitational acceleration of the player
    public void setGravAccel(double g)
    {
        gravAccel = g;
    }

    // Sets state of player
    public void setPlayerState(PlayerState s)
    {
        currentState = s;
    }

    // Sets saved TimeState of the player
    public void setSavedState(TimeState t)
    {
        savedState = t;
    }

    // Sets power-up timer of the player
    public void setPowTimer(int t)
    {
        powTimer = t;
    }

    // ===== OTHER METHODS =====

    // Changes x-speed of player
    public void changeXSpeed(double s)
    {
        xSpeed += s;
    }

    // Changes y-speed of player
    public void changeYSpeed(double s)
    {
        ySpeed += s;
    }

    // Moves player vertically based on gravity
    public void moveVertical()
    {
        // Controls falling movement of player from gravity
        ySpeed += gravAccel*screenRatioY;
        yPos += (int)ySpeed;
    }

    // Moves player horizontally, checks for wall collision
    public void moveHorizontal(ArrayList<Platform> platforms)
    {
        // Change player's x-position by their speed (adjusted for screen
        // size), assume they're not embedded in a wall
        xPos += (int)(xSpeed*screenRatioX);
        slope = 0;

        // Increment "slope" counter as long as the player is running into a wall
        for(Platform p : platforms)
        {
            while(slope < 8 && Rect.intersects(getCollisionShape(), p.getCollisionShape()))
            {
                slope++;
            }
        }

        // Push player out of the wall if they touched the wall long enough.
        // 1 is added or subtracted from xSpeed to ensure the player is always pushed at least
        // 1 pixel out of the wall, even when their speed is zero.
        if(slope == 8)
        {
            if(xSpeed < 0)
            {
                xPos -= (int)(xSpeed - 1);
            }
            else if(xSpeed > 0)
            {
                xPos -= (int)(xSpeed + 1);
            }
        }
    }

    // Indicates if player jumps, controls whether they can
    public void jump()
    {
        // If player hasn't already made a jump action and is NOT falling,
        // move them upwards and set the "falling" counter to indicate
        // that they can only fall
        if(!jumping && falling < 3)
        {
            // Downwards gravity
            if(gravAccel >= 0)
            {
                // Jump upwards at normal speed if player doesn't have jump power, jump
                // 1.5x as high if they do
                if(currentPower != null && currentPower.getType().equals("JUMP"))
                {
                    ySpeed = -jumpSpeed*1.5*screenRatioY;
                }
                else
                {
                    ySpeed = -jumpSpeed*screenRatioY;
                }
            }
            else
            {
                // Jump downwards at normal speed if player doesn't have jump power, jump
                // 1.5x as high if they do
                if(currentPower != null && currentPower.getType().equals("JUMP"))
                {
                    ySpeed = jumpSpeed*1.5*screenRatioY;
                }
                else
                {
                    ySpeed = jumpSpeed*screenRatioY;
                }
            }

            falling = 4;
            currentState = PlayerState.JUMPING;

            // Keeps player from doing multiple jumps while holding down
            // the jump action
            jumping = true;
        }
    }

    // Returns collision shape of the player for determining when the player
    // touches other entities
    public Rect getCollisionShape()
    {
        return new Rect(xPos, yPos, xPos + width, yPos + height);
    }

    // Flips the player's image for when gravity goes upwards
    public void flipImages()
    {
        flip = !flip;

        // Iterates through each image of the player's sprite array and flips it vertically
        for(int i = 0; i < images.length; i++)
        {
            Matrix matrix = new Matrix();
            matrix.postScale(1, -1, images[i].getWidth() / 2f, images[i].getHeight() / 2f);
            images[i] = Bitmap.createBitmap(images[i], 0, 0, images[i].getWidth(), images[i].getHeight(), matrix, true);
            images[i] = Bitmap.createScaledBitmap(images[i], width, height, false);
        }
    }

    // Handles player collision with platforms
    public void touchPlatforms(ArrayList<Platform> platforms)
    {
        // Increment "falling" variable to count how long it has been
        // since the player last touched ground
        falling++;

        // Check collision with all platforms in the level
        for(Platform p : platforms)
        {
            while(Rect.intersects(getCollisionShape(), p.getCollisionShape()))
            {
                // Platform collision for downwards gravity
                if(gravAccel >= 0)
                {
                    // Push player down if they're moving upwards and hitting a platform
                    // (stops them from going through ceilings)
                    if(ySpeed < 0)
                    {
                        // Accounts for stronger y-speeds
                        if(ySpeed < -jumpSpeed*0.7)
                        {
                            yPos -= (int)ySpeed;
                        }
                        // Accounts for weaker y-speeds
                        else
                        {
                            yPos -= (int)(ySpeed*0.7);
                        }
                    }
                    // Move player up if they're falling into a platform and reset the
                    // "falling" counter
                    else
                    {
                        yPos--;
                        falling = 0;
                        currentState = PlayerState.IDLE;
                    }
                }
                // Platform collision for upwards gravity
                else
                {
                    // Push player up if they're moving downwards and hitting a platform
                    // (stops them from going through floors)
                    if(ySpeed > 0)
                    {
                        // Accounts for stronger y-speeds
                        if(ySpeed > jumpSpeed*0.7)
                        {
                            yPos -= (int)ySpeed;
                        }
                        // Accounts for weaker y-speeds
                        else
                        {
                            yPos -= (int)(ySpeed*0.7);
                        }
                    }
                    // Move player down if they're rising into a platform and reset the
                    // "falling" counter
                    else
                    {
                        yPos++;
                        falling = 0;
                        currentState = PlayerState.IDLE;
                    }
                }

                // Set y-speed to zero for colliding with ceiling or floor
                ySpeed = 0;
            }
        }
    }

    // Handles player collision with gravity pads
    public void touchGravityPads(ArrayList<GravityPad> gravPads)
    {
        for(GravityPad g : gravPads)
        {
            // If the gravity pad is on and the player touches it, reverse the direction of gravity
            // and set the cooldown timer for the gravity pad
            if(g.canUse())
            {
                if(Rect.intersects(getCollisionShape(), g.getCollisionShape()))
                {
                    gravAccel = -gravAccel;
                    g.setCooldown(50);

                    // Flips the player image to match the direction of gravity
                    flipImages();
                }
            }
        }
    }

    // Handles player collision with goalpost.
    public void touchGoal(Goal gPost)
    {
        // If player touches the goalpost, set levelComplete to true.
        if(Rect.intersects(getCollisionShape(), gPost.getCollisionShape()))
        {
            levelComplete = true;
        }
    }

    // Handles player collision with time machines
    public void touchTimeMachines(ArrayList<TimeMachine> machines)
    {
        // Flag for whether the player has touched a time machine
        boolean touchingMachine = false;

        // If the player touches any time machine, set the button-showing flag to True
        for(TimeMachine m : machines)
        {
            if(Rect.intersects(getCollisionShape(), m.getCollisionShape()))
            {
                touchingMachine = true;
            }
        }

        // If the player is touching a machine, show the time-changing button;
        // otherwise, hide it
        if(touchingMachine)
        {
            gameView.getTimeChangeButton().setShow(true);
        }
        else
        {
            gameView.getTimeChangeButton().setShow(false);
        }
    }

    // Handles player collision with power-ups and how long the player keeps the power
    public void touchPowerUps(ArrayList<PowerUp> powerups)
    {
        for(PowerUp p : powerups)
        {
            // If player touches a power-up, it's interactable, and the player doesn't already
            // have a power, switch sprites to match that power and gain its effect
            if(Rect.intersects(getCollisionShape(), p.getCollisionShape()) && p.isActive() && currentPower == null)
            {
                // Sets sprite to speed power sprite
                if(p.getType().equals("SPEED"))
                {
                    spriteNum = 1;
                }
                // Sets sprite to jump power sprite
                else if(p.getType().equals("JUMP"))
                {
                    spriteNum = 2;
                }
                // Sets sprite to shield power sprite
                else if(p.getType().equals("SHIELD"))
                {
                    spriteNum = 3;
                }

                // Links player to the power-up to determine when it can reappear later and
                // what effect the player has
                currentPower = p;

                // Sets the power timer for how long the player keeps the power-up
                powTimer = currentPower.getDuration();

                // Hides the power-up to avoid multiple collisions
                p.setActive(false);
            }
        }

        // Decrements the power timer if it's greater than zero and the player currently has
        // a power
        if(currentPower != null)
        {
            if(powTimer > 0)
            {
                powTimer--;

                // If power timer is about to end, make the player sprite flicker
                if(powTimer <= 50)
                {
                    flicker(currentPower);
                }
            }
            // If the power timer runs out, make the power-up reappear where it was, remove the
            // power from the player, and revert them back to their original sprite
            else if(powTimer == 0)
            {
                currentPower.setActive(true);
                currentPower = null;
                spriteNum = 0;
            }
        }
    }

    // Makes the player "flicker" between images to indicate when a power-up is about
    // to run out
    public void flicker(PowerUp pow)
    {
        // Player shows the powered costume when the power timer is in the ranges 1-10, 21-30,
        // and 41-50
        if((powTimer > 0 && powTimer <= 10) || (powTimer > 20 && powTimer <= 30) || (powTimer > 40 && powTimer <= 50))
        {
            if(pow.getType().equals("SPEED"))
            {
                spriteNum = 1;
            }
            else if(pow.getType().equals("JUMP"))
            {
                spriteNum = 2;
            }
            else if(pow.getType().equals("SHIELD"))
            {
                spriteNum = 3;
            }
        }
        // Player shows normal costume when the power timer is in the ranges 11-20 and 31-40
        else
        {
            spriteNum = 0;
        }
    }

    // Handles player collision with enemies
    // ===== NEEDS TO IMPLEMENT SHIELD ABILITY FOR REMOVING ENEMIES =====
    public void touchEnemies(ArrayList<Enemy> enemies)
    {
        for(int i = 0; i < enemies.size(); i++)
        {
            if(Rect.intersects(getCollisionShape(), enemies.get(i).getCollisionShape()) && enemies.get(i).isAlive())
            {
                // If the player touches an enemy and they don't have the shield power-up,
                // send them back to the start of the level
                if(currentPower == null || (currentPower != null && !currentPower.getType().equals("SHIELD")))
                {
                    gameView.resetPlayer();
                }
                // Kill the enemy if player does have shield power-up when they touch the enemy
                else
                {
                    enemies.get(i).killEnemy(this);
                }
            }
        }
    }

    // Creates a TimeState when the player manipulates time to save a previous
    // state they were in
    public void createTimeState()
    {
        savedState = new TimeState(xPos, yPos, xSpeed, ySpeed, images[spriteNum]);
    }

    // Animates the player
    // ===== USE ONLY IF USING BITMAP ARRAY FOR SPRITES =====
    /*
    public void animate()
    {
        switch(currentState)
        {
            case IDLE:

                image = imageArray[0];
                break;

            case WALKING:

                image = imageArray[1 + frame]
        }
        imageArray[spriteNum].
    }
    */
}
