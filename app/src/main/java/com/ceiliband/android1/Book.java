package com.ceiliband.android1;

import java.util.ArrayList;

public class Book {
    // A Book is either a title and a list of chapters (themselves Book objects, list may be empty),
    // or a title and a TuneSet
    public String title;
    public ArrayList<Book> chapters;
    public TuneSet tuneSet;

    @Override
    public String toString() {
        return title;
    }

    public Book(String titleString, ArrayList<Book> chapterList) {
        title = titleString;
        chapters = chapterList;
        tuneSet = null;
    }

    public Book(String titleString, TuneSet tunes) {
        title = titleString;
        chapters = null;
        tuneSet = tunes;
    }

    public boolean isTuneSet() {
        return tuneSet != null;
    }

    public TuneSet GetTuneSetAtPath (ArrayList<String> path) {
        Book currentBook = this;
        if (path.size() > 1) for (int i=1; i<path.size(); i++) {
            String desiredTitle = path.get(i);
            if (currentBook.title == desiredTitle) break;
            for (Book book : currentBook.chapters) {
                if (book.title.equals(desiredTitle)) {
                    if (book.isTuneSet()) return book.tuneSet;
                    currentBook = book;
                    break;
                }
            }
        }
        return null;
    }

    public void GetTitles (ArrayList<String> path, ArrayList<String> listToPopulate) {
        Book currentBook = this;
        if (path.size() > 1) for (int i=1; i<path.size(); i++) {
            String desiredTitle = path.get(i);
            if (currentBook.title == desiredTitle) break;
            for (Book book : currentBook.chapters) {
                if (book.title.equals(desiredTitle)) {
                    if (book.isTuneSet()) return;
                    currentBook = book;
                    break;
                }
            }
        }
        listToPopulate.clear();
        for (int i = 0; i < currentBook.chapters.size(); i++)
            listToPopulate.add(currentBook.chapters.get(i).title);
    }

    public boolean CacheAllTunes() {
        if (isTuneSet()) return tuneSet.CacheAllTunes();
        else {
            boolean success = true;
            for (Book book : chapters) {
                if (!book.CacheAllTunes()) success = false;
            }
            return success;
        }
    }

}
