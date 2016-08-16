package com.yossibarel.drummap;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import Utils.Extras;
import views.fragments.FxFragment;

public class SendFxActivity extends AppCompatActivity {


    private Button mBtnSend1;
    private Button mBtnSend2;
    private FxFragment mFxFragment1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_send_fx);
        mBtnSend1 = (Button) findViewById(R.id.btnSend1);

        mFxFragment1 = new FxFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Extras.EXTRA_CHANNEL_INDEX, -1);
        mFxFragment1.setArguments(bundle);
replace(mFxFragment1);



    }

    private void replace(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.content, fragment).commit();
    }
}
