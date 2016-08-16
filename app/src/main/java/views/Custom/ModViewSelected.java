package views.Custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yossibarel.drummap.R;

import models.ModItem;

/**
 * Created by yossibarel on 30/04/16.
 */
public class ModViewSelected extends LinearLayout {
    private ModItem mMod;
    private OnModChangeListener mOnModChange;
    private CheckBox vMod;
    private RangeBarPrecent rbPrecent;

    public ModViewSelected(Context context, ModItem mod, OnModChangeListener onModChangeListener) {
        super(context);
        mMod = mod;
        mOnModChange = onModChangeListener;
        init();
    }

    public ModViewSelected(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public interface OnModChangeListener {
        void onModSelectedChange(ModItem mod);

        void onOffsetSelectedChanged(ModItem mod);
    }

    private void init() {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_mod_selected, this, true);


        final TextView tvName = (TextView) view.findViewById(R.id.tvModName);
        rbPrecent = (RangeBarPrecent) view.findViewById(R.id.rbOffset);
        vMod = (CheckBox) view.findViewById(R.id.vSelected);
        tvName.setText(mMod.mName);

        rbPrecent.setFormater(new RangeBarPrecent.ValueLeftRightFormatter() {
            @Override
            public String getLeft(double value) {
                return String.format("%.2f", value / 100.0);
            }

            @Override
            public String getRight(double value) {
                return String.format("%.2f", value / 100.0);
            }
        });
        rbPrecent.setProgress1(mMod.mStart);
        rbPrecent.setProgress2(mMod.mEnd);
        rbPrecent.setOnRangeChangeListener(new RangeBarPrecent.OnRangeChangeListener() {
            @Override
            public void onRangeChange(double left, double right ) {

                    mMod.mStart = left;
                    mMod.mEnd = right;
                    mOnModChange.onOffsetSelectedChanged(mMod);

            }
        });
        vMod.setChecked(mMod.isSelected);

        vMod.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mMod.isSelected = vMod.isChecked();
                mOnModChange.onModSelectedChange(mMod);
            }
        });

    }

    public void update() {
        vMod.setChecked(mMod.isSelected);
        rbPrecent.setProgress1(mMod.mStart);
        rbPrecent.setProgress2(mMod.mEnd);

    }
}
