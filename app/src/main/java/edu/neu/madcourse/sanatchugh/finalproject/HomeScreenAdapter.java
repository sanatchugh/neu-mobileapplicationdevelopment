package edu.neu.madcourse.joeyhuang.finalproject;

import edu.neu.madcourse.joeyhuang.R;
import java.util.ArrayList;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeScreenAdapter extends ArrayAdapter<HomeScreenModel> {

    private final Context context;
    private final ArrayList<HomeScreenModel> modelsArrayList;

    public HomeScreenAdapter(Context context, ArrayList<HomeScreenModel> modelsArrayList) {

        super(context, R.layout.mainscreen_list_item, modelsArrayList);

        this.context = context;
        this.modelsArrayList = modelsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater

        View rowView = null;
        if(!modelsArrayList.get(position).isGroupHeader()){
            rowView = inflater.inflate(R.layout.mainscreen_list_item, parent, false);

            // 3. Get icon,title & counter views from the rowView
            ImageView imgView = (ImageView) rowView.findViewById(R.id.item_icon);
            TextView titleView = (TextView) rowView.findViewById(R.id.item_title);
            TextView counterView = (TextView) rowView.findViewById(R.id.item_counter);

            // 4. Set the text for textView
            imgView.setImageResource(modelsArrayList.get(position).getIcon());
            titleView.setText(modelsArrayList.get(position).getTitle());
            counterView.setText(modelsArrayList.get(position).getCounter());

            if(Integer.parseInt(modelsArrayList.get(position).getCounter()) <= 2){
                counterView.setBackgroundColor(Color.rgb(255,99,71));
            }
            else if(Integer.parseInt(modelsArrayList.get(position).getCounter()) >= 2 && Integer.parseInt(modelsArrayList.get(position).getCounter()) <= 5){
                counterView.setBackgroundColor(Color.rgb(255,255,102));
            }

        }
        else{
//            rowView = inflater.inflate(R.layout.mainscreen_list_header, parent, false);
//            TextView titleView = (TextView) rowView.findViewById(R.id.header);
//            titleView.setText(modelsArrayList.get(position).getTitle());

        }

        return rowView;
    }
}