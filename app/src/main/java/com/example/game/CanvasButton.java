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
import android.view.MotionEvent;
import com.example.logindemo.R;

public class CanvasButton
{
    // x-y coordinates of the button drawn onto the Canvas
    private int x, y;

    // Width and height of the button's Bitmap image and boundaries
    private int width, height;

    // Link to the player object for saving time states
    private Player playerChar;

    // Bitmap image set of the button and the index for the button's current
    // image
    private Bitmap[] images;
    private int spriteNum;

    // Type of the button, defines what the button does when user taps inside its
    // area
    private String functionType;

    // GameView the button is associated with, for changing time periods
    private GameView currentGV;

    // Hiding variable to determine whether the button is visible on-screen
    private boolean show;

    // Constructor for the image button. String "type" defines the function
    // and look of the button.
    public CanvasButton(int x, int y, String type, Player pChar, GameView gv, Resources res)
    {
        // Initializes the x-y coordinates of the button, adjusted for screen size
        this.x = (int)(x*screenRatioX);
        this.y = (int)(y*screenRatioY);

        // Initializes the type of the button to determine what function it activates
        // upon being tapped
        functionType = type;

        // Links the player character to the button in case the function involves a Player
        // function like saving time states
        playerChar = pChar;

        // Links the current GameView to the button for changing time periods
        currentGV = gv;

        // Initializes spriteNum to 0, using the first image of the button's sprite set by
        // default
        spriteNum = 0;

        // Creates the button and its functionality based on what String
        // is inputted
        createButton(type, res);
    }

    // === GETTERS ===

    // Returns the x-coordinate of the button
    public int getX()
    {
        return x;
    }

    // Returns the y-coordinate of the button
    public int getY()
    {
        return y;
    }

    // Returns the width of the button
    public int getWidth()
    {
        return width;
    }

    // Returns the height of the button
    public int getHeight()
    {
        return height;
    }

    // Returns the Bitmap image of the button
    public Bitmap getImage()
    {
        return images[spriteNum];
    }

    // Returns the sprite number of the button
    public int getSpriteNum()
    {
        return spriteNum;
    }

    // === SETTERS ===

    // Sets the visibility (and availability) of the button
    public void setShow(boolean s)
    {
        show = s;
    }

    // Sets the sprite number of the button to change its look
    public void setSpriteNum(int n)
    {
        spriteNum = n;
    }

    // === OTHER METHODS ===

    // Creates the button and its functionality based on what String was
    // inputted. Each type of button has its own set of one or more images.
    public void createButton(String type, Resources res)
    {
        // Button for saving a time state to return to
        if(type.equals("SAVESTATE"))
        {
            // Shows the button by default
            show = true;

            // Retrieves the TimeState saving button PNGs and halves their dimensions.
            // TimeState saving buttons have two associated PNGs: one for saving and one
            // for returning.
            images = new Bitmap[2];
            images[0] = BitmapFactory.decodeResource(res, R.drawable.timesave_button);
            images[1] = BitmapFactory.decodeResource(res, R.drawable.timereturn_button);
            width = images[0].getWidth()/2;
            height = images[0].getHeight()/2;
        }
        else if(type.equals("TIMECHANGE"))
        {
            // Hides the button by default since you need to touch a time machine to use
            // this button
            show = false;

            // Retrieves the time-changing button PNG and scales it to 3/7 size
            images = new Bitmap[1];
            images[0] = BitmapFactory.decodeResource(res, R.drawable.time_change_button);
            width = (int)(images[0].getWidth()*(3/7f));
            height = (int)(images[0].getHeight()*(3/7f));
        }

        // Scales the button to device screen size
        if(images != null)
        {
            width = (int)(width*screenRatioX);
            height = (int)(height*screenRatioY);

            // Creates the final, scaled image of the button
            for(int i = 0; i < images.length; i++)
            {
                images[i] = Bitmap.createScaledBitmap(images[i], width, height, false);
            }
        }
        // If the button PNGs couldn't be retrieved for use, send an error message
        else
        {
            Log.d("IMAGE ERROR", "Failed to decode and use button image(s).");
        }
    }

    // Performs a function upon tapping within the button's area, based on the
    // button's type
    public void buttonClick(MotionEvent event)
    {
        // Only allows use of the button if it's visible
        if(show)
        {
            // Temporarily stores x-y coordinates of where the user tapped the screen to
            // check whether the user clicked the button
            float xTap = event.getX();
            float yTap = event.getY();

            // If user taps within the button's boundaries, perform its specified action
            if(xTap >= x && xTap <= x + width && yTap >= y && yTap <= y + height)
            {
                // Time save button for returning to a previous player state
                if(functionType.equals("SAVESTATE"))
                {
                    // If the player hasn't created a TimeState, create one at their location
                    if(playerChar.getSavedState() == null)
                    {
                        playerChar.createTimeState();

                        // Switches image to the "time return" button
                        spriteNum = 1;
                    }
                    // If the player HAS created a TimeState, return them back to that state
                    else
                    {
                        // Reverts the player back to the x-y location and speeds they were at
                        // when they created the TimeState
                        playerChar.setXPos(playerChar.getSavedState().getX());
                        playerChar.setYPos(playerChar.getSavedState().getY());
                        playerChar.setXSpeed(playerChar.getSavedState().getXSpeed());
                        playerChar.setYSpeed(playerChar.getSavedState().getYSpeed());

                        // Switches the button image back to the "time save" image
                        spriteNum = 0;

                        // Removes the TimeState upon activating the button
                        playerChar.setSavedState(null);
                    }
                }
                // Time period changing button
                else if(functionType.equals("TIMECHANGE"))
                {
                    // Switches the time period of the level from present to future
                    // or future to present
                    currentGV.setPresentTime(!currentGV.isPresentTime());
                }
            }
        }
    }

    // Returns collision shape of the button to make it translucent when the player passes
    // through it
    public Rect getCollisionShape()
    {
        return new Rect(x, y, x + width, y + height);
    }

    // Draws the button to a given Canvas
    public void drawButton(Canvas levelMap)
    {
        // Only draws the button to the Canvas if it's meant to be shown
        if(show)
        {
            Paint alpha = new Paint();

            // Makes the button translucent if it's touching the player
            if(Rect.intersects(getCollisionShape(), playerChar.getCollisionShape()))
            {
                alpha.setAlpha(75);
            }
            else
            {
                alpha.setAlpha(175);
            }

            // Draws the button to the Canvas
            levelMap.drawBitmap(images[spriteNum], x, y, alpha);
        }
    }
}
