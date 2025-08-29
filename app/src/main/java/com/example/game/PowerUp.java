package com.example.game;


import static com.example.game.GameView.screenRatioX;
import static com.example.game.GameView.screenRatioY;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import com.example.logindemo.R;

public class PowerUp
{
    // x-y position of the power-up
    private int x, y;

    // Dimensions of the power-up image
    private int width, height;

    // Bitmap image of the power-up
    private Bitmap image;

    // Type of power-up (speed, jump, shield)
    private String type;

    // Duration of the power-up
    private int duration;

    // Variable for determining when the power-up is shown and if it can be touched
    private boolean active;

    public PowerUp(int x, int y, String type, Resources res)
    {
        // Initializes the x-y coordinate location of the power-up, adjusting for
        // device screen size.
        this.x = (int)(x*screenRatioX);
        this.y = (int)(y*screenRatioY);

        // Sets the type of power-up to be created
        this.type = type;

        // Makes the power-up visible and interactable on creation
        active = true;

        // Creates the power-up based on the type given
        createPowerUp(type, res);
    }

    // === GETTERS ===

    // Returns x-coordinate of the power-up
    public int getX()
    {
        return x;
    }

    // Returns y-coordinate of the power-up
    public int getY()
    {
        return y;
    }

    // Returns the type of the power-up
    public String getType()
    {
        return type;
    }

    // Returns the Bitmap image of the power-up
    public Bitmap getImage()
    {
        return image;
    }

    // Returns a boolean indicating whether the power-up can be touched
    public boolean isActive()
    {
        return active;
    }

    // Returns the initial duration of the power-up
    public int getDuration()
    {
        return duration;
    }

    // === SETTERS ===

    // Sets the "active" state of the power-up to determine if it can be
    // touched or not.
    public void setActive(boolean act)
    {
        active = act;
    }

    // === OTHER METHODS ===

    // Method for creating the power-up based on the input String given
    public void createPowerUp(String pType, Resources res)
    {
        // Uses the speed-power up image. Speed power-up duration is 500 ticks.
        if(pType.equals("SPEED"))
        {
            image = BitmapFactory.decodeResource(res, R.drawable.speed_power);
            duration = 300;
        }
        // Uses the jump power-up image. Jump power-up duration is 500 ticks.
        else if(pType.equals("JUMP"))
        {
            image = BitmapFactory.decodeResource(res, R.drawable.jump_power);
            duration = 300;
        }
        // Uses the shield power-up image. Shield power-up duration is 500 ticks.
        else if(pType.equals("SHIELD"))
        {
            image = BitmapFactory.decodeResource(res, R.drawable.shield_power);
            duration = 300;
        }

        // Scales the size of the power-up image to the device screen size
        if(image != null)
        {
            width = image.getWidth()/7;
            height = image.getHeight()/7;
            width = (int)(width*screenRatioX);
            height = (int)(height*screenRatioY);

            // Creates the final Bitmap of the power-up, scaled to device screen size
            image = Bitmap.createScaledBitmap(image, width, height, false);
        }
        // If couldn't decode and use the power-up image, send an error message.
        else
        {
            Log.d("IMAGE ERROR", "Could not decode and use power-up image.");
        }

    }

    // Draws the power-up on the Canvas, depending on whether the Player has touched
    public void drawPowerUp(Canvas levelMap, Paint paintInfo)
    {
        // If the image was successfully retrieved:
        if(image != null)
        {
            // Draw the
            if(active)
            {
                levelMap.drawBitmap(image, x, y, paintInfo);
            }
        }

    }

    // Method to get the collision rectangle for detecting contact with the power-up
    public Rect getCollisionShape()
    {
        return new Rect(x, y, x + width, y + height);
    }
}