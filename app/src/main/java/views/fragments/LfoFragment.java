package views.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.yossibarel.drummap.DrumMapJni;
import com.yossibarel.drummap.R;

import Utils.Extras;
import enums.WaveType;
import interfaces.CloseAnimationListener;
import interfaces.AnimtionListener;
import views.Custom.LinearLayoutTouched;
import views.Custom.RelativeLayoutTouced;
import views.Custom.WaveFormView;

/**
 * Created by yossibarel on 03/04/16.
 */
public class LfoFragment extends Fragment implements WaveFormView.OnDrawListener, AnimtionListener {

    static final int LFO_1 = 0;
    static final int LFO_2 = 1;
    private int mChannelIndex;
    private WaveFormView waveView1;
    private WaveFormView waveView2;
    private DrumMapJni drumMap;
    private WaveFormView waveViewMain;
    private SeekBar sbPresent;
    private WaveFormView[] lfoViewArray = new WaveFormView[2];
    private SeekBar[] sbRateArray = new SeekBar[2];
    private SeekBar[] sbAmpArray = new SeekBar[2];
    TextView[] tvArray = new TextView[2];
    public static final String[] RATE_BEATS = {"1/16", "1/8", "1/4", "1/2", "1/1", "2/1", "4/1", "8/1", "16/1"};
    private CloseAnimationListener mCurrentOpenLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_synth_lfo, container, false);
        mChannelIndex = getArguments().getInt(Extras.EXTRA_CHANNEL_INDEX, -1);
        drumMap = DrumMapJni.getInstance();
        waveView1 = (WaveFormView) view.findViewById(R.id.waveView1);
        waveView2 = (WaveFormView) view.findViewById(R.id.waveView2);
        waveViewMain = (WaveFormView) view.findViewById(R.id.waveViewMain);
        waveView1.setOnDrawListener(this);
        waveView2.setOnDrawListener(this);


        SeekBar sbRateWave1 = (SeekBar) view.findViewById(R.id.sbRateWave1);
        SeekBar sbRateWave2 = (SeekBar) view.findViewById(R.id.sbRateWave2);

        final TextView tvRate1 = (TextView) view.findViewById(R.id.tvRate1);
        final TextView tvRate2 = (TextView) view.findViewById(R.id.tvRate2);

        SeekBar sbAmplitude1 = (SeekBar) view.findViewById(R.id.sbAmplitude1);
        SeekBar sbAmplitude2 = (SeekBar) view.findViewById(R.id.sbAmplitude2);

        sbPresent = (SeekBar) view.findViewById(R.id.sbPresent);

        sbAmpArray[LFO_1] = sbAmplitude1;
        sbAmpArray[LFO_2] = sbAmplitude2;
        tvArray[LFO_1] = tvRate1;
        tvArray[LFO_2] = tvRate2;
        sbRateArray[LFO_1] = sbRateWave1;
        sbRateArray[LFO_2] = sbRateWave2;
        lfoViewArray[LFO_1] = waveView1;
        lfoViewArray[LFO_2] = waveView2;
        updateLfo(LFO_1);
        updateLfo(LFO_2);
        Button btnSinus1 = (Button) view.findViewById(R.id.btnSinus1);
        Button btnSaw1 = (Button) view.findViewById(R.id.btnSaw1);
        Button btnRect1 = (Button) view.findViewById(R.id.btnRect1);
        Button btnTriangle1 = (Button) view.findViewById(R.id.btnTriangle1);

        Button btnSinus2 = (Button) view.findViewById(R.id.btnSinus2);
        Button btnSaw2 = (Button) view.findViewById(R.id.btnSaw2);
        Button btnRect2 = (Button) view.findViewById(R.id.btnRect2);
        Button btnTriangle2 = (Button) view.findViewById(R.id.btnTriangle2);

        SeekBar sbEnvA = (SeekBar) view.findViewById(R.id.sbEnvA);
        SeekBar sbEnvD = (SeekBar) view.findViewById(R.id.sbEnvD);
        SeekBar sbEnvS = (SeekBar) view.findViewById(R.id.sbEnvS);
        SeekBar sbEnvR = (SeekBar) view.findViewById(R.id.sbEnvR);
        sbEnvA.setProgress((int) (drumMap.getEnvADSR(1, mChannelIndex, 0) * 100));
        sbEnvD.setProgress((int) (drumMap.getEnvADSR(1, mChannelIndex, 1) * 100));
        sbEnvS.setProgress((int) (drumMap.getEnvADSR(1, mChannelIndex, 2) * 100));
        sbEnvR.setProgress((int) (drumMap.getEnvADSR(1, mChannelIndex, 3) * 100));
        sbEnvA.setOnSeekBarChangeListener(sbADSRListener);
        sbEnvD.setOnSeekBarChangeListener(sbADSRListener);
        sbEnvS.setOnSeekBarChangeListener(sbADSRListener);
        sbEnvR.setOnSeekBarChangeListener(sbADSRListener);


        sbRateWave1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double rate = ((double) progress) / 100.0;

                if (fromUser)
                    drumMap.setLFORateWave(mChannelIndex, LFO_1, rate);
                waveView1.setRate(16.0 / Math.pow(2, (double) (int) (rate * 8.0)));
                waveView1.invalidate();

                tvRate1.setText(RATE_BEATS[(int) (rate * 8.0)]);


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sbRateWave2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double rate = ((double) progress) / 100.0;


                if (fromUser)
                    drumMap.setLFORateWave(mChannelIndex, LFO_2, rate);
                waveView2.setRate(16.0 / Math.pow(2, (double) (int) (rate * 8.0)));
                waveView2.invalidate();

                tvRate2.setText(RATE_BEATS[(int) (rate * 8.0)]);


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sbPresent.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                drumMap.setLFOPresentWave(mChannelIndex, (double) sbPresent.getProgress() / 100.0);
                changeMainWaveForm();

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sbAmplitude1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                double amp = (double) (progress - 100) / 100.0;
                drumMap.setLFOAmplitudeWave(mChannelIndex, LFO_1, amp);
                waveView1.setAmplitude(amp);
                waveView1.invalidate();


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sbAmplitude2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double amp = (double) (progress - 100) / 100.0;
                drumMap.setLFOAmplitudeWave(mChannelIndex, LFO_2, amp);
                waveView2.setAmplitude(amp);
                waveView2.invalidate();


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btnSinus1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drumMap.setLFOTypeWave(mChannelIndex, LFO_1, 0);
                waveView1.setWaveType(WaveType.SINUS);
                waveView1.invalidate();

            }
        });
        btnSaw1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drumMap.setLFOTypeWave(mChannelIndex, LFO_1, 1);
                waveView1.setWaveType(WaveType.SAW);
                waveView1.invalidate();

            }
        });
        btnRect1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drumMap.setLFOTypeWave(mChannelIndex, LFO_1, 2);
                waveView1.setWaveType(WaveType.RECTANGLE);
                waveView1.invalidate();

            }
        });
        btnTriangle1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drumMap.setLFOTypeWave(mChannelIndex, LFO_1, 3);
                waveView1.setWaveType(WaveType.TRIANGLE);
                waveView1.invalidate();

            }
        });


        btnSinus2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drumMap.setLFOTypeWave(mChannelIndex, LFO_2, 0);
                waveView2.setWaveType(WaveType.SINUS);
                waveView2.invalidate();

            }
        });
        btnSaw2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drumMap.setLFOTypeWave(mChannelIndex, LFO_2, 1);
                waveView2.setWaveType(WaveType.SAW);
                waveView2.invalidate();

            }
        });
        btnRect2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drumMap.setLFOTypeWave(mChannelIndex, LFO_2, 2);
                waveView2.setWaveType(WaveType.RECTANGLE);
                waveView2.invalidate();

            }
        });
        btnTriangle2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drumMap.setLFOTypeWave(mChannelIndex, LFO_2, 3);
                waveView2.setWaveType(WaveType.TRIANGLE);
                waveView2.invalidate();

            }
        });
        createResizeUiManager(view);

        return view;
    }

    void changeMainWaveForm() {
        if (waveView1.getWave() == null || waveView2.getWave() == null)
            return;
        waveViewMain.setWave(sum2Waves(waveView1.getWave(), waveView2.getWave(), (double) sbPresent.getProgress() / 100.0));
        waveViewMain.invalidate();
    }

    double[] sum2Waves(double[] wave1, double[] wave2, double presnet1) {
        double[] res = new double[wave1.length];
        for (int i = 0; i < wave1.length; i++) {
            res[i] = wave1[i] * presnet1 + wave2[i] * (1.0 - presnet1);
        }
        return res;
    }

    @Override
    public void onDraw() {
        changeMainWaveForm();
    }

    SeekBar.OnSeekBarChangeListener sbADSRListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            int type = 0;
            switch (seekBar.getId()) {

                case R.id.sbEnvA:
                    type = 0;
                    break;
                case R.id.sbEnvD:
                    type = 1;
                    break;
                case R.id.sbEnvS:
                    type = 2;
                    break;
                case R.id.sbEnvR:
                    type = 3;
                    break;
            }
            drumMap.setEnvADSR(1, mChannelIndex, type, (double) (progress + 1) / 101.0);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    void updateLfo(int lfoType) {
        double present = drumMap.getLfoWavePresent(mChannelIndex);
        double amp = drumMap.getLFOAmplitudeWave(mChannelIndex, lfoType);
        lfoViewArray[lfoType].setAmplitude(amp);
        double rate = drumMap.getLFORateWave(mChannelIndex, lfoType);

        tvArray[lfoType].setText(RATE_BEATS[(int) (rate * 8.0)]);
        lfoViewArray[lfoType].setRate(16.0 / Math.pow(2, (double) (int) (rate * 8.0)));
        sbAmpArray[lfoType].setProgress((int) ((amp + 1.0) * 100));
        sbPresent.setProgress((int) (present * 100.0));


        int wave = drumMap.getLFOTypeWave(mChannelIndex, lfoType);
        switch (wave) {
            case 0:
                lfoViewArray[lfoType].setWaveType(WaveType.SINUS);
                break;
            case 1:
                lfoViewArray[lfoType].setWaveType(WaveType.SAW);
                break;
            case 2:
                lfoViewArray[lfoType].setWaveType(WaveType.RECTANGLE);
                break;
            case 3:
                lfoViewArray[lfoType].setWaveType(WaveType.TRIANGLE);
                break;
        }
        sbRateArray[lfoType].setProgress((int) (rate * 100.0));
        lfoViewArray[lfoType].invalidate();

    }

    private void createResizeUiManager(View view) {
        RelativeLayoutTouced loLfo1 = (RelativeLayoutTouced) view.findViewById(R.id.loLfo1);
        loLfo1.setAnimationParam(0, 0, 1.5f, 1.5f, 1000, this);
        RelativeLayoutTouced loLfo2 = (RelativeLayoutTouced) view.findViewById(R.id.loLfo2);

        loLfo2.setAnimationParam(0, loLfo2.getLayoutParams().height, 1.5f, 1.5f, 1000, this);
        RelativeLayoutTouced loLfo12 = (RelativeLayoutTouced) view.findViewById(R.id.loLfo12);
        loLfo12.setAnimationParam(loLfo12.getLayoutParams().width, 0, 1.5f, 1.5f, 1000, this);
        LinearLayoutTouched loADSR = (LinearLayoutTouched) view.findViewById(R.id.loADSR);
        loADSR.setAnimationParam(loADSR.getLayoutParams().width, loADSR.getLayoutParams().height, 1.5f, 1.5f, 1000, this);


    }


    @Override
    public void onOpenAnimation(CloseAnimationListener view) {
        if (mCurrentOpenLayout != null && mCurrentOpenLayout != view)
            mCurrentOpenLayout.closeAnimation();
        mCurrentOpenLayout = view;

    }

    @Override
    public void onCloseAnimation(CloseAnimationListener view) {

    }
}
