package views.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.yossibarel.drummap.DrumMapJni;
import com.yossibarel.drummap.R;

import java.util.ArrayList;
import java.util.List;

import Utils.Extras;
import effects.Effect;
import effects.FxFactory;
import views.Custom.FxHelper.FxListAdapter;
import views.Custom.FxHelper.OnEffectsListChangedListener;
import views.Custom.FxHelper.OnStartDragListener;
import views.Custom.FxHelper.SimpleItemTouchHelperCallback;
import views.Custom.ViewEffect;
import views.dialogs.ListEffectDialog;

/**
 * Created by yossibarel on 18/04/16.
 */
public class FxFragment extends Fragment implements ViewEffect.ViewEffectListener, OnEffectsListChangedListener,
        OnStartDragListener {
    private RecyclerView mRecyclerView;
    private FxListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ItemTouchHelper mItemTouchHelper;
    private List<Effect> mEffects;
    DrumMapJni drumMap;
    private int mChannelIndex;

    private FrameLayout mParentFx;
    private LinearLayout.LayoutParams mSaveLP;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fx, container, false);

        drumMap = DrumMapJni.getInstance();

        mChannelIndex = getArguments().getInt(Extras.EXTRA_CHANNEL_INDEX, -1);

        mParentFx = (FrameLayout) view.findViewById(R.id.parentFx);

        int[] effects = drumMap.getActiveEffect(mChannelIndex);
        /*for (int i = 0; i < effects.length; i++) {
            createEffect(effects[i], false);
        }*/
        setupRecyclerView(view, effects);
        Button btnAddFx = (Button) view.findViewById(R.id.btnAddFx);
        btnAddFx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ListEffectDialog dialog = new ListEffectDialog();
                dialog.show(getActivity(), new ListEffectDialog.OnSelectEffectListener() {
                    @Override
                    public void onSelectEffect(int indexFx) {

                        createEffect(indexFx, true);
                    }
                });
            }
        });


        return view;

    }

    private ArrayList<Effect> getEffectFromIniArr(int[] iFx) {
        ArrayList<Effect> effectArrayList = new ArrayList<>();
        for (int i = 0; i < iFx.length; ++i) {
            Effect fx = FxFactory.createEffect(mChannelIndex, drumMap, iFx[i]);
            fx.setIndex(i);
            effectArrayList.add(fx);
        }
        return effectArrayList;
    }

    private void createEffect(int indexFx, boolean isNew) {
        Effect effect = FxFactory.createEffect(mChannelIndex, drumMap, indexFx);

        if (isNew) {
            if (drumMap.addEffect(mChannelIndex, indexFx)) {
                if (effect != null) {
                    effect.setIndex(mEffects.size());
                    //ViewEffect viewEffect = new ViewEffect(getActivity(), effect);
                    mEffects.add(effect);
                    mAdapter.notifyDataSetChanged();
                    // viewEffect.setListener(FxFragment.this);
                    //  mllFxContainer.addView(viewEffect);

                }
            }
        } else {
            if (effect != null) {
                effect.setIndex(mEffects.size());
                mEffects.add(effect);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onStartDrag(ViewEffect viewEffect) {

        if (mParentFx.getChildAt(mParentFx.getChildCount() - 1) != viewEffect) {
            mSaveLP = (LinearLayout.LayoutParams) viewEffect.getLayoutParams();
            Log.d("drag", "down");

            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(viewEffect.getWidth(), viewEffect.getHeight());
            viewEffect.setLayoutParams(lp);
            mParentFx.addView(viewEffect);
        }
    }

    @Override
    public void onStopDrag(ViewEffect viewEffect) {

        Log.d("drag", "up");
        mParentFx.removeView(viewEffect);


        viewEffect.setY(0);
        viewEffect.setX(0);
    }

    private void setupRecyclerView(View view, int[] iFx) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.fx_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mEffects = getEffectFromIniArr(iFx);

        //setup the adapter with empty list
        mAdapter = new FxListAdapter(mEffects, getActivity(), this, this, mChannelIndex);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
       /* mRecyclerView.addItemDecoration(new VerticalDividerItemDecoration.Builder(this)
                .colorResId(R.color.colorPrimaryDark)
                .size(2)
                .build());*/
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);

    }


    @Override
    public void onCloseEffect() {

    }
}
