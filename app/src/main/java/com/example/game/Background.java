package com.example.game;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import com.example.logindemo.R;

public class Background
{
    private int x = 0, y = 0;

    // Background PNG
    private Bitmap image;

    // Default constructor, uses "daytime" background
    Background(int screenX, int screenY, Resources res)
    {
        image = BitmapFactory.decodeResource(res, R.drawable.daytime);

        // screenX and screenY = width and height of background PNG, respectively
        image = Bitmap.createScaledBitmap(image, screenX, screenY, false);
    }

    // Custom constructor, allows you to choose which background is used
    Background(int screenX, int screenY, Resources res, String selection)
    {
        // Selects one of three backgrounds and retrieves the image
        if(selection.equals("DAY"))
        {
            image = BitmapFactory.decodeResource(res, R.drawable.daytime);
        }
        else if(selection.equals("NIGHT"))
        {
            image = BitmapFactory.decodeResource(res, R.drawable.night);
        }
        else if(selection.equals("SPACE"))
        {
            image = BitmapFactory.decodeResource(res, R.drawable.space);
        }
        else if(selection.equals("FACILITY"))
        {
            image = BitmapFactory.decodeResource(res, R.drawable.facility);
        }

        // Scales the background PNG to the size of the device screen
        // screenX and screenY = width and height of background PNG, respectively
        image = Bitmap.createScaledBitmap(image, screenX, screenY, false);

    }

    // === GETTERS ===

    // Returns x-coordinate of top-left corner of background image
    public int getX()
    {
        return x;
    }

    // Returns y-coordinate of top-left corner of the background image
    public int getY()
    {
        return y;
    }

    // Returns image of the Background object
    public Bitmap getImage()
    {
        return image;
    }

    // === OTHER METHODS ===

    // Draws Background image to the game Canvas
    // Draws the background of the level
    public void drawBackground(Canvas levelMap, Paint paintInfo)
    {
        levelMap.drawBitmap(image, x, y, paintInfo);
    }
}
