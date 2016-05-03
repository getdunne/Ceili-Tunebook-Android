package com.ceiliband.android1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class Tune {
    String title;
    String repeatNotes;
    Bitmap bitmap;
    float width, height;

    public Tune(String path, String title, String repeatNotes) {
        this.title = title;
        this.repeatNotes = repeatNotes;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        options.inScaled = true;
        bitmap = BitmapFactory.decodeFile(path, options);
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        Log.i("crap", String.format("%s: %dx%d", path, (int)width, (int)height));
    }

    public String getTitle() {
        return title;
    }

    public String getRepeatNotes() {
        return repeatNotes;
    }

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
}
