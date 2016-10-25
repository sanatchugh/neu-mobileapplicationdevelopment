package edu.neu.madcourse.sanatchugh;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View.OnClickListener;

public class MyActivity extends AppCompatActivity {

    private Button buttonq, buttona, buttone, buttontt, buttonsc, buttond, buttonsr, buttonfp;
    final Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Sanat Chugh");
        setSupportActionBar(toolbar);

        // About Button
        buttona = (Button) findViewById(R.id.aboutbutton);
        buttona.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.activity_tic_tac_toe);
                dialog.setTitle("About");

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String IMEI = tm.getDeviceId();

                // set title
                alertDialogBuilder.setTitle("About");

                // set dialog message
                alertDialogBuilder
                        .setMessage("Name | Sanat Chugh\n" +
                                "Email | chugh.s@husky.neu.edu\n" +
                                "Year | 3rd year MS\n" +
                                "Degree | Computer Science\n" +
                                "Device IMEI | " + IMEI)
                        .setCancelable(true)
                        .setIcon(R.drawable.profilepic);

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });


        // TicTacToe Button
        buttontt = (Button) findViewById(R.id.tictactoebutton);
        buttontt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(MyActivity.this, TicTacToe.class);
                startActivity(intent);
            }
        });


        // Dictionary Button
        buttond = (Button) findViewById(R.id.dictonarybutton);
        buttond.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(MyActivity.this, Dictionary2.class);
                startActivity(intent);
            }
        });


        // Scraggle Button
        buttonsc = (Button) findViewById(R.id.scragglebutton);
        buttonsc.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(MyActivity.this, Scraggle.class);
                startActivity(intent);
            }
        });

        // Error Button
        buttone = (Button) findViewById(R.id.errorbutton);
        buttone.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String IMEI = tm.getDeviceId();
                System.out.println(0 / 0);

            }
        });

        // The Trickiest Part Button
        buttonsr = (Button) findViewById(R.id.thetrickiestpart);
        buttonsr.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent("edu.neu.madcourse.joeyhuang.finalproject.SpeechSearch");
                startActivity(intent);
            }
        });


        // Final Project Button
        buttonfp = (Button) findViewById(R.id.finalproject);
        buttonfp.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent("edu.neu.madcourse.joeyhuang.finalproject.HomeScreen");
                startActivity(intent);
            }
        });

        //Communictions Button
        Button buttonc = (Button) findViewById(R.id.commbutton);
        buttonc.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(MyActivity.this, CommunicationMain.class);
                startActivity(intent);
            }
        });

        // Quit Button
        buttonq = (Button) findViewById(R.id.quitbutton);
        buttonq.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set title
                alertDialogBuilder.setTitle("About");

                // set dialog message
                alertDialogBuilder
                        .setMessage("Click Yes to Exit")
                        .setCancelable(false)
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, close
                                // current activity
                                MyActivity.this.finish();
                            }
                        })
                        .setNegativeButton("No",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });

//       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
