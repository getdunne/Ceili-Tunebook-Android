package com.ceiliband.android1;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import java.io.IOException;

public class TuneSet_Activity extends AppCompatActivity {

    private TuneSet_View tuneSetView;

    private void GoFullScreen() {

        int mUIFlag = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

        getWindow().getDecorView().setSystemUiVisibility(mUIFlag);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove title
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.tuneset_view);
        tuneSetView = (TuneSet_View)findViewById(R.id.myView);
        new DownloadBitmapsTask().execute();
        GoFullScreen();
    }

    private class DownloadBitmapsTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... param) {
            int count = App.tuneSet.getTuneCount();
            try {
                boolean allTunesDownloadedOK = true;
                for (int i=0; i<count; i++) {
                    Tune tune = App.tuneSet.getTune(i);
                    if (!tune.downloadBitmap() || !tune.readCachedBitmap()) allTunesDownloadedOK = false;
                }
                return allTunesDownloadedOK;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                tuneSetView.ClearScrollPoints();
            }
        }
    }

    private Handler myHandler = new Handler();
    private float myTargetY;

    private Runnable myForwardRunnable = new Runnable() {

        @Override
        public void run() {
            if (tuneSetView.yoffset < myTargetY)
            {
                tuneSetView.yoffset += 10;
                if (tuneSetView.yoffset > myTargetY) tuneSetView.yoffset = myTargetY;
                tuneSetView.invalidate();
                myHandler.postDelayed(this, 5);
            }
        }
    };

    private Runnable myBackwardRunnable = new Runnable() {

        @Override
        public void run() {
            if (tuneSetView.yoffset > myTargetY)
            {
                tuneSetView.yoffset -= 10;
                if (tuneSetView.yoffset < myTargetY) tuneSetView.yoffset = myTargetY;
                tuneSetView.invalidate();
                myHandler.postDelayed(this, 5);
            }
        }
    };

    public void ScrollForward() {
        myTargetY = tuneSetView.GetForwardScrollTargetY();
        if (tuneSetView.yoffset > myTargetY) {
            tuneSetView.GetBackwardScrollTargetY();
            tuneSetView.ScrollForward();
        }
        else {
            myHandler.post(myForwardRunnable);
        }
    }

    public void ScrollBackward() {
        myTargetY = tuneSetView.GetBackwardScrollTargetY();
        if (tuneSetView.yoffset < myTargetY) {
            tuneSetView.GetForwardScrollTargetY();
            tuneSetView.ScrollBackward();
        }
        else {
            myHandler.post(myBackwardRunnable);
        }
    }

    @Override
    public void onBackPressed() {
        int count = App.tuneSet.getTuneCount();
        for (int i=0; i<count; i++) {
            Tune tune = App.tuneSet.getTune(i);
            tune.releaseBitmap();
        }
        finish();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                onBackPressed();
                return true;
            case KeyEvent.KEYCODE_ENTER:
                tuneSetView.ScrollToStart();
                return true;
            case KeyEvent.KEYCODE_DPAD_UP:
                ScrollBackward();
                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                ScrollForward();
                return true;
            case KeyEvent.KEYCODE_R:
                // Rebuild default scroll points
                tuneSetView.ClearScrollPoints();
                return true;
            case KeyEvent.KEYCODE_M:
                // Modify current scroll point
                tuneSetView.ResetScrollPoint();
                return true;
            case KeyEvent.KEYCODE_A:
                // Add new scroll point (insert after current)
                tuneSetView.AddScrollPoint();
                return true;
            case KeyEvent.KEYCODE_D:
                tuneSetView.DeleteScrollPoint();
                return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int index = event.getActionIndex();
        int action = event.getActionMasked();

        if (action == MotionEvent.ACTION_UP) {
            GoFullScreen();
            return true;
        }
        return false;
    }

}
