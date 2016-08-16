package views.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.yossibarel.drummap.R;

import java.util.ArrayList;

import effects.Effect;
import models.ModItem;
import views.Custom.ModViewSelected;

/**
 * Created by yossibarel on 06/04/16.
 */
public class ModDialog implements ModViewSelected.OnModChangeListener {

    public static final int LFO_1 = 0;
    public static final int LFO_2 = 1;
    public static final int LFO_1_2 = 2;
    public static final int CONTROL_X1 = 2;
    public static final int CONTROL_Y1 = 3;
    public static final int CONTROL_X2 = 4;
    public static final int CONTROL_Y2 = 5;
    public static final int SENSOR_X = 6;
    public static final int SENSOR_Y = 7;
    public static final int ADSR_ENV = 8;
    public static final int CURVE_1 = 9;
    public static final int CURVE_2 = 10;
    private ModDialogListener mListener;
    private Effect mEffect;
    private int mSelected;
    private int mKeyEffectParam;

    ArrayList<ModItem> mods = new ArrayList<>();
    private AlertDialog dialog;

    @Override
    public void onModSelectedChange(ModItem mod) {
        if (mSelected != -1) {

            mods.get(mSelected).isSelected = false;
            /*mods.get(mSelected).mStart = 0.0;
            mods.get(mSelected).mEnd = 100.0;*/


            mEffect.removeModFx(mKeyEffectParam, mSelected);
            mods.get(mSelected).viewHolder.update();
        }

        if (mod.isSelected)
            mSelected = mod.mIndex;
        else
            mSelected = -1;
        if (mod.isSelected)
            switch (mod.mIndex) {
                case 0:
                    mEffect.setLFO(mKeyEffectParam, 1, 0, mod.mIndex, mod.mStart/100.0, mod.mEnd/100.0, false);
                    break;
                case 1:
                    mEffect.setLFO(mKeyEffectParam, 0, 1, mod.mIndex, mod.mStart/100.0, mod.mEnd/100.0, false);
                    break;
                case 2:
                    mEffect.setLFO(mKeyEffectParam, 1, 1, mod.mIndex, mod.mStart/100.0, mod.mEnd/100.0, false);
                    break;
                case 3:
                    mEffect.setControlXY(mKeyEffectParam, 0, mod.mIndex, mod.mStart/100.0, mod.mEnd/100.0, false);
                    break;
                case 4:
                    mEffect.setControlXY(mKeyEffectParam, 1, mod.mIndex, mod.mStart/100.0, mod.mEnd/100.0, false);
                    break;
                case 5:
                    mEffect.setControlXY(mKeyEffectParam, 2, mod.mIndex, mod.mStart/100.0, mod.mEnd/100.0, false);
                    break;
                case 6:
                    mEffect.setControlXY(mKeyEffectParam, 3, mod.mIndex, mod.mStart/100.0, mod.mEnd/100.0, false);
                    break;
                case 7:
                    mEffect.setControlXY(mKeyEffectParam, 4, mod.mIndex, mod.mStart/100.0, mod.mEnd/100.0, false);
                    break;
                case 8:
                    mEffect.setControlXY(mKeyEffectParam, 5, mod.mIndex, mod.mStart/100.0, mod.mEnd/100.0, false);
                    break;
                case 9:
                    mEffect.setAdsrEnvelope(mKeyEffectParam, 6, mod.mIndex, mod.mStart/100.0, mod.mEnd/100.0, false);
                    break;
                case 10:
                    mEffect.setCurve(mKeyEffectParam, 0, mod.mIndex, mod.mStart/100.0, mod.mEnd/100.0, false);
                    break;
                case 11:
                    mEffect.setCurve(mKeyEffectParam, 1, mod.mIndex, mod.mStart/100.0, mod.mEnd/100.0, false);
                    break;
            }
        mListener.onSelectMod();


    }

    @Override
    public void onOffsetSelectedChanged(ModItem mod) {
        if (mod.isSelected)
            switch (mod.mIndex) {
                case 0:
                    mEffect.setLFO(mKeyEffectParam, 1, 0, mod.mIndex, mod.mStart/100.0, mod.mEnd/100.0, true);
                    break;
                case 1:
                    mEffect.setLFO(mKeyEffectParam, 0, 1, mod.mIndex, mod.mStart/100.0, mod.mEnd/100.0, true);
                    break;
                case 2:
                    mEffect.setLFO(mKeyEffectParam, 1, 1, mod.mIndex, mod.mStart/100.0, mod.mEnd/100.0, true);
                    break;
                case 3:
                    mEffect.setControlXY(mKeyEffectParam, 0, mod.mIndex, mod.mStart/100.0, mod.mEnd/100.0, true);
                    break;
                case 4:
                    mEffect.setControlXY(mKeyEffectParam, 1, mod.mIndex, mod.mStart/100.0, mod.mEnd/100.0, true);
                    break;
                case 5:
                    mEffect.setControlXY(mKeyEffectParam, 2, mod.mIndex, mod.mStart/100.0, mod.mEnd/100.0, true);
                    break;
                case 6:
                    mEffect.setControlXY(mKeyEffectParam, 3, mod.mIndex, mod.mStart/100.0, mod.mEnd/100.0, true);
                    break;
                case 7:
                    mEffect.setControlXY(mKeyEffectParam, 4, mod.mIndex, mod.mStart/100.0, mod.mEnd/100.0, true);
                    break;
                case 8:
                    mEffect.setControlXY(mKeyEffectParam, 5, mod.mIndex, mod.mStart/100.0, mod.mEnd/100.0, true);
                    break;
                case 9:
                    mEffect.setAdsrEnvelope(mKeyEffectParam, 6, mod.mIndex, mod.mStart/100.0, mod.mEnd/100.0, true);
                    break;
                case 10:
                    mEffect.setCurve(mKeyEffectParam, 0, mod.mIndex, mod.mStart/100.0, mod.mEnd/100.0, true);
                    break;
                case 11:
                    mEffect.setCurve(mKeyEffectParam, 1, mod.mIndex, mod.mStart/100.0, mod.mEnd/100.0, true);
                    break;
            }
    }


    public interface ModDialogListener {
        void onSelectMod();
    }

    public void show(final Context context, final Effect effect, final int keyEffectParam, ModDialogListener listener, final int selected) {

        mListener = listener;
        mEffect = effect;
        mSelected = selected;
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(R.layout.send_mod_dialog, null);
        LinearLayout loContent = (LinearLayout) view.findViewById(R.id.content);
        builderSingle.setView(view);

        mKeyEffectParam = keyEffectParam;

        mods.add(new ModItem("LFO 1", 0));
        mods.add(new ModItem("LFO 2", 1));
        mods.add(new ModItem("LFO 1+2", 2));
        mods.add(new ModItem("Control X1", 3));
        mods.add(new ModItem("Control Y1", 4));
        mods.add(new ModItem("Control X2", 5));
        mods.add(new ModItem("Control Y2", 6));
        mods.add(new ModItem("Sensor X", 7));
        mods.add(new ModItem("Sensor Y", 8));
        mods.add(new ModItem("ADSR Envelope", 9));
        mods.add(new ModItem("Curve 1", 10));
        mods.add(new ModItem("Curve 2", 11));
        if (selected != -1) {
            mods.get(selected).isSelected = true;
        }
        for (int i = 0; i < mods.size(); i++) {
            ModViewSelected viewMod = new ModViewSelected(context, mods.get(i), this);
            mods.get(i).viewHolder = viewMod;

            loContent.addView(viewMod);

        }

        Button btnClose = (Button) view.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });
        dialog = builderSingle.create();

        dialog.show();
    }
}
