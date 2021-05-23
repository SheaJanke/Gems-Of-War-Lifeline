package com.cowbraingames.optimalmatcher_gemsofwar;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 1;
    private static final int USE_CAMERA = 2;
    private static final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 3;
    private GridView gridView;
    private ArrayList<Result> results;
    private int[][] grid;
    private RecyclerView resultsList;
    private ImageView testImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        gridView = (GridView) findViewById(R.id.board);
        resultsList = (RecyclerView) findViewById(R.id.results_list);
        testImg = findViewById(R.id.testImg);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        if(!hasCameraPermissions()){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, REQUEST_CAMERA);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(hasCameraPermissions()){
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(MainActivity.this);
                }
            }
        });
    }

    public boolean hasCameraPermissions(){
        return ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        switch (requestCode){
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                CropImage.ActivityResult cropResult = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri imageUri = cropResult.getUri();
                    try{
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                        Board board = new Board(getApplicationContext(), bitmap);
                        BoardDetection boardDetection = new BoardDetection(bitmap, testImg);
                        grid = board.getGrid();
                        boolean[][] selected = new boolean[8][8];
                        gridView.setAdapter(new ImageAdapter(this, grid, selected));
                        gridView.invalidateViews();
                        results = BoardUtils.getResults(grid);
                        System.out.println("Size: " + results.size());
                        Collections.sort(results, new Comparator<Result>() {
                            @Override
                            public int compare(Result result1, Result result2) {
                                if(result1.totalMatched() != result2.totalMatched()){
                                    return result2.totalMatched() - result1.totalMatched();
                                }
                                return result1.getDisplayResults().get(0).orbType - result2.getDisplayResults().get(0).orbType;
                            }
                        });

                        resultsList.setLayoutManager(new LinearLayoutManager(this));
                        ResultsListAdapter resultsListAdapter = new ResultsListAdapter(this, results, board, gridView);
                        resultsList.setAdapter(resultsListAdapter);

                        for(int i = 0; i < results.size(); i++){
                            Result r = results.get(i);
                            System.out.println("Result: " + r.r1 + r.c1 + r.r2 + r.c2 + " " + r.totalMatched() + r.getExtraTurn());
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = cropResult.getError();
                }
                break;
            default:
                break;
        }
    }

}
