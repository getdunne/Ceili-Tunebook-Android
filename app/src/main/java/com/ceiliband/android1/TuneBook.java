package com.ceiliband.android1;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.JsonReader;
import android.util.JsonToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class TuneBook {
    private String name;
    private String url;
    private Book book;
    private boolean bookReady;

    public boolean isBookReady() { return bookReady; }
    public Book getBook() { return book; }

    private String GetCachedJsonFileName() {
        return "book" + Uri.parse(url).getLastPathSegment() + ".json";
    }

    public TuneBook(String bookName, String bookUrl) {
        name = bookName;
        url = bookUrl;
        File cachedFile = new File(App.context.getFilesDir(), GetCachedJsonFileName());
        if (cachedFile.exists()) {
            // Read cached JSON file and rebuild book
            bookReady = unpackBookJson();
        }
        else {
            // Start with placeholder book
            book = new Book(name, new ArrayList<Book>());
            bookReady = false;
        }
    }

    public boolean downloadBookJSon() throws IOException {
        InputStream is = null;
        try {
            URL bookUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) bookUrl.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            is = conn.getInputStream();
            FileOutputStream os = null;
            try {
                os = App.context.openFileOutput(GetCachedJsonFileName(), Context.MODE_PRIVATE);
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

    public boolean unpackBookJson() {
        FileInputStream is = null;
        try {
            is = App.context.openFileInput(GetCachedJsonFileName());
            book = readJsonStream(is);
            is.close();
            bookReady = book != null;
            return bookReady;
        } catch (Exception e) {
            return false;
        }
    }

    private Book readJsonStream(InputStream stream) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(stream, "UTF-8"));
        Book book = null;
        try {
            book = readBookArray(reader);
        } finally {
            reader.close();
        }
        return book;
    }

    private Book readBookArray(JsonReader reader) throws IOException {
        String title = null;
        ArrayList<Book> chapters = null;
        TuneSet tunes = null;

        // a book is always an array
        reader.beginArray();
        // either [title, [chapters...]] or [title, wrap, [tune... ] or empty []
        if (reader.peek() == JsonToken.STRING) {
            title = reader.nextString();
            if (reader.peek() == JsonToken.BEGIN_ARRAY) {
                // list of chapters follows, and each chapter is a book
                chapters = new ArrayList<Book>();
                reader.beginArray();
                while (reader.hasNext())
                    chapters.add(readBookArray(reader));
                reader.endArray();
            }
            else {
                // wrap and list of tunes follows
                boolean wrap = reader.nextBoolean();
                tunes = new TuneSet(wrap);
                reader.beginArray();
                while (reader.hasNext()) {
                    reader.beginArray();
                    String url = reader.nextString();
                    String tuneTitle = reader.nextString();
                    String repeats = reader.nextString();
                    tunes.AddTune(tuneTitle, url, repeats);
                    reader.endArray();
                }
                reader.endArray();
            }
        }
        reader.endArray();
        if (title == null) return null;
        else if (tunes != null)
            return new Book(title, tunes);
        return new Book(title, chapters);
    }
}
