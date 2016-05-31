package com.ceiliband.android1;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Book_Activity extends AppCompatActivity {

    TextView titleText;
    ListView chapterList;
    Button backButton;
    protected Book book;
    ArrayList<String> titlesList;
    ArrayAdapter adapter;
    ArrayList<String> path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_activity);
        titleText = (TextView)findViewById(R.id.titleText);
        chapterList = (ListView)findViewById(R.id.chapterList);
        backButton = (Button)findViewById(R.id.backButton);
        book = App.book;
        path = new ArrayList<String>();
        path.add(book.title);
        SetTitleAndBackButton();
        titlesList = new ArrayList<String>();
        book.GetTitles(path, titlesList);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, titlesList);
        chapterList.setAdapter(adapter);

        chapterList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final String selectedTitle = (String)parent.getItemAtPosition(position);
                path.add(selectedTitle);
                TuneSet tunes = book.GetTuneSetAtPath(path);
                if (tunes == null) {
                    // more chapters: drill down
                    titleText.setText(selectedTitle);
                    book.GetTitles(path, titlesList);
                    adapter.notifyDataSetChanged();
                    SetTitleAndBackButton();
                }
                else {
                    // tune set: open it
                    path.remove(path.size()-1);
                    OpenTuneSet(tunes);
                }
            }

        });

        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (path.size() > 1) {
                    path.remove(path.size() - 1);
                    book.GetTitles(path, titlesList);
                    adapter.notifyDataSetChanged();
                    SetTitleAndBackButton();
                }
                else finish();
            }
        });
    }

    private void SetTitleAndBackButton() {
        int depth = path.size();
        if (depth == 1) {
            backButton.setText("(Back)");
            titleText.setText(path.get(0));
        }
        else {
            backButton.setText("Back to " + path.get(depth - 2));
            titleText.setText(path.get(depth - 1));
        }
    }

    private void OpenTuneSet(TuneSet tunes) {
        App.tuneSet = tunes;
        Intent intent = new Intent(this, TuneSet_Activity.class);
        startActivity(intent);
    }
}
