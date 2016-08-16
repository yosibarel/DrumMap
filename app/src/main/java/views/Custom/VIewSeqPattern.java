package views.Custom;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.yossibarel.drummap.R;

import interfaces.TextValueFormater;

/**
 * Created by yossibarel on 12/04/16.
 */
public class VIewSeqPattern extends LinearLayout {
    private final Context mContext;
    private OnValueChangeListener mListener;
    private int mIndex = 0;
    private SeekBar sbValue;
    private ToggleButton btnEnable;
    private View vIndexPlaying;
    private TextView tvValue;
    private TextValueFormater mFormater;
    boolean mIsEnable = true;
    private View vEnableSeq;

    public VIewSeqPattern(Context context) {
        super(context);
        mContext = context;
        init();
    }


    public VIewSeqPattern(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public void setTextValueFormater(TextValueFormater formater) {
        mFormater = formater;
    }

    public interface OnValueChangeListener {
        void onValueChange(int indexPattern, double val);

        void onActiveChange(int indexPattern, boolean enable);

        void onEnableClick(int index);
    }

    public void setIsPlaying(boolean isPlaying) {
        if (isPlaying)
            vIndexPlaying.setBackgroundResource(R.drawable.drw_seq_pattern_playing);
        else
            vIndexPlaying.setBackgroundResource(R.drawable.drw_seq_pattern_not_playing);
    }

    public void setEnable(boolean enable) {
        if (enable)
            vIndexPlaying.setBackgroundResource(R.drawable.drw_seq_pattern_not_playing);
        else
            vIndexPlaying.setBackgroundResource(R.drawable.drw_seq_pattern_disable);
    }

    public void setOnValueChangeListener(OnValueChangeListener l) {
        mListener = l;
    }

    public void setIndex(int index) {
        mIndex = index;

    }

    public void setValue(double value) {
        tvValue.setText(mFormater.getValue(value));
        sbValue.setProgress((int) (value * 100));
    }

    public void setEnablePattern(double val) {

        btnEnable.setChecked((int) val == 1);
        Log.d("btnEnable", val + "" + " " + ((int) val == 1) + " " + btnEnable.isChecked());
    }

    private void init() {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.seq_pattern, this, true);
        vIndexPlaying = findViewById(R.id.vIndexPlaying);
        vEnableSeq = findViewById(R.id.vEnableSeq);
        tvValue = (TextView) findViewById(R.id.tvValue);
        sbValue = (SeekBar) view.findViewById(R.id.sbValue);
        btnEnable = (ToggleButton) findViewById(R.id.btnEnable);
        sbValue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mListener != null && fromUser) {
                    tvValue.setText(mFormater.getValue(progress / 100.0));
                    mListener.onValueChange(mIndex, progress / 100.0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        btnEnable.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mListener != null)
                    mListener.onActiveChange(mIndex, btnEnable.isChecked());
            }
        });
        vEnableSeq.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onEnableClick(mIndex);
            }
        });
    }
}
