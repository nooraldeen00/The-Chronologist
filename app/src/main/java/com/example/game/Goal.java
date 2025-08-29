package com.example.game;

import static com.example.game.GameView.screenRatioX;
import static com.example.game.GameView.screenRatioY;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import com.example.logindemo.R;

public class Goal
{
    // x-y coordinate locations of the Goal
    private int x;
    private int y;

    // Height and width of the Goal
    private int width;
    private int height;

    // Bitmap image for the goalpost.
    private Bitmap image;

    public Goal(int x, int y, Resources res)
    {
        // Retrieves the PNG for the Goalpost.
        image = BitmapFactory.decodeResource(res, R.drawable.goalpost);

        // Scales the goalpost image to the device screen size.
        width = (image.getWidth()*3)/2;
        height = (image.getHeight()*3)/2;
        width = (int)(width*screenRatioX);
        height = (int)(height*screenRatioY);

        image = Bitmap.createScaledBitmap(image, width, height, false);

        // Initializes x-y location of the goalpost, adjusted for device
        // screen size.
        // Adjusts y-location for height of the goalpost's image.
        // Adjust x-location to center the goalpost on the desired position.
        this.x = (int)((x-width/2f)*screenRatioX);
        this.y = (int)((y-height)*screenRatioY);
    }

    /* === GETTERS === */

    // Return x-coordinate of the Goalpost.
    public int getX(){return x;}

    // Return y-coordinate of the Goalpost.
    public int getY(){return y;}

    // Return activation box width of the Goalpost.
    public int getWidth(){return width;}

    // Return activation box height of the Goalpost.
    public int getHeight() {return height;}

    public Bitmap getImage() {return image;}

    // Returns collision shape of the goalpost for determining when the player
    // touches it. It's made to be smaller than the actual image for a more logical
    // activation.
    public Rect getCollisionShape()
    {
        return new Rect(x, y, x+width, y+height);
    }

    // === OTHER METHODS ===

    public void drawGoal(Canvas levelMap, Paint paintInfo)
    {
        levelMap.drawBitmap(image, x, y, paintInfo);
    }
}
