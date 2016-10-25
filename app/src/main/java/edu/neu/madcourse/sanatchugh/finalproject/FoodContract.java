package edu.neu.madcourse.joeyhuang.finalproject;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by jzhuang on 4/10/16.
 */
public class FoodContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public FoodContract() {}

    /* Inner class that defines the table contents */
    public static abstract class ReferenceEntry implements BaseColumns {
        public static final String R_TABLE_NAME = "reference";

        public static final String AUTHORITY = "edu.neu.madcourse.joeyhuang";
        public static final Uri R_CONTENT_URI = Uri.parse("content://"
                + AUTHORITY + "/" + R_TABLE_NAME);

        public static final String R_TITLE = "title";
        public static final String R_EDIBLEDAYS = "edibledays";
        public static final String R_UNITS = "units";
        public static final String R_CATEGORY = "category";
        public static final String R_USES = "uses";
    }

    /* Inner class that defines the table contents */
    public static abstract class ListEntry implements BaseColumns {
        public static final String L_TABLE_NAME = "list";

        public static final String AUTHORITY = "edu.neu.madcourse.joeyhuang.finalproject";
        public static final Uri L_CONTENT_URI = Uri.parse("content://"
                + AUTHORITY + "/" + L_TABLE_NAME);

        //public static final String COLUMN_NAME_ENTRY_ID = "entryid";
        public static final String L_FOODID = "food_id";
        public static final String L_WEIGHT = "weight";
        public static final String L_COST = "cost";
        public static final String L_REMINDED = "reminded";
        public static final String L_EXPDATE = "exp_date";
    }
}
