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
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.canhub.cropper.CropImage;
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


public class MainActivity extends AppCompatActivity {

    public static final int USE_CAMERA = 1;
    private GridView gridView;
    private BoardGrid boardGrid;
    private ResultsList resultsList;
    private CameraManager cameraManager;
    private Context context;
    private ProgressBar spinner;
    private TextView description;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        Activity mainActivity = this;
        setContentView(R.layout.activity_main);
        cameraManager = new CameraManager(context, mainActivity);
        gridView = findViewById(R.id.board);
        boardGrid = new BoardGrid(context, gridView);
        resultsList = new ResultsList(context, findViewById(R.id.results_list), boardGrid);
        spinner = findViewById(R.id.spinner);
        description = findViewById(R.id.layout_description);
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
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState){
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
        if(resultCode != Activity.RESULT_OK){
            return;
        }
        switch (requestCode){
            case USE_CAMERA:
                handleCameraResult();
                break;
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                handleCropResult(data);
                break;
        }
    }

    private void handleCameraResult() {
        if(LocalStorageManager.getAutoCropPreference(context)){
            try {
                Bitmap boardBitmap = cameraManager.handleAutoCropCameraResult();
                updateBoardGrid(boardBitmap);
            } catch (Exception e) {
                e.printStackTrace();
                ErrorDialogManager.showErrorDialog(context, R.string.error_dialog_title_camera, R.string.error_dialog_description_camera);
            }
        }else{
            cameraManager.startCropActivity();
        }
    }

    private void handleCropResult(@Nullable Intent data) {
        try {
            Bitmap boardBitmap = cameraManager.handleCropCameraResult(data);
            updateBoardGrid(boardBitmap);
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDialogManager.showErrorDialog(context, R.string.error_dialog_title_crop, R.string.error_dialog_description_crop);
        }
    }

    private void updateBoardGrid(Bitmap boardBitmap) {
        description.setVisibility(View.INVISIBLE);
        gridView.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.VISIBLE);
        boardGrid.setBoard(null);
        new Thread(() -> {
            try{
                BoardDetection boardDetection = new BoardDetection(boardBitmap);
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
