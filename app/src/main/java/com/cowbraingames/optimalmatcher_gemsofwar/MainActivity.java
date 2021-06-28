package com.cowbraingames.optimalmatcher_gemsofwar;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


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

    private void handleCameraResult() {
        spinner.setVisibility(View.VISIBLE);
        new Thread(() -> {
            try{
                Bitmap boardBitmap = getRotatedBoard();
                BoardDetection boardDetection = new BoardDetection(boardBitmap, testImg, mainActivity);
                Board board = new Board(getApplicationContext(), boardDetection.getOrbs());
                runOnUiThread(() -> {
                    grid = board.getGrid();
                    boolean[][] selected = new boolean[8][8];
                    gridView.setAdapter(new ImageAdapter(context, grid, selected, gridView.getColumnWidth()));
                    gridView.invalidateViews();
                    results = BoardUtils.getSortedResults(grid);
                    resultsList.setLayoutManager(new LinearLayoutManager(context));
                    ResultsListAdapter resultsListAdapter = new ResultsListAdapter(context, results, grid, gridView);
                    resultsList.setAdapter(resultsListAdapter);
                    spinner.setVisibility(View.INVISIBLE);
                });
            }catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private Bitmap getRotatedBoard() throws IOException {
        Bitmap boardBitmap = BitmapFactory.decodeFile(imagePath);
        ExifInterface ei = new ExifInterface(imagePath);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);
        Bitmap rotatedBitmap;
        switch(orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = rotateBitmap(boardBitmap, 90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmap = rotateBitmap(boardBitmap, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmap = rotateBitmap(boardBitmap, 270);
                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                rotatedBitmap = boardBitmap;
        }
        return rotatedBitmap;
    }

    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }
}
