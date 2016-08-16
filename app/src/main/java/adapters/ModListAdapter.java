package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.yossibarel.drummap.R;

import java.util.List;

import models.ModItem;
import views.Custom.RangeBarPrecent;

/**
 * Created by yossibarel on 20/04/16.
 */
public class ModListAdapter extends ArrayAdapter<ModItem> {
    private final List<ModItem> mMods;
    private final Context mContext;

    private OnModChangeListener mOnModChange;


    public interface OnModChangeListener {
        void onModSelectedChange(ModItem mod);

        void onOffsetSelectedChanged(double start, double end);
    }

    public ModListAdapter(Context context, List<ModItem> mods, OnModChangeListener listener) {
        super(context, R.layout.item_mod_selected, mods);
        mContext = context;
        mOnModChange = listener;
        mMods = mods;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_mod_selected, parent, false);

        final ModItem mMod = mMods.get(position);
        final TextView tvName = (TextView) view.findViewById(R.id.tvModName);
        RangeBarPrecent rbPrecent = (RangeBarPrecent) view.findViewById(R.id.rbOffset);
        final CheckBox vMod = (CheckBox) view.findViewById(R.id.vSelected);
        tvName.setText(mMod.mName);
        rbPrecent.setProgress1(mMod.mStart);
        rbPrecent.setProgress2(mMod.mEnd);
        rbPrecent.setFormater(new RangeBarPrecent.ValueLeftRightFormatter() {
            @Override
            public String getLeft(double value) {
                return String.format("%.2f", value);
            }

            @Override
            public String getRight(double value) {
                return String.format("%.2f", value);
            }
        });
        rbPrecent.setOnRangeChangeListener(new RangeBarPrecent.OnRangeChangeListener() {
            @Override
            public void onRangeChange(double left, double right) {

                    mMod.mStart = left;
                    mMod.mEnd = right;

                    mOnModChange.onOffsetSelectedChanged(left, right);

            }
        });
        vMod.setChecked(mMod.isSelected);
        vMod.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mMod.isSelected = isChecked;
                mOnModChange.onModSelectedChange(mMod);
            }
        });
        return view;
    }
}