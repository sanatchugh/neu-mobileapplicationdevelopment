package edu.neu.madcourse.sanatchugh;

/**
 * Created by sanatchugh on 3/22/16.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;

import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class CommunicationMain extends Activity implements OnClickListener {

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    public static final String PROPERTY_ALERT_TEXT = "alertText";
    public static final String PROPERTY_TITLE_TEXT = "titleText";
    public static final String PROPERTY_CONTENT_TEXT = "contentText";
    public static final String PROPERTY_NTYPE = "nType";

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    static final String TAG = "GCM Sample Demo";
    TextView mDisplay;
    EditText mMessage;
    GoogleCloudMessaging gcm;
    SharedPreferences prefs;
    Context context;
    String regid, regidp1;
    RemoteClient remoteClient, remoteClient2;
    final List<String> results=new ArrayList<String>();

    Timer timer;
    TimerTask timerTask;
    final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.communication_main);
        mDisplay = (TextView) findViewById(R.id.communication_display);
//        mMessage = (EditText) findViewById(R.id.communication_edit_message);
        gcm = GoogleCloudMessaging.getInstance(this);
        context = getApplicationContext();
        Firebase.setAndroidContext(this);
        remoteClient = new RemoteClient(this);
        remoteClient2 = new RemoteClient(this);
    }

    @SuppressLint("NewApi")
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
                Integer.MIN_VALUE);
        Log.i(TAG, String.valueOf(registeredVersion));
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private SharedPreferences getGCMPreferences(Context context) {
        return getSharedPreferences(CommunicationMain.class.getSimpleName(), Context.MODE_PRIVATE);
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private static void setRegisterValues() {
        CommunicationConstants.alertText = "Register Notification";
        CommunicationConstants.titleText = "Register";
        CommunicationConstants.contentText = "Registering Successful!";
    }

    private static void setUnregisterValues() {
        CommunicationConstants.alertText = "Unregister Notification";
        CommunicationConstants.titleText = "Unregister";
        CommunicationConstants.contentText = "Unregistering Successful!";
    }

    private static void setSendMessageValues(String msg) {
        CommunicationConstants.alertText = "Message Notification";
        CommunicationConstants.titleText = "Sending Message";
        CommunicationConstants.contentText = msg;
    }

    private void registerInBackground(final String username) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    setRegisterValues();
                    regid = gcm.register(CommunicationConstants.GCM_SENDER_ID);

                    // implementation to store and keep track of registered devices here

                    msg = "Device registered, registration ID=" + regid;

                    final String username_temp=username;
                    sendRegistrationIdToBackend(username_temp, regid);
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                mDisplay.append(msg + "\n");
            }
        }.execute(null, null, null);
    }

    private void sendRegistrationIdToBackend(String username_temp, String regid_temp) {

        remoteClient.saveValue(username_temp, regid);
        // Your implementation here.
    }

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onClick(final View view) {
//        if (view == findViewById(R.id.communication_send)) {
//            String message = ((EditText) findViewById(R.id.communication_edit_message))
//                    .getText().toString();
//            if (message != "") {
//                sendMessage(message);
//            } else {
//                Toast.makeText(context, "Sending Context Empty!",
//                        Toast.LENGTH_LONG).show();
//            }
//        } else if (view == findViewById(R.id.communication_clear)) {
//            mMessage.setText("");
//        } else
        if (view == findViewById(R.id.communication_unregistor_button)) {
            EditText username_text=(EditText)findViewById(R.id.communication_username);
            String username = username_text.getText().toString();
            unregister(username);
        } else if (view == findViewById(R.id.communication_registor_button)) {
            if (checkPlayServices()) {
                regid = getRegistrationId(context);
                if (TextUtils.isEmpty(regid)) {
                    EditText username_text=(EditText)findViewById(R.id.communication_username);
                    ListView list=(ListView)findViewById(R.id.communication_listview);

                    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.word_value, results);
                    list.setAdapter(adapter);
                    switch (username_text.getText().toString()){
                        case "messi":{
                            results.add("suarez");
                            results.add("neymar");
                            break;
                        }
                        case "neymar":{
                            results.add("messi");
                            results.add("suarez");
                            break;
                        }
                        case "suarez":{
                            results.add("messi");
                            results.add("neymar");
                            break;
                        }
                        default:
                            break;
                    }
                    adapter.notifyDataSetChanged();
                    registerInBackground(username_text.getText().toString());
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            //Retrieve position where the user has clicked on the ListView
                            String test = results.get(position);
                            remoteClient.fetchValue(test);
                            startTimer(test);
                        }
                    });
                }
            }
        }

    }

    public void startTimer(String key) {
        //set a new Timer
        timer = new Timer();
        //initialize the TimerTask's job
        initializeTimerTask(key);
        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        // The values can be adjusted depending on the performance
        timer.schedule(timerTask, 5000, 1000);
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void initializeTimerTask(final String key) {
        timerTask = new TimerTask() {
            public void run() {
                Log.d(TAG, "isDataFetched >>>>" + remoteClient.isDataFetched());
                if(remoteClient.isDataFetched())
                {
                    handler.post(new Runnable() {

                        public void run() {
                            String value=remoteClient.getValue(key);
                            Log.d(TAG, "Value >>>>" + value);
                            if(value == null)
                                Toast.makeText(context, key + " is offline", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(context, key + " is registered with:" + value, Toast.LENGTH_SHORT).show();
                        }
                    });

                    stoptimertask();
                }

            }
        };
    }


    private void unregister(final String s) {
        Log.d(CommunicationConstants.TAG, "UNREGISTER USERID: " + regid);
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";

                try {
                    msg = "Sent unregistration";
                    setUnregisterValues();
                    gcm.unregister();
                    remoteClient.saveValue(s, null);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                removeRegistrationId(getApplicationContext());
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                ((TextView) findViewById(R.id.communication_display))
                        .setText(regid);
            }
        }.execute();
    }

    private void removeRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(CommunicationConstants.TAG, "Removig regId on app version "
                + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(PROPERTY_REG_ID);
        editor.commit();
        regid = null;
    }

    @SuppressLint("NewApi")
    private void sendMessage(final String message) {
        if (regid == null || regid.equals("")) {
            Toast.makeText(this, "You must register first", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (message.isEmpty()) {
            Toast.makeText(this, "Empty Message", Toast.LENGTH_LONG).show();
            return;
        }

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                List<String> regIds = new ArrayList<String>();
                String reg_device = regid;
                int nIcon = R.drawable.ic_stat_cloud;
                int nType = CommunicationConstants.SIMPLE_NOTIFICATION;
                Map<String, String> msgParams;
                msgParams = new HashMap<String, String>();
                msgParams.put("data.alertText", "Notification");
                msgParams.put("data.titleText", "Notification Title");
                msgParams.put("data.contentText", message);
                msgParams.put("data.nIcon", String.valueOf(nIcon));
                msgParams.put("data.nType", String.valueOf(nType));
                setSendMessageValues(message);
                GcmNotification gcmNotification = new GcmNotification();
                regIds.clear();
                regIds.add(reg_device);
                gcmNotification.sendNotification(msgParams, regIds,
                        edu.neu.madcourse.sanatchugh.CommunicationMain.this);
                msg = "sending information...";
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        }.execute(null, null, null);
    }
}
