package com.ceiliband.android1;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    private MyView myView;

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

        setContentView(R.layout.activity_main);
        myView = (MyView)findViewById(R.id.myView);

        GoFullScreen();
    }

    private Handler myHandler = new Handler();
    private float myTargetY;

    private Runnable myForwardRunnable = new Runnable() {

        @Override
        public void run() {
            if (myView.yoffset < myTargetY)
            {
                myView.yoffset += 10;
                if (myView.yoffset > myTargetY) myView.yoffset = myTargetY;
                myView.invalidate();
                myHandler.postDelayed(this, 5);
            }
        }
    };

    private Runnable myBackwardRunnable = new Runnable() {

        @Override
        public void run() {
            if (myView.yoffset > myTargetY)
            {
                myView.yoffset -= 10;
                if (myView.yoffset < myTargetY) myView.yoffset = myTargetY;
                myView.invalidate();
                myHandler.postDelayed(this, 5);
            }
        }
    };

    public void ScrollForward() {
        myTargetY = myView.GetForwardScrollTargetY();
        if (myView.yoffset > myTargetY) {
            myView.GetBackwardScrollTargetY();
            myView.ScrollForward();
        }
        else {
            myHandler.post(myForwardRunnable);
        }
    }

    public void ScrollBackward() {
        myTargetY = myView.GetBackwardScrollTargetY();
        if (myView.yoffset < myTargetY) {
            myView.GetForwardScrollTargetY();
            myView.ScrollBackward();
        }
        else {
            myHandler.post(myBackwardRunnable);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:
                myView.ScrollToStart();
                return true;
            case KeyEvent.KEYCODE_DPAD_UP:
                ScrollBackward();
                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                ScrollForward();
                return true;
            case KeyEvent.KEYCODE_0:
            case KeyEvent.KEYCODE_1:
            case KeyEvent.KEYCODE_2:
            case KeyEvent.KEYCODE_3:
            case KeyEvent.KEYCODE_4:
            case KeyEvent.KEYCODE_5:
            case KeyEvent.KEYCODE_6:
            case KeyEvent.KEYCODE_7:
            case KeyEvent.KEYCODE_8:
            case KeyEvent.KEYCODE_9:
                myView.SetTuneSetIndex(keyCode - KeyEvent.KEYCODE_0);
                return true;
            case KeyEvent.KEYCODE_C:
                // Clear scroll points, i.e. leave only 1
                myView.ClearScrollPoints(1);
                return true;
            case KeyEvent.KEYCODE_R:
                // Rebuild default scroll points
                myView.ClearScrollPoints(0);
                return true;
            case KeyEvent.KEYCODE_M:
                // Modify current scroll point
                myView.ResetScrollPoint();
                return true;
            case KeyEvent.KEYCODE_A:
                // Add new scroll point (insert after current)
                myView.AddScrollPoint();
                return true;
            case KeyEvent.KEYCODE_D:
                myView.DeleteScrollPoint();
                return true;
        }
        return false;
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
