package ants.mobile.ants_insight.Model;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

import adx.Utils;
import ants.mobile.ants_insight.InsightSharedPref;

public class Anonymous {

    private static final int REQUEST_WRITE_AND_READ_STORAGE = 1;

    private static Anonymous ourInstance = null;

    private Anonymous() {
    }

    public static Anonymous getInstance() {
        if (ourInstance == null) {
            ourInstance = new Anonymous();
        }
        return (ourInstance);
    }

    public void requestPermission(Context mContext) {
        boolean hasPermission = (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(Utils.getActivity(mContext),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_AND_READ_STORAGE);
        }
    }

    public File saveIndexToStorageLocal(Context mContext, String index) {
        File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/Android/data/ants.insight.sdk/");
        if (!directory.exists())
            directory.mkdir();

        File indexFile = new File(directory, "index.txt");
        if (!indexFile.exists()) {
            try {
                indexFile.createNewFile();
                InsightSharedPref.setIndexFilePath(mContext, indexFile.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream fOut = new FileOutputStream(indexFile);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fOut);
            outputWriter.write(index);
            outputWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return indexFile;
    }

    public boolean isFileExists() {
        File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/Android/data/ants.insight.sdk/");
        if (!directory.exists()) {
            directory.mkdir();
            return false;
        }
        File indexFile = new File(directory, "index.txt");
        return indexFile.exists();
    }

    public StringBuilder getIndexFromStorageLocal() {

        StringBuilder text = new StringBuilder();
        try {
            File indexFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/Android/data/ants.insight.sdk/index.txt");
            BufferedReader br = new BufferedReader(new FileReader(indexFile));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }

}