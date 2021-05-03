package com.cowbraingames.optimalmatcher_gemsofwar;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Pair;

public class Board {

    private Bitmap board;
    private int width;
    private int height;

    private Color[] colors = {
            Color.valueOf(0.5f,0.5f,0.5f),
            Color.valueOf(0.5f,0.5f,0.5f),
            Color.valueOf(0.74f,0.18f,0.14f), // fire
            Color.valueOf(0.03f,0.50f,0.80f), // water
            Color.valueOf(0.2f,0.85f,0.18f), // earth
            Color.valueOf(0.87f,0.85f,0.3f),  // light
            Color.valueOf(0.5f,0.15f,0.75f)  // dark
    };

    public Board(Bitmap board){
        this.board = board;
        width = board.getWidth();
        height = board.getHeight();
        getAverageColor(2,1);
        getAverageColor(6,1);
        getAverageColor(0,2);
        getAverageColor(4,5);
    }

    private Color getAverageColor(int row, int col){
        int colIndex = width/24 * (3 * col + 1);
        int rowIndex = height/24 * (3 * row + 1);
        float totalBlue = 0, totalGreen = 0, totalRed = 0;
        for(int i = 0; i < height/24; i++){
            for(int j = 0; j < width/24; j++){
                Color c = board.getColor(rowIndex+i, colIndex+j);
                totalBlue += c.blue();
                totalGreen += c.green();
                totalRed += c.red();
            }
        }
        int totalSquares = (width/24) * (height/24);
        System.out.println(totalRed/totalSquares);
        System.out.println(totalGreen/totalSquares);
        System.out.println(totalBlue/totalSquares);
        return new Color();
    }
}
