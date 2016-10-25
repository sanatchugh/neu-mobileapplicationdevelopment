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

public class Dictonary extends AppCompatActivity {

    // List view
    private ListView lv;

    // Listview Adapter
    ArrayAdapter<String> adapter;
    // Search EditText
    EditText inputSearch;
    HashMap hmap = new HashMap<String, ArrayList<String>>();

    // The name of the file to open.
    String fileName = "res/raw/wordlist1.txt";

    // This will reference one line at a time
    String line = null;

    public static final String TABLE_NAME = "events";
    public static final String TIME = "time";
    public static final String TITLE = "title";
    private static String[] FROM = { _ID, TIME, TITLE, };
    private static String ORDER_BY = TIME + " DESC";
    private DictionaryDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictonary);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
            // FileReader reads text files in the default encoding.
            //FileReader fileReader = new FileReader(fileName);
            InputStream is = getResources().openRawResource(R.raw.wordlist2);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            // Always wrap FileReader in BufferedReader.
            //BufferedReader bufferedReader = new BufferedReader(fileReader);
            while ((line = reader.readLine()) != null) {
                Object obj = line.substring(0,3);
                if ((hmap.containsKey(obj))){
                    ArrayList<String> where =new ArrayList<String>();
                    where= (ArrayList<String>) hmap.get(obj);
                    where.add(line);
                    hmap.put(line.substring(0,3),where);
                }
                else {
                    ArrayList<String> where =new ArrayList<String>();
                    where.add(line);
                    hmap.put(line.substring(0,3), where);
                }
            }

            // Always close files.
            //bufferedReader.close();
            reader.close();
        } catch (FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            fileName + ex);
        } catch (IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + fileName + ex);
            // Or we could just do this:
            // ex.printStackTrace();
        }

        String ret = "";
        final List<String> results=new ArrayList<String>();
        final List<String> test=new ArrayList<String>();
        test.add("please");
        test.add("plea");
        test.add("work");


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
                    if(test.contains(s.toString()))
                    {
                        if(results.contains(s.toString()))
                        {

                        }
                        else {
                            results.add(s.toString());
                            adapter.notifyDataSetChanged();
                        }
                    }

                    if(hmap.containsKey(s.toString().substring(0,3)))
                    {
                        List<String> hmapList=(List<String>) hmap.get(s.toString().substring(0,3));
                        if(hmapList.contains(s.toString()))
                        {
                            if(results.contains(s.toString()))
                            {

                            }
                            else {
                                results.add(s.toString());
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
//                    if(hmap.containsKey(s.subSequence(0, 3))){
//                        ArrayList<String> subresults = (ArrayList<String>) hmap.get(s.subSequence(0, 3));
//                        if(subresults.contains(s)) {
//                            results.add((String) s);
//                            adapter.notifyDataSetChanged();
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