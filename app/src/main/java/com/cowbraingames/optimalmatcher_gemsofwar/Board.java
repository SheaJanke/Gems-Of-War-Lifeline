package com.cowbraingames.optimalmatcher_gemsofwar;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.O)
public class Board {

    private Bitmap board;
    private int width;
    private int height;
    private Color[] colors = {
            Color.valueOf(0.87f,0.72f,0.68f), // super skull
            Color.valueOf(0.65f,0.65f,0.65f), // skull
            Color.valueOf(0.78f,0.33f,0.30f), // fire
            Color.valueOf(0.03f,0.50f,0.80f), // water
            Color.valueOf(0.2f,0.85f,0.18f), // earth
            Color.valueOf(0.55f,0.35f,0.35f), // ground
            Color.valueOf(0.87f,0.85f,0.3f),  // light
            Color.valueOf(0.5f,0.15f,0.75f)  // dark
    };
    private int[][] grid;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public Board(Bitmap board){
        this.board = board;
        width = board.getWidth();
        height = board.getHeight();
        System.out.println("width: " + width + " height: " + height);
        grid = new int[8][8];
        fillGrid();
        printGrid();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void fillGrid(){
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                Color color = getAverageColor(i, j);
                grid[i][j] = getClosestType(color);
            }
        }
    }

    private int getClosestType(Color color) {
        float minDifference = 3;
        int minIndex = -1;
        for(int i = 0; i < colors.length; i++){
            float curDiff = getDiff(color, colors[i]);
            if(curDiff < minDifference){
                minIndex = i;
                minDifference = curDiff;
            }
        }
        return minIndex;
    }

    private float getDiff(Color color1, Color color2) {
        float diff = 0;
        diff += Math.abs(color1.red() - color2.red());
        diff += Math.abs(color1.green() - color2.green());
        diff += Math.abs(color1.blue() - color2.blue());
        return diff;
    }

    private float getSquaredDiff(Color color1, Color color2) {
        float diff = 0;
        diff += Math.pow(color1.red() - color2.red(),2);
        diff += Math.pow(color1.green() - color2.green(), 2);
        diff += Math.pow(color1.blue() - color2.blue(), 2);
        return diff;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private Color getAverageColor(int row, int col){
        int colIndex = width/24 * (3 * col + 1);
        int rowIndex = height/24 * (3 * row + 1);
        float totalBlue = 0, totalGreen = 0, totalRed = 0;
        for(int i = 0; i < height/24; i++){
            for(int j = 0; j < width/24; j++){
                Color c = board.getColor(colIndex+j, rowIndex+i);
                totalBlue += c.blue();
                totalGreen += c.green();
                totalRed += c.red();
            }
        }
        int totalSquares = (width/24) * (height/24);
        return Color.valueOf(totalRed/totalSquares, totalGreen/totalSquares, totalBlue/totalSquares);
    }

    public void printGrid(){
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                System.out.print(grid[i][j]);
            }
            System.out.println();
        }
    }

    public int[][] getGrid(){
        return grid;
    }
}
