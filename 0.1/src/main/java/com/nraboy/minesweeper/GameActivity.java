package com.nraboy.minesweeper;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;

public class GameActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        GameView gv = new GameView(this);
        gv.setMetrics(metrics);

        setContentView(gv);  // Set the view to display content from our GameView SurfaceView
    }

}
