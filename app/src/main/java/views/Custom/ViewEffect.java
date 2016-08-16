package views.Custom;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.yossibarel.drummap.R;

import effects.Effect;

/**
 * Created by yossibarel on 18/04/16.
 */
public class ViewEffect extends LinearLayout {


    private Context mContext;
    private Effect mEffect;
    private View view;
    private Float mSaveX;
    private float mSaveY;
    private ViewEffectListener mListener;

    public void setListener(ViewEffectListener l) {
        this.mListener = l;
    }


    public ViewEffect(Context context, Effect effect) {
        super(context);
        mEffect = effect;
        mContext = context;
        init();


    }

    public ViewEffect(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void init() {
        view = LayoutInflater.from(getContext()).inflate(R.layout.view_effect, this, true);

        LinearLayout llFxParamContainer = (LinearLayout) view.findViewById(R.id.llFxParamContainer);
        TextView tvName = (TextView) view.findViewById(R.id.tvFxName);

        tvName.setText(mEffect.getName());
        ToggleButton btnEnable = (ToggleButton) view.findViewById(R.id.btnEnable);
        btnEnable.setChecked(mEffect.getFxValue(Effect.FX_ENABLED) > 0);
        btnEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mEffect.setEnable(isChecked);
            }
        });
        for (int i = 0; i < mEffect.getNumParams(); i++) {
            ViewEffectParam fxParam = new ViewEffectParam(mContext, mEffect, i);
            ((LayoutParams) fxParam.getLayoutParams()).height = LayoutParams.MATCH_PARENT;
            llFxParamContainer.addView(fxParam);
        }
       /*float dps = llFxParamContainer.getChildAt(0).getLayoutParams().width * mEffect.getNumParams();
        float pxs = dps * mContext.getResources().getDisplayMetrics().density;
        llFxParamContainer.getLayoutParams().width = (int) pxs;*/
        findViewById(R.id.vDrag).setOnTouchListener(new View.OnTouchListener() {
            float dX, dY;

            @Override
            public boolean onTouch(View view, MotionEvent event) {

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        if (mListener != null)
                            mListener.onStartDrag(ViewEffect.this);
                        dX = getX() - event.getRawX();
                        dY = getY() - event.getRawY();
                        if (mSaveX == null) {
                            mSaveX = getX();
                            mSaveY = getY();
                        }
                        break;

                    case MotionEvent.ACTION_MOVE:

                        animate()
                                .x(event.getRawX() + dX)
                                .y(event.getRawY() + dY)
                                .setDuration(0)
                                .start();
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mListener != null)
                            mListener.onStopDrag(ViewEffect.this);
                        break;

                }
                return true;
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        Log.d("drag", "onMeasure");

        if (view.getLayoutParams() instanceof LinearLayout.LayoutParams) {
            Log.d("drag", "onMeasure LinearLayout");
            view.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));

        } else if (view.getLayoutParams() instanceof FrameLayout.LayoutParams) {
            Log.d("drag", "onMeasure FrameLayout");
            view.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
        }
        view.setX(0);
        view.setY(0);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public Effect getEffect() {
        return mEffect;
    }

    public interface ViewEffectListener {
        void onStartDrag(ViewEffect viewEffect);

        void onStopDrag(ViewEffect viewEffect);
    }
}
