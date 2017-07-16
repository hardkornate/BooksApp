package com.example.android.booksapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>>, SearchView.OnQueryTextListener {

    public static final String BOOKSEARCHURL = "https://www.googleapis.com/books/v1/volumes?q=";
    /**
     * Constant value for the earthquake loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    public static final int BOOK_LOADER_ID = 1;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    /**
     * Adapter for the list of Books
     */
    private BookAdapter mAdapter;

    private SearchView mSearchView;
    private ListView bookListView;
    private View loadingIndicator;


    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchView = (SearchView) findViewById(R.id.action_search);
        bookListView = (ListView) findViewById(R.id.list);
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        bookListView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of earthquakes as input
        mAdapter = new BookAdapter(this, new ArrayList<Book>());
        // Set the adapter on the {@link ListView}
        bookListView.setAdapter(mAdapter);

        loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected book.
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current book that was clicked on
                Book currentBook = mAdapter.getItem(position);
                // Convert the String URL into a URI object (to pass into the Intent constructor)
                if (currentBook.getUrl() != null) {
                    Uri bookUri = Uri.parse(currentBook.getUrl());
                    // Create a new intent to view the book URI
                    Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);
                    // Send the intent to launch a new activity
                    startActivity(websiteIntent);
                }
            }
        });
    }

    public boolean isOnline() {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        // If there is a network connection, fetch data
        return (networkInfo != null && networkInfo.isConnected());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(true);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true; // we start the search activity manually
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (isOnline()) {
            // Get a reference to the LoaderManager, in order to interact with loaders
            Bundle bundle = new Bundle();
            bundle.putString("QUERY", newText);
            getLoaderManager().initLoader(BOOK_LOADER_ID, bundle, this);
            getLoaderManager().restartLoader(BOOK_LOADER_ID, bundle, this);
        } else {
            loadingIndicator.setVisibility(View.GONE);
            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }

        return false;
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {

        String query = bundle.getString("QUERY");
        String url = BOOKSEARCHURL + query;

        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.VISIBLE);

        return new BookLoader(this, url);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "There are no books on the current topic.."
        mEmptyStateTextView.setText(R.string.no_books);

        // Clear the adapter of previous book data
        mAdapter.clear();

        // If there is a valid list of {@link Book}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (books != null && !books.isEmpty()) {
            mAdapter.addAll(books);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }
}

