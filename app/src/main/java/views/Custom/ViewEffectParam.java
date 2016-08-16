package views.Custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.yossibarel.drummap.R;

import effects.Effect;
import views.dialogs.ModDialog;

/**
 * Created by yossibarel on 01/04/16.
 */
public class ViewEffectParam extends LinearLayout implements ModDialog.ModDialogListener {


    private Effect mEffect;
    private int mFxParamKey;
    private Context mContext;
    private Button btnSendMod;


    public ViewEffectParam(Context context, Effect effect, int fxParamKey) {
        super(context);
        mEffect = effect;
        mFxParamKey = fxParamKey;
        mContext = context;

        init();


    }

    public ViewEffectParam(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    void init() {
        View view = LayoutInflater.from(getContext()).inflate(
                R.layout.view_effect_param, this);
        view.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        TextView tvFxParamName = (TextView) view.findViewById(R.id.tvFxParamName);
        final TextView tvFxParamValue = (TextView) view.findViewById(R.id.tvFxParamValue);
        SeekBar sbFxParamValue = (SeekBar) findViewById(R.id.sbFxParamValue);
        btnSendMod = (Button) findViewById(R.id.btnLFO);
        tvFxParamName.setText(mEffect.getParaName(mFxParamKey));
        double res = mEffect.getFxValue(mFxParamKey);
        sbFxParamValue.setProgress((int) (res * 100));

        tvFxParamValue.setText(String.format("%.2f",(float)res));

        final int modIndex = mEffect.getIsInModeState(mFxParamKey);
        if (modIndex != -1)
            btnSendMod.setBackgroundResource(R.drawable.drw_mod_button);
        sbFxParamValue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                float val = (float) progress / 100f;
                // String sval = String.format("%.02d", val);
                tvFxParamValue.setText(String.format("%.2f",(float)val));
                mEffect.setFx(mFxParamKey, val);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        btnSendMod.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ModDialog dialog = new ModDialog();
                dialog.show(mContext, mEffect, mFxParamKey, ViewEffectParam.this, mEffect.getIsInModeState(mFxParamKey));
            }
        });


    }

    @Override
    public void onSelectMod() {
        int modIndex = mEffect.getIsInModeState(mFxParamKey);
        if (modIndex != -1)
            btnSendMod.setBackgroundResource(R.drawable.drw_mod_button);
        else
            btnSendMod.setBackgroundResource(R.drawable.drw_mod_button_normal);
    }
}
