package edu.neu.madcourse.joeyhuang.finalproject;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import edu.neu.madcourse.joeyhuang.R;

import static android.provider.BaseColumns._ID;
import static edu.neu.madcourse.joeyhuang.finalproject.FoodContract.ListEntry.L_FOODID;
import static edu.neu.madcourse.joeyhuang.finalproject.FoodContract.ListEntry.L_COST;
import static edu.neu.madcourse.joeyhuang.finalproject.FoodContract.ListEntry.L_WEIGHT;
import static edu.neu.madcourse.joeyhuang.finalproject.FoodContract.ListEntry.L_EXPDATE;
import static edu.neu.madcourse.joeyhuang.finalproject.FoodContract.ListEntry.L_REMINDED;
import static edu.neu.madcourse.joeyhuang.finalproject.FoodContract.ListEntry.L_CONTENT_URI;
import static edu.neu.madcourse.joeyhuang.finalproject.FoodContract.ReferenceEntry.R_EDIBLEDAYS;
import static edu.neu.madcourse.joeyhuang.finalproject.FoodContract.ReferenceEntry.R_TITLE;
import static edu.neu.madcourse.joeyhuang.finalproject.FoodContract.ReferenceEntry.R_USES;

public class HomeScreen extends ListActivity
        implements LoaderManager.LoaderCallbacks<Cursor>{

    // Notification scheduler service
    private ScheduleClient scheduleClient;

    // Request code to pass to speechsearch activity
    static final int ADD_FOOD_REQUEST = 1;
    static final int RESULT_OK = 1;

    // From, To, Where clauses
    private String[] mFromClause = {_ID, R_TITLE, L_FOODID, L_COST, L_WEIGHT, L_EXPDATE, L_REMINDED};
    private int[] mToClause = { R.id.rowid, R.id.title, R.id.food_id, R.id.cost, R.id.weight, R.id.expdate, R.id.reminded };
    private String mOrderClause = "date(" + L_EXPDATE + ")" + " ASC";
    private String mWhereClause = L_EXPDATE + " >= date('now')";
    private String AND = " and ";
    private String DELETECLAUSE = _ID + "=?";

    // The loader's unique id (within this activity)
    private final static int LOADER_ID = 1;

    // The adapter that binds our data to the ListView
    private ReferenceListAdapter mAdapter;

    // Cursor loader manager
    private LoaderManager mLoaderManager;

    // Query results storage
    int[] idResults;
    String[] titleResults;
    int[] foodIDResults;
    int[] costResults;
    int[] weightResults;
    String[] dateResults;
    int[] remindedResults;
    Date[] convertedDates;
    List<Calendar> calendarDates = new ArrayList<Calendar> ();

    // Date formats
    private static SimpleDateFormat year_first_sdf = new SimpleDateFormat("yyyy-MM-dd");

    // Shared preferences
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    public static final String MP_MONEY = "moneysaved";
    public static final String MP_WEIGHT = "weightsaved";
    public static final String CUR_MONTH = "currentmonth";
    public static final String FIRSTTIME = "firsttimeapplaunch";

    // Goal text views
    TextView money_tv;
    TextView weight_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.final_home_screen);

        SharedPreferences sharedpreferences2 = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor2 = sharedpreferences2.edit();
        if (sharedpreferences2.contains(FIRSTTIME)) {
        } else {
            editor2.putInt(FIRSTTIME, 0);
            editor2.apply();
            //InstructionScreen
            Target addButton = new ViewTarget(R.id.add_button, this);

            new ShowcaseView.Builder(this, false)
                    .setTarget(addButton)
                    .setContentTitle("Hey There")
                    .setContentText("Click on this button to add new food to your list")
                    .setStyle(1)
                    .build();
        }


        // Initialize the adapter. It starts off empty.
        mAdapter = new ReferenceListAdapter(this, R.layout.final_list_item, null, mFromClause, mToClause, 0);

        // Associate the adapter with the ListView
        setListAdapter(mAdapter);

        // Initialize the loader
        mLoaderManager = getLoaderManager();
        mLoaderManager.initLoader(LOADER_ID, null, this);

        // Get goal total textviews
        money_tv = (TextView) this.findViewById(R.id.moneysavedamount);
        weight_tv = (TextView) this.findViewById(R.id.wastepreventedamount);

        // Get the current month
        int current_month = Calendar.getInstance().get(Calendar.MONTH);

        // Get shared preferences
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedpreferences.edit();

        // Get goal totals from sharedprefs and set text fields to them
        initializeGoals(current_month);

        // Create a new service client and bind our activity to this service
        // Used for scheduling alarmtasks for notifications
        scheduleClient = new ScheduleClient(this);
        scheduleClient.doBindService();
    }

    // Set up goal tracking
    private void initializeGoals(int current_month) {

        // If no record of current month
        if (!sharedpreferences.contains(CUR_MONTH)) {
            money_tv.setText("Money saved this month: $0 out of $50");
            weight_tv.setText("Waste prevented this month: 0lb out of 50lb");

            editor.putInt(CUR_MONTH, current_month);
            editor.putInt(MP_MONEY, 0);
            editor.putInt(MP_WEIGHT, 0);
            editor.apply();
        } else {
            // If the recorded current month is the same as the actual current month
            if (sharedpreferences.getInt(CUR_MONTH, 0) == current_month) {
                initializeGoalsHelper();
            } else {
                // If current month changed
                money_tv.setText("Money saved this month: $0 out of $50");
                weight_tv.setText("Waste prevented this month: 0lb out of 50lb");

                editor.putInt(MP_MONEY, 0);
                editor.putInt(MP_WEIGHT, 0);
                editor.putInt(CUR_MONTH, current_month);
                editor.apply();
            }
        }
    }

    private void initializeGoalsHelper() {
        if (sharedpreferences.contains(MP_MONEY)) {
            String w_string = String.format("%.1f", sharedpreferences.getInt(MP_WEIGHT, 0)/16.0);
            String m_string = String.valueOf(sharedpreferences.getInt(MP_MONEY, 0));
            money_tv.setText("Money saved this month: $" + m_string + " out of $50");
            weight_tv.setText("Waste prevented this month: " + w_string + "lb out of 20lb");
        } else {
            money_tv.setText("Money saved this month: $0 out of $50");
            weight_tv.setText("Waste prevented this month: 0lb out of 50lb");

            editor.putInt(MP_MONEY, 0);
            editor.putInt(MP_WEIGHT, 0);
            editor.apply();
        }
    }

    // Add values to the user's monetary and waste goals
    private void addToGoalTotals(int cost, int weight) {
        if (sharedpreferences.contains(MP_MONEY)) {
            cost += sharedpreferences.getInt(MP_MONEY, 0);
            weight += sharedpreferences.getInt(MP_WEIGHT, 0);
        }

        money_tv.setText("Money saved this month: $" + String.valueOf(cost) + " out of $50");
        weight_tv.setText("Waste prevented this month: " + String.valueOf(weight) + "lb out of 20lb");

        editor.putInt(MP_MONEY, cost);
        editor.putInt(MP_WEIGHT, weight);
        editor.apply();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Create a new CursorLoader
        return new CursorLoader(this, L_CONTENT_URI, mFromClause, mWhereClause, null, mOrderClause);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case LOADER_ID:
                // The data is now available to use
                mAdapter.swapCursor(cursor);

                int result_count = cursor.getCount();

                if (result_count > 0) {
                    idResults = new int[result_count];
                    titleResults = new String[result_count];
                    foodIDResults = new int[result_count];
                    costResults = new int[result_count];
                    weightResults = new int[result_count];
                    dateResults = new String[result_count];
                    remindedResults = new int[result_count];
                    convertedDates = new Date[result_count];

                    int index = 0;

                    cursor.moveToFirst();

                    do {
                        //R.id.rowid, R.id.title, R.id.food_id, R.id.cost, R.id.weight, R.id.date, R.id.reminded
                        idResults[index] = cursor.getInt(cursor.getColumnIndex(_ID));
                        titleResults[index] = cursor.getString(cursor.getColumnIndex(R_TITLE));
                        foodIDResults[index] = cursor.getInt(cursor.getColumnIndex(L_FOODID));
                        costResults[index] = cursor.getInt(cursor.getColumnIndex(L_COST));
                        weightResults[index] = cursor.getInt(cursor.getColumnIndex(L_WEIGHT));
                        dateResults[index] = cursor.getString(cursor.getColumnIndex(L_EXPDATE));
                        remindedResults[index] = cursor.getInt(cursor.getColumnIndex(L_REMINDED));

                        try {
                            convertedDates[index] = year_first_sdf.parse(dateResults[index]);

                            /*
                            cal.setTime(convertedDates[index]);
                            if (scheduleClient.isServiceBound()) {
                                Log.d("SERVICE BOUND", "SUCCESS");
                                scheduleClient.setAlarmForNotification(cal);
                            } else {
                                Log.d("SERVICE NOT BOUND", "FAILURE");
                            }
                            */

                        } catch (ParseException pe) {
                            Log.d("PARSING ERROR", pe.toString());
                        }

                        index++;
                    } while (cursor.moveToNext());

                    // Update the notifications settings in shared preferences
                    updateNotifications();
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // The loader's data is unavailable
        mAdapter.swapCursor(null);
    }

    // Remove past notifications from SharedPreferences
    private void pruneExpiredNotifications() {
        Map<String, ?> allEntries = sharedpreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getKey().matches("^(\\d{4}-\\d{2}-\\d{2})$")) {
                try {
                    // If the notification date is in the past, remove it
                    if (Calendar.getInstance().getTime().after(year_first_sdf.parse(entry.getKey()))) {
                        editor.remove(entry.getKey());
                    }
                } catch (ParseException pe) {
                    Log.d("PARSE ERROR", pe.toString());
                }
            }
        }
    }
    // Sync the notifications list in SharedPreferences to the user's list
    private void updateNotifications() {
        // Clear calendar dates for alarms
        calendarDates.clear();

        // Delete any date keys and attached values
        Map<String, ?> allEntries = sharedpreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getKey().matches("^(\\d{4}-\\d{2}-\\d{2})$")) {
                editor.remove(entry.getKey());
            }
        }
        editor.apply();

        // Declare dates
        Calendar cal = Calendar.getInstance();
        Date date2 = cal.getTime();

        // For each food item, add notification entry to sharedprefs and set alarm
        for (int i = 0; i < titleResults.length; i++) {
            cal = Calendar.getInstance();

            // Determine if we need to add 1 notification entry or 2
            // If food item expires in less than 3 days from now, only add 1 notification
            long days = TimeUnit.DAYS.convert(
                    convertedDates[i].getTime() - date2.getTime(), TimeUnit.MILLISECONDS);
            if (days >= 3) {
                // Get the date 3 days before the expiration date
                cal.setTime(convertedDates[i]);
                cal.add(Calendar.DATE, -3);
                // Write the notification for the advance date
                writeNotificationToPrefs(year_first_sdf.format(cal.getTime()), true, i);
                // Add calendar date to alarm dates
                calendarDates.add(cal);
            }
            // Write the notification for expiration date
            writeNotificationToPrefs(dateResults[i], false, i);
            cal = Calendar.getInstance();
            cal.setTime(convertedDates[i]);
            calendarDates.add(cal);
        }

        // Set the notification alarms
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                Log.d("WAITING FOR SERVICE","WAITING");

                // Wait for service to bind
                while (!scheduleClient.isServiceBound()) {}
                for (Calendar c : calendarDates) {
                    scheduleClient.setAlarmForNotification(c);
                }
                return "Alarms set";
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.d("ALARMS FINISHED", "FINALLY");
            }
        }.execute(null, null, null);
    }

    private void writeNotificationToPrefs(String date, boolean advance_notice, int index) {
        // Initialize entry string set
        Set<String> entry;

        String notification_type;
        if (advance_notice) {
            notification_type = ":3";
        } else {
            notification_type = ":0";
        }

        // If date is in the keys, update entry
        if (sharedpreferences.contains(date)) {
            // DO NOT MODIFY THIS SET, MAY CORRUPT DATA
            entry = sharedpreferences.getStringSet(date, null);

            // Make a copy of the retrieved set, and modify that
            Set<String> entry_copy = new HashSet<String>();
            entry_copy.addAll(entry);
            entry_copy.add(idResults[index] + ":" + titleResults[index] + notification_type);
            editor.putStringSet(date, entry_copy);

        } else {
            // Create new entry
            entry = new HashSet<String>();
            entry.add(idResults[index] + ":" + titleResults[index] + notification_type);
            editor.putStringSet(date, entry);
        }

        editor.apply();
    }

    // Add new food button, launches SpeechSearch activity
    public void add_button(View view) {
        Intent intent = new Intent(this, SpeechSearch.class);
        //startActivity(intent);
        startActivityForResult(intent, ADD_FOOD_REQUEST);
    }

    // Called when SpeechSearch activity returns, refreshes the ListView
    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_FOOD_REQUEST) {
            if (resultCode == RESULT_OK) {
                // Refresh the ListView
                mLoaderManager.restartLoader(LOADER_ID, null, this);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // Adapter class to handle ListView data binding and button listeners
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
            Button remove_button = (Button) view.findViewById(R.id.btn_remove);
            TextView cost_view = (TextView) view.findViewById(R.id.cost);
            TextView weight_view = (TextView) view.findViewById(R.id.weight);
            TextView title_view = (TextView) view.findViewById(R.id.title);
            TextView expire_view = (TextView) view.findViewById(R.id.expire);

            // Set the title view to the right result
            title_view.setText(cursor.getString(cursor.getColumnIndex(R_TITLE)));
            cost_view.setText(cursor.getString(cursor.getColumnIndex(L_COST)));
            weight_view.setText(cursor.getString(cursor.getColumnIndex(L_WEIGHT)));

            // Calculate days left til expiration
            Date date1 = null;
            Date date2 = Calendar.getInstance().getTime();
            try {
                date1 = year_first_sdf.parse(cursor.getString(cursor.getColumnIndex(L_EXPDATE)));
            } catch (ParseException p) {
                Log.d("Parsing error", p.toString());
            }
            long days = TimeUnit.DAYS.convert(date1.getTime() - date2.getTime(), TimeUnit.MILLISECONDS) + 1;

            // Set text label
            if (days == 0) {
                expire_view.setText("Expires TODAY!");
            } else {
                expire_view.setText("Expires in " + String.valueOf(days) + " day(s)");
            }


            // Set "I ATE THIS" button listener
            remove_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get item's position in listview
                    final int position = getListView().getPositionForView(v);
                    if (position != ListView.INVALID_POSITION) {
                        // Check if item was notified. If so, add to goal tracking totals
                        if (remindedResults[position] == 1) {
                            addToGoalTotals(costResults[position], weightResults[position]);
                        }


                        // Remove item from user list
                        String[] delete_id = {String.valueOf(idResults[position])};
                        removeListItem(delete_id);

                        // TODO: Remove notification



                    }

                }
            });
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return inflater.inflate(layout, null);
        }

    }

    // Remove item from user list and refresh the ListView
    private void removeListItem(String[] delete_id) {
        getContentResolver().delete(L_CONTENT_URI, DELETECLAUSE, delete_id);
        mLoaderManager.restartLoader(LOADER_ID, null, this);
    }

    @Override
    protected void onResume() {
        if(scheduleClient != null)
            scheduleClient.doBindService();
        super.onResume();
    }

    // Break connection to the notification scheduling service when activity stopped
    @Override
    protected void onStop() {
        // When our activity is stopped ensure we also stop the connection to the service
        // this stops us leaking our activity into the system *bad*
        if(scheduleClient != null)
            scheduleClient.doUnbindService();
        super.onStop();
    }
}
