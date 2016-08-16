package com.yossibarel.drummap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.util.ArrayList;

import views.Custom.MixerViewItem;

public class MixerActivity extends AppCompatActivity implements MixerViewItem.MixerViewItemListener {

    ArrayList<MixerViewItem> viewChannelsMixer = new ArrayList<>();
    private DrumMapJni drumMap;
    private boolean mIsActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_mixer);
        LinearLayout channelContent = (LinearLayout) findViewById(R.id.channelContent);
        drumMap = DrumMapJni.getInstance();
        boolean isSoloState = false;
        for (int i = 0; i < 16; i++) {
            MixerViewItem mv = new MixerViewItem(this, drumMap, i);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int) Utils.SizeCalc.pxFromDp(this, 112), LinearLayout.LayoutParams.MATCH_PARENT);
            mv.setLayoutParams(lp);
            viewChannelsMixer.add(mv);
            channelContent.addView(mv);
            mv.setListener(this);
            isSoloState = isSoloState || mv.isSolo();
        }
        if (isSoloState)
            onSoloChannel(-1, true);
    }

    @Override
    protected void onStart() {
        for (int i = 0; i < 16; i++) {
            viewChannelsMixer.get(i).updateParam();
        }
        mIsActive = true;
        threadViewMeter = new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    float[] vmParam = drumMap.getAllViewMeters();
                    for (int i = 0; i < 16; i++) {
                        viewChannelsMixer.get(i).updateViewMeter(vmParam[i * 2], vmParam[i * 2 + 1]);
                        Log.d("viewMeter", "L =" + vmParam[i * 2] + " R = " + vmParam[i * 2 + 1]);
                    }

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (mIsActive);
            }
        });

        threadViewMeter.start();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mIsActive = false;
        super.onStop();
    }

    Thread threadViewMeter;

    @Override
    public void onSoloChannel(int channel, boolean isSolo) {
        for (MixerViewItem mv : viewChannelsMixer) {
            mv.updateSolo(isSolo);
        }
    }

    @Override
    public void onMuteChannel(int channel, boolean isMute) {
        for (MixerViewItem mv : viewChannelsMixer) {
            mv.updateMute();
        }
    }
}
