package edu.neu.madcourse.joeyhuang.finalproject;

import android.app.Activity;
import android.view.View;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import edu.neu.madcourse.joeyhuang.R;


public class HandleShowcase extends Activity implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        Target moneySaved = new ViewTarget(R.id.moneysavedamount, this);
        ShowcaseView showcaseView =  new ShowcaseView.Builder(this, false)
                .setOnClickListener(nextShowcase())
                .setTarget(moneySaved)
                .setContentTitle("Savings")
                .setContentText("This shows you how much money you save in Month :)")
                .setStyle(1)
                .build();
    }

    private View.OnClickListener nextShowcase() {
        Target moneySaved = new ViewTarget(R.id.wastepreventedamount, this);
        ShowcaseView showcaseView =  new ShowcaseView.Builder(this, false)
                .setOnClickListener(nextShowcase2())
                .setTarget(moneySaved)
                .setContentTitle("Waste Prevented")
                .setContentText("This shows you how much waste you prevented in Month ;)")
                .setStyle(1)
                .build();
        return null;
    }

    private View.OnClickListener nextShowcase2() {
        Target moneySaved = new ViewTarget(R.id.add_button, this);
        ShowcaseView showcaseView =  new ShowcaseView.Builder(this, false)
                .setTarget(moneySaved)
                .setContentTitle("Add Food")
                .setContentText("Here you can add the new food you get :D")
                .setStyle(1)
                .build();
        return null;
    }

}
