package edu.neu.madcourse.joeyhuang.finalproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import edu.neu.madcourse.joeyhuang.R;


public class AppDescription extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.final_description);
    }
    public void startApplication(View view) {
        Intent intent = new Intent(this, HomeScreen.class);
        startActivity(intent);
    }
    public void startAcknowledgements(View view) {
        Intent intent = new Intent(this, AppAcknowledgements.class);
        startActivity(intent);
    }
}
