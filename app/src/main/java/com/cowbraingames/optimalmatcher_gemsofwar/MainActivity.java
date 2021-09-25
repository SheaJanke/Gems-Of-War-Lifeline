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

import com.cowbraingames.optimalmatcher_gemsofwar.Permissions.PermissionsManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
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
import android.util.Pair;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private static final int USE_CAMERA = 2;
    private GridView gridView;
    private ArrayList<Result> results;
    private int[][] grid;
    private RecyclerView resultsList;
    private ResultsListAdapter resultsListAdapter;
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

        PermissionsManager.requestAllPermissions(MainActivity.this, this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            try {
                if(PermissionsManager.hasPermission(this, PermissionsManager.CAMERA)){
                    startCameraActivity();
                }else{
                    PermissionsManager.requestPermissionIfNotGranted(MainActivity.this, this, PermissionsManager.CAMERA);
                }
            } catch(PermissionsManager.InvalidPermissionCodeException e){
                System.out.println("Invalid permission code");
            }
        });

        resultsList.addOnItemTouchListener(new RecyclerTouchListener(mainActivity, resultsList, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                ArrayList<Pair<RecyclerView, Integer>> rows = resultsListAdapter.getRows();
                updateHighlighted(rows, position);
                System.out.println("onClick: " + position);
            }

            @Override
            public void onLongClick(View view, int position) {
                ArrayList<Pair<RecyclerView, Integer>> rows = resultsListAdapter.getRows();
                updateHighlighted(rows, position);
                Intent intent = new Intent(context, FinalBoardActivity.class);
                String flatFinalBoard = "";
                int[][] finalBoard = results.get(position).getFinalBoard();
                System.out.println("Position pressed: " + position);
                //System.out.println("Final board:");
                for(int i = 0; i < 8; i++){
                    for(int j = 0; j < 8; j++){
                        //System.out.print(finalBoard[i][j] == -1 ? 1 : 0);
                        flatFinalBoard += finalBoard[i][j] + " ";
                    }
                    //System.out.println();
                }
                //System.out.println(flatFinalBoard);
                intent.putExtra("FLAT_FINAL_BOARD", flatFinalBoard);
                context.startActivity(intent);
            }
        }));
    }

    private void updateHighlighted(ArrayList<Pair<RecyclerView, Integer>> rows, int position){
        boolean[][] selected = new boolean[8][8];
        Result result = results.get(position);
        selected[result.r1][result.c1] = true;
        selected[result.r2][result.c2] = true;
        gridView.setAdapter(new ImageAdapter(context, grid, selected, gridView.getColumnWidth()));
        for(int i = 0; i < rows.size(); i++){
            if(rows.get(i).second%2 == 0){
                rows.get(i).first.setBackgroundResource(R.color.boardDark);
            }else{
                rows.get(i).first.setBackgroundResource(R.color.boardLight);
            }
        }
        RecyclerView resultRow = rows.get(position).first;
        if(position%2 == 0) {
            resultRow.setBackgroundResource(R.drawable.dark_border);
        }else{
            resultRow.setBackgroundResource(R.drawable.light_border);
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
                    resultsListAdapter = new ResultsListAdapter(context, results);
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
