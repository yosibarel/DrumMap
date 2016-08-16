package views.Custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.yossibarel.drummap.DrumMapActivity;
import com.yossibarel.drummap.DrumMapJni;
import com.yossibarel.drummap.R;

import models.Channel;

/**
 * Created by yossibarel on 29/07/16.
 */
public class ViewChannel extends LinearLayout {
    private final Channel mChannel;
    private DrumMapJni mDrumMap;
    private View mViewLoaded;
    private View mViewmeter;
    private OnEditListener mListener;
    private ToggleButton mBtnMute;
    private boolean mIsMuted;
    private ToggleButton mBtnSolo;
    private boolean mIsSolo;

    public ViewChannel(Context context, Channel channel) {
        super(context);
        mChannel = channel;
        init();

    }

    public void setOnEditChannelListener(OnEditListener listener) {
        mListener = listener;
    }

    private void init() {
        mDrumMap = DrumMapJni.getInstance();
        LayoutInflater inflater = LayoutInflater.from(getContext());

        View view = inflater.inflate(R.layout.item_edit_channel, this, true);
        view.setMinimumHeight(DrumMapActivity.HEIGHT / 4);
        view.setMinimumWidth(DrumMapActivity.WIDTH / 4);
        Button btnTap = (Button) view.findViewById(R.id.btnTap);
        btnTap.setText((mChannel.getIndex() + 1) + "");
        btnTap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onEdit(mChannel);

            }
        });

        mViewLoaded = view.findViewById(R.id.vLoaded);
        mViewmeter = view.findViewById(R.id.vViewmeter);
        mBtnMute = (ToggleButton) findViewById(R.id.btnMute);
        mBtnMute.setChecked(mIsMuted = mDrumMap.getIsMuted(mChannel.getIndex()));
        mBtnMute.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsMuted = mBtnMute.isChecked();

                mDrumMap.setMuteed(mChannel.getIndex(), mIsMuted);
                mListener.onMuteChannel(mChannel, mIsMuted);
            }
        });

        mBtnSolo = (ToggleButton) findViewById(R.id.btnSolo);
        mBtnSolo.setChecked(mIsSolo = mDrumMap.getIsSolo(mChannel.getIndex()));

        mBtnSolo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsSolo = mBtnSolo.isChecked();
                mDrumMap.setSolo(mChannel.getIndex(), mIsSolo);
                mListener.onSoloChannel(mChannel, mIsSolo);

            }
        });
        updateLoaded(DrumMapJni.getInstance().getIsLoaded(mChannel.getIndex()));

    }

    public void updateViewMeter(float alpha) {
        mViewmeter.setAlpha(alpha);
    }

    public void updateLoaded(boolean isLoaded) {
        
    }

    public void update() {
        mBtnSolo.setChecked(mIsSolo = mDrumMap.getIsSolo(mChannel.getIndex()));
        mBtnMute.setChecked(mIsMuted = mDrumMap.getIsMuted(mChannel.getIndex()));
        boolean isLoaded = mDrumMap.getIsLoaded(mChannel.getIndex());
        if (isLoaded) {
            mViewLoaded.setBackgroundResource(R.drawable.drw_seq_pattern_not_playing);
        } else {
            mViewLoaded.setBackgroundResource(R.drawable.drw_seq_pattern_disable);
        }
    }

    public void updateMute() {

    }

    public void updateSolo(boolean soloState) {
        mBtnSolo.setChecked(mIsSolo = mDrumMap.getIsSolo(mChannel.getIndex()));
        if (soloState && !mIsSolo || mIsMuted) {
            mViewmeter.setBackgroundResource(R.drawable.drw_seq_pattern_disable);
        } else {
            mViewmeter.setBackgroundResource(R.drawable.drw_yellow_cyrcle);
        }
    }

    public boolean getIsSolo() {
        return mIsSolo = mDrumMap.getIsSolo(mChannel.getIndex());
    }

    public interface OnEditListener {


        void onEdit(Channel channel);

        void onMuteChannel(Channel mChannel, boolean mIsMuted);

        void onSoloChannel(Channel mChannel, boolean mIsSolo);
    }

}
