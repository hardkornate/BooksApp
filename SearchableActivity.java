package com.example.android.booksapp;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import static com.example.android.booksapp.MainActivity.BOOK_LOADER_ID;


public class SearchableActivity extends AppCompatActivity {

    public static final String BOOKSEARCH =
            "GET https://www.googleapis.com/books/v1/volumes?q=";
    private static String LOG_TAG = SearchableActivity.class.getSimpleName();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            setIntent(intent);
            handleIntent(intent);
        }
    }

    private void handleIntent(Intent intent) {
        if (intent.getExtras() != null){
            try{
                Bundle bundle = intent.getExtras();
                String query = (bundle.getString("QUERY", "new"));
                String mQuery = BOOKSEARCH + query;

                bundle.putString("QUERY", mQuery);
                super.getLoaderManager().restartLoader(BOOK_LOADER_ID, bundle, (android.app.LoaderManager.LoaderCallbacks<BookLoader>) this);
        } catch (NullPointerException e) {
                Log.e(LOG_TAG, "Null pointer from QUERY at handleIntent", e);
            }

            }


        }
    }
