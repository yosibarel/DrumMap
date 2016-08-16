package com.yossibarel.drummap;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import Utils.Extras;
import views.fragments.EditWaveFragment;
import views.fragments.FxFragment;
import views.fragments.LfoFragment;
import views.fragments.SeqFragment;
import views.fragments.SynthFragment;


public class EditChannelActivity extends AppCompatActivity implements SynthFragment.OnEditWaveSelectedListener, SynthFragment.OnChannelSelectedListener {

    private int mChannelIndex;

    private DrumMapJni drumMap;


    FragmentManager mFragmentManager;
    private Button btnLfoSynth;
    private Button btnFileSynth;
    private Button btnSeq;
    private Button btnFxSynth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_edit_channel);
        mChannelIndex = getIntent().getIntExtra(Extras.EXTRA_CHANNEL_INDEX, -1);
        drumMap = DrumMapJni.getInstance();


        mFragmentManager = getSupportFragmentManager();
        SynthFragment fragment = new SynthFragment();
        fragment.setOnChannelSelectedListener(this);
        fragment.setOnEditWaveSelectedListener(this);
        fragment.setArguments(getIntent().getExtras());
        mCurrentFragment = fragment;
        mFragmentManager.beginTransaction().add(R.id.fragmentContent, fragment, fragment.getClass().getSimpleName()).commit();
        btnLfoSynth = (Button) findViewById(R.id.btnOscSynth);
        btnFileSynth = (Button) findViewById(R.id.btnFileSynth);
        btnSeq = (Button) findViewById(R.id.btnSeq);
        btnFxSynth = (Button) findViewById(R.id.btnFxSynth);
        btnSeq.setOnClickListener(synthClickSelected);
        btnLfoSynth.setOnClickListener(synthClickSelected);
        btnFileSynth.setOnClickListener(synthClickSelected);
        btnFxSynth.setOnClickListener(synthClickSelected);
        btnFileSynth.setEnabled(false);
        //getFragmentManager().beginTransaction().replace(R.id.fragmentContent, new MyPreferenceFragment()).commit();

    }


    Fragment mCurrentFragment;
    View.OnClickListener synthClickSelected = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCurrentFragment = new LfoFragment();

            switch (v.getId()) {
                case R.id.btnOscSynth:
                    btnLfoSynth.setEnabled(false);
                    btnFxSynth.setEnabled(true);
                    btnFileSynth.setEnabled(true);
                    mCurrentFragment = new LfoFragment();
                    btnSeq.setEnabled(true);
                    break;

                case R.id.btnFileSynth:
                    btnFileSynth.setEnabled(false);
                    btnFxSynth.setEnabled(true);
                    btnLfoSynth.setEnabled(true);
                    btnSeq.setEnabled(true);
                    mCurrentFragment = new SynthFragment();
                    ((SynthFragment) mCurrentFragment).setOnEditWaveSelectedListener(EditChannelActivity.this);
                    ((SynthFragment) mCurrentFragment).setOnChannelSelectedListener(EditChannelActivity.this);

                    break;
                case R.id.btnSeq:
                    btnFileSynth.setEnabled(true);
                    btnFxSynth.setEnabled(true);
                    btnLfoSynth.setEnabled(true);
                    btnSeq.setEnabled(false);
                    mCurrentFragment = new SeqFragment();
                    break;
                case R.id.btnFxSynth:
                    btnFileSynth.setEnabled(true);
                    btnFxSynth.setEnabled(false);
                    btnLfoSynth.setEnabled(true);
                    btnSeq.setEnabled(true);
                    mCurrentFragment = new FxFragment();
                    break;
            }

            mCurrentFragment.setArguments(getIntent().getExtras());
           /* Fragment f = null;
            if ((f = mFragmentManager.findFragmentByTag(fragment.getClass().getSimpleName())) != null) {
                mFragmentManager.beginTransaction().replace(R.id.fragmentContent, f, fragment.getClass().getSimpleName()).commit();


            } else {
                mFragmentManager.beginTransaction().replace(R.id.fragmentContent, fragment).addToBackStack(fragment.getClass().getSimpleName()).commit();

            }
*/
            replaceFragment(mCurrentFragment);

        }
    };

    private void replaceFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getName();

        FragmentManager manager = getSupportFragmentManager();
        // boolean fragmentPopped = manager.popBackStackImmediate(backStateName, FragmentManager.POP_BACK_STACK_INCLUSIVE);


        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.fragmentContent, fragment);
        // ft.addToBackStack(backStateName);
        ft.commit();

    }

    private void clearFragmentManager() {
        int backStackCount = mFragmentManager.getBackStackEntryCount();
        for (int i = 0; i < backStackCount; i++) {

            // Get the back stack fragment id.
            int backStackId = getSupportFragmentManager().getBackStackEntryAt(i).getId();

            mFragmentManager.popBackStack(backStackId, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        }
    }

    @Override
    public void onBackPressed() {

        if (mCurrentFragment != null && mCurrentFragment instanceof SynthFragment) {
            if (((SynthFragment) mCurrentFragment).onBackPress())
                return;

        }
        if (mFragmentManager.getBackStackEntryCount() > 1) {
            mFragmentManager.popBackStack();
        } else
            super.onBackPressed();
    }

    @Override
    public void onEditWaveSelectedListener(String filePath, int indexChannel) {

        btnFileSynth.setEnabled(true);
        EditWaveFragment editWaveFragmentDialog = new EditWaveFragment();
        Bundle bandle = new Bundle();
        bandle.putInt(Extras.EXTRA_CHANNEL_INDEX, indexChannel);
        bandle.putString(Extras.EXTRA_FILE_PATH, filePath);
        editWaveFragmentDialog.setArguments(bandle);
        // mFragmentManager.beginTransaction().replace(R.id.fragmentContent, editWaveFragmentDialog, editWaveFragmentDialog.getClass().getSimpleName()).commit();
        replaceFragment(editWaveFragmentDialog);
    }

    @Override
    public void onChannelSelected(int channel) {

        clearFragmentManager();
        getIntent().putExtra(Extras.EXTRA_CHANNEL_INDEX, channel);
        SynthFragment fragment = new SynthFragment();
        fragment.setOnEditWaveSelectedListener(EditChannelActivity.this);
        fragment.setOnChannelSelectedListener(EditChannelActivity.this);
        fragment.setArguments(getIntent().getExtras());

        FragmentTransaction ft = mFragmentManager.beginTransaction();
        if (channel > mChannelIndex) {
            ft.setCustomAnimations(R.anim.anim_right_in, R.anim.anim_left_out);
        } else {
            ft.setCustomAnimations(R.anim.anim_left_in, R.anim.anim_right_out);

        }

        ft.replace(R.id.fragmentContent, fragment, fragment.getClass().getSimpleName());
        ft.commit();
        mChannelIndex = channel;
        Log.d("channel", mChannelIndex + "");
    }

}
