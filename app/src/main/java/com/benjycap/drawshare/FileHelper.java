package com.benjycap.drawshare;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Ben on 14/11/2014.
 */
public class FileHelper {

    // TODO file extension

    private static final String TAG = "FileHelper";

    public static void Save(Context context, PaintedPathList.SerializableInstance instance, String fileName) {
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream outputStream = new ObjectOutputStream(fos);
            outputStream.writeObject(instance);
            outputStream.close();
            fos.close();
        } catch (FileNotFoundException fnfe) {
            Log.e(TAG, "Error Saving File.", fnfe);
        } catch (IOException ioe) {
            Log.e(TAG, "Error Saving File.", ioe);
        }
    }

    public static PaintedPathList.SerializableInstance Load(Context context, String fileName) {
        try {
            FileInputStream fis = context.openFileInput(fileName);
            ObjectInputStream inputStream = new ObjectInputStream(fis);
            PaintedPathList.SerializableInstance loadedInstance = (PaintedPathList.SerializableInstance)inputStream.readObject();
            inputStream.close();
            fis.close();
            return loadedInstance;
        } catch (IOException ioe) {
            Log.e(TAG, "Error Loading File.", ioe);
        } catch (ClassNotFoundException cnfe) {
            Log.e(TAG, "Error Loading File.", cnfe);
        }

        Log.e(TAG, "General Error");
        return null;
    }

    public static boolean Delete(Context context, String fileName) {
        boolean deleteSuccessful = context.deleteFile(fileName);
        return deleteSuccessful;
    }

}
