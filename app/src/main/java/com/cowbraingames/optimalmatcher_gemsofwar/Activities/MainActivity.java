package com.cowbraingames.optimalmatcher_gemsofwar.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.cowbraingames.optimalmatcher_gemsofwar.BoardDetection.BoardDetection;
import com.cowbraingames.optimalmatcher_gemsofwar.BoardDetection.BoardUtils;
import com.cowbraingames.optimalmatcher_gemsofwar.BoardDisplay.Board.MainActivityBoard;
import com.cowbraingames.optimalmatcher_gemsofwar.BoardDisplay.BoardGrid;
import com.cowbraingames.optimalmatcher_gemsofwar.Camera.CameraManager;
import com.cowbraingames.optimalmatcher_gemsofwar.Exceptions.BoardNotFoundException;
import com.cowbraingames.optimalmatcher_gemsofwar.Permissions.PermissionsManager;
import com.cowbraingames.optimalmatcher_gemsofwar.R;
import com.cowbraingames.optimalmatcher_gemsofwar.ResultsList.ResultsList;
import com.cowbraingames.optimalmatcher_gemsofwar.Storage.LocalStorageManager;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.ErrorDialogManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.canhub.cropper.CropImage;

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
        startTutorialIfRequired();
    }

    private void startTutorialIfRequired() {
        boolean tutorialCompleted = LocalStorageManager.getTutorialCompleted(context);
        if(!tutorialCompleted) {
            startTutorial();
        }
    }

    private void startTutorial() {
        LocalStorageManager.setTutorialCompleted(context, true);
        Intent intent = new Intent(context, TutorialActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        final int itemId = item.getItemId();
        if(itemId == R.id.settings) {
            Intent intent = new Intent(context, SettingsActivity.class);
            startActivity(intent);
            return true;
        }else if(itemId == R.id.tutorial) {
            startTutorial();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        System.out.println("Activity Result: " + requestCode + " " + resultCode + " " + data);
        if(resultCode != Activity.RESULT_OK){
            return;
        }
        switch (requestCode){
            case USE_CAMERA:
                if(LocalStorageManager.getAutoCropPreference(context)){
                    try {
                        Bitmap boardBitmap = cameraManager.handleAutoCropCameraResult();
                        updateBoardGrid(boardBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    cameraManager.startCropCameraActivity();
                }
                break;
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                try {
                    Bitmap boardBitmap = cameraManager.handleCropCameraResult(data);
                    updateBoardGrid(boardBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    private void updateBoardGrid(Bitmap boardBitmap) {
        spinner.setVisibility(View.VISIBLE);
        new Thread(() -> {
            try{
                BoardDetection boardDetection = new BoardDetection(boardBitmap, testImg, mainActivity);
                MainActivityBoard board = new MainActivityBoard(context, boardDetection.getOrbs());
                runOnUiThread(() -> {
                    boardGrid.setBoard(board);
                    resultsList.setResults(BoardUtils.getSortedResults(board.getGemTypes()));
                    spinner.setVisibility(View.INVISIBLE);
                });
            }catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    spinner.setVisibility(View.INVISIBLE);
                    if(e instanceof BoardNotFoundException){
                        ErrorDialogManager.showErrorDialog(context, R.string.error_dialog_title_board_not_found, R.string.error_dialog_description_board_not_found);
                    }
                });
            }
        }).start();
    }
}
