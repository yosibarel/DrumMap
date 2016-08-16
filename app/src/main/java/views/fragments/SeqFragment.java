package views.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.yossibarel.drummap.DrumMapJni;
import com.yossibarel.drummap.R;

import java.util.ArrayList;

import Utils.Extras;
import Utils.TextFormaterCreator;
import interfaces.TextValueFormater;
import views.Custom.VIewSeqPattern;

/**
 * Created by yossibarel on 12/04/16.
 */
public class SeqFragment extends Fragment implements VIewSeqPattern.OnValueChangeListener {


    private int mIndexChannel;
    private DrumMapJni mDrumMap;
    ArrayList<VIewSeqPattern> mPatterns = new ArrayList<>();
    private int mIndexValue = 0;
    static final String[] StringArrRate = {"1/32", "1/16", "1/8", "1/4", "1/2", "1/1"};
    static final double[] DoubleArrRate = {4.0, 2.0, 1.0, 0.5, 0.25};
    private LinearLayout contentPatterns;
    ArrayList<TextValueFormater> mFormaters;
    private int mMaxIndex;
    private boolean mIsStart;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sequencer, container, false);

        mIndexChannel = getArguments().getInt(Extras.EXTRA_CHANNEL_INDEX, -1);
        mDrumMap = DrumMapJni.getInstance();
        mFormaters = TextFormaterCreator.getCollection();
        contentPatterns = (LinearLayout) view.findViewById(R.id.contentPatterns);
        RadioGroup rbValueType = (RadioGroup) view.findViewById(R.id.gbValueType);
        final ToggleButton btnEbableSeq = (ToggleButton) view.findViewById(R.id.btnEnableSeq);
        final TextView tvRate = (TextView) view.findViewById(R.id.tvRate);
        SeekBar sbRate = (SeekBar) view.findViewById(R.id.sbRate);
        Button btnResetSeq = (Button) view.findViewById(R.id.btnResetSeq);
        Button btnRandSeq = (Button) view.findViewById(R.id.btnRandSeq);

        mMaxIndex = mDrumMap.getSeqIndexEnable(mIndexChannel);
        mPatterns.clear();
        for (int i = 0; i < contentPatterns.getChildCount(); i++) {
            VIewSeqPattern pattern = (VIewSeqPattern) contentPatterns.getChildAt(i);
            pattern.setIndex(i);
            pattern.setOnValueChangeListener(this);

            pattern.setEnable(i <= mMaxIndex);
            mPatterns.add(pattern);
        }
        updateECheckedPattern();
        updatePattern();
        for (int i = 0; i < rbValueType.getChildCount(); i++) {
            RadioButton rb = (RadioButton) rbValueType.getChildAt(i);
            rb.setTag(i);
        }
        rbValueType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                mIndexValue = (int) group.findViewById(group.getCheckedRadioButtonId()).getTag();
                updatePattern();
            }
        });

        btnEbableSeq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDrumMap.enableSequencer(mIndexChannel, btnEbableSeq.isChecked());
            }
        });
        int retProgress = mDrumMap.getSeqRateProgress(mIndexChannel);
        sbRate.setProgress(retProgress);
        tvRate.setText(StringArrRate[retProgress]);
        sbRate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                tvRate.setText(StringArrRate[progress]);
                if (fromUser)
                    mDrumMap.setSeqRate(mIndexChannel, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        btnResetSeq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDrumMap.resetSeq(mIndexChannel, mIndexValue);
                updatePattern();
            }
        });
        btnRandSeq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDrumMap.randSeq(mIndexChannel, mIndexValue);
                updatePattern();
            }
        });
        createControlStep(view);
        createSeqState(view);

        return view;
    }

    @Override
    public void onStart() {
        mIsStart = true;
        threadIndexPlaying = new Thread(new Runnable() {
            @Override
            public void run() {
                while (mIsStart) {
                    try {

                        final int currentPlaying = mDrumMap.getSeqIndexPlaying(mIndexChannel);
                        if (currentPlaying != lastIndexPlaying || mMaxIndex == 0) {
                            contentPatterns.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (!isAdded())
                                        return;
                                    mPatterns.get(currentPlaying).setIsPlaying(true);
                                    if (lastIndexPlaying != -1 && lastIndexPlaying != currentPlaying)
                                        mPatterns.get(lastIndexPlaying).setIsPlaying(false);
                                    lastIndexPlaying = currentPlaying;

                                }
                            });
                        }


                        Thread.sleep(50);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        threadIndexPlaying.start();
        super.onStart();
    }

    @Override
    public void onStop() {
        mIsStart = false;
        if (!threadIndexPlaying.isInterrupted())
            threadIndexPlaying.interrupt();
        super.onStop();
    }

    void updateECheckedPattern() {
        for (int i = 0; i < contentPatterns.getChildCount(); i++) {

            VIewSeqPattern pattern = (VIewSeqPattern) contentPatterns.getChildAt(i);
            pattern.setEnablePattern(mDrumMap.getSeqPattern(mIndexChannel, i, -1));

        }
    }


    void updatePattern() {
        for (int i = 0; i < contentPatterns.getChildCount(); i++) {

            VIewSeqPattern pattern = (VIewSeqPattern) contentPatterns.getChildAt(i);
            pattern.setTextValueFormater(mFormaters.get(mIndexValue));
            pattern.setValue(mDrumMap.getSeqPattern(mIndexChannel, i, mIndexValue));

        }
    }

    @Override
    public void onValueChange(int indexPattern, double val) {
        mDrumMap.setValueSequencerPattern(mIndexChannel, indexPattern, mIndexValue, val);
    }

    @Override
    public void onActiveChange(int indexPattern, boolean enable) {
        mDrumMap.enableSequencerPattern(mIndexChannel, indexPattern, enable);
    }

    @Override
    public void onEnableClick(int index) {
        mDrumMap.setSeqIndexEnable(mIndexChannel, index);
        for (int i = 0; i < mPatterns.size(); ++i) {
            mMaxIndex = index;
            mPatterns.get(i).setEnable(i <= index);
        }
    }

    int lastIndexPlaying = -1;
    Thread threadIndexPlaying;

    private void createControlStep(View view) {
        RadioGroup rgSteps = (RadioGroup) view.findViewById(R.id.rgSteps);
        int currentStepState = mDrumMap.getSeqStepState(mIndexChannel);
        for (int i = 0; i < rgSteps.getChildCount(); i++) {
            RadioButton rb = (RadioButton) rgSteps.getChildAt(i);

            rb.setTag(i);
            if (i == currentStepState) {
                rb.setChecked(true);

            }

        }
        rgSteps.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int stepState = (int) group.findViewById(group.getCheckedRadioButtonId()).getTag();
                mDrumMap.setSeqStepState(stepState, mIndexChannel);
            }
        });

    }

    private void createSeqState(View view) {
        RadioGroup rgSteps = (RadioGroup) view.findViewById(R.id.rgSeqState);
        int currentState = mDrumMap.getSeqState(mIndexChannel);
        for (int i = 0; i < rgSteps.getChildCount(); i++) {
            RadioButton rb = (RadioButton) rgSteps.getChildAt(i);

            rb.setTag(i);
            if (i == currentState) {
                rb.setChecked(true);

            }

        }
        rgSteps.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int state = (int) group.findViewById(group.getCheckedRadioButtonId()).getTag();
                mDrumMap.setSeqState(state, mIndexChannel);
            }
        });

    }

}
