package com.ceiliband.android1;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class TuneSet {
    private boolean wrap;
    private int wrapTo;
    private ArrayList<Tune> tunes;

    // wrapping and text height

    TuneSet(boolean wrap, int wrapTo) {
        this.wrap = wrap;
        this.wrapTo = wrapTo;
        tunes = new ArrayList<Tune>();
    }

    public void AddTune(String title, String fileName, String repeatNotes) {
        tunes.add(new Tune(title, fileName, repeatNotes));
    }

    public Tune getTune(int index) {
        return tunes.get(index);
    }

    public int getTuneCount() {
        return tunes.size();
    }

    public boolean getWrap() {
        return wrap;
    }

    public int getWrapTo() { return wrapTo; }

    public boolean CacheAllTunes() {
        boolean success = true;
        for (Tune tune : tunes)
            try {
                if (!tune.downloadBitmap()) success = false;
            } catch (IOException e) {
                success = false;
                e.printStackTrace();
            }
        return success;
    }
}
