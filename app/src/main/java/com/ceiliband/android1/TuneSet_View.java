package com.ceiliband.android1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class TuneSet_View extends View {

    TuneSet tunes;
    private float textHeight;

    // scrolling
    public float yoffset;   // current scroll position
    private ArrayList<Float> ylist;
    private int ycur;       // index of current scroll position in ylist
    private int ywrap;      // index of scroll position for start of wrap tune
    private int speed;      // scroll speed, units per iteration

    public TuneSet_View(Context context) {
        super(context);
        init();
    }

    public TuneSet_View(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TuneSet_View(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        tunes = App.tuneSet;
        textHeight = 30;

        ylist = new ArrayList<Float>();
        ycur = 0;
        yoffset = 0;
        speed = 10;
    }

    private void dumpScrollPosList() {
        //Log.i("crap", "Scroll Points:");
        //for (int i=0; i<ycount; i++) Log.i("crap", String.format("  %d", (int)ylist[i]));
    }

    private void InitScrollPoints(float canvasWidth, float canvasHeight) {
        ylist.add(0.0f);
        float y = 0;
        float hh, th;
        ywrap = 0;

        for (int i = 0; i < tunes.getTuneCount() - 1; i++) {
            // Transition: halfway to next tune
            Tune tune = tunes.getTune(i);
            hh = 0.5f * tune.getScaledHeight(canvasWidth);
            y += textHeight + hh;
            tune = tunes.getTune(i+1);
            th = tune.getScaledHeight(canvasWidth);
            if (y + hh + 0.5f * th - ylist.get(ylist.size()-1) > canvasHeight) ylist.add(y);
            else Log.i("crap", String.format("Skip halfway thru tune %d", i));
            // Top of next tune
            y += hh;
            if (y + th + textHeight - ylist.get(ylist.size()-1) > canvasHeight) ylist.add(y);
            else Log.i("crap", String.format("Skip top of tune %d", i+1));
            if (tunes.getWrap() && (i+1) == tunes.getWrapTo())
                ywrap = ylist.size() - 1;
        }
        if (tunes.getWrap()) {
            // halfway round to wrap tune
            hh = 0.5f * tunes.getTune(tunes.getTuneCount() - 1).getScaledHeight(canvasWidth);
            y += textHeight + hh;
            th = tunes.getTune(tunes.getWrapTo()).getHeight();
            if (y + hh + 0.5f * th - ylist.get(ylist.size()-1) > canvasHeight) ylist.add(y);
            else Log.i("crap", String.format("Skip halfway thru tune %d", tunes.getTuneCount()-1));
            // all the way round to 1st tune
            y += hh;
            if (y + th + textHeight - ylist.get(ylist.size()-1) > canvasHeight) ylist.add(y);
            else Log.i("crap", String.format("Skip end of last tune"));
        }
        Log.i("crap", String.format("InitScrollPoints: canvas %dx%d", (int)canvasWidth, (int)canvasHeight));
        dumpScrollPosList();
    }

    public void ClearScrollPoints() {
        ylist.clear();
        ycur = 0;
        yoffset = 0.0f;
        invalidate();
        dumpScrollPosList();
    }

    public void ResetScrollPoint() {
        ylist.set(ycur, yoffset);
        dumpScrollPosList();
    }

    public void AddScrollPoint() {
        ylist.add(yoffset);
        Collections.sort(ylist);
        dumpScrollPosList();
    }

    public void DeleteScrollPoint() {
        if (ylist.size() > 1 && ycur > 0) {
            ylist.remove(ycur);
        }
        dumpScrollPosList();
    }

    public void ScrollToStart() {
        ycur = 0;
        yoffset = ylist.get(ycur);
        invalidate();
        Log.i("crap", String.format("ycur %d yoffset %f", ycur, yoffset));
    }

    public void ScrollForward() {
        if (++ycur >= ylist.size()) ycur = ywrap;
        yoffset = ylist.get(ycur);
        invalidate();
        Log.i("crap", String.format("ycur %d yoffset %f", ycur, yoffset));
    }

    public void ScrollBackward() {
        if (--ycur < 0) ycur = ylist.size() - 1;
        yoffset = ylist.get(ycur);
        invalidate();
        Log.i("crap", String.format("ycur %d yoffset %f", ycur, yoffset));
    }

    public float GetForwardScrollTargetY() {
        if (++ycur >= ylist.size()) ycur = ywrap;
        Log.i("crap", String.format("ycur %d", ycur));
        return ylist.get(ycur);
    }

    public float GetBackwardScrollTargetY() {
        if (--ycur < 0) ycur = ylist.size() - 1;
        Log.i("crap", String.format("ycur %d", ycur));
        return ylist.get(ycur);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        float canvasWidth = canvas.getWidth();
        float canvasHeight = canvas.getHeight();

        // If we haven't already done so, initialize scroll points list
        if (ylist.size() == 0) InitScrollPoints(canvasWidth, canvasHeight);

        // Clear canvas before drawing anything
        canvas.drawColor(Color.WHITE);

        // Prepare to draw text
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLUE);
        paint.setTextSize(textHeight);
        float textIndent = 20;

        // Draw loop
        float top = -yoffset;
        int ii = 0;
        while (top < canvasHeight) {
            Tune tune = tunes.getTune(ii);
            float scaledHeight = tune.getScaledHeight(canvasWidth);
            float bot = top + textHeight + scaledHeight;
            if (bot >= 0 && top <= canvasHeight) {
                // draw only images which won't be fully clipped anyway
                Rect src = new Rect(0, 0, (int)tune.getWidth(), (int)tune.getHeight());
                Rect dst = new Rect(0, (int)(top + textHeight), (int)canvasWidth, (int)(bot));
                if (tune.isBitmapReady())
                    canvas.drawBitmap(tune.getBitmap(), src, dst, null);
                paint.setUnderlineText(ii == tunes.getWrapTo());
                paint.setTextAlign(Paint.Align.LEFT);
                canvas.drawText(tune.getTitle(), textIndent, textHeight + top, paint);
                paint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText(tune.getRepeatNotes(), canvasWidth - textIndent, textHeight + top, paint);
            }
            top += textHeight + scaledHeight;
            if (++ii > tunes.getTuneCount()-1) {
                if (tunes.getWrap()) ii -= (tunes.getTuneCount() - tunes.getWrapTo());   // advance image index with wrap
                else break;
            }
        }
    }

    private float tx, ty, dx, dy, yoff;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();

        switch(action) {
            case MotionEvent.ACTION_DOWN:
                tx = event.getX();
                ty = event.getY();
                yoff = yoffset;
                return true;
            case MotionEvent.ACTION_MOVE:
                dx = event.getX() - tx;
                dy = event.getY() - ty;
                yoffset = yoff - dy;
                invalidate();
                return true;
        }
        return false;
    }
}
