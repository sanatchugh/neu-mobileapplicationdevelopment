package edu.neu.madcourse.sanatchugh;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import static android.provider.BaseColumns._ID;

public class Dictionary2 extends AppCompatActivity {

    // List view
    private ListView lv;
    DictionaryDatabase mydb;

    // Listview Adapter
    ArrayAdapter<String> adapter;
    // Search EditText
    EditText inputSearch;
    HashMap hmap = new HashMap<String, ArrayList<String>>();

    // The name of the file to open.
    String fileName = "res/raw/wordlist1.txt";

    // This will reference one line at a time
    String line = null;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictonary);
        mydb=new DictionaryDatabase(this);

        //Clear Text
        Button clear_it = (Button) findViewById(R.id.clear_text);
        clear_it.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                inputSearch.setText("");
            }
        });

        //Acknowledgements
        Button ack = (Button) findViewById(R.id.acknowledge);
        ack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                inputSearch.setText("");
            }
        });

        //Return to Menu
        Button retm = (Button) findViewById(R.id.returnback);
        retm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }
        });


        try {
            InputStream is = getResources().openRawResource(R.raw.wordlist2);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            while ((line = reader.readLine()) != null) {
                mydb.insertWord(line);
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

        String ret = "";
        final List<String> results=new ArrayList<String>();
//        final List<String> test=new ArrayList<String>();
//        test.add("please");
//        test.add("plea");
//        test.add("work");


        // Listview Data
        String products[] = {""};

        lv = (ListView) findViewById(R.id.list_view);
        inputSearch = (EditText) findViewById(R.id.word_Search);


        // Adding items to listview
        adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.word_value, results);
        lv.setAdapter(adapter);

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Dictonary.this.adapter.getFilter().filter(s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Dictonary.this.adapter.getFilter().filter(s);
                if (s.length() > 2){
                    String test = mydb.getWord(s.toString());
                    if(test != null)
                    {
                        results.add(test);
                        adapter.notifyDataSetChanged();
                    }

//                    if(hmap.containsKey(s.toString().substring(0,3)))
//                    {
//                        List<String> hmapList=(List<String>) hmap.get(s.toString().substring(0,3));
//                        if(hmapList.contains(s.toString()))
//                        {
//                            if(results.contains(s.toString()))
//                            {
//
//                            }
//                            else {
//                                results.add(s.toString());
//                                adapter.notifyDataSetChanged();
//                            }
//                        }
//                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Dictonary.this.adapter.getFilter().filter(s);
            }
        });

    }

}