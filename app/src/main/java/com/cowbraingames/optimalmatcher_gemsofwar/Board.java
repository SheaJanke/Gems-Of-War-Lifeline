package com.cowbraingames.optimalmatcher_gemsofwar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.RequiresApi;

import com.cowbraingames.optimalmatcher_gemsofwar.ml.Model;
import com.cowbraingames.optimalmatcher_gemsofwar.ml.ModelUnquant;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;


@RequiresApi(api = Build.VERSION_CODES.O)
public class Board {

    private Bitmap board;
    private Context context;
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
    public Board(Context context, Bitmap board){
        this.context = context;
        this.board = board;
        width = board.getWidth();
        height = board.getHeight();
        System.out.println("width: " + width + " height: " + height);
        grid = new int[8][8];
        predictEachSquare2();
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

    public void saveEachSquare(){
        Random rand = new Random();
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                Bitmap img = Bitmap.createBitmap(board, i * width/8, j * height/8, width/8, height/8);
                String name = String.valueOf(rand.nextInt(100000));
                saveBitmap(name, img);
                System.out.println("Saved");
            }
        }
    }

    public void predictEachSquare(){
        try {
            Model model = Model.newInstance(context);
            ImageProcessor imageProcessor = new ImageProcessor.Builder()
                    .add(new ResizeOp(224,224, ResizeOp.ResizeMethod.BILINEAR))
                    .build();
            TensorImage tImage  = new TensorImage(DataType.UINT8);

            for(int i = 0; i < 8; i++){
                for(int j = 0; j < 8; j++){
                    Bitmap img = Bitmap.createBitmap(board, i * width/8, j * height/8, width/8, height/8);
                    tImage.load(img);
                    tImage = imageProcessor.process(tImage);
                    saveBitmap(String.valueOf(i + j), tImage.getBitmap());
                    // Creates inputs for reference.
                    TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.UINT8);
                    inputFeature0.loadBuffer(tImage.getBuffer());

                    // Runs model inference and gets result.
                    Model.Outputs outputs = model.process(inputFeature0);
                    TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
                    float[] results = outputFeature0.getFloatArray();
                    for(int k = 0; k < results.length; k++){
                        System.out.print(results[k]);
                        System.out.print(" ");
                    }
                    System.out.println();
                }
            }

            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
            System.out.println("Exception");
        }
    }

    private void predictEachSquare2(){
        try {
            ModelUnquant model = ModelUnquant.newInstance(context);
            ImageProcessor imageProcessor = new ImageProcessor.Builder()
                    .add(new ResizeOp(224,224, ResizeOp.ResizeMethod.BILINEAR))
                    .add(new NormalizeOp(127.5f, 127.5f))
                    .build();
            TensorImage tImage  = new TensorImage(DataType.FLOAT32);
            for(int i = 0; i < 8; i++){
                for(int j = 0; j < 8; j++){
                    Bitmap img = Bitmap.createBitmap(board, i * width/8, j * height/8, width/8, height/8);
                    tImage.load(img);
                    tImage = imageProcessor.process(tImage);
                    // Creates inputs for reference.
                    TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
                    inputFeature0.loadBuffer(tImage.getBuffer());

                    // Runs model inference and gets result.
                    ModelUnquant.Outputs outputs = model.process(inputFeature0);
                    TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
                    float[] results = outputFeature0.getFloatArray();
                    int maxIndex = 0;
                    float maxVal = 0;
                    for(int k = 0; k < results.length; k++){
                        System.out.print(results[k]);
                        System.out.print(' ');
                        if(results[k] > maxVal){
                            maxIndex = k;
                            maxVal = results[k];
                        }
                    }
                    System.out.println();
                    grid[j][i] = maxIndex;
                }
            }
            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
    }

    public static void saveBitmap(String bitName,
                                  Bitmap mBitmap) {//  ww  w.j  a va 2s.c  o  m

        Random r = new Random();
        File f = new File(Environment.getExternalStorageDirectory()
                .toString() + "/" + bitName + String.valueOf(r.nextInt(10000))+ ".png");
        try {/* ww  w  .  j a va2 s . c  om*/
            f.createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int[][] getGrid(){
        return grid;
    }
}
