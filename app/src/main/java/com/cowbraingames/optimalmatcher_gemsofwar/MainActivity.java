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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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
    private Context context;
    private Activity mainActivity;
    private ContentValues values;
    private Uri imageUri;
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
        setSupportActionBar(toolbar);


        values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Gems of War Board");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From 'Optimal Matcher - Gems of War'");
        imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        System.out.println("onCreate");

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
        fab.setOnClickListener(view -> {
            if(hasCameraPermissions()){
                /*
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(MainActivity.this);
                 */
                String fileName = "boardImg";
                File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                System.out.println("Here1");
                try {
                    File imageFile = File.createTempFile(fileName, ".png", storageDirectory);
                    imagePath = imageFile.getAbsolutePath();
                    System.out.println("Here2");
                    Uri imgUri = FileProvider.getUriForFile(MainActivity.this, "com.cowbraingames.optimalmatcher_gemsofwar.fileprovider", imageFile);
                    System.out.println("Uri: " + imgUri.getPath());
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
                    startActivityForResult(intent, USE_CAMERA);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
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
        System.out.println("Restoring: " + imagePath);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                new Thread(() -> {
                    CropImage.ActivityResult cropResult = CropImage.getActivityResult(data);
                    if (resultCode == RESULT_OK) {
                        Uri imageUri = cropResult.getUri();
                        try{
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
                            BoardDetection boardDetection = new BoardDetection(bitmap, testImg, mainActivity);
                            Board board = new Board(getApplicationContext(), boardDetection.getOrbs());
                            runOnUiThread(() -> {
                                grid = board.getGrid();
                                boolean[][] selected = new boolean[8][8];
                                gridView.setAdapter(new ImageAdapter(context, grid, selected));
                                gridView.invalidateViews();
                                results = BoardUtils.getResults(grid);
                                Collections.sort(results, (result1, result2) -> {
                                    if(result1.totalMatched() != result2.totalMatched()){
                                        return result2.totalMatched() - result1.totalMatched();
                                    }
                                    return result1.getDisplayResults().get(0).orbType - result2.getDisplayResults().get(0).orbType;
                                });
                                resultsList.setLayoutManager(new LinearLayoutManager(context));
                                ResultsListAdapter resultsListAdapter = new ResultsListAdapter(context, results, board, gridView);
                                resultsList.setAdapter(resultsListAdapter);
                            });


                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        Exception error = cropResult.getError();
                    }
                }).start();
                break;
            case USE_CAMERA:
                if(resultCode == Activity.RESULT_OK){
                    System.out.println(imagePath);
                    Bitmap boardBitmap = BitmapFactory.decodeFile(imagePath);
                    testImg.setImageBitmap(boardBitmap);
                }
                new Thread(() -> {
                    try{
                        /*
                        BoardDetection boardDetection = new BoardDetection(boardBitmap, testImg, mainActivity);
                        Board board = new Board(getApplicationContext(), boardDetection.getOrbs());
                        runOnUiThread(() -> {
                            grid = board.getGrid();
                            boolean[][] selected = new boolean[8][8];
                            gridView.setAdapter(new ImageAdapter(context, grid, selected));
                            gridView.invalidateViews();
                            results = BoardUtils.getResults(grid);
                            results.sort((result1, result2) -> {
                                if (result1.totalMatched() != result2.totalMatched()) {
                                    return result2.totalMatched() - result1.totalMatched();
                                }
                                return result1.getDisplayResults().get(0).orbType - result2.getDisplayResults().get(0).orbType;
                            });
                            resultsList.setLayoutManager(new LinearLayoutManager(context));
                            ResultsListAdapter resultsListAdapter = new ResultsListAdapter(context, results, board, gridView);
                            resultsList.setAdapter(resultsListAdapter);
                        });

                         */
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            default:
                break;
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

}
