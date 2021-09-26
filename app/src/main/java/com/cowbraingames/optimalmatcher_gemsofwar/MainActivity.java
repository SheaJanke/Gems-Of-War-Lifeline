package com.cowbraingames.optimalmatcher_gemsofwar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;

import com.cowbraingames.optimalmatcher_gemsofwar.BoardDisplay.BoardGrid;
import com.cowbraingames.optimalmatcher_gemsofwar.Camera.CameraManager;
import com.cowbraingames.optimalmatcher_gemsofwar.Permissions.PermissionsManager;
import com.cowbraingames.optimalmatcher_gemsofwar.ResultsList.ResultsList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    public static final int USE_CAMERA = 1;
    private BoardGrid boardGrid;
    private ResultsList resultsList;
    private CameraManager cameraManager;
    private ImageView testImg;
    private Context context;
    private Activity mainActivity;
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        mainActivity = this;
        setContentView(R.layout.activity_main);
        cameraManager = new CameraManager(context, mainActivity);
        boardGrid = new BoardGrid(context, findViewById(R.id.board));
        resultsList = new ResultsList(context, findViewById(R.id.results_list), boardGrid);
        testImg = findViewById(R.id.testImg);
        spinner = findViewById(R.id.spinner);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PermissionsManager.requestAllPermissions(MainActivity.this, this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> cameraManager.handleCameraButtonClicked());
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        cameraManager.saveImagePath(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        cameraManager.restoreImagePath(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case USE_CAMERA:
                if(resultCode == Activity.RESULT_OK){
                    try {
                        Bitmap boardBitmap = cameraManager.handleCameraResult(requestCode);
                        updateBoardGrid(boardBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            default:
                break;
        }
    }

    private void updateBoardGrid(Bitmap boardBitmap) {
        spinner.setVisibility(View.VISIBLE);
        new Thread(() -> {
            try{
                BoardDetection boardDetection = new BoardDetection(boardBitmap, testImg, mainActivity);
                Board board = new Board(getApplicationContext(), boardDetection.getOrbs());
                runOnUiThread(() -> {
                    int[][] boardOrbs = board.getGrid();
                    boardGrid.setBoardOrbs(boardOrbs);
                    resultsList.setResults(BoardUtils.getSortedResults(boardOrbs));
                    spinner.setVisibility(View.INVISIBLE);
                });
            }catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
