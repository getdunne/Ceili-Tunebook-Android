package com.ceiliband.android1;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.IOException;

public class Main_Activity extends AppCompatActivity {

    private Button getBookJsonButton;
    private Button browseBookButton;
    private Button cacheBookButton;
    private Button clearCacheButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        getBookJsonButton = (Button)findViewById(R.id.getBookButton);
        browseBookButton = (Button)findViewById(R.id.browseBookButton);
        cacheBookButton = (Button)findViewById(R.id.cacheBookButton);
        clearCacheButton = (Button)findViewById(R.id.clearCacheButton);

        App.tuneBook = new TuneBook("KCB Big Book", App.baseUrl + "/book_json/1");
        App.book = App.tuneBook.getBook();

        if (App.tuneBook.isBookReady()) getBookJsonButton.setText("Book is ready (Tap to update)");
        else if (App.InternetReady()) getBookJsonButton.setText("Download Book table of contents");
        else getBookJsonButton.setText("Book not ready, no internet connection");

        if (!App.InternetReady()) {
            browseBookButton.setText("Browse book (some tunes not available)");
            cacheBookButton.setText("No internet: can't download tunes");
        }

        getBookJsonButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                App.tuneBook = new TuneBook("KCB Big Book", App.baseUrl + "/book_json/1");
                if (App.InternetReady())
                    new DownloadJsonTask().execute();
            }
        });

        browseBookButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                OpenBook();
            }
        });

        cacheBookButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (App.tuneBook.isBookReady() && App.InternetReady()) {
                    cacheBookButton.setText("Downloading tunes...");
                    new DownloadAllTunes().execute();
                }
            }
        });

        clearCacheButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clearCacheButton.setText("Clearing Cache...");
                File dir = App.context.getFilesDir();
                File[] files = dir.listFiles();
                if (files != null) {
                    int i;
                    for (i = 0; i < files.length; i++) {
                        Log.i("netcrap", "Deleting " + files[i].getAbsolutePath());
                        files[i].delete();
                    }
                }
                getBookJsonButton.setText("Download Book table of contents");
                clearCacheButton.setText("Clear Cache");
            }
        });
    }

    private class DownloadJsonTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... param) {
            try {
                return App.tuneBook.downloadBookJSon();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result && App.tuneBook.unpackBookJson()) {
                App.book = App.tuneBook.getBook();
                getBookJsonButton.setText("Book open (downloaded)");
            }
        }
    }

    private class DownloadAllTunes extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... param) {
            return App.tuneBook.getBook().CacheAllTunes();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) cacheBookButton.setText("All tunes downloaded");
            else cacheBookButton.setText("Download failed");
        }
    }

    private void OpenBook() {
        Intent intent = new Intent(this, Book_Activity.class);
        startActivity(intent);
    }
}
