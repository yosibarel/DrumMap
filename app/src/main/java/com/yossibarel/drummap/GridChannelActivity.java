package com.yossibarel.drummap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import views.Custom.MainGridControlerView;

public class GridChannelActivity extends AppCompatActivity {

    private MainGridControlerView mGridController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_grid_channel);
        mGridController = (MainGridControlerView) findViewById(R.id.gridChannelControler);
        DrumMapJni mDrumMap=DrumMapJni.getInstance();
        mGridController.setBpm(mDrumMap.getBpm());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGridController.startLoopThread();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGridController.stopLoopThread();
    }
}
