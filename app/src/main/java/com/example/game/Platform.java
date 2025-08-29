package com.example.game;

import static com.example.game.GameView.screenRatioX;
import static com.example.game.GameView.screenRatioY;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import com.example.logindemo.R;

public class Platform
{
    // Current x-y coordinates of the platform
    private int x, y;

    // Limits on the platform's movement in the x and y directions
    private int xStart, xEnd, yStart, yEnd;

    // x and y speeds of the platform
    private double xSpeed;
    private double ySpeed;

    // Width and height of the platform
    private int width;
    private int height;

    // Type and image of the platform, defining wha tit looks like
    private String tileType;
    private Bitmap image;

    // Constants for the max height and width of platforms on Pixel 4 device
    public static final int MAX_WIDTH = 1080;
    public static final int MAX_HEIGHT = 2280;

    // Default constructor for fixed platforms. Adjusts position and size according to device
    // screen size.
    public Platform(int x, int y, int w, int h, Resources res, String tileType)
    {
        // Sets starting position and speeds of the platform
        this.x = (int)(x*screenRatioX);
        this.y = (int)(y*screenRatioY);
        xSpeed = 0;
        ySpeed = 0;

        // Sets x-y movement bounds of the platform to the same coordinates as the initial
        // x-y position since the platform isn't moving
        xStart = xEnd = x;
        yStart = yEnd = y;

        // Sets tileType of the platform to determine its appearance
        this.tileType = tileType;

        // Width and height get scaled later in the createImage function
        width = w;
        height = h;

        createImage(res);
    }

    // Constructor for moving platforms. Adjusts position, size, and speed according to device
    // screen size.
    public Platform(int x1, int y1, int x2, int y2, double xs, double ys, int w, int h, Resources res, String tileType)
    {
        // Sets initial location and speeds of the platform. Absolute values of the speeds are
        // used to help with checking movement bounds later.
        x = (int)(x1*screenRatioX);
        y = (int)(y1*screenRatioY);
        xSpeed = Math.abs(xs*screenRatioX);
        ySpeed = Math.abs(ys*screenRatioY);

        // Sets the platform's x-y movement bounds, adjusted for device screen size
        xStart = (int)(screenRatioX*Math.min(x1, x2));
        xEnd = (int)(screenRatioX*Math.max(x1, x2));
        yStart = (int)(screenRatioY*Math.min(y1, y2));
        yEnd = (int)(screenRatioY*Math.max(y1, y2));

        // Sets tileType of the platform to determine its appearance
        this.tileType = tileType;

        // Sets width and height of the platform, scaled later in the createImage function
        width = w;
        height = h;

        createImage(res);
    }

    // === GETTERS ===

    // Returns current x-coordinate of platform
    public int getX()
    {
        return x;
    }

    // Returns current y-coordinate of platform
    public int getY()
    {
        return y;
    }

    // Returns x-speed of the platform
    public double getXSpeed()
    {
        return xSpeed;
    }

    // Returns y-speed of the platform
    public double getYSpeed()
    {
        return ySpeed;
    }

    // Returns width of the platform
    public int getWidth()
    {
        return width;
    }

    // Returns height of the platform
    public int getHeight()
    {
        return height;
    }

    // Returns Bitmap image of the platform
    public Bitmap getImage()
    {
        return image;
    }

    // === OTHER METHODS ===

    // Creates a tile image by either cutting a subimage from a resource PNG or setting
    // up image scaling for directly drawing on the level Canvas later
    public void createImage(Resources res)
    {
        if (tileType.equals("WOOD"))
        {
            // Decodes the "wood_tiles" PNG for use
            image = BitmapFactory.decodeResource(res, R.drawable.wood_tiles);
        }

        // For tileTypes where the platform is taken from a PNG,
        // cut the platform image into the desired size, given the dimensions
        // requested don't exceed those of the image source. Otherwise, use the max
        // dimensions possible.
        if (tileType.equals("WOOD"))
        {
            if (width <= image.getWidth() && height <= image.getHeight())
            {
                image = Bitmap.createBitmap(image, 0, 0, width, height);
            }
            else
            {
                width = image.getWidth();
                height = image.getHeight();
            }
        }

        // Scales the platform image to fit device size
        width = (int) (width * screenRatioX);
        height = (int) (height * screenRatioY);

        // Creates a scaled Bitmap for platforms cut from PNGs
        // *** MUST HAVE ONE createdScaledBitmap PER BITMAP IMAGE ***
        if (tileType.equals("WOOD"))
        {
            image = Bitmap.createScaledBitmap(image, width, height, false);
        }
    }

    // Draws platform onto a given Canvas
    public void drawPlatform(Canvas levelMap, Paint paintInfo)
    {
        // Sets antialiasing to "false" to smooth edges of platforms
        paintInfo.setAntiAlias(false);

        if(tileType.equals("DIRT"))
        {
            // Sets color to brown if dirt tile selected
            paintInfo.setColor(Color.parseColor("#875B45"));
        }
        else if(tileType.equals("STONE"))
        {
            // Sets color to dark gray if stone tile selected
            paintInfo.setColor(Color.parseColor("#838282"));
        }
        else if(tileType.equals("METAL"))
        {
            // Sets color to blue-gray if metal tile selected
            paintInfo.setColor(Color.parseColor("#ACBABB"));
        }
        else if(tileType.equals("DARK_METAL"))
        {
            // Sets color to black if dark metal tile selected
            paintInfo.setColor(Color.parseColor("#000000"));
        }
        else if(tileType.equals("GRASS"))
        {
            // Sets color to green if grass tile selected
            paintInfo.setColor(Color.parseColor("#49bf5a"));
        }

        // Draws platform images either directly on the Canvas or from a Bitmap
        if(tileType.equals("WOOD"))
        {
            // Uses wood_tiles PNG to draw the platform Bitmap onto the level Canvas
            levelMap.drawBitmap(image, x, y, paintInfo);
        }
        else
        {
            // Uses Canvas functions to directly draw platforms onto the level
            levelMap.drawRect(x, y, x + width, y + height, paintInfo);
        }
    }

    // Returns collision shape of the platform for determining when the player
    // touches it
    public Rect getCollisionShape()
    {
        return new Rect(x, y, x + width, y + height);
    }

    // Moves the platform based on its given x-y speeds and movement bounds. Movement
    // isn't adjusted for screen size since the speeds were already adjusted upon the
    // platform's creation.
    public void movePlatform()
    {
        // If the platform hits either end of its movement boundaries, reverse its
        // movement direction
        if((xSpeed > 0 && (x + width >= xEnd)) || (xSpeed < 0 && x <= xStart))
        {
            xSpeed = -xSpeed;
        }
        if((ySpeed > 0 && (y + height >= yEnd)) || (ySpeed < 0 && y <= yStart))
        {
            ySpeed = -ySpeed;
        }

        // Move the platform with its x-y speeds
        x += (int)xSpeed;
        y += (int)ySpeed;
    }
}