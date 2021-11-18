package com.cowbraingames.optimalmatcher_gemsofwar.Camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageView;
import com.cowbraingames.optimalmatcher_gemsofwar.Activities.MainActivity;
import com.cowbraingames.optimalmatcher_gemsofwar.Permissions.PermissionsManager;

import java.io.File;
import java.io.IOException;

public class CameraManager {

    private final Context context;
    private final Activity activity;
    private static final String FILE_NAME = "boardImg.png";
    private String imagePath;
    private Uri imgUri;

    public CameraManager(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        setupImageFile();
    }

    private void setupImageFile() {
        File storageDirectory = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            File imageFile = new File(storageDirectory, FILE_NAME);
            imageFile.createNewFile();
            imagePath = imageFile.getAbsolutePath();
            imgUri = FileProvider.getUriForFile(context, "com.cowbraingames.optimalmatcher_gemsofwar.fileprovider", imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleCameraButtonClicked() {
        if(PermissionsManager.hasPermission(context, PermissionsManager.CAMERA)){
            startCameraActivity();
        }else{
            PermissionsManager.requestPermissionIfNotGranted(activity, context, PermissionsManager.CAMERA);
        }
    }

    private void startCameraActivity() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        activity.startActivityForResult(intent, MainActivity.USE_CAMERA);
    }

    public void startCropActivity() {
        CropImage.activity(imgUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(activity);
    }


    public void saveImagePath(Bundle savedInstanceState) {
        savedInstanceState.putString("imagePath", imagePath);
    }

    public void restoreImagePath(Bundle savedInstanceState) {
        imagePath = savedInstanceState.getString("imagePath");
    }


    public Bitmap handleCropCameraResult(@Nullable Intent data) throws IOException {
        CropImage.ActivityResult result = CropImage.getActivityResult(data);
        assert result != null;
        Uri imageUri = result.getUriContent();
        return MediaStore.Images.Media.getBitmap(activity.getContentResolver(), imageUri);
    }

    public Bitmap handleAutoCropCameraResult() throws IOException {
        return getRotatedBoard();
    }


    private Bitmap getRotatedBoard() throws IOException {
        Bitmap boardBitmap = BitmapFactory.decodeFile(imagePath);
        android.media.ExifInterface ei = new android.media.ExifInterface(imagePath);
        int orientation = ei.getAttributeInt(android.media.ExifInterface.TAG_ORIENTATION,
                android.media.ExifInterface.ORIENTATION_UNDEFINED);
        Bitmap rotatedBitmap;
        switch(orientation) {

            case android.media.ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = rotateBitmap(boardBitmap, 90);
                break;

            case android.media.ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmap = rotateBitmap(boardBitmap, 180);
                break;

            case android.media.ExifInterface.ORIENTATION_ROTATE_270:
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
