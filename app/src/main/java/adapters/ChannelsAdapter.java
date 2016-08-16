package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ToggleButton;

import com.yossibarel.drummap.DrumMapActivity;
import com.yossibarel.drummap.DrumMapJni;
import com.yossibarel.drummap.R;

import java.util.List;

import models.Channel;

/**
 * Created by yossibarel on 29/03/16.
 */
public class ChannelsAdapter extends BaseAdapter {
    private final List<Channel> mChannels;
    private final Context mContext;
    private final DrumMapJni mDrumMap;
    private OnTapListener mListener;

    public ChannelsAdapter(Context context, List<Channel> channels, DrumMapJni drumMap) {

        mContext = context;
        mChannels = channels;
        mDrumMap = drumMap;

    }

    public void setOnTapListener(OnTapListener listener) {
        mListener = listener;
    }

    @Override
    public int getCount() {
        return mChannels.size();
    }

    @Override
    public Object getItem(int position) {
        return mChannels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mChannels.get(position).getIndex();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_channel, parent, false);
        convertView.setMinimumHeight(DrumMapActivity.HEIGHT / 4);
        convertView.setMinimumWidth(DrumMapActivity.WIDTH / 4);
        Button btnTap = (Button) convertView.findViewById(R.id.btnTap);
        Channel channel = mChannels.get(position);
        final ToggleButton btnPlay = (ToggleButton) convertView.findViewById(R.id.btnPlay);
        if (channel.getIsStopPlayOnRelease()) {
            btnPlay.setVisibility(View.GONE);
            btnTap.setVisibility(View.VISIBLE);
            btnTap.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {


                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            mListener.onTap(mChannels.get(position));
                            break;
                        case MotionEvent.ACTION_UP:
                            mListener.onStopTap(mChannels.get(position));
                        case MotionEvent.ACTION_OUTSIDE:
                            mListener.onStopTap(mChannels.get(position));
                        case MotionEvent.ACTION_HOVER_EXIT:
                            mListener.onStopTap(mChannels.get(position));
                            break;
                    }

                    return false;
                }
            });
            btnTap.setText((position + 1) + "");

        } else {
            btnPlay.setVisibility(View.VISIBLE);
            btnTap.setVisibility(View.GONE);
            btnPlay.setText((position + 1) + "");
            btnPlay.setTextOff((position + 1) + "");
            btnPlay.setTextOn((position + 1) + "");
            btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (btnPlay.isChecked())
                        mListener.onTap(mChannels.get(position));
                    else
                        mListener.onStopTap(mChannels.get(position));
                }
            });
        }
        btnPlay.setChecked(mDrumMap.getIsPlay(mChannels.get(position).getIndex()));

        return convertView;
    }

    public interface OnTapListener {
        void onTap(Channel channel);

        void onStopTap(Channel channel);


    }
}
