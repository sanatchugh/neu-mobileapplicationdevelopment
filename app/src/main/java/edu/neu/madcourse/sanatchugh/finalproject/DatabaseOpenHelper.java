package edu.neu.madcourse.joeyhuang.finalproject;

/**
 * Created by jzhuang on 4/10/16.
 */

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DatabaseOpenHelper extends SQLiteAssetHelper {
    private static final String LOG_TAG = "SQLITE";

    private static final String DB_NAME = "food.db";
    //private static String DB_PATH = "/data/data/edu.neu.madcourse.joeyhuang/databases/";
    private static final int DATABASE_VERSION = 1;

    public DatabaseOpenHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
    }
}
