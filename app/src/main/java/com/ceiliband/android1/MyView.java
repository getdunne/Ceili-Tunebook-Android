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

import java.util.Arrays;

public class MyView extends View {

    TuneSet tunes;
    int tuneSetIndex;
    private float textHeight;

    // scrolling
    public float yoffset;   // current scroll position
    private float[] ylist;  // list of positions
    private int ycount;     // count of valid values in ylist
    private int ycur;       // index of current scroll position in ylist
    private int speed;      // scroll speed, units per iteration

    public MyView(Context context) {
        super(context);
        init();
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        textHeight = 30;

        ylist = new float[50];
        ycount = 0;
        ycur = 0;
        yoffset = 0;
        speed = 10;

        tuneSetIndex = 0;
        SelectTuneSet(tuneSetIndex);
    }

    private void dumpScrollPosList() {
        Log.i("crap", "Scroll Points:");
        for (int i=0; i<ycount; i++) Log.i("crap", String.format("  %d", (int)ylist[i]));
    }

    private void InitScrollPoints(float canvasWidth, float canvasHeight) {
        ycount = 1;
        ylist[0] = 0;

        float y = 0;
        float hh, th;
        int i;
        Tune tune;

        for (i = 0; i < tunes.getTuneCount() - 1; i++) {
            // Transition: halfway to next tune
            tune = tunes.getTune(i);
            hh = 0.5f * tune.getScaledHeight(canvasWidth);
            y += textHeight + hh;
            tune = tunes.getTune(i+1);
            th = tune.getScaledHeight(canvasWidth);
            if (y + hh + 0.5f * th - ylist[ycount-1] > canvasHeight) ylist[ycount++] = y;
            else Log.i("crap", String.format("Skip halfway thru tune %d", i));
            // Top of next tune
            y += hh;
            if ((y + th + textHeight - ylist[ycount-1]) > canvasHeight) ylist[ycount++] = y;
            else Log.i("crap", String.format("Skip top of tune %d", i+1));
        }
        if (tunes.getWrap()) {
            // halfway round to 1st tune
            hh = 0.5f * tunes.getTune(tunes.getTuneCount() - 1).getScaledHeight(canvasWidth);
            y += textHeight + hh;
            th = tunes.getTune(0).getHeight();
            if (y + hh + 0.5f * th - ylist[ycount-1] > canvasHeight) ylist[ycount++] = y;
            else Log.i("crap", String.format("Skip halfway thru tune %d", tunes.getTuneCount()-1));
            // all the way round to 1st tune
            y += hh;
            if ((y + th + textHeight - ylist[ycount-1]) > canvasHeight) ylist[ycount++] = y;
            else Log.i("crap", String.format("Skip end of last tune"));
        }
        Log.i("crap", String.format("InitScrollPoints: canvas %dx%d", (int)canvasWidth, (int)canvasHeight));
        dumpScrollPosList();
    }

    public void ClearScrollPoints(int count) {
        ycount = count;
        ycur = count-1;
        yoffset = ylist[ycur];
        dumpScrollPosList();
    }

    public void ResetScrollPoint() {
        ylist[ycur] = yoffset;
        System.out.printf("Entry %d fixed to %f\n", ycur, yoffset);
        dumpScrollPosList();
    }

    public void AddScrollPoint() {
        if (ycount < 99) {
            ylist[ycount++] = yoffset;
            dumpScrollPosList();
            Arrays.sort(ylist, 0, ycount);
        }
        dumpScrollPosList();
    }

    public void DeleteScrollPoint() {
        if (ycount > 1 && ycur > 0) {
            ycount--;
            for (int i=ycur; i < ycount; i++) ylist[i] = ylist[i+1];
        }
        dumpScrollPosList();
    }

    public void ScrollToStart() {
        ycur = 0;
        yoffset = ylist[ycur];
        invalidate();
        Log.i("crap", String.format("ycur %d yoffset %f", ycur, yoffset));
    }

    public void ScrollForward() {
        if (++ycur >= ycount) ycur = 0;
        yoffset = ylist[ycur];
        invalidate();
        Log.i("crap", String.format("ycur %d yoffset %f", ycur, yoffset));
    }

    public void ScrollBackward() {
        if (--ycur < 0) ycur = ycount - 1;
        yoffset = ylist[ycur];
        invalidate();
        Log.i("crap", String.format("ycur %d yoffset %f", ycur, yoffset));
    }

    public float GetForwardScrollTargetY() {
        if (++ycur >= ycount) ycur = 0;
        Log.i("crap", String.format("ycur %d", ycur));
        return ylist[ycur];
    }

    public float GetBackwardScrollTargetY() {
        if (--ycur < 0) ycur = ycount - 1;
        Log.i("crap", String.format("ycur %d", ycur));
        return ylist[ycur];
    }

    private void SelectTuneSet(int tsi) {
        if (tsi == 0) {
            tunes = new TuneSet(4, true);
            tunes.SetTune(0, "/Removable/MicroSD/FlowersOfEdinburgh.JPG", "Flowers of Edinburgh", "(2A,2B)x2");
            tunes.SetTune(1, "/Removable/MicroSD/SwallowsTail.JPG", "Swallow's Tail", "(2A,2B)x2");
            tunes.SetTune(2, "/Removable/MicroSD/Teetotaler.JPG", "The Teetotaller", "(2A,2B)x2");
            tunes.SetTune(3, "/Removable/MicroSD/StarOfMunster.JPG", "The Star of Munster", "(2A,2B)x2");
        } else {
            tunes = new TuneSet(4, false);
            tunes.SetTune(0, "/Removable/MicroSD/DennisMurphy.JPG", "Dennis Murphy's", "(2A,2B)x2");
            tunes.SetTune(1, "/Removable/MicroSD/RattlinBog.JPG", "Rattlin Bog", "(2A,2B)x2");
            tunes.SetTune(2, "/Removable/MicroSD/JohnRyanPolka.JPG", "John Ryan's Polka", "(2A,2B)x2");
            tunes.SetTune(3, "/Removable/MicroSD/DennisMurphy.JPG", "Dennis Murphy's", "1A,1B");
        }
    }

    public void SetTuneSetIndex(int tsi) {
        tuneSetIndex = tsi;
        SelectTuneSet(tuneSetIndex);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        float canvasWidth = canvas.getWidth();
        float canvasHeight = canvas.getHeight();

        // If we haven't already done so, initialize scroll points list
        if (ycount == 0) InitScrollPoints(canvasWidth, canvasHeight);

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
                canvas.drawBitmap(tune.bitmap, src, dst, null);
                paint.setUnderlineText(ii == 0);
                paint.setTextAlign(Paint.Align.LEFT);
                canvas.drawText(tune.getTitle(), textIndent, textHeight + top, paint);
                paint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText(tune.getRepeatNotes(), canvasWidth - textIndent, textHeight + top, paint);
            }
            top += textHeight + scaledHeight;
            if (++ii > 3) {
                if (tunes.getWrap()) ii = 0;   // advance image index with wrap
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
