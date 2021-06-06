package com.cowbraingames.optimalmatcher_gemsofwar;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 1;
    private static final int USE_CAMERA = 2;
    private static final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 3;
    private GridView gridView;
    private ArrayList<Result> results;
    private int[][] grid;
    private RecyclerView resultsList;
    private ImageView testImg;
    private Context context;
    private Activity mainActivity;
    private ProgressBar spinner;
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        mainActivity = this;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        gridView = findViewById(R.id.board);
        resultsList = findViewById(R.id.results_list);
        testImg = findViewById(R.id.testImg);
        spinner = findViewById(R.id.spinner);
        setSupportActionBar(toolbar);

        requestPermissions();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            if(hasCameraPermissions()){
                startCameraActivity();
            }
        });
    }

    private void requestPermissions(){
        if(!hasCameraPermissions()){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, REQUEST_CAMERA);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);
        }
    }

    private void startCameraActivity(){
        String fileName = "boardImg.png";
        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            File imageFile = new File(storageDirectory, fileName);
            imageFile.createNewFile();
            imagePath = imageFile.getAbsolutePath();
            Uri imgUri = FileProvider.getUriForFile(MainActivity.this, "com.cowbraingames.optimalmatcher_gemsofwar.fileprovider", imageFile);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
            startActivityForResult(intent, USE_CAMERA);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasCameraPermissions(){
        return ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("imagePath", imagePath);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        imagePath = savedInstanceState.getString("imagePath");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case USE_CAMERA:
                if(resultCode == Activity.RESULT_OK){
                    handleCameraResult();
                }
            default:
                break;
        }
    }

    private void handleCameraResult(){
        spinner.setVisibility(View.VISIBLE);
        Bitmap boardBitmap = BitmapFactory.decodeFile(imagePath);
        new Thread(() -> {
            try{
                BoardDetection boardDetection = new BoardDetection(boardBitmap, testImg, mainActivity);
                Board board = new Board(getApplicationContext(), boardDetection.getOrbs());
                runOnUiThread(() -> {
                    grid = board.getGrid();
                    boolean[][] selected = new boolean[8][8];
                    gridView.setAdapter(new ImageAdapter(context, grid, selected));
                    gridView.invalidateViews();
                    results = BoardUtils.getSortedResults(grid);
                    results.sort((result1, result2) -> {
                        if(result1.getExtraTurn() != result2.getExtraTurn()){
                            if(result1.getExtraTurn()){
                                return -1;
                            }else{
                                return 1;
                            }
                        }
                        if (result1.totalMatched() != result2.totalMatched()) {
                            return result2.totalMatched() - result1.totalMatched();
                        }
                        return result1.getDisplayResults().get(0).orbType - result2.getDisplayResults().get(0).orbType;
                    });
                    resultsList.setLayoutManager(new LinearLayoutManager(context));
                    ResultsListAdapter resultsListAdapter = new ResultsListAdapter(context, results, board, gridView);
                    resultsList.setAdapter(resultsListAdapter);
                    spinner.setVisibility(View.INVISIBLE);
                });
            }catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
