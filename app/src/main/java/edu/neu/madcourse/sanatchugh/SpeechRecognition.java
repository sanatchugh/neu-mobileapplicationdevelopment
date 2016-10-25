package edu.neu.madcourse.sanatchugh;

/**
 * Created by sanatchugh on 4/10/16.
 */
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.PatternSyntaxException;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class SpeechRecognition extends Activity {

    private TextView txtSpeechInput;
    private TextView speech_itemname;
    private TextView speech_price;
    private TextView speech_weight;
    private ImageButton btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speech_layout);

        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        speech_itemname = (TextView) findViewById(R.id.speech_itemname);
        speech_price = (TextView) findViewById(R.id.speech_price);
        speech_weight = (TextView) findViewById(R.id.speech_weight);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);

        // hide the action bar
        //getActionBar().hide();

        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

    }

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    txtSpeechInput.setText(result.get(0));
                    String test=result.get(0);
                    String[] splitArray=new String[100];
                    try {
                        splitArray = test.split("\\s+");
                    } catch (PatternSyntaxException ex) {
                        //
                    }
                    speech_itemname.setText(splitArray[1]);
//                    speech_itemname.setText();
//                    if(result.get(2)=="$")
//                    {
//                        speech_price.setText(result.get(3));
//                    }
//                    if(result.get(6)=="pounds" || result.get(6)=="ounces"){
//                        speech_weight.setText(result.get(5));
//                    }
                }
                break;
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}