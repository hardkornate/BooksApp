package com.example.android.booksapp;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import static android.app.SearchManager.USER_QUERY;

public class SearchableActivity extends AppCompatActivity {

    private static final String BOOKSEARCH =
            "GET https://www.googleapis.com/books/v1/volumes?q=";

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
        if (intent != null){
            String mQuery = BOOKSEARCH.concat(intent.getStringExtra("USER_QUERY"));
            Bundle bundle = intent.getExtras();
            bundle.putString("QUERY", mQuery);
            getLoaderManager().restartLoader(0, bundle, null);

        }
    }
}