package edu.neu.madcourse.sanatchugh;

/**
 * Created by sanatchugh on 3/4/16.
 */
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;

public class PuzzleView extends View {
    private final Game game;

    private float width; // width of one tile
    private float height; // height of one tile
    private int selX; // X index of selection
    private int selY; // Y index of selection
    private final Rect selRect = new Rect();

    public PuzzleView(Context context) {
        super(context);
        this.game = (Game) context;
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = w / 9f;
        height = h / 9f;
        getRect(selX, selY, selRect);
        super.onSizeChanged(w, h, oldw, oldh);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        // Draw the background...
        Paint background = new Paint();
        background.setColor(getResources().getColor(R.color.puzzle_background));
        canvas.drawRect(0, 0, getWidth(), getHeight()/2, background);


        // Draw the board...

        // Define colors for the grid lines
        Paint dark = new Paint();
        dark.setColor(getResources().getColor(R.color.puzzle_dark));
        Paint hilite = new Paint();
        hilite.setColor(getResources().getColor(R.color.puzzle_hilite));
        Paint light = new Paint();
        light.setColor(getResources().getColor(R.color.puzzle_light));
        // Draw the minor grid lines
        for (int i = 0; i < 9; i++) {
            canvas.drawLine(0, i * height/2, getWidth(), i * height/2,light);
            canvas.drawLine(0, i * height/2 + 1, getWidth(), i * height/2 + 1, hilite);
            canvas.drawLine(i * width, 0, i * width, getHeight()/2, light);
            canvas.drawLine(i * width + 1, 0, i * width + 1, getHeight()/2, hilite);
        }
        // Draw the major grid lines
        for (int i = 0; i < 9; i++) {
            if (i % 3 != 0)
                continue;
            canvas.drawLine(0, i * height/2, getWidth(), i * height/2, dark);
            canvas.drawLine(0, i * height/2 + 1, getWidth(), i * height/2 + 1, hilite);
            canvas.drawLine(i * width, 0, i * width, getHeight()/2, dark);
            canvas.drawLine(i * width + 1, 0, i * width + 1, getHeight()/2, hilite);
        }
    }


    private void getRect(int x, int y, Rect rect) {
        rect.set((int) (x * width), (int) (y * height), (int) (x
                * width + width), (int) (y * height + height));
    }

}