package com.cowbraingames.optimalmatcher_gemsofwar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.RequiresApi;

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

    public Board(Context context, Bitmap board){
        this.context = context;
        this.board = board;
        width = board.getWidth();
        height = board.getHeight();
        grid = new int[8][8];
        predictEachSquare();
    }

    private void predictEachSquare(){
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
                        if(results[k] > maxVal){
                            maxIndex = k;
                            maxVal = results[k];
                        }
                    }
                    grid[j][i] = maxIndex;
                    /*
                    ImageProcessor imageProcessor2 = new ImageProcessor.Builder()
                            .add(new ResizeOp(224,224, ResizeOp.ResizeMethod.BILINEAR))
                            .build();
                    TensorImage tImage2  = new TensorImage(DataType.UINT8);
                    tImage2.load(img);
                    tImage2 = imageProcessor2.process(tImage2);
                    saveBitmap(tImage2.getBitmap(), maxVal > 0.9 ? maxIndex: -1);
                     */
                }
            }
            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
    }

    public static void saveBitmap(Bitmap mBitmap, int index) {
        String dirName = getDirName(index);
        Random r = new Random();
        File f1 = new File(Environment.getExternalStorageDirectory(), "Gems_of_war");
        if (!f1.exists()) {
            f1.mkdirs();
        }
        f1 = new File(Environment.getExternalStorageDirectory().toString() + "/Gems_of_war", dirName);
        if (!f1.exists()) {
            f1.mkdirs();
        }
        File f = new File(Environment.getExternalStorageDirectory().toString() + "/Gems_of_war/" + dirName, String.valueOf(r.nextInt(100000)) + ".png");
        try{
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

    private static String getDirName(int index){
        switch (index){
            case -1:
                return "unknown";
            case 0:
                return "skull";
            case 1:
                return "super_skull";
            case 2:
                return "fire";
            case 3:
                return "water";
            case 4:
                return "earth";
            case 5:
                return "ground";
            case 6:
                return "light";
            case 7:
                return "dark";
            case 8:
                return "block";
            default:
                return "none";
        }
    }

    public int[][] getGrid(){
        return grid;
    }


}
