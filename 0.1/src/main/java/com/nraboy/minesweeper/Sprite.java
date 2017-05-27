package com.nraboy.minesweeper;

import android.graphics.*;

public class Sprite {

    protected int x;
    protected int y;
    private GameView gameView;
    private Bitmap sheet;
    protected int width;  // OUTPUT w
    protected int height; // OUTPUT h
    private int inwide;
    private int inhigh;

    public Sprite(GameView gameView, Bitmap bmp, int x, int y, int columns, int rows) {
        this.gameView = gameView;
        this.sheet = bmp;
        this.x = x;
        this.y = y;
        this.inwide = bmp.getWidth() / columns;  // The width of each sprite in the sheet based on column count
        this.inhigh = bmp.getHeight() / rows;   // The height of each sprite in the sheet based on row count
        width = inwide; // button dims don't change
        height = inhigh;
    }

    /*
     * Draw the sprite to the canvas.  Since we are working with sprite sheets, the particular
     * row and column of the sprite need to be specified.  All sprite sheets must contain sprites
     * that are the same size
     *
     * @param    Canvas canvas
     * @param    int spriteColumn
     * @param    int spriteRow
     * @return   void
     */
    public void onDraw(Canvas canvas, int spriteColumn, int spriteRow) {
        // The source rectangle of the sprite sheet based on the column and row specified and the sprite dimensions
        Rect src = new Rect((spriteColumn * inwide), (spriteRow * inhigh),
                       (spriteColumn * inwide) + inwide, (spriteRow * inhigh) + inhigh);
        // The destination rectangle of where to place the cut sprite on the canvas
        Rect dst = new Rect(this.x, this.y, this.x + this.width, this.y + this.height);
        canvas.drawBitmap(this.sheet, src, dst, null);
    }

    /*
     * Check to see if a point, x and y, has collided in the dimensions of the particular
     * sprite.  This is for example a collision of touching or clicking a sprite
     *
     * @param    float otherX
     * @param    float otherY
     * @return   boolean collision
     */
    public boolean hasCollided(float otherX, float otherY) {
        return this.x < otherX && this.y < otherY && this.x + this.width > otherX && this.y + this.height > otherY;
    }

    /*
     * Set the current horizontal position of the sprite
     *
     * @param    int x
     * @return   void
     */
    public void setX(int x) {
        this.x = x;
    }

    /*
     * Set the current vertical position of the sprite
     *
     * @param    int y
     * @return   void
     */
    public void setY(int y) {
        this.y = y;
    }

    /*
     * Return the current horizontal position of the sprite
     *
     * @param
     * @return   int x
     */
    public int getX() {
        return this.x;
    }

    /*
     * Return the current vertical position of the sprite
     *
     * @param
     * @return   int y
     */
    public int getY() {
        return this.y;
    }

    /*
     * Return the width that each sprite in the sheet has
     *
     * @param
     * @return   int width
     */
    public int getWidth() {
        return this.width;
    }

    /*
     * Return the height that each sprite in the sheet has
     * @param
     * @return   int height
     */
    public int getHeight() {
        return this.height;
    }

    public void setWide(int w) { width = w; }
    public void setHigh(int h) { height = h; }
}
