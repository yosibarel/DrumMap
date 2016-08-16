package views.Custom.FxHelper;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.yossibarel.drummap.DrumMapJni;
import com.yossibarel.drummap.R;

import java.util.Collections;
import java.util.List;

import effects.Effect;
import views.Custom.ViewEffectParam;

/**
 * Created by yossibarel on 03/08/16.
 */
public class FxListAdapter extends
        RecyclerView.Adapter<FxListAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {

    private final int mChannel;
    private List<Effect> mEffects;
    private Context mContext;
    private OnStartDragListener mDragStartListener;
    private OnEffectsListChangedListener mListChangedListener;

    public FxListAdapter(List<Effect> effects, Context context,
                         OnStartDragListener dragLlistener,
                         OnEffectsListChangedListener listChangedListener, int channel) {
        mEffects = effects;
        mContext = context;
        mDragStartListener = dragLlistener;
        mListChangedListener = listChangedListener;
        mChannel = channel;

    }


    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from
                (parent.getContext()).inflate(R.layout.view_effect, parent, false);
        ItemViewHolder viewHolder = new ItemViewHolder(rowView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {

        final Effect selectedEffect = mEffects.get(position);

        holder.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedEffect.close(selectedEffect.getIndex());
                mEffects.remove(selectedEffect.getIndex());
                mListChangedListener.onCloseEffect();
                notifyItemRemoved(selectedEffect.getIndex());
                updateIndex();
            }
        });
        holder.btnEnable.setChecked(selectedEffect.getFxValue(Effect.FX_ENABLED) > 0);
        holder.btnEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                selectedEffect.setEnable(isChecked);
            }
        });
        holder.llFxParamContainer.removeAllViews();
        for (int i = 0; i < selectedEffect.getNumParams(); i++) {
            ViewEffectParam fxParam = new ViewEffectParam(mContext, selectedEffect, i);
            ((LinearLayout.LayoutParams) fxParam.getLayoutParams()).height = LinearLayout.LayoutParams.MATCH_PARENT;
            holder.llFxParamContainer.addView(fxParam);
        }

        holder.tvName.setText(selectedEffect.getName());
        holder.handleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }

                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mEffects.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mEffects, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        Log.d("move", "from " + fromPosition + " to " + toPosition);
        DrumMapJni.getInstance().replaceFxChannelPosition(mChannel, fromPosition, toPosition);
        updateIndex();

    }

    @Override
    public void onItemDismiss(int position) {
        Log.d("move", "dismis");

    }

    void updateIndex() {
        for (int i = 0; i < mEffects.size(); ++i) {
            mEffects.get(i).setIndex(i);
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {
        public final View handleView;
        public final LinearLayout llFxParamContainer;
        public final TextView tvName;
        public final ToggleButton btnEnable;
        public final View btnClose;


        public ItemViewHolder(View itemView) {
            super(itemView);

            handleView = itemView.findViewById(R.id.vDrag);
            llFxParamContainer = (LinearLayout) itemView.findViewById(R.id.llFxParamContainer);
            tvName = (TextView) itemView.findViewById(R.id.tvFxName);
            btnEnable = (ToggleButton) itemView.findViewById(R.id.btnEnable);
            btnClose = itemView.findViewById(R.id.btnClose);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);// http://valokafor.com/wp-admin/post.php?post=1804&action=edit#ckgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }
}
