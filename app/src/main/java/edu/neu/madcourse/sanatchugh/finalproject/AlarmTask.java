package edu.neu.madcourse.joeyhuang.finalproject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class AlarmTask implements Runnable{
    // The date selected for the alarm
    private final Calendar date;
    // The android system alarm manager
    private final AlarmManager am;
    // Your context to retrieve the alarm manager from
    private final Context context;

    // The hour of the day the alarm should be set to
    private final int HOUR_OF_DAY = 8;

    // Request code based on the date
    private final int request_code;

    // Date format
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    public AlarmTask(Context context, Calendar date) {
        this.context = context;
        this.am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Set hour of the day that the alarm should go off
        date.set(Calendar.HOUR, HOUR_OF_DAY);
        this.date = date;

        // We set the unique request code based on the date, which looks like "20160428"
        // Any additional alarm task requests for the same date will simply overwrite
        this.request_code = Integer.parseInt(sdf.format(date.getTime()));

        //TESTING
        //Calendar test = Calendar.getInstance();
        //test.add(Calendar.SECOND, 30);
        //this.date = test;
    }

    @Override
    public void run() {

        // Request to start are service when the alarm date is upon us
        // We don't start an activity as we just want to pop up a notification into the system bar not a full activity
        Intent intent = new Intent(context, NotifyService.class);
        intent.putExtra(NotifyService.INTENT_NOTIFY, true);
        PendingIntent pendingIntent = PendingIntent.getService(context, request_code, intent, 0);

        // Sets an alarm - note this alarm will be lost if the phone is turned off and on again
        am.set(AlarmManager.RTC, date.getTimeInMillis(), pendingIntent);
    }
}
