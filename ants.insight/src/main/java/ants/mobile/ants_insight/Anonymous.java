package ants.mobile.ants_insight;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

import static ants.mobile.ants_insight.Constants.Constants.INDEX_FILE_PATH;

public class Anonymous {

    private static Anonymous ourInstance = null;

    private Anonymous() {
    }

    public static Anonymous getInstance() {
        if (ourInstance == null) {
            ourInstance = new Anonymous();
        }
        return (ourInstance);
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
                InsightSharedPref.savePreference(INDEX_FILE_PATH, indexFile.getPath());
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