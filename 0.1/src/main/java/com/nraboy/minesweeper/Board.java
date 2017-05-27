package com.nraboy.minesweeper;

import java.util.*;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.*;
import android.content.*;
import android.util.DisplayMetrics;
import android.view.*;

public class Board {

    public static final int OVER = 1;
    public static final int FINISH = 2;
    private static final int MAX_LIFE = 30; // 'medium' board move limit

    Cell[][] grid;
    private GameView gameView;
    private Bitmap cellSpriteSheet;
    private int boardSize;
    private int bombCount;
    private int cellsRevealed;

    Cell[] cBtn;
    Random rng;
    int _moves;

    public Board(GameView gameView, int boardSize, int bombCount) {
        this.grid = new Cell[boardSize][boardSize];
        this.gameView = gameView;
        this.cellSpriteSheet = BitmapFactory.decodeResource(this.gameView.context.getResources(), R.drawable.cell_spritesheet_md);
        this.boardSize = boardSize;
        this.bombCount = bombCount;

        cBtn = new Cell[6];
        rng = new Random();
    }

    /*
     * Draw a particular sprite from the sprite sheet to the screen.  If the cell has
     * not yet been revealed, show the blank sprite.  If the cell has been revealed and is
     * an indication of cheating, show a flag sprite.  If the cell has been revealed, show a
     * bomb.  Otherwise, show the number of neighbors that contain bombs in the cell.
     *
     * @param    Canvas canvas
     * @return   void
     */
    public void draw(Canvas canvas) {
        for(int i = 0; i < this.grid.length; i++) {
            for(int j = 0; j < this.grid.length; j++) {
                grid[i][j].onDraw(canvas);

/*
                if(this.grid[i][j].isRevealed) {
                    if(this.grid[i][j].isCheat) {
                        this.grid[i][j].onDraw(canvas, 2, 3);
                    } else if(this.grid[i][j].isBomb) {
                        this.grid[i][j].onDraw(canvas, 1, 0);
                    } else {
                        switch(this.grid[i][j].getBombNeighborCount()) {
                            case 0:
                                this.grid[i][j].onDraw(canvas, 2, 0);
                                break;
                            case 1:
                                this.grid[i][j].onDraw(canvas, 0, 1);
                                break;
                            case 2:
                                this.grid[i][j].onDraw(canvas, 1, 1);
                                break;
                            case 3:
                                this.grid[i][j].onDraw(canvas, 2, 1);
                                break;
                            case 4:
                                this.grid[i][j].onDraw(canvas, 0, 2);
                                break;
                            case 5:
                                this.grid[i][j].onDraw(canvas, 1, 2);
                                break;
                            case 6:
                                this.grid[i][j].onDraw(canvas, 2, 2);
                                break;
                            case 7:
                                this.grid[i][j].onDraw(canvas, 0, 3);
                                break;
                            case 8:
                                this.grid[i][j].onDraw(canvas, 1, 3);
                                break;
                            default:
                                this.grid[i][j].onDraw(canvas, 0, 0);
                                break;
                        }
                    }
                } else {
                    this.grid[i][j].onDraw(canvas, 0, 0);
                }
*/
            }
        }

        for (int i=0; i < cBtn.length; i++)
            cBtn[i].onDraw(canvas); //, i % 3, 1+i/3);
    }

    private void makeRandomBoard() throws Exception {
        int w = grid.length;
        int h = grid.length;
        for (int x = 0; x < w; x++)
        {
            for (int y = 0; y < h; y++)
            {
                int color = 1 + rng.nextInt(6);
                grid[x][y] = new Cell(gameView, cellSpriteSheet, x, y, false);
                grid[x][y].isRevealed = true;
                grid[x][y].setColor(color);
                grid[x][y].setDColor(Cell.Colors[color-1]);
            }
        }

        /* KBR it took me forever to notice, but in the python original, this
           code is mis-implemented so it is effectively a NOP. Turn it off!

        // make board easier by setting some boxes to same color as a neighbor
        int boxesToChange = 200;
        for (int i = 0; i < boxesToChange; i++)
        {
            int x = 1+rng.nextInt(w-2);
            int y = 1+rng.nextInt(h-2);
            int direction = rng.nextInt(4);
            switch (direction)
            {
                case 0: // change left and up neighbors
                    grid[x-1][y].color = grid[x][y].color;
                    grid[x][y-1].color = grid[x][y].color;
                    break;
                case 1: // change right and down neighbors
                    grid[x+1][y].color = grid[x][y].color;
                    grid[x][y+1].color = grid[x][y].color;
                    break;
                case 2: // change right and up neighbors
                    grid[x][y-1].color = grid[x][y].color;
                    grid[x+1][y].color = grid[x][y].color;
                    break;
                case 3: // change left and down neighbors
                    grid[x][y+1].color = grid[x][y].color;
                    grid[x-1][y].color = grid[x][y].color;
                    break;
                default:
                    throw new Exception();
            }

        }*/
    }

    /*
     * Reset the board to the initial state.  This can act the same as an initialization function.
     * Resetting involves creating a new board, and placing bombs in new positions.
     *
     * @param
     * @return   void
     */
    public void reset()
    {
        try {
            makeRandomBoard();
        } catch (Exception e) {
            e.printStackTrace();
        }
        _moves = 0;
/*
        for(int i = 0; i < this.grid.length; i++) {
            for(int j = 0; j < this.grid.length; j++) {
                this.grid[i][j] = new Cell(this.gameView, this.cellSpriteSheet, i, j, false);
            }
        }
*/

        for (int i=0; i < cBtn.length; i++)
        {
            cBtn[i] = new Cell(gameView, cellSpriteSheet, i, 0, false);
            cBtn[i].isRevealed = true;
            cBtn[i].setColor(i+1);
            cBtn[i].setDColor(Cell.Colors[i]);
        }

        //this.shuffleBombs(this.bombCount);
        //this.calculateCellNeighbors();
        this.setPositions();
        this.cellsRevealed = 0;
    }

    /*
     * Pick random locations for bombs to appear in the board grid.  If the grid location
     * already contains a bomb, loop until it finds a spot that is available.
     *
     * @param    int bombCount
     * @return   void
     */
    public void shuffleBombs(int bombCount) {
        boolean spotAvailable = true;
        Random random = new Random();
        int row;
        int column;
        for(int i = 0; i < bombCount; i++) {
            do {
                column = random.nextInt(8);
                row = random.nextInt(8);
                spotAvailable = this.grid[column][row].isBomb;
            } while (spotAvailable);
            this.grid[column][row].isBomb = true;
        }
    }

    /*
     * Determine all the cells that touch a particular cell
     *
     * @param
     * @return   void
     */
    public void calculateCellNeighbors() {
        for(int x = 0; x < this.grid.length; x++) {
            for(int y = 0; y < this.grid.length; y++) {
                for(int i = this.grid[x][y].getX() - 1; i <= this.grid[x][y].getX() + 1; i++) {
                    for(int j = this.grid[x][y].getY() - 1; j <= this.grid[x][y].getY() + 1; j++) {
                        if(i >= 0 && i < this.grid.length && j >= 0 && j < this.grid.length) {
                            this.grid[x][y].addNeighbor(this.grid[i][j]);
                        }
                    }
                }
            }
        }
    }

    /*
     * Set the position of each cell on the board.  To make things a little more pleasing visually,
     * determine an offset so that the grid is a little more centered in the screen
     *
     * @param
     * @return   void
     */
    public void setPositions() {
        //int horizontalOffset = (320 - (this.boardSize * gridSize)) / 2;
        int horizontalOffset = MARGIN;
        for(int i = 0; i < this.grid.length; i++) {
            for(int j = 0; j < this.grid.length; j++) {
                this.grid[i][j].setX(horizontalOffset + i * gridSize);
                this.grid[i][j].setY(90 + j * gridSize);
                this.grid[i][j].setHigh(gridSize);
                this.grid[i][j].setWide(gridSize);
            }
        }

        int colorbtnY = 90 + grid.length * gridSize + MARGIN;
        for (int i = 0; i < cBtn.length; i++)
        {
            cBtn[i].setX(MARGIN + 20 + i * (gridSize+20+MARGIN));
            cBtn[i].setY(colorbtnY);
            cBtn[i].setHigh(gridSize+20);
            cBtn[i].setWide(gridSize+20);
        }
    }

    /*
     * Reveal a particular cell on the board.  If it is not a bomb, check to see if it has zero
     * neighbors that are bombs.  Reveal all cells recursively that are neighbors until it finds
     * a neighboring cell that is a bomb and stop.
     *
     * @param    Cell c
     * @return   boolean bomb
     */
    public boolean reveal(Cell c) {
        c.reveal();
        if(!c.isBomb) {
            this.cellsRevealed++;
            if(c.getBombNeighborCount() == 0) {
                ArrayList<Cell> neighbors = c.getNeighbors();
                for(int i = 0; i < neighbors.size(); i++) {
                    if(!neighbors.get(i).isRevealed) {
                        reveal(neighbors.get(i));
                    }
                }
            }
        }
        return c.isBomb;
    }

    /*
     * Reveal a board cell only if it is a bomb.  This is useful for scenarios like game
     * over, where you might want to show the user all the bombs on the board
     *
     * @param    Cell c
     * @return   void
     */
    public void showBombs(Cell c) {
        if(c.isBomb) {
            c.reveal();
        }
    }

    /*
     * Return how many cells on the board have been revealed.  Useful for calculating score
     *
     * @param
     * @return   int count
     */
    public int getRevealedCount() {
        return this.cellsRevealed;
    }

    private int gridSize;

    private static int MARGIN = 25;

    public void setMetrics(DisplayMetrics metrics)
    {
        gridSize = (metrics.widthPixels - 2 * MARGIN) / boardSize;
        setPositions();
    }

    private ValueAnimator basicAnimator(Cell who, int newColor)
    {
        ValueAnimator anim = ObjectAnimator.ofInt(who, "DColor", who.getDColor(), newColor);
        anim.setDuration(250);
        anim.setEvaluator(new ArgbEvaluator());
        anim.setRepeatCount(0);
        return anim;
    }

    private void animate(int oldc, int newc, final View gView)
    {
        int oldV = Cell.Colors[oldc-1];
        int newV = Cell.Colors[newc-1];
        ValueAnimator anim = basicAnimator(grid[0][0], newV);
/*
        ValueAnimator anim = ObjectAnimator.ofInt(grid[0][0], "DColor", oldV, newV);
        anim.setDuration(250);
        anim.setEvaluator(new ArgbEvaluator());
        anim.setRepeatCount(0);
*/
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                gView.invalidate();
            }
        });
        //anim.start();

        Collection<Animator> cellAnims = new LinkedList<Animator>();
        cellAnims.add(anim);
        for (Cell acell: cellsToAnimate)
        {
            cellAnims.add(basicAnimator(acell, newV));
        }

        AnimatorSet allAnim = new AnimatorSet();
        allAnim.playTogether( cellAnims );
        allAnim.start();
    }

    private LinkedList<Cell> cellsToAnimate;

    public int touch(float x, float y, View gView)
    {
        for (int i = 0; i < 6; i++)
        {
            if (cBtn[i].hasCollided(x,y) && validMove(i+1))
            {
                cellsToAnimate = new LinkedList<Cell>();

                int oldc = grid[0][0].getColor();
                int newc = i+1;
                fill(i + 1);

                animate(oldc, newc, gView);

                _moves++;
            }
        }
        if (score() == 289 && _moves <= MAX_LIFE)
            return FINISH;
        if (_moves >= MAX_LIFE)
            return OVER;
        return 0;
    }

    private boolean validMove(int newC)
    {
        return grid[0][0].getColor() != newC;
    }

    private void fill(int oldC, int newC, int x, int y)
    {
        int color = grid[x][y].getColor();
        if (oldC == newC || color != oldC)
            return;

        cellsToAnimate.add(grid[x][y]);

        grid[x][y].setColor(newC); // NOTE we are _not_ calling setDColor; the animation should do that
        if ( x > 0 )
            fill(oldC, newC, x-1, y);
        if ( x < boardSize - 1)
            fill(oldC, newC, x+1, y);
        if ( y > 0 )
            fill(oldC, newC, x, y-1);
        if ( y < boardSize - 1)
            fill(oldC, newC, x, y+1);
    }

    private void fill(int color)
    {
        fill(grid[0][0].getColor(), color, 0, 0);
    }

    public int score()
    {
        int color = grid[0][0].getColor();
        int score = 0;
        for (int x = 0; x < boardSize; x++)
            for (int y = 0; y < boardSize; y++)
                if (grid[x][y].getColor() == color)
                    score++;
        return score;
    }

    public int moves()
    {
        return _moves;
    }
}
