package com.cowbraingames.optimalmatcher_gemsofwar.Camera;

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

import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;

import com.cowbraingames.optimalmatcher_gemsofwar.MainActivity;
import com.cowbraingames.optimalmatcher_gemsofwar.Permissions.PermissionsManager;

import java.io.File;
import java.io.IOException;

public class CameraManager {

    private final Context context;
    private final Activity activity;
    private static final String FILE_NAME = "boardImg.png";
    private String imagePath;

    public CameraManager(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public void handleCameraButtonClicked() {
        if(PermissionsManager.hasPermission(context, PermissionsManager.CAMERA)){
            startCameraActivity();
        }else{
            PermissionsManager.requestPermissionIfNotGranted(activity, context, PermissionsManager.CAMERA);
        }
    }

    private void startCameraActivity() {
        File storageDirectory = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            File imageFile = new File(storageDirectory, FILE_NAME);
            imageFile.createNewFile();
            imagePath = imageFile.getAbsolutePath();
            Uri imgUri = FileProvider.getUriForFile(context, "com.cowbraingames.optimalmatcher_gemsofwar.fileprovider", imageFile);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
            activity.startActivityForResult(intent, MainActivity.USE_CAMERA);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveImagePath(Bundle savedInstanceState) {
        savedInstanceState.putString("imagePath", imagePath);
    }

    public void restoreImagePath(Bundle savedInstanceState) {
        imagePath = savedInstanceState.getString("imagePath");
    }

    public Bitmap handleCameraResult(int requestCode) throws IOException {
        assert requestCode == MainActivity.USE_CAMERA;
        return getRotatedBoard();
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
