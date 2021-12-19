package com.cowbraingames.optimalmatcher_gemsofwar.Permissions;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PermissionsManager {
    public static final int CAMERA = 1;
    public static final int WRITE_EXTERNAL_STORAGE = 2;
    public static final int ALL_PERMISSIONS = 3;

    private static final Map<Integer, String> permissionMapping = new HashMap<Integer, String>() {{
        put(CAMERA, Manifest.permission.CAMERA);
        put(WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }};

    public static void requestAllPermissions(Activity activity, Context context) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for(Integer permissionCode: permissionMapping.keySet()){
            try {
                String permission = getPermissionFromCode(permissionCode);
                if(!hasPermission(context, permission)){
                    permissionsToRequest.add(permission);
                }
            }catch (InvalidPermissionCodeException e){
                e.printStackTrace();
            }
        }
        if(!permissionsToRequest.isEmpty()){
            String[] requestArray = new String[permissionsToRequest.size()];
            requestArray = permissionsToRequest.toArray(requestArray);
            ActivityCompat.requestPermissions(activity, requestArray, ALL_PERMISSIONS);
        }
    }

    public static void requestPermissionIfNotGranted(Activity activity, Context context, int permissionCode) {
        try {
            String permission = getPermissionFromCode(permissionCode);
            if(!hasPermission(context, permission)){
                requestPermission(activity, permissionCode, permission);
            }
        } catch (InvalidPermissionCodeException e) {
            e.printStackTrace();
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

    public static boolean hasPermission(Context context, int permissionCode) {
        try {
            return hasPermission(context, getPermissionFromCode(permissionCode));
        } catch(InvalidPermissionCodeException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static class InvalidPermissionCodeException extends Exception {
        public InvalidPermissionCodeException(){
            super();
        }
    }
}


