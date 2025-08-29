package com.example.game;

import static com.example.game.GameView.screenRatioX;
import static com.example.game.GameView.screenRatioY;
import com.example.logindemo.R;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class GravityPad
{
    // x-y coordinate locations of the gravity pad
    private int x;
    private int y;

    // Height and width of the gravity pad
    private int width;
    private int height;

    // Bitmap image array for on and off states of the gravity pad
    // 0 = off
    // 1 = on
    private Bitmap[] images;

    // Cooldown timer to control how long it takes for the player to be able
    // to use the gravity pad again
    // 0 = gravity pad ready to activate
    private int cooldown;

    // "Flip" boolean to indicate whether the gravity pad is right-side up or
    // upside down.
    // True = upside down
    // False = right-side up
    private boolean flip;

    public GravityPad(int x, int y, boolean f, Resources res)
    {
        // Creates the Bitmap image array for the gravity pads different
        // states
        images = new Bitmap[2];

        // Sets orientation of the gravity pad based on the "flip" value.
        // If "flip" is True, the flipped variants of the gravity pad PNGs
        // are used instead.
        flip = f;

        // Retrieves the PNGs for the gravity pad's on and off states,
        // along with their dimensions.
        if(flip)
        {
            images[0] = BitmapFactory.decodeResource(res, R.drawable.gravity_pad_off_flipped);
            images[1] = BitmapFactory.decodeResource(res, R.drawable.gravity_pad_on_flipped);
        }
        else
        {
            images[0] = BitmapFactory.decodeResource(res, R.drawable.gravity_pad_off);
            images[1] = BitmapFactory.decodeResource(res, R.drawable.gravity_pad_on);
        }

        // Scales the gravity pad images to the device screen size
        width = images[0].getWidth()/2;
        height = images[0].getHeight()/2;
        width = (int)(width*screenRatioX);
        height = (int)(height*screenRatioY);
        for(int i = 0; i < images.length; i++)
        {
            images[i] = Bitmap.createScaledBitmap(images[i], width, height, false);
        }

        // Initializes x-y location of the gravity pad, adjusted for device
        // screen size. Adjusts y-location for height and orientation of the
        // gravity pad's image.
        if(flip)
        {
            this.x = (int)(x*screenRatioX);
            this.y = (int)(y*screenRatioY);
        }
        else
        {
            this.x = (int)(x*screenRatioX);
            this.y = (int)((y-height)*screenRatioY);
        }

        // Set the gravity pad cooldown to 0, indicating it can be activated
        cooldown = 0;
    }

    // === GETTERS ===

    // Return x-coordinate of the gravity pad
    public int getX()
    {
        return x;
    }

    // Return y-coordinate of the gravity pad
    public int getY()
    {
        return y;
    }

    // Return activation box width of the gravity pad
    public int getWidth()
    {
        return width;
    }

    // Return activation box height of the gravity pad
    public int getHeight()
    {
        return height;
    }

    // Returns a status flag based on the gravity pad's cooldown timer,
    // indicating whether it can be used
    public boolean canUse()
    {
        return cooldown == 0;
    }

    // Returns Bitmap image of the gravity pad based on whether it's usable or not
    public Bitmap getImage()
    {
        // If cooldown is still active, return "off" image
        if(cooldown > 0)
        {
            return images[0];
        }
        // If no longer in cooldown, return "on" image
        else
        {
            return images[1];
        }
    }

    // Returns collision shape of the gravity pad for determining when the player
    // touches it. It's made to be smaller than the actual image for a more logical
    // activation.
    public Rect getCollisionShape()
    {
        if(flip)
        {
            return new Rect(x, y, x + width, y + (int)(height*0.25));
        }
        else
        {
            return new Rect(x, y + (int)(height*0.75), x + width, y + height);
        }
    }

    // === SETTERS ===

    // Sets cooldown timer for gravity pad
    public void setCooldown(int c)
    {
        cooldown = c;
    }

    // === OTHER METHODS ===

    // Draws the gravity pad onto the Canvas based on its on or off state
    public void drawPad(Canvas levelMap, Paint paintInfo)
    {
        // If gravity pad is in cooldown, draw its "off" state and decrement the
        // cooldown timer
        if(cooldown > 0)
        {
            cooldown--;
            levelMap.drawBitmap(images[0], x, y, paintInfo);
        }
        // If gravity pad isn't in cooldown, draw its "on" state
        else
        {
            levelMap.drawBitmap(images[1], x, y, paintInfo);
        }
    }
}
