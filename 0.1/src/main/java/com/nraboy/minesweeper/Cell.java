package com.nraboy.minesweeper;

import java.util.*;
import android.graphics.*;

public class Cell extends Sprite {

    public boolean isRevealed;
    public boolean isBomb;
    public boolean isCheat;
    private ArrayList<Cell> neighbors;
    private int bombNeighborCount;

    public Cell(GameView gameView, Bitmap spriteSheet, int x, int y, boolean isBomb) {
        super(gameView, spriteSheet, x, y, 3, 4);
        this.isBomb = isBomb;
        this.isRevealed = false;
        this.isCheat = false;
        this.bombNeighborCount = 0;
        this.neighbors = new ArrayList<Cell>();
    }

    /*
     * Keep track of every neighbor that touches the current cell.  Will have a maximum of
     * eight neighbors if not an edge or corner cell.  If the neighbor is a bomb, then add
     * it to the count.
     *
     * @param    Cell neighbor
     * @return   void
     */
    public void addNeighbor(Cell neighbor) {
        this.neighbors.add(neighbor);
        if(neighbor.isBomb) {
            this.bombNeighborCount++;
        }
    }

    /*
     * Get all neighbors that touch the current cell.  Will have a maximum of eight neighbors if
     * not an edge or corner cell
     *
     * @param
     * @return    ArrayList<Cell> neighbors
     */
    public ArrayList<Cell> getNeighbors() {
        return this.neighbors;
    }

    /*
     * Get the total number of neighbors to the current cell that have bombs.  Can be a number from
     * zero to eight
     *
     * @param
     * @return    int total
     */
    public int getBombNeighborCount()
    {
        return color; //this.bombNeighborCount;
    }

    /*
     * Reveal the current cell
     *
     * @param
     * @return    void
     */
    public void reveal() {
        this.isRevealed = true;
    }

    public static int Colors[] = new int[] { Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW, Color.GRAY, Color.MAGENTA};

    public void onDraw(Canvas canvas)
    {
        RectF dst = new RectF(this.x, this.y, this.x + this.width, this.y + this.height);
        Paint rPaint = new Paint();
        rPaint.setColor(_dcolor); //Colors[color-1]);
        //canvas.drawRoundRect(dst, 4, 4, rPaint);
        canvas.drawRect(dst, rPaint);
    }

    private int _dcolor;
    public int getDColor() { return _dcolor; }
    public void setDColor(int color) { _dcolor = color; }

    private int color; // color for inkspill
    public int getColor() { return color; }
    public void setColor(int colorDex) { color = colorDex; }
}
