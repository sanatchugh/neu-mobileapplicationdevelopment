package edu.neu.madcourse.joeyhuang.finalproject;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;

import static android.provider.BaseColumns._ID;
import static edu.neu.madcourse.joeyhuang.finalproject.FoodContract.ListEntry.AUTHORITY;
import static edu.neu.madcourse.joeyhuang.finalproject.FoodContract.ListEntry.L_CONTENT_URI;
import static edu.neu.madcourse.joeyhuang.finalproject.FoodContract.ListEntry.L_TABLE_NAME;
import static edu.neu.madcourse.joeyhuang.finalproject.FoodContract.ListEntry.L_FOODID;
import static edu.neu.madcourse.joeyhuang.finalproject.FoodContract.ListEntry.L_COST;
import static edu.neu.madcourse.joeyhuang.finalproject.FoodContract.ListEntry.L_WEIGHT;
import static edu.neu.madcourse.joeyhuang.finalproject.FoodContract.ListEntry.L_EXPDATE;
import static edu.neu.madcourse.joeyhuang.finalproject.FoodContract.ListEntry.L_REMINDED;
import static edu.neu.madcourse.joeyhuang.finalproject.FoodContract.ReferenceEntry.R_TABLE_NAME;
import static edu.neu.madcourse.joeyhuang.finalproject.FoodContract.ReferenceEntry.R_EDIBLEDAYS;
import static edu.neu.madcourse.joeyhuang.finalproject.FoodContract.ReferenceEntry.R_TITLE;
import static edu.neu.madcourse.joeyhuang.finalproject.FoodContract.ReferenceEntry.R_UNITS;
import static edu.neu.madcourse.joeyhuang.finalproject.FoodContract.ReferenceEntry.R_USES;
import static edu.neu.madcourse.joeyhuang.finalproject.FoodContract.ReferenceEntry.R_CATEGORY;


public class ListProvider extends ContentProvider {
    private static final int EVENTS = 1;
    private static final int EVENTS_ID = 2;

    /** The MIME type of a directory of events */
    private static final String CONTENT_TYPE
            = "vnd.android.cursor.dir/vnd.edu.neu.madcourse.joeyhuang.finalproject.list";

    /** The MIME type of a single event */
    private static final String CONTENT_ITEM_TYPE
            = "vnd.android.cursor.item/vnd.edu.neu.madcourse.joeyhuang.finalproject.list";

    private DatabaseOpenHelper events;
    private UriMatcher uriMatcher;
    // ...

    private static final HashMap<String, String> mColumnMap = buildColumnMap();



    @Override
    public boolean onCreate() {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "list", EVENTS);
        uriMatcher.addURI(AUTHORITY, "list/#", EVENTS_ID);
        events = new DatabaseOpenHelper(getContext());
        return true;
    }



    @Override
    public Cursor query(Uri uri, String[] projection,
                        String selection, String[] selectionArgs, String orderBy) {
        if (uriMatcher.match(uri) == EVENTS_ID) {
            long id = Long.parseLong(uri.getPathSegments().get(1));
            selection = appendRowId(selection, id);
        }

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        String tables = "list LEFT OUTER JOIN reference ON (list.food_id = reference._id)";
        queryBuilder.setTables(tables);
        queryBuilder.setProjectionMap(mColumnMap);

        // Get the database and run the query
        SQLiteDatabase db = events.getReadableDatabase();
        //Cursor cursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, orderBy);
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, orderBy);

        // Tell the cursor what uri to watch, so it knows when its
        // source data changes
        cursor.setNotificationUri(getContext().getContentResolver(),
                uri);
        return cursor;
    }

    private static HashMap<String, String> buildColumnMap() {
        HashMap<String, String> map = new HashMap<String, String>();

        // List table columns
        map.put(_ID, addPrefix(L_TABLE_NAME, _ID) + " as " + _ID);
        map.put(L_COST, addPrefix(L_TABLE_NAME, L_COST) + " as " + L_COST);
        map.put(L_EXPDATE, addPrefix(L_TABLE_NAME, L_EXPDATE) + " as " + L_EXPDATE);
        map.put(L_FOODID, addPrefix(L_TABLE_NAME, L_FOODID) + " as " + L_FOODID);
        map.put(L_REMINDED, addPrefix(L_TABLE_NAME, L_REMINDED) + " as " + L_REMINDED);
        map.put(L_WEIGHT, addPrefix(L_TABLE_NAME, L_WEIGHT) + " as " + L_WEIGHT);

        // Reference table columns
        //map.put(_ID, addPrefix(R_TABLE_NAME, _ID) + " as " + alias(R_TABLE_NAME, _ID));
        map.put(R_CATEGORY, addPrefix(R_TABLE_NAME, R_CATEGORY) + " AS " + R_CATEGORY);
        map.put(R_EDIBLEDAYS, addPrefix(R_TABLE_NAME, R_EDIBLEDAYS) + " AS " + R_EDIBLEDAYS);
        map.put(R_TITLE, addPrefix(R_TABLE_NAME, R_TITLE) + " AS " + R_TITLE);
        map.put(R_UNITS, addPrefix(R_TABLE_NAME, R_UNITS) + " AS " + R_UNITS);
        map.put(R_USES, addPrefix(R_TABLE_NAME, R_USES) + " AS " + R_USES);

        return map;
    }

    private static String addPrefix (String table, String column) {
        return table + "." + column;
    }

    private static String alias (String table, String column) {
        return table + "_" + column;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case EVENTS:
                return CONTENT_TYPE;
            case EVENTS_ID:
                return CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }



    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = events.getWritableDatabase();

        // Validate the requested uri
        if (uriMatcher.match(uri) != EVENTS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // Insert into database
        long id = db.insertOrThrow(L_TABLE_NAME, null, values);

        // Notify any watchers of the change
        Uri newUri = ContentUris.withAppendedId(L_CONTENT_URI, id);
        getContext().getContentResolver().notifyChange(newUri, null);
        return newUri;
    }



    @Override
    public int delete(Uri uri, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = events.getWritableDatabase();
        int count;
        switch (uriMatcher.match(uri)) {
            case EVENTS:
                count = db.delete(L_TABLE_NAME, selection, selectionArgs);
                break;
            case EVENTS_ID:
                long id = Long.parseLong(uri.getPathSegments().get(1));
                count = db.delete(L_TABLE_NAME, appendRowId(selection, id),
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // Notify any watchers of the change
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }



    @Override
    public int update(Uri uri, ContentValues values,
                      String selection, String[] selectionArgs) {
        SQLiteDatabase db = events.getWritableDatabase();
        int count;
        switch (uriMatcher.match(uri)) {
            case EVENTS:
                count = db.update(L_TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case EVENTS_ID:
                long id = Long.parseLong(uri.getPathSegments().get(1));
                count = db.update(L_TABLE_NAME, values, appendRowId(
                        selection, id), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // Notify any watchers of the change
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }



    /** Append an id test to a SQL selection expression */
    private String appendRowId(String selection, long id) {
        return _ID + "=" + id
                + (!TextUtils.isEmpty(selection)
                ? " AND (" + selection + ')'
                : "");
    }


}