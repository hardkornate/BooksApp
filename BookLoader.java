package com.example.android.booksapp;

/**
 * Created by Owner on 7/9/17.
 */

import android.content.Context;
import android.content.AsyncTaskLoader;
import java.util.List;


/**
 * Loads a list of Books by using an AsyncTask to perform the
 * network request to the Google Books API URL.
 */

public class BookLoader extends AsyncTaskLoader<List<Book>> {

    private static final String LOG_TAG = BookLoader.class.getName();

    private String mUrl;

    /**
     * Constructs a new {@link BookLoader} object
     * @param context
     * @param url
     */
    public BookLoader (Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<Book> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform network request, parse the response, and extract a list of books
        // matching search criteria
        List<Book> books = QueryUtils.fetchBookData(mUrl);
        return books;
    }
}