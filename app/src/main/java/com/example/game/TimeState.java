package com.example.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class TimeState
{
    // x-y positions and speeds saved in this time-frozen state
    // All of these are final since they shouldn't ever change once
    // initialized
    private final int x, y;
    private final double xSpeed, ySpeed;

    // Bitmap image to show where the time-frozen state is made
    private Bitmap image;

    // Constructor for the time-frozen state
    public TimeState(int xPos, int yPos, double xs, double ys, Bitmap img)
    {
        // Saves the given x-y positions and speeds to this time-frozen state
        // None of these are adjusted for device screen size since they're already
        // adjusted for in each object that creates a SavedState
        x = xPos;
        y = yPos;
        xSpeed = xs;
        ySpeed = ys;

        // Saves the Bitmap image of the object that creates this saved time state
        image = img;
    }

    // === GETTERS ===

    // Returns x-coordinate of the SavedState
    public int getX()
    {
        return x;
    }

    // Returns y-coordinate of the SavedState
    public int getY()
    {
        return y;
    }

    // Returns x-speed of the SavedState
    public double getXSpeed()
    {
        return xSpeed;
    }

    // Returns y-speed of the SavedState
    public double getYSpeed()
    {
        return ySpeed;
    }

    // Returns Bitmap image of the SavedState
    public Bitmap getImage()
    {
        return image;
    }

    // === OTHER METHODS ===

    // Draws a translucent Bitmap image of the saved time-frozen state
    // to the level Canvas
    public void drawSavedState(Canvas levelMap)
    {
        Paint alpha = new Paint();
        alpha.setAlpha(122);
        levelMap.drawBitmap(image, x, y, alpha);
    }
}
