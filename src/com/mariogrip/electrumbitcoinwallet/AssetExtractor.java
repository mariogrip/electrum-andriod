package com.mariogrip.electrumbitcoinwallet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
 
import android.app.Activity;
import android.util.Log;
 
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
 
/**
 * Handles the extraction of assets from an APK file.
 *
 * When the run() method is called, it compares the APK timestamp
 * with the timestamp in the extracted assets. If the APK is newer
 * (or there are no extracted assets), it extracts them to the
 * application's data folder and resets the extracted assets' timestamp.
 *
 * The APK is newer whenever there is a new installation or an update.
 *
 */
public class AssetExtractor {
     
    private final static String LOGTAG = "AssetExtractor";
     
    private Activity mActivity;
    private AssetManager mAssetManager;
    private static String mAssetModifiedPath = "lastmodified.txt";
     
     
    public AssetExtractor(Activity activity) {
        mActivity = activity;
        mAssetManager = mActivity.getAssets();
    }
     
    /* Returns the path to the data files */
    public String getDataFilesPath() {
        return mActivity.getApplicationInfo().dataDir + "/files/";
    }
     
    /* Sets the asset modification time  */
    private void setAssetLastModified(long time) {
        String filename = this.getDataFilesPath() + mAssetModifiedPath;
        try {
            BufferedWriter bwriter = new BufferedWriter(new FileWriter(new File(filename)));
            bwriter.write(String.format("%d", time));
            bwriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
     
    /* Returns the asset modification time  */
    private long getAssetLastModified() {
        String filename = this.getDataFilesPath() + mAssetModifiedPath;
        try {
            BufferedReader breader = new BufferedReader(new FileReader(new File(filename)));
            String contents = breader.readLine();
            breader.close();
            return Long.valueOf(contents).longValue();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }
     
    /*
     * Returns the time of this app's last update
     * Considers new installs and updates
     */
    private long getAppLastUpdate() {
        PackageManager pm = mActivity.getPackageManager();
        try {
            PackageInfo pkgInfo = pm.getPackageInfo(mActivity.getPackageName(),
                    PackageManager.GET_PERMISSIONS);
            return pkgInfo.lastUpdateTime;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }
     
    /* Recursively deletes the contents of a folder*/
    private void recursiveDelete(File file) {
        if (file.isDirectory()) {
            for (File f : file.listFiles())
                recursiveDelete(f);
        }
        Log.i(LOGTAG, "Removing " + file.getAbsolutePath());
        file.delete();
    }
     
    /**
     * Copy the asset at the specified path to this app's data directory. If the
     * asset is a directory, its contents are also copied.
     *
     * @param path
     * Path to asset, relative to app's assets directory.
     */
    private void copyAssets(String path) {
        // Ignore the following asset folders
        if (path.equals("images")
                || path.equals("sounds")
                || path.equals("webkit")
                || path.equals("databases") // Motorola
                || path.equals("kioskmode")) // Samsung
                    return;
        try {
            String[] assetList = mAssetManager.list(path);
            if (assetList == null || assetList.length == 0)
                throw new IOException();
 
            // Make the directory.
            File dir = new File(this.getDataFilesPath(), path);
            dir.mkdirs();
 
            // Recurse on the contents.
            for (String entry : assetList) {
                if (path == "")
                    copyAssets(entry);
                else
                    copyAssets(path + "/" + entry);
            }
        } catch (IOException e) {
            copyFileAsset(path);
        }
    }
     
    /**
     * Copy the asset file specified by path to app's data directory. Assumes
     * parent directories have already been created.
     *
     * @param path
     * Path to asset, relative to app's assets directory.
     */
    private void copyFileAsset(String path) {
        File file = new File(this.getDataFilesPath(), path);
        Log.i(LOGTAG, String.format("Extracting %s to %s", path, file.getAbsolutePath()));
        try {
            InputStream in = mAssetManager.open(path);
            OutputStream out = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int read = in.read(buffer);
            while (read != -1) {
                out.write(buffer, 0, read);
                read = in.read(buffer);
            }
            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
     
     
    public void run() {
        // Get the times of last modifications
        long appLastUpdate = this.getAppLastUpdate();
        long assetLastModified = this.getAssetLastModified();
        if (appLastUpdate > assetLastModified) {
            // Clear previous assets
            Log.i(LOGTAG, "Removing private assets");
            File file = new File(this.getDataFilesPath());
            this.recursiveDelete(file);
            file.mkdir();
            // Extract new assets
            Log.i(LOGTAG, "Extracting assets");
            this.copyAssets("");
            // Update extract asset's timestamp
            this.setAssetLastModified(appLastUpdate);
            Log.i(LOGTAG, "Done!");
        } else {
            // Extracted assets are up to date
            Log.i(LOGTAG, "Assets are up to date");
        }
    }
}