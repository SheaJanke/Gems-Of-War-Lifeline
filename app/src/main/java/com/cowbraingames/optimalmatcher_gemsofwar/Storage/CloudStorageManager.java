package com.cowbraingames.optimalmatcher_gemsofwar.Storage;

import android.graphics.Bitmap;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Random;

public class CloudStorageManager {
    private static final FirebaseStorage storage  = FirebaseStorage.getInstance();
    private static final String PATH_TO_REPORTED = "reported_images";
    private static final Random random = new Random();

    public static void uploadReportedImage(Bitmap reportedImage) {
        StorageReference storageRef = storage.getReference();
        String fileName = getRandomFileName(".png");
        StorageReference fileRef = storageRef.child(PATH_TO_REPORTED).child(fileName);
        uploadImage(fileRef, reportedImage);
    }

    private static void uploadImage(StorageReference fileRef, Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = fileRef.putBytes(data);
        uploadTask.addOnFailureListener(exception -> {
            System.out.println("Upload Failure");
        }).addOnSuccessListener(taskSnapshot -> {
            System.out.println("Upload Success");
        });
    }

    private static String getRandomFileName(String extension) {
        int randomInt = random.nextInt();
        return randomInt + extension;
    }

}
