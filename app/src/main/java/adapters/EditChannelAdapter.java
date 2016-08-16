package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.yossibarel.drummap.DrumMapActivity;
import com.yossibarel.drummap.DrumMapJni;
import com.yossibarel.drummap.R;

import java.util.List;

import models.Channel;

/**
 * Created by yossibarel on 03/04/16.
 */
public class EditChannelAdapter extends BaseAdapter {
    private final List<Channel> mChannels;
    private final Context mContext;
    private OnEditListener mListener;
    private View mViewLoaded;
    private View mViewmeter;

    public EditChannelAdapter(Context context, List<Channel> channels) {

        mContext = context;
        mChannels = channels;
    }

    public void setOnEditChannelListener(OnEditListener listener) {
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_edit_channel, parent, false);
        view.setMinimumHeight(DrumMapActivity.HEIGHT / 4);
        view.setMinimumWidth(DrumMapActivity.WIDTH / 4);
        Button btnTap = (Button) view.findViewById(R.id.btnTap);
        btnTap.setText((position + 1) + "");
        btnTap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onEdit(mChannels.get(position));

            }
        });

        mViewLoaded = view.findViewById(R.id.vLoaded);
        mViewmeter = view.findViewById(R.id.vViewmeter);
        updateLoaded(DrumMapJni.getInstance().getIsLoaded(position));
        return view;
    }

    public void updateViewMeter(float alpha) {
        mViewmeter.setAlpha(alpha);
    }

    public void updateLoaded(boolean isLoaded) {
        if (isLoaded) {
            mViewLoaded.setBackgroundResource(R.drawable.drw_seq_pattern_not_playing);
        } else {
            mViewLoaded.setBackgroundResource(R.drawable.drw_seq_pattern_disable);
        }
    }

    public interface OnEditListener {


        void onEdit(Channel channel);
    }
}