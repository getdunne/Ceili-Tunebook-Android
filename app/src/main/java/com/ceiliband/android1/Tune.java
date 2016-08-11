package com.ceiliband.android1;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Tune {
    private String title;
    private String repeatNotes;
    private String url;
    private Bitmap bitmap;
    private float width, height;

    public String getTitle() {
        return title;
    }
    public String getRepeatNotes() {
        return repeatNotes;
    }
    public boolean isBitmapReady() { return bitmap != null; }
    public Bitmap getBitmap() {
        return bitmap;
    }
    public float getWidth() {
        return width;
    }
    public float getHeight() {
        return height;
    }
    public float getHeightWidthRatio () {
        return height / width;
    }
    public float getScaledHeight (float w) { return w * height / width; }

    public Tune(String title, String imageUrl, String repeatNotes) {
        this.title = title;
        this.url = imageUrl;
        this.repeatNotes = repeatNotes;
        bitmap = null;
        width = 100;
        height = 40;
    }

    private String GetCachedBitmapFileName() {
        return "image" + Uri.parse(url).getLastPathSegment();
    }

    public boolean downloadBitmap() throws IOException {
        File file = new File(App.context.getFilesDir(), GetCachedBitmapFileName());
        //if (file.exists()) return true;
        Log.i("netcrap", "Downloading " + GetCachedBitmapFileName());
        InputStream is = null;
        try {
            URL bookUrl = new URL(App.baseUrl + url);
            HttpURLConnection conn = (HttpURLConnection) bookUrl.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            is = conn.getInputStream();
            FileOutputStream os = null;
            try {
                os = App.context.openFileOutput(GetCachedBitmapFileName(), Context.MODE_PRIVATE);
                byte[] buffer = new byte[1024];
                int read;
                while ((read = is.read(buffer)) != -1) os.write(buffer, 0, read);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } finally {
                if (os != null) os.close();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (is != null) is.close();
        }
    }

    public boolean readCachedBitmap() {
        if (bitmap != null) return true;
        FileInputStream is = null;
        try {
            is = App.context.openFileInput(GetCachedBitmapFileName());
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            bitmap = BitmapFactory.decodeStream(is, null, options);
            is.close();
            width = bitmap.getWidth();
            height = bitmap.getHeight();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void releaseBitmap() {
        bitmap = null;
    }
}
