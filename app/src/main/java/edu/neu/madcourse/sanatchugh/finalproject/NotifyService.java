package edu.neu.madcourse.joeyhuang.finalproject;

import android.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.provider.BaseColumns._ID;
import static edu.neu.madcourse.joeyhuang.finalproject.FoodContract.ListEntry.L_CONTENT_URI;
import static edu.neu.madcourse.joeyhuang.finalproject.FoodContract.ListEntry.L_REMINDED;


public class NotifyService extends Service {

    public class ServiceBinder extends Binder {
        NotifyService getService() {
            return NotifyService.this;
        }
    }

    // Name of an intent extra we can use to identify if this service was started to create a notification
    public static final String INTENT_NOTIFY = "edu.neu.madcourse.joeyhuang.finalproject.service.INTENT_NOTIFY";
    // The system notification manager
    private NotificationManager mNM;

    // Date format
    private static SimpleDateFormat year_first_sdf = new SimpleDateFormat("yyyy-MM-dd");

    // Max number of notifications to show
    private static final int MAX_NOTIFICATIONS = 4;

    // Initialize notification id
    private int notification_id = 0;

    // Selection clause for updating notified status
    private static String mRefSelectionClause = _ID + " = ?";

    @Override
    public void onCreate() {
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // If this service was started by out AlarmTask intent then we want to show our notification
        if(intent.getBooleanExtra(INTENT_NOTIFY, false)){
            showNotification();
            //testNotification();
        }

        // We don't care if this service is stopped as we have already delivered our notification
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients
    private final IBinder mBinder = new ServiceBinder();

    private void testNotification() {
        Log.d("NOTIFICATIONS", "SETTING");

        // Get the shared preferences to get the notifications list
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        Calendar cal = Calendar.getInstance();
        String today = year_first_sdf.format(cal.getTime());
        // If there are notifications scheduled for today's date

        Map<String, ?> allEntries = preferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getKey().matches("^(\\d{4}-\\d{2}-\\d{2})$")) {
                Set<String> notification_strings = (Set<String>) entry.getValue();

                for (String s : notification_strings) {
                    String[] parts = s.split(":");
                    setNotified(parts[0]);
                    setSingleNotification(parts[1], parts[2]);
                }
            }
        }

        //Stopping the Service
        stopSelf();
    }

    /**
     * Creates a notification and shows it in the OS drag-down status bar
     */
    private void showNotification() {

        // Get the shared preferences to get the notifications list
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        Calendar cal = Calendar.getInstance();
        String today = year_first_sdf.format(cal.getTime());
        // If there are notifications scheduled for today's date
        if (preferences.contains(today)) {

            // Get the notifications scheduled for today
            Set<String> notification_strings = preferences.getStringSet(today, null);

            int num_notifications = notification_strings.size();
            String item_id;
            String title;
            String notification_type;

            // Update items with notified status
            for (String s : notification_strings) {
                String[] parts = s.split(":");
                setNotified(parts[0]);
            }
            // If there are more notifications than max allowed
            if (num_notifications > MAX_NOTIFICATIONS) {
                // Holders for different types of item titles
                List<String> titles_3 = new ArrayList<String>();
                List<String> titles_0 = new ArrayList<String>();

                for (String s : notification_strings) {
                    String[] parts = s.split(":");
                    title = parts[1];
                    notification_type = parts[2];
                    if (notification_type.equals("3")) {
                        titles_3.add(title);
                    } else {
                        titles_0.add(title);
                    }
                }

                if (titles_3.size() > 1) {
                    setGroupNotification(titles_3.size(), "3");
                } else if (titles_3.size() == 1) {
                    setSingleNotification(titles_3.get(0), "3");
                }

                if (titles_0.size() > 1) {
                    setGroupNotification(titles_0.size(), "0");
                } else if (titles_0.size() == 1) {
                    setSingleNotification(titles_0.get(0), "0");
                }

            } else {
                // If there is enough room, just make single notifications
                for (String s : notification_strings) {
                    String[] parts = s.split(":");
                    title = parts[1];
                    notification_type = parts[2];
                    setSingleNotification(title, notification_type);
                }
            }
        }

        //Stopping the Service
        stopSelf();
    }

    private void setSingleNotification (String title, String type) {
        //Pending Event for the notification to open into
        PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, HomeScreen.class), 0);

        String days;
        if (type.equals("3")) {
            days = "in 3 days!";
        } else {
            days = "today!";
        }

        //Building the Notification
        Notification notification = new NotificationCompat.Builder(this)
                .setTicker("Food Hero")
                .setSmallIcon(edu.neu.madcourse.joeyhuang.R.drawable.final_project_icon)
                .setContentTitle("Your food is expiring soon")
                .setContentText(title + " expiring " + days)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();

        //Starting the Notification
        mNM.notify(notification_id, notification);
        notification_id++;
    }

    private void setGroupNotification(int size, String type) {
        //Pending Event for the notification to open into
        PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, HomeScreen.class), 0);

        String days;
        if (type.equals("3")) {
            days = "in 3 days!";
        } else {
            days = "today!";
        }

        //Building the Notification
        Notification notification = new NotificationCompat.Builder(this)
                .setTicker("Food Hero")
                .setSmallIcon(edu.neu.madcourse.joeyhuang.R.drawable.final_project_icon)
                .setContentTitle("Your food is expiring soon")
                .setContentText(String.valueOf(size) + " food items are expiring " + days)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();

        //Starting the Notification
        mNM.notify(notification_id, notification);
        notification_id++;
    }

    // Update notification field for notified items
    private void setNotified(String item_id) {
        ContentValues referenceValues = new ContentValues();
        referenceValues.put(L_REMINDED, 1);
        String[] id_array = {item_id};

        getContentResolver().update(L_CONTENT_URI, referenceValues, mRefSelectionClause, id_array);
    }
}
