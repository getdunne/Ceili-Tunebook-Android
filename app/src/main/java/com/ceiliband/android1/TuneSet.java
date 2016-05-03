package com.ceiliband.android1;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.util.Arrays;

public class TuneSet {
    // tunes list and count
    private Tune[] tunes;
    private int tuneCount;

    // wrapping and text height
    private boolean wrap;

    TuneSet(int tuneCount, boolean wrap) {
        this.tuneCount = tuneCount;
        this.wrap = wrap;

        tunes = new Tune[tuneCount];
    }

    public void SetTune(int index, String path, String title, String repeatNotes) {
        tunes[index] = new Tune(path, title, repeatNotes);
    }

    public Tune getTune(int index) {
        return tunes[index];
    }

    public int getTuneCount() {
        return tuneCount;
    }

    public boolean getWrap() {
        return wrap;
    }
}
