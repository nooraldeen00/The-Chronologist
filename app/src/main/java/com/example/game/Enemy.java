package com.example.game;

import static com.example.game.GameView.screenRatioX;
import static com.example.game.GameView.screenRatioY;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import com.example.logindemo.R;

import java.util.ArrayList;

public class Enemy
{
    // x-y location and speeds of the enemy
    private int x, y;
    private double xSpeed, ySpeed;

    // Gravitational acceleration on the enemy
    private double gravAccel;

    // Movement speed of the enemy when it charges towards the player
    private static final double chargeSpeed = 30;

    // Range at which the enemy will try to attack the player
    private static final int attackRange = 200;

    // Friction variable to simulate "sliding" motion and momentum
    // of the enemy
    private static final double friction = 0.9;

    // Fixed value for how long the enemy remains on-screen after being killed
    private static final double afterDeathTime = 100;

    // "Slope" variable for collision with walls
    private int slope;

    // Width and height of the enemy Bitmap images
    private int width, height;

    // Bitmap image sprites for the enemy and its sprite number
    // for determining which sprite it's currently showing
    // spriteNum = 0 is Neutral
    // spriteNum = 1 is Angry
    // spriteNum = 2 is Dead
    private Bitmap[] images;
    private int spriteNum;

    // GameView the enemy is associated with, stored for interaction with
    // the level's entities and the player
    private GameView currentGV;

    // "Flip" variable to indicate the orientation of the enemy
    private boolean flip;

    // Cooldown timer for indicating when the enemy will attack again
    // 0 = enemy ready to attack
    private int cooldown;

    // Boolean indicating if the enemy is still alive
    private boolean alive;

    // Timer for how long the enemy remains on-screen after being killed
    private int existTimer;

    // Constructor for the enemy
    public Enemy(int xLoc, int yLoc, double gravAccel, Resources res)
    {
        // Makes the enemy right-side up on creation
        flip = false;

        // Initializes the enemy sprite number to 0, making it "neutral"
        // on creation
        spriteNum = 0;

        // Sets attack cooldown to zero, making enemy ready to attack on
        // creation
        cooldown = 0;

        // Initializes the enemy's x-y speeds and "slope" to 0
        xSpeed = 0;
        ySpeed = 0;
        slope = 0;

        // Initializes the level's gravitational acceleration on the enemy
        this.gravAccel = gravAccel;

        // Initializes the Bitmap image array for the enemy and fills it
        // with its sprites, scaled for device screen size
        images = new Bitmap[3];
        createImages(res);

        // Sets the x-y location of the enemy. y-position is adjusted for the
        // enemy's height, and x-position is centered.
        x = (int)((xLoc-width/2f)*screenRatioX);
        y = (int)((yLoc-height)*screenRatioY);

        // Makes the enemy alive on creation
        alive = true;
    }

    // === GETTERS ===

    // Returns x-coordinate of the enemy
    public int getX()
    {
        return x;
    }

    // Returns y-coordinate of the enemy
    public int getY()
    {
        return y;
    }

    // Returns the x-speed of the enemy
    public double getXSpeed()
    {
        return xSpeed;
    }

    // Returns the y-speed of the enemy
    public double getYSpeed()
    {
        return ySpeed;
    }

    // Returns the width of the enemy
    public int getWidth()
    {
        return width;
    }

    // Returns the height of the enemy
    public int getHeight()
    {
        return height;
    }

    // Returns the current Bitmap image of the enemy
    public Bitmap getImage()
    {
        return images[spriteNum];
    }

    // Returns the gravitational acceleration on the enemy
    public double getGravAccel()
    {
        return gravAccel;
    }

    // Returns flag indicating if the enemy is alive or not
    public boolean isAlive()
    {
        return alive;
    }

    // Returns current remaining time on the enemy's existence timer
    // for when it dies
    public int getExistTimer()
    {
        return existTimer;
    }

    // === SETTERS ===

    // Sets the x-coordinate of the enemy
    public void setX(int xPos)
    {
        x = xPos;
    }

    // Sets the y-coordinate of the enemy
    public void setY(int yPos)
    {
        y = yPos;
    }

    // === OTHER METHODS ===

    // Draws the enemy to a given level Canvas, based on its spriteNum condition
    public void drawEnemy(Canvas levelMap, Paint paintInfo)
    {
        // If enemy is alive, draw them normally
        if(alive)
        {
            levelMap.drawBitmap(images[spriteNum], x, y, paintInfo);
        }
        // If enemy is killed, fade them out while showing the dead PNG
        else
        {
            if(existTimer > 0)
            {
                // Decrement the exist timer
                existTimer--;

                // Make the translucency of the enemy a percentage based on
                // how much time they have left over the original max existence
                // timer
                Paint alpha = new Paint();
                alpha.setAlpha((int)(existTimer*255/afterDeathTime));

                // Draw the enemy to the level map
                levelMap.drawBitmap(images[spriteNum], x, y, alpha);
            }
        }
    }

    // Links a GameView to the enemy for interaction with its entities
    // and platforms
    public void linkGV(GameView gv)
    {
        currentGV = gv;
    }

    // Initializes the Bitmap array of image sprites for the enemy,
    // adjusting them for device screen size
    public void createImages(Resources res)
    {
        // Retrieves the neutral, angry, and dead PNGs for the enemy
        images[0] = BitmapFactory.decodeResource(res, R.drawable.enemy_neutral);
        images[1] = BitmapFactory.decodeResource(res, R.drawable.enemy_angry);
        images[2] = BitmapFactory.decodeResource(res, R.drawable.enemy_dead);

        // Scales the dimensions of the PNGs to device screen size
        width = images[0].getWidth()/3;
        height = images[0].getHeight()/3;
        width = (int)(width*screenRatioX);
        height = (int)(height*screenRatioY);
        for(int i = 0; i < images.length; i++)
        {
            images[i] = Bitmap.createScaledBitmap(images[i], width, height, false);
        }
    }

    // Flips the enemy's image sprites for when the direction of gravity changes
    public void flipImages()
    {
        flip = !flip;

        // Iterates through each image of the enemy's sprite array and flips it vertically
        for(int i = 0; i < images.length; i++)
        {
            Matrix matrix = new Matrix();
            matrix.postScale(1, -1, images[i].getWidth() / 2f, images[i].getHeight() / 2f);
            images[i] = Bitmap.createBitmap(images[i], 0, 0, images[i].getWidth(), images[i].getHeight(), matrix, true);
            images[i] = Bitmap.createScaledBitmap(images[i], width, height, false);
        }
    }

    // Returns the collision hitbox of the enemy for determining when it touches
    // the player or a gravity pad
    public Rect getCollisionShape()
    {
        return new Rect(x, y, x + width, y + height);
    }

    // Handles enemy collision with platforms
    public void touchPlatforms(ArrayList<Platform> platforms)
    {
        // Check collision with all platforms in the level
        for(Platform p : platforms)
        {
            while(Rect.intersects(getCollisionShape(), p.getCollisionShape()))
            {
                // Platform collision for downwards gravity. While the enemy
                // is falling down into a platform, push them up
                if(gravAccel >= 0)
                {
                    // Push enemy down if they're moving upwards and hitting a platform
                    // (stops them from going through ceilings)
                    if(ySpeed < 0)
                    {
                        // Accounts for stronger y-speeds
                        if(ySpeed < -10)
                        {
                            y -= (int)ySpeed;
                        }
                        // Accounts for weaker y-speeds
                        else
                        {
                            y -= (int)(ySpeed*0.7);
                        }
                    }
                    // Move enemy up if they're falling into a platform
                    else
                    {
                        y--;
                    }
                }
                // Platform collision for upwards gravity. While the enemy is
                // rising up into a ceiling, push them down
                else
                {
                    // Push enemy up if they're moving downwards and hitting a platform
                    // (stops them from going through floors)
                    if(ySpeed > 0)
                    {
                        // Accounts for stronger y-speeds
                        if(ySpeed > 10)
                        {
                            y -= (int)ySpeed;
                        }
                        // Accounts for weaker y-speeds
                        else
                        {
                            y -= (int)(ySpeed*0.7);
                        }
                    }
                    // Move enemy down if they're rising into a platform
                    else
                    {
                        y++;
                    }
                }

                // Set y-speed to zero for colliding with ceiling or floor
                ySpeed = 0;
            }
        }
    }

    // Moves enemy vertically based on gravity
    public void moveVertical()
    {
        // Controls falling movement of player from gravity
        ySpeed += gravAccel*screenRatioY;
        y += (int)ySpeed;
    }

    // Moves enemy horizontally in response to proximity to player, checks for wall collision
    public void moveHorizontal(ArrayList<Platform> platforms)
    {
        // Assume the enemy isn't embedded in a wall
        slope = 0;

        // Enemy only attacks if it's alive
        if(alive)
        {
            // Calculates distance from the enemy to the player
            Player target = currentGV.getPlayerChar();
            int targetX = target.getXPos();
            int targetY = target.getYPos();
            double dist = Math.sqrt(Math.pow(x - targetX, 2) + Math.pow(y - targetY, 2));

            // If enemy is ready to attack and player is within 50 pixels of it, change speed
            // to move towards the player
            if(cooldown == 0)
            {
                if(dist <= attackRange)
                {
                    // Set the enemy sprite to "angry" for attacking
                    spriteNum = 1;

                    // Player is to the left of the enemy or directly on its x-position
                    if(x - targetX >= 0)
                    {
                        xSpeed = -chargeSpeed;
                    }
                    // Player is to the right of the enemy
                    else
                    {
                        xSpeed = chargeSpeed;
                    }

                    // Set the cooldown timer of the enemy to keep it from attacking
                    // again immediately afterwards
                    cooldown = 50;
                }
            }
            else
            {
                // Decrement the attack cooldown timer while the enemy is still in cooldown
                cooldown--;

                // Set the enemy sprite to "neutral" if its cooldown is zero to indicate it's
                // passive again
                if(cooldown == 0)
                {
                    spriteNum = 0;
                }
            }
        }

        // Move the enemy horizontally with friction imposed on it
        x += (int)xSpeed;
        xSpeed *= friction;

        // Increment "slope" counter as long as the enemy is running into a wall
        for(Platform p : platforms)
        {
            while(slope < 8 && Rect.intersects(getCollisionShape(), p.getCollisionShape()))
            {
                slope++;
            }
        }

        // Push the enemy out of the wall if it's touched the wall long enough.
        // 1 is added or subtracted from xSpeed to ensure the enemy is always pushed at least
        // 1 pixel out of the wall, even when its speed is zero.
        if(slope == 8)
        {
            if(xSpeed < 0)
            {
                x -= (int)(xSpeed - 1);
            }
            else if(xSpeed > 0)
            {
                x -= (int)(xSpeed + 1);
            }
        }
    }

    // Handles enemy collision with gravity pads
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

    // "Kills" the enemy when player touches them and shield power is activated
    public void killEnemy(Player playerChar)
    {
        if(alive)
        {
            // Indicate the enemy is dead
            alive = false;

            // Set enemy sprite to dead image
            spriteNum = 2;

            // Launch enemy in the direction the player is moving at 5x their
            // speed
            xSpeed = 5*playerChar.getXSpeed();
            ySpeed = 5*playerChar.getYSpeed();

            // Set timer for how long enemy remains on-screen before disappearing
            existTimer = (int)afterDeathTime;
        }
    }
}
