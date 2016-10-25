package edu.neu.madcourse.joeyhuang.finalproject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import edu.neu.madcourse.joeyhuang.R;


public class NotifyMe extends Activity  {
    public static String item;
    public static long diffDays;
    // This is a handle so that we can call methods on our service
    private ScheduleClient scheduleClient;
    // This is the date picker used to select the date for our notification
    private DatePicker picker;
    private String expiration;
    private String expiration1;
    private String expiration2;
    private String expiration3;
    private String expiration5;
    private String expiration7;
    private String expiration3neg;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifyme_layout);

        // Create a new service client and bind our activity to this service
        scheduleClient = new ScheduleClient(this);
        scheduleClient.doBindService();

        // Get a reference to our date picker
        picker = (DatePicker) findViewById(R.id.scheduleTimePicker);
    }

    //OnClick Defined in XML
    public void onDateSelectedButtonClick(View v){

        // Get the date from our datepicker
        int day = picker.getDayOfMonth();
        int month = picker.getMonth();
        int year = picker.getYear();

        item="Eggs";
        expiration3neg="04/18/2016";
        expiration = "04/22/2016";
        expiration1 = "04/23/2016";
        expiration2 = "04/24/2016";
        expiration3 = "04/25/2016";
        expiration5 = "04/27/2016";
        expiration7 = "04/29/2016";

        // Create a new calendar set to the date chosen
        // we set the time to midnight (i.e. the first minute of that day)
        Calendar c = Calendar.getInstance();
        int current_day = c.get(Calendar.DAY_OF_MONTH);
        int current_month = (c.get(Calendar.MONTH))+1;
        int current_year = c.get(Calendar.YEAR);
        String expiration_month = (expiration.substring(0, 2));
        String expiration_day = (expiration.substring(3,5));
        String expiration_year = (expiration.substring(6,10));
        String dateStart = ""+current_month+"/"+(current_day)+"/"+current_year+" 00:00:00";
        String dateStop = ""+expiration_month+"/"+expiration_day+"/"+expiration_year+" 00:00:00";

        //HH converts hour in 24 hours format (0-23), day calculation
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        Date d1 = null;
        Date d2 = null;

        try {
            d1 = format.parse(dateStart);
            d2 = format.parse(dateStop);

            //in milliseconds
            long diff = d2.getTime() - d1.getTime();
            Log.i("NotifyMe", String.valueOf(d1));
            Log.i("NotifyMe", String.valueOf(d2));
            diffDays = diff / (24 * 60 * 60 * 1000);
            Log.i("NotifyMe", String.valueOf(diffDays));


        } catch (Exception e) {
            e.printStackTrace();
        }

        if(diffDays==0 || diffDays==1 || diffDays==3){
            c.set(current_year, current_month-1, current_day);
            c.set(Calendar.HOUR_OF_DAY, 8);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            Log.i("NotifyMe:", String.valueOf(c));
            // Ask our service to set an alarm for that date, this activity talks to the client that talks to the service
            scheduleClient.setAlarmForNotification(c);
        }
        else {
            //Set Notification on day of expiration
            c.set(Integer.parseInt(expiration_year), (Integer.parseInt(expiration_month))-1, Integer.parseInt(expiration_day));
            c.set(Calendar.HOUR_OF_DAY, 8);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            Log.i("NotifyMe:", String.valueOf(c));
            diffDays=0;
            scheduleClient.setAlarmForNotification(c);

            //Set Notification 3 days before expiration date
            c.set(current_year, current_month-1, current_day);
            c.set(Calendar.HOUR_OF_DAY, 8);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            scheduleClient.setAlarmForNotification(c);
        }



    }

    @Override
    protected void onStop() {
        // When our activity is stopped ensure we also stop the connection to the service
        // this stops us leaking our activity into the system *bad*
        if(scheduleClient != null)
            scheduleClient.doUnbindService();
        super.onStop();
    }
}
