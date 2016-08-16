package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.yossibarel.drummap.DrumMapJni;
import com.yossibarel.drummap.R;

import java.util.List;

import effects.Effect;
import views.dialogs.EffectDialog;

/**
 * Created by yossibarel on 02/04/16.
 */
public class EffectAdapter extends ArrayAdapter<Effect> {

    private final Context mContext;
    private final DrumMapJni mDrumMap;
    private final int mChannelIndex;
    private List<Effect> mItems;
    private LayoutInflater mInflater;

    public EffectAdapter(Context context, List<Effect> items, DrumMapJni draumMap, int channelIndex) {
        super(context, R.layout.item_effect, items);
        mInflater = LayoutInflater.from(context);
        mItems = items;
        mDrumMap = draumMap;
        mContext = context;
        mChannelIndex = channelIndex;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.item_effect, parent, false);
        Button btnShow = (Button) view.findViewById(R.id.btnShow);
        CheckBox cbEnable = (CheckBox) view.findViewById(R.id.cbActive);
        final Effect effect = mItems.get(position);
        cbEnable.setText(effect.getName());
        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EffectDialog.showDialog(effect, mContext);


            }
        });
        cbEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mDrumMap.setFx(mChannelIndex, effect.mFxType, Effect.FX_ENABLED, isChecked ? 1 : 0);
            }
        });

        return view;
    }
}
