package com.cowbraingames.optimalmatcher_gemsofwar.Permissions;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.HashMap;
import java.util.Map;

public class PermissionsManager {
    public static final int CAMERA = 1;
    public static final int WRITE_EXTERNAL_STORAGE = 2;

    private static final Map<Integer, String> permissionMapping = new HashMap<Integer, String>() {{
        put(CAMERA, Manifest.permission.CAMERA);
        put(WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }};

    public static void requestAllPermissions(Activity activity, Context context) {
        permissionMapping.keySet().forEach(permissionCode -> {
            try {
                requestPermissionIfNotGranted(activity, context, permissionCode);
            } catch (InvalidPermissionCodeException e) {
                System.out.println("Invalid permission code: " + permissionCode);
            }
        });
    }

    public static void requestPermissionIfNotGranted(Activity activity, Context context, int permissionCode) throws InvalidPermissionCodeException {
        String permission = getPermissionFromCode(permissionCode);
        if(!hasPermission(context, permission)){
            requestPermission(activity, permissionCode, permission);
        }
    }

    private static String getPermissionFromCode(int permissionCode) throws InvalidPermissionCodeException {
        if(!permissionMapping.containsKey(permissionCode)){
            throw new InvalidPermissionCodeException();
        }
        return permissionMapping.get(permissionCode);
    }

    private static boolean hasPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private static void requestPermission(Activity activity, int permissionCode, String permission) {
        ActivityCompat.requestPermissions(activity, new String[]{ permission }, permissionCode);
    }

    public static boolean hasPermission(Context context, int permissionCode) throws InvalidPermissionCodeException {
        return hasPermission(context, getPermissionFromCode(permissionCode));
    }

    public static class InvalidPermissionCodeException extends Exception {
        public InvalidPermissionCodeException(){
            super();
        }
    }
}


