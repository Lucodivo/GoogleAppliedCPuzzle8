package com.google.engedu.puzzle8;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class PuzzleBoard {

    private static final int NUM_TILES = 3; // N*N board
    private static final int NUM_TILES_TOTAL = NUM_TILES * NUM_TILES;
    private static final int[][] NEIGHBOUR_COORDS = {
            {-1, 0},
            {1, 0},
            {0, -1},
            {0, 1}
    };
    public ArrayList<PuzzleTile> tiles;
    private int steps = 0;
    private PuzzleBoard previousBoard;
    public int priority;

    PuzzleBoard(Bitmap bitmap, int parentWidth) {
        tiles = new ArrayList<>();
        previousBoard = null;
        steps = 0;

        Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getWidth());
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(croppedBitmap, parentWidth, parentWidth, false);

        int width = scaledBitmap.getWidth();
        int height = scaledBitmap.getHeight();
        int tileWidth = width / NUM_TILES;
        int tileHeight = height / NUM_TILES;

        // create (NxN) - 1 tiles using the bitmap
        for (int i = 0; i < NUM_TILES_TOTAL - 1; i++) {
            int xMul = (i % 3);
            int yMul = (i / 3);
            Bitmap newBitmap = Bitmap.createBitmap(scaledBitmap, xMul * tileWidth, yMul * tileHeight,
                    tileWidth, tileHeight);

            PuzzleTile pTile = new PuzzleTile(newBitmap, i);
            tiles.add(pTile);
        }

        PuzzleTile blankTile = null;
        tiles.add(blankTile);
        setPriority();
    }

    PuzzleBoard(PuzzleBoard otherBoard) {
        tiles = (ArrayList<PuzzleTile>) otherBoard.tiles.clone();
        steps = otherBoard.steps + 1;
        previousBoard = otherBoard;
    }

    public void reset() {
        // Nothing for now but you may have things to reset once you implement the solver.
        steps = 0;
        previousBoard = null;
        priority = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        return tiles.equals(((PuzzleBoard) o).tiles);
    }

    public void draw(Canvas canvas) {
        if (tiles == null) {
            return;
        }
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                tile.draw(canvas, i % NUM_TILES, i / NUM_TILES);
            }
        }
    }

    public boolean click(float x, float y) {
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                if (tile.isClicked(x, y, i % NUM_TILES, i / NUM_TILES)) {
                    return tryMoving(i % NUM_TILES, i / NUM_TILES);
                }
            }
        }
        return false;
    }

    private boolean tryMoving(int tileX, int tileY) {
        for (int[] delta : NEIGHBOUR_COORDS) {
            int nullX = tileX + delta[0];
            int nullY = tileY + delta[1];
            if (nullX >= 0 && nullX < NUM_TILES && nullY >= 0 && nullY < NUM_TILES &&
                    tiles.get(XYtoIndex(nullX, nullY)) == null) {
                swapTiles(XYtoIndex(nullX, nullY), XYtoIndex(tileX, tileY));
                return true;
            }

        }
        return false;
    }

    public boolean resolved() {
        for (int i = 0; i < NUM_TILES * NUM_TILES - 1; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile == null || tile.getNumber() != i)
                return false;
        }
        return true;
    }

    private int XYtoIndex(int x, int y) {
        return x + y * NUM_TILES;
    }

    protected void swapTiles(int i, int j) {
        PuzzleTile temp = tiles.get(i);
        tiles.set(i, tiles.get(j));
        tiles.set(j, temp);
    }

    public ArrayList<PuzzleBoard> neighbours() {
        ArrayList<PuzzleBoard> neighborBoards = new ArrayList<>();

        int nullIndex = -1;
        for (int i = 0; i < NUM_TILES_TOTAL; i++) {
            if (tiles.get(i) == null) {
                nullIndex = i;
                break;
            }
        }
        if (nullIndex != -1) {
            if (nullIndex - NUM_TILES >= 0) {
                PuzzleBoard newBoard = new PuzzleBoard(this);
                if (previousBoard != null) {
                    if (previousBoard.tiles.get(nullIndex - NUM_TILES) != null) {
                        newBoard.swapTiles(nullIndex, nullIndex - NUM_TILES);
                        newBoard.setPriority();
                        neighborBoards.add(newBoard);
                    }
                } else {
                    newBoard.swapTiles(nullIndex, nullIndex - NUM_TILES);
                    newBoard.setPriority();
                    neighborBoards.add(newBoard);
                }
            }
            if (nullIndex + NUM_TILES < NUM_TILES_TOTAL) {
                PuzzleBoard newBoard = new PuzzleBoard(this);
                if (previousBoard != null) {
                    if (previousBoard.tiles.get(nullIndex + NUM_TILES) != null) {
                        newBoard.swapTiles(nullIndex, nullIndex + NUM_TILES);
                        newBoard.setPriority();
                        neighborBoards.add(newBoard);
                    }
                } else {
                    newBoard.swapTiles(nullIndex, nullIndex - NUM_TILES);
                    newBoard.setPriority();
                    neighborBoards.add(newBoard);
                }
            }
            if ((nullIndex % 3) != 2) {
                PuzzleBoard newBoard = new PuzzleBoard(this);
                if (previousBoard != null) {
                    if (previousBoard.tiles.get(nullIndex + 1) != null) {
                        newBoard.swapTiles(nullIndex, nullIndex + 1);
                        newBoard.setPriority();
                        neighborBoards.add(newBoard);
                    }
                } else {
                    newBoard.swapTiles(nullIndex, nullIndex - NUM_TILES);
                    newBoard.setPriority();
                    neighborBoards.add(newBoard);
                }
            }
            if ((nullIndex % 3) != 0) {
                PuzzleBoard newBoard = new PuzzleBoard(this);
                if (previousBoard != null) {
                    if (previousBoard.tiles.get(nullIndex - 1) != null) {
                        newBoard.swapTiles(nullIndex, nullIndex - 1);
                        newBoard.setPriority();
                        neighborBoards.add(newBoard);
                    }
                } else {
                    newBoard.swapTiles(nullIndex, nullIndex - NUM_TILES);
                    newBoard.setPriority();
                    neighborBoards.add(newBoard);
                }
            }
        }

        return neighborBoards;
    }

    public void setPriority() {
        int manhattanDistance = 0;
        for (int i = 0; i < tiles.size(); i++) {
            int indexX = i % NUM_TILES;
            int indexY = i / NUM_TILES;
            PuzzleTile currentTile = tiles.get(i);
            if (currentTile != null) {
                manhattanDistance += Math.abs(indexX - (currentTile.getNumber() % NUM_TILES))
                        + Math.abs(indexY - (currentTile.getNumber() / NUM_TILES));
            }
        }
        priority = manhattanDistance + steps;
    }

    public PuzzleBoard getPreviousBoard() {
        return previousBoard;
    }

}
