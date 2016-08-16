package views.Custom;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.yossibarel.drummap.DrumMapJni;
import com.yossibarel.drummap.EditChannelActivity;
import com.yossibarel.drummap.R;

import Utils.Extras;

/**
 * Created by yossibarel on 25/04/16.
 */
public class MixerViewItem extends LinearLayout {

    private int mChannel;
    private DrumMapJni mDrumMap;
    private View mView;
    private SeekBar sbVol;
    private TextView tvVol;
    private ViewMeter mVMRight;
    private ViewMeter mVMLeft;
    private SeekBar sbStereo;
    private MixerViewItemListener mListener;
    private boolean mIsSolo;
    private boolean mIsMuted;
    private ToggleButton mBtnMute;
    private ToggleButton mBtnSolo;
    private ToggleButton mBtnSend;
    private SeekBar mSbSend;
    private TextView mTvSend;

    public void updateSolo(boolean soloState) {
        mBtnSolo.setChecked(mIsSolo = mDrumMap.getIsSolo(mChannel));
        if (soloState && !mIsSolo || mIsMuted) {
            mVMLeft.setMuteState(true);
            mVMRight.setMuteState(true);
        } else {
            mVMLeft.setMuteState(false);
            mVMRight.setMuteState(false);
        }
    }

    public void updateMute() {

    }

    public boolean isSolo() {
        return mIsSolo;
    }

    public interface MixerViewItemListener {
        void onSoloChannel(int channel, boolean isSolo);

        void onMuteChannel(int channel, boolean isMute);
    }

    public MixerViewItem(Context context, DrumMapJni drumMapJni, int channel) {
        super(context);
        mChannel = channel;
        mDrumMap = drumMapJni;

        init();
    }


    public MixerViewItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setListener(MixerViewItemListener l) {
        mListener = l;
    }

    private void init() {
        mView = LayoutInflater.from(getContext()).inflate(
                R.layout.mixer_channel_view, this, true);
        mVMLeft = (ViewMeter) mView.findViewById(R.id.vmLeft);
        mVMRight = (ViewMeter) mView.findViewById(R.id.vmRight);
        sbVol = (SeekBar) mView.findViewById(R.id.sbVol);
        createSendEffect();

        tvVol = (TextView) mView.findViewById(R.id.tvVol);
        Button btnChannel = (Button) mView.findViewById(R.id.btnChannel);
        btnChannel.setText("Ch" + (mChannel + 1));

        sbVol.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvVol.setText(progress + "");
                if (fromUser)
                    mDrumMap.setVolume(mChannel, progress / 100.0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        btnChannel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent activity = new Intent(getContext(), EditChannelActivity.class);
                activity.putExtra(Extras.EXTRA_CHANNEL_INDEX, mChannel);
                getContext().startActivity(activity);

            }
        });
        mBtnMute = (ToggleButton) findViewById(R.id.btnMute);
        mBtnMute.setChecked(mIsMuted = mDrumMap.getIsMuted(mChannel));
        mBtnMute.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsMuted = mBtnMute.isChecked();
                mVMRight.setMuteState(mIsMuted);
                mVMLeft.setMuteState(mIsMuted);
                mDrumMap.setMuteed(mChannel, mIsMuted);
                mListener.onMuteChannel(mChannel, mIsMuted);
            }
        });
        mVMRight.setMuteState(mIsMuted);
        mVMLeft.setMuteState(mIsMuted);
        mBtnSolo = (ToggleButton) findViewById(R.id.btnSolo);
        mBtnSolo.setChecked(mIsSolo = mDrumMap.getIsSolo(mChannel));

        mBtnSolo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsSolo = mBtnSolo.isChecked();
                mDrumMap.setSolo(mChannel, mIsSolo);
                mListener.onSoloChannel(mChannel, mIsSolo);

            }
        });
        sbStereo = (SeekBar) mView.findViewById(R.id.sbStereo);
        sbStereo.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });

        sbStereo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    double left;
                    double right;
                    if (progress > 100) {

                        left = (double) (100 - (progress - 100)) / 100;
                        right = 1.0;
                    } else {
                        left = 1.0;
                        right = (double) progress / 100;
                    }
                    // mStereoFx.setFx(0, (float) progress / 200);
                    mDrumMap.setStereo(mChannel, left, right, progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if (progress < 120 && progress > 80) {

                    mDrumMap.setStereo(mChannel, 1.0, 1.0, 100);
                    seekBar.setProgress(100);
                }

            }
        });

    }

    private void createSendEffect() {
        mBtnSend = (ToggleButton) mView.findViewById(R.id.btnSend1);
        mSbSend = (SeekBar) mView.findViewById(R.id.sbSend1);
        mTvSend = (TextView) mView.findViewById(R.id.tvSend1);

        mBtnSend.setChecked(DrumMapJni.getInstance().isEnbableSendFx1(mChannel));
        double sendFx1 = DrumMapJni.getInstance().getSendFx1(mChannel);
        double sendFx2 = DrumMapJni.getInstance().getSendFx2(mChannel);
        mTvSend.setText((int) (sendFx1 * 100) + "");
        mSbSend.setProgress((int) (sendFx1 * 100));

        mBtnSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DrumMapJni.getInstance().enableSendFx1(mChannel, mBtnSend.isChecked());

            }
        });

        mSbSend.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mTvSend.setText("" + progress);
                DrumMapJni.getInstance().setSendFx1(mChannel, (double) progress / 100.0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    public void updateViewMeter(final float left, final float right) {
        mVMLeft.post(new Runnable() {
            @Override
            public void run() {

                mVMLeft.setValue(left);
                mVMRight.setValue(right);
                mVMLeft.invalidate();
                mVMRight.invalidate();
            }
        });
    }


    public void updateParam() {
        int vol = (int) (mDrumMap.getVolume(mChannel) * 100.0);
        sbVol.setProgress(vol);
        tvVol.setText(vol + "");
        sbStereo.setProgress(mDrumMap.getStereoProgress(mChannel));
    }
}
