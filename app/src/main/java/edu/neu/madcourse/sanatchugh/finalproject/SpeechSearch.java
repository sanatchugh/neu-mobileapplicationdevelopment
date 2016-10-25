package edu.neu.madcourse.joeyhuang.finalproject;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.ListView;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.Reference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import edu.neu.madcourse.joeyhuang.R;

// Reference columns
import static android.provider.BaseColumns._ID;
import static edu.neu.madcourse.joeyhuang.finalproject.FoodContract.ReferenceEntry.R_EDIBLEDAYS;
import static edu.neu.madcourse.joeyhuang.finalproject.FoodContract.ReferenceEntry.R_TITLE;
import static edu.neu.madcourse.joeyhuang.finalproject.FoodContract.ReferenceEntry.R_UNITS;
import static edu.neu.madcourse.joeyhuang.finalproject.FoodContract.ReferenceEntry.R_USES;
import static edu.neu.madcourse.joeyhuang.finalproject.FoodContract.ReferenceEntry.R_CONTENT_URI;

// List columns
import static edu.neu.madcourse.joeyhuang.finalproject.FoodContract.ListEntry.L_FOODID;
import static edu.neu.madcourse.joeyhuang.finalproject.FoodContract.ListEntry.L_COST;
import static edu.neu.madcourse.joeyhuang.finalproject.FoodContract.ListEntry.L_WEIGHT;
import static edu.neu.madcourse.joeyhuang.finalproject.FoodContract.ListEntry.L_EXPDATE;
import static edu.neu.madcourse.joeyhuang.finalproject.FoodContract.ListEntry.L_REMINDED;
import static edu.neu.madcourse.joeyhuang.finalproject.FoodContract.ListEntry.L_CONTENT_URI;


public class SpeechSearch extends ListActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    //Expiration
    private Button tvDisplayDate;
    private int year;
    private int month;
    private int day;
    static final int DATE_DIALOG_ID = 999;

    //Listview item data holders
    private String mTitle;

    private ImageButton btnspk;
    private ImageButton btnnotify;
    private TextView returnedText;
    private TextView speechmessage;
    private TextView errormessageuser;
    private TextView emptymessage;
    private Dialog dialog;
    //private ToggleButton toggleButton;
    private ProgressBar progressBar;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String LOG_TAG = "SpeechRecognitionActivity";
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private HashMap hm;
    // The name of the file to open.
    String fileName = "res/raw/food_wordlist";

    // This will reference one line at a time
    String line = null;

    // DB CONSTANTS
    private static String[] FROM = {_ID, R_TITLE, R_EDIBLEDAYS, R_UNITS, R_USES};
    private String[] mFromClause = {_ID, R_TITLE, R_EDIBLEDAYS, R_UNITS, R_USES};
    private static int[] TO = {R.id.rowid, R.id.title, R.id.days, R.id.units,};
    private int[] mToClause = {R.id.rowid, R.id.title, R.id.days, R.id.units,};
    private String ORDER_BY = R_TITLE + " DESC LIMIT 10";
    private String mOrderClause = R_TITLE + " DESC LIMIT 10";
    private static String WHERE = R_TITLE + " like '%";
    private static String WHERE_END = "%'";
    private static String AND = " and ";
    private static String mRefSelectionClause = _ID + " = ?";
    private static final String DATABASE_NAME = "food.db";

    // The loader's unique id (within this activity)
    private final static int LOADER_ID = 1;

    // The adapter that binds our data to the ListView
    private ReferenceListAdapter mAdapter;

    // Cursor loader manager
    private LoaderManager mLoaderManager;

    // Where clause for queries
    private String mWhereClause = null;

    // Update values for Reference table
    ContentValues mReferenceValues;

    // Results
    String[] titleResults = new String[10];
    int[] idResults = new int[10];
    int[] daysResults = new int[10];
    int[] usesResults = new int[10];

    // Date formats
    private static SimpleDateFormat month_first_sdf = new SimpleDateFormat("M/d/yyyy");
    private static SimpleDateFormat year_first_sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.final_speech);

        emptymessage = (TextView) findViewById(android.R.id.empty);

        returnedText = (TextView) findViewById(R.id.speech_result);
        errormessageuser = (TextView) findViewById(R.id.errormessage);
        hm = new HashMap();

        //Add wordlist to hashmap

        try {
            // FileReader reads text files in the default encoding.
            //FileReader fileReader = new FileReader(fileName);
            InputStream is = getResources().openRawResource(R.raw.food_wordlist);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            // Always wrap FileReader in BufferedReader.
            //BufferedReader bufferedReader = new BufferedReader(fileReader);
            int i = 0;
            while ((line = reader.readLine()) != null) {
                hm.put(line, null);
                i++;
            }
            reader.close();
        } catch (FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            fileName + ex);
        } catch (IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + fileName + ex);
        }


        //Button for Speech Testing
        btnspk = (ImageButton) findViewById(R.id.btnSpeak);
        btnspk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput(getString(R.string.speech_prompt));
            }
        });

        /*
        //Button for Ntification Testing
        btnnotify=(ImageButton) findViewById(R.id.btnNotify);
        btnnotify.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(SpeechSearch.this, NotifyMe.class);
                startActivity(intent);
            }
        });
        */

        // Initialize the adapter. It starts off empty.
        mAdapter = new ReferenceListAdapter(this, R.layout.final_reference_item, null, mFromClause, mToClause, 0);

        // Associate the adapter with the ListView
        setListAdapter(mAdapter);

        // Initial query for top 10 used items
        mOrderClause = R_USES + " DESC LIMIT 10";
        mWhereClause = R_USES + " > 0";

        // Initialize the loader
        mLoaderManager = getLoaderManager();
        mLoaderManager.initLoader(LOADER_ID, null, this);

        // Reset order back to default
        mOrderClause = ORDER_BY;

        // EXAMPLE QUERY
        //String[] terms = {"chicken", "breasts"};
        //query(terms);

    }

    /**
     * Query function based on search terms
     */
    void query(String[] terms) {
        emptymessage.setText("Oops! No results were found for your search");

        if (terms == null) {
            mWhereClause = null;
        } else {
            int index = 0;
            mWhereClause = null;
            for (String s : terms) {
                if (mWhereClause == null) {
                    mWhereClause = WHERE + s + WHERE_END;
                } else {
                    mWhereClause += WHERE + s + WHERE_END;
                }

                if (index < terms.length - 1) {
                    mWhereClause += AND;
                }
                index++;
            }
        }

        mLoaderManager.restartLoader(LOADER_ID, null, this);
    }

    //GOOGLE VOICE SEARCH PROMPT

    /**
     * Showing google speech input dialog
     */
    private void promptSpeechInput(String text) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                text);
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }

    /**
     * Receiving speech input
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    String text = null;
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    text = checkIfValidInput(result);
                    if (text == "<Non-food word>")
                        promptSpeechInput(getString(R.string.speech_prompt_error));
                    errormessageuser.setText("You searched for: " + text);
                    query(text.split("\\s+"));
                }
                break;
            }

        }
    }

    private String checkIfValidInput(ArrayList<String> result) {
        String queryRes = null;
        for (int i = 0; i < 5; i++) {
            queryRes = checkforValidSpeechString(result.get(i));
            if (queryRes != null)
                break;
        }
        if (queryRes == null)
            return "<Non-food word>";
        else
            return queryRes;
    }

    private String checkforValidSpeechString(String s) {
        int i = 0, count = 0;
        String[] parts = s.split("\\ ");
        int length = parts.length;
        while (i < length)
            if (hm.containsKey(parts[i])) {
                count++;
                i++;
            } else
                i++;

        if (count == length)
            return s;
        else
            return null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Create a new CursorLoader
        return new CursorLoader(this, R_CONTENT_URI, mFromClause, mWhereClause, null, mOrderClause);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case LOADER_ID:

                // The data is now available to use
                mAdapter.swapCursor(cursor);

                int index = 0;
                while (cursor.moveToNext()) {
                    titleResults[index] = cursor.getString(cursor.getColumnIndex(R_TITLE));
                    idResults[index] = cursor.getInt(cursor.getColumnIndex(_ID));
                    daysResults[index] = cursor.getInt(cursor.getColumnIndex(R_EDIBLEDAYS));
                    usesResults[index] = cursor.getInt(cursor.getColumnIndex(R_USES));

                    index++;
                }

                break;

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // The loader's data is unavailable
        mAdapter.swapCursor(null);
    }

    private class ReferenceListAdapter extends SimpleCursorAdapter {
        LayoutInflater inflater;
        private int mSelectedPosition;
        private Context context;
        private int layout;

        public ReferenceListAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
            this.context = context;
            this.layout = layout;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            super.bindView(view, context, cursor);

            // Get views of elements
            Button add_button = (Button) view.findViewById(R.id.btn_add);
            Button date_button = (Button) view.findViewById(R.id.date);
            TextView title_view = (TextView) view.findViewById(R.id.title);

            // Set the title view to the right result
            title_view.setText(cursor.getString(cursor.getColumnIndex(R_TITLE)));

            // Set "Add to my list" button listener
            add_button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get item's position in listview
                    int position = getListView().getPositionForView(v);
                    if (position != ListView.INVALID_POSITION) {
                        // Get the item's view
                        View child_view = (View) v.getParent();

                        // Get the views of this item with data
                        EditText money_view = (EditText) child_view.findViewById(R.id.money);
                        EditText weight_view = (EditText) child_view.findViewById(R.id.weight);
                        Button date_view = (Button) child_view.findViewById(R.id.date);
                        TextView tv = (TextView) child_view.findViewById(R.id.title);
                        ToggleButton units_view = (ToggleButton) child_view.findViewById(R.id.ozOrlb);

                        // Add entry to list table
                        if (addListEntry(idResults[position], weight_view.getText().toString(),
                                money_view.getText().toString(), date_view.getText().toString(),
                                daysResults[position], units_view.getText().toString())) {
                            // Write usage+1 to reference entry
                            addUsage(idResults[position], usesResults[position]);


                            // Go back to home activity
                            returnToMain();
                        }


                    }

                }
            });

            date_button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get item's position in listview
                    int position = getListView().getPositionForView(v);
                    if (position != ListView.INVALID_POSITION) {
                        // Get the item's view
                        View child_view = getListView().getChildAt(position);
                        Button date_view = (Button) child_view.findViewById(R.id.date);

                        tvDisplayDate = date_view;
                        showDialog(DATE_DIALOG_ID);
                    }

                }
            });
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return inflater.inflate(layout, null);
        }

    }

    // Go back to user list activity
    public void listButton(View view) {
        finish();
    }

    // Go back to user list activity
    public void returnToMain() {
        //Intent resultIntent = new Intent();
        //resultIntent.putExtra("requestcode", ADD_FOOD_REQUEST);
        finish();
    }

    // Add +1 to usage for food's ID
    private void addUsage(int id, int uses) {
        mReferenceValues = new ContentValues();
        mReferenceValues.put(R_USES, uses + 1);
        String[] id_array = {String.valueOf(id)};

        getContentResolver().update(R_CONTENT_URI, mReferenceValues, mRefSelectionClause, id_array);
    }

    // Add new entry to list table
    // food_id, weight, cost, reminded, exp_date
    // food_id: grabbed from cursor result
    // weight: grabbed from weight edittext, POSITIVE, ROUNDED
    // cost: grabbed from cost edittext, POSITIVE, ROUNDED
    // reminded: set to 0
    // exp_date: grab button text, if changed, set as that date.
    //          otherwise, calculate exp_date from current date + edibledays
    private boolean addListEntry(int id, String weight, String cost, String exp_date,
                                 int add_days, String units) {

        mReferenceValues = new ContentValues();

        // Check null or empty strings
        if (weight == null || weight.isEmpty()) {
            show_message("Invalid weight, try again!");
            return false;
        }
        if (cost == null || cost.isEmpty()) {
            show_message("Invalid cost, try again!");
            return false;
        }

        // If weight or cost are negative, throw error
        if (Float.parseFloat(weight) < 0) {
            show_message("Invalid weight, try again!");
            return false;
        }

        if (Float.parseFloat(cost) < 0) {
            show_message("Invalid cost, try again!");
            return false;
        }

        if (units.equals("lb")) {
            weight = String.valueOf(Integer.parseInt(weight) * 16);
        }

        int db_weight = (int) (Float.parseFloat(weight) + 0.5);
        int db_cost = (int) (Float.parseFloat(cost) + 0.5);

        // Check if exp_date is "Set expiration date"
        // If so, calculate expiration date by adding edibledays to current date
        Calendar cal = Calendar.getInstance();
        if (exp_date.equals("Set expiration date")) {

            // Add edibledays to today's date
            cal.add(Calendar.DATE, add_days);
        } else {

            try {
                cal.setTime(month_first_sdf.parse(exp_date));

            } catch (ParseException p) {
                Log.d("Parsing error", p.toString());
            }
        }
        String db_date = year_first_sdf.format(cal.getTime());

        // Write values to db
        mReferenceValues = new ContentValues();
        mReferenceValues.put(L_FOODID, id);
        mReferenceValues.put(L_WEIGHT, db_weight);
        mReferenceValues.put(L_COST, db_cost);
        mReferenceValues.put(L_REMINDED, 0);
        mReferenceValues.put(L_EXPDATE, db_date);
        getContentResolver().insert(L_CONTENT_URI, mReferenceValues);

        return true;
    }

    // Toast messages for errors
    private void show_message(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID: {
                Calendar c = Calendar.getInstance();
                int day = c.get(Calendar.DAY_OF_MONTH);
                int month = (c.get(Calendar.MONTH));
                int year = c.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(this, datePickerListener, year, month, day);
                datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                Log.i("SpeechSearch", String.valueOf(datePickerDialog));

                // set date picker as current date
                return datePickerDialog;
            }
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            // set selected date into textview
            tvDisplayDate.setText(new StringBuilder().append(month + 1)
                    .append("/").append(day).append("/").append(year)
                    .append(" "));

        }
    };
}