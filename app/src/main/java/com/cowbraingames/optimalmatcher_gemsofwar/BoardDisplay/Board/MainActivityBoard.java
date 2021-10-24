package com.cowbraingames.optimalmatcher_gemsofwar.BoardDisplay.Board;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.cowbraingames.optimalmatcher_gemsofwar.BoardDisplay.BoardGridAdapter;
import com.cowbraingames.optimalmatcher_gemsofwar.Storage.CloudStorageManager;
import com.cowbraingames.optimalmatcher_gemsofwar.R;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.Constants;
import com.cowbraingames.optimalmatcher_gemsofwar.ml.ModelUnquant;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;

public class MainActivityBoard extends Board{
    private final Context context;
    private final Bitmap[][] unprocessedImages;
    private final Bitmap[][] processedImages;
    private final int[][] orbTypes;

    public MainActivityBoard(Context context, Bitmap[][] unprocessedImages) {
        this.context = context;
        this.unprocessedImages = unprocessedImages;
        processedImages = new Bitmap[Constants.BOARD_SIZE][Constants.BOARD_SIZE];
        orbTypes = new int[Constants.BOARD_SIZE][Constants.BOARD_SIZE];
        predictEachSquare();
    }

    private void predictEachSquare(){
        try {
            ModelUnquant model = ModelUnquant.newInstance(context);
            ImageProcessor imageResizer = new ImageProcessor.Builder()
                    .add(new ResizeOp(224,224, ResizeOp.ResizeMethod.BILINEAR))
                    .build();
            ImageProcessor imageNoramlizer = new ImageProcessor.Builder()
                    .add(new NormalizeOp(127.5f, 127.5f))
                    .build();
            TensorImage tImage  = new TensorImage(DataType.FLOAT32);
            for(int i = 0; i < Constants.BOARD_SIZE; i++){
                for(int j = 0; j < Constants.BOARD_SIZE; j++){
                    Bitmap img = unprocessedImages[i][j];
                    tImage.load(img);
                    tImage = imageResizer.process(tImage);
                    processedImages[i][j] = tImage.getBitmap();
                    tImage = imageNoramlizer.process(tImage);

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
                    orbTypes[i][j] = maxIndex;
                }
            }
            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
            e.printStackTrace();
        }
    }

    @Override
    public int[][] getOrbTypes() {
        return orbTypes;
    }

    @Override
    public void reportOrb(int row, int col) {
        final Dialog reportDialog = new Dialog(context);
        reportDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        reportDialog.setCancelable(true);
        reportDialog.setContentView(R.layout.report_dialog);

        final ImageView image = reportDialog.findViewById(R.id.report_dialog_image);
        final ImageView prediction = reportDialog.findViewById(R.id.report_dialog_prediction);
        final Button correctButton = reportDialog.findViewById(R.id.report_dialog_correct_button);
        final Button incorrectButton = reportDialog.findViewById(R.id.report_dialog_incorrect_button);

        Bitmap orbBitmap = processedImages[row][col];
        image.setImageBitmap(orbBitmap);

        int predictedOrbType = orbTypes[row][col];
        prediction.setImageResource(BoardGridAdapter.orbID[predictedOrbType]);

        correctButton.setOnClickListener(view -> {
            reportDialog.dismiss();
        });
        incorrectButton.setOnClickListener((view -> {
            CloudStorageManager.uploadReportedImage(orbBitmap);
            reportDialog.dismiss();
        }));
        reportDialog.show();
    }
}