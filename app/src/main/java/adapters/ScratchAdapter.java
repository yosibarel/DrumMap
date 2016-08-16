package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ToggleButton;

import com.yossibarel.drummap.DrumMapActivity;
import com.yossibarel.drummap.R;

import java.util.List;

import models.Channel;

/**
 * Created by yossibarel on 10/04/16.
 */
public class ScratchAdapter  extends ArrayAdapter<Channel> {
    private final List<Channel> mChannels;
    private final Context mContext;


    public ScratchAdapter(Context context, List<Channel> channels) {
        super(context, R.layout.item_scratch, channels);
        mContext = context;
        mChannels = channels;
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_scratch, parent, false);
        view.setMinimumHeight(DrumMapActivity.SCRATCH_HEIGHT / 2);
        view.setMinimumWidth(DrumMapActivity.SCRATCH_WIDTH / 8);
        final ToggleButton btnEnable = (ToggleButton) view.findViewById(R.id.btnEnable);
        btnEnable.setHeight(DrumMapActivity.SCRATCH_HEIGHT / 2);
        btnEnable.setWidth(DrumMapActivity.SCRATCH_WIDTH / 8);
        btnEnable.setTextOff((position + 1) + "");
        btnEnable.setTextOn((position + 1) + "");
        btnEnable.setText((position + 1) + "");
        btnEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mChannels.get(position).setScratchEnable(btnEnable.isChecked());
            }
        });

        return view;
    }


}