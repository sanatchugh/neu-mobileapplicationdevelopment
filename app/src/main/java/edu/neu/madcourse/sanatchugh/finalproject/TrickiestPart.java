/***
 * Excerpted from "Hello, Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/eband4 for more book information.
 ***/
package edu.neu.madcourse.joeyhuang.finalproject;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;

import edu.neu.madcourse.joeyhuang.R;

import static android.provider.BaseColumns._ID;
import static edu.neu.madcourse.joeyhuang.trickiestpart.FoodContract.ReferenceEntry.R_CATEGORY;
import static edu.neu.madcourse.joeyhuang.trickiestpart.FoodContract.ReferenceEntry.R_CONTENT_URI;
import static edu.neu.madcourse.joeyhuang.trickiestpart.FoodContract.ReferenceEntry.R_EDIBLEDAYS;
import static edu.neu.madcourse.joeyhuang.trickiestpart.FoodContract.ReferenceEntry.R_TITLE;
import static edu.neu.madcourse.joeyhuang.trickiestpart.FoodContract.ReferenceEntry.R_UNITS;

public class TrickiestPart extends ListActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    // ...
    private static String[] FROM = { _ID, R_TITLE, R_EDIBLEDAYS, R_UNITS, };
    private static int[] TO = { R.id.rowid, R.id.title, R.id.days, R.id.units, };
    private static String ORDER_BY = R_TITLE + " DESC";

    // The loader's unique id (within this activity)
    private final static int LOADER_ID = 1;

    // The adapter that binds our data to the ListView
    private SimpleCursorAdapter mAdapter;

    //TextView empty;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trickiest_main);

        /*
        empty = (TextView) findViewById(R.id.test);
        empty.setText(this.getDatabasePath("food.db").toString());
        */

        // Initialize the adapter. It starts off empty.
        mAdapter = new SimpleCursorAdapter(this, R.layout.trickiest_item, null, FROM, TO, 0);

        // Associate the adapter with the ListView
        setListAdapter(mAdapter);

        // Initialize the loader
        LoaderManager lm = getLoaderManager();
        lm.initLoader(LOADER_ID, null, this);

        //addFood("Apples", 21, "lb", "Fresh Fruits");
    }

    private void addFood(String title, int edibledays, String units, String category) {
        // Insert a new record into the Events data source.
        // You would do something similar for delete and update.
        ContentValues values = new ContentValues();
        values.put(R_EDIBLEDAYS, edibledays);
        values.put(R_UNITS, units);
        values.put(R_TITLE, title);
        values.put(R_CATEGORY, category);
        getContentResolver().insert(R_CONTENT_URI, values);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Create a new CursorLoader
        return new CursorLoader(this, R_CONTENT_URI, FROM, null, null, ORDER_BY);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case LOADER_ID:
                // The data is now available to use
                mAdapter.swapCursor(cursor);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // The loader's data is unavailable
        mAdapter.swapCursor(null);
    }
}