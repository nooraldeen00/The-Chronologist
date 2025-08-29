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
import android.util.Log;
import com.example.logindemo.R;

public class TimeMachine
{
    // x-y location of the time machine
    private int x, y;

    // Width and height of the time machine PNGs
    private int width, height;

    // Bitmap images of the time machine
    private Bitmap[] images;

    // Constructor for the time machine. Adjusts x and y positions for
    // device screen size
    public TimeMachine(int x, int y, boolean flip, Resources res)
    {
        // Creates the Bitmap image array for the time machine's different
        // states, adjusting for device screen size
        images = new Bitmap[2];
        images[0] = BitmapFactory.decodeResource(res, R.drawable.time_machine_pres);
        images[1] = BitmapFactory.decodeResource(res, R.drawable.time_machine_future);

        // Scales the time machine images to the device screen size
        width = images[0].getWidth()/2;
        height = images[0].getHeight()/2;
        width = (int)(width*screenRatioX);
        height = (int)(height*screenRatioY);
        for(int i = 0; i < images.length; i++)
        {
            images[i] = Bitmap.createScaledBitmap(images[i], width, height, false);
        }

        // Initializes x-y location of the time machine, adjusted for screen
        // size, height, and orientation. x-position is centered.
        this.x = (int)((x-width/2f)*screenRatioX);
        if(flip)
        {
            this.y = (int)(y*screenRatioY);
        }
        else
        {
            this.y = (int)((y-height)*screenRatioY);
        }

        // Flips the time machine orientation if "flip" variable is True
        if(flip)
        {
            flipImages();
        }
    }

    // === OTHER METHODS ===

    // Flips the time machine's images for different orientations
    public void flipImages()
    {
        for(int i = 0; i < images.length; i++)
        {
            Matrix matrix = new Matrix();
            matrix.postScale(1, -1, images[i].getWidth() / 2f, images[i].getHeight() / 2f);
            images[i] = Bitmap.createBitmap(images[i], 0, 0, images[i].getWidth(), images[i].getHeight(), matrix, true);
            images[i] = Bitmap.createScaledBitmap(images[i], width, height, false);
        }
    }

    // Returns collision shape of the time machine for determining when the player touches it
    public Rect getCollisionShape()
    {
        return new Rect(x, y, x + width, y + height);
    }

    // Draws the time machine to a given Canvas
    public void drawTimeMachine(Canvas levelMap, Paint paintInfo, GameView currentGV)
    {
        // Draws the time machine to the level Canvas based on what time period the level is
        // currently in
        if(images != null)
        {
            // Draws the "present time" state of the time machine given the level is in
            // present time
            if(currentGV.isPresentTime())
            {
                levelMap.drawBitmap(images[0], x, y, paintInfo);
            }
            // Draws the "future time" state of the time machine given the level is in
            // future time
            else
            {
                levelMap.drawBitmap(images[1], x, y, paintInfo);
            }
        }
        else
        {
            Log.d("DRAW ERROR:", "Could not retrieve images for drawing.");
        }
    }
}
