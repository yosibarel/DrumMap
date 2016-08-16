package views.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yossibarel.drummap.R;

import effects.Effect;
import views.Custom.ViewEffectParam;

/**
 * Created by yossibarel on 03/04/16.
 */
public class EffectDialog extends Dialog {
    private final Effect mEffect;
    private final Context mContext;

    public EffectDialog(Context context, Effect effect) {
        super(context);
        mEffect = effect;
        mContext = context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_effect);
        TextView tvName = (TextView) findViewById(R.id.tvFxName);
        LinearLayout llFxParamContainer = (LinearLayout) findViewById(R.id.llFxParamContainer);
        Button btnClose = (Button) findViewById(R.id.btnClose);
        Button btnPlay = (Button) findViewById(R.id.btnPlay);
        tvName.setText(mEffect.getName());
        btnPlay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mEffect.keyDown();
                        break;
                    case MotionEvent.ACTION_UP:
                        mEffect.keyRelese();
                    case MotionEvent.ACTION_OUTSIDE:
                        mEffect.keyRelese();
                    case MotionEvent.ACTION_HOVER_EXIT:
                        mEffect.keyRelese();
                        break;
                }

                return false;
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        for (int i = 0; i < mEffect.getNumParams(); i++) {
            ViewEffectParam fxParam = new ViewEffectParam(mContext, mEffect, i);
            llFxParamContainer.addView(fxParam);
        }
        float dps = llFxParamContainer.getChildAt(0).getLayoutParams().width*mEffect.getNumParams();
        float pxs = dps * mContext.getResources().getDisplayMetrics().density;
        llFxParamContainer.getLayoutParams().width = (int)pxs;

    }

    public static void showDialog(Effect effect,Context context) {

        EffectDialog effectDialog = new EffectDialog(context, effect);
        effectDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        effectDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        effectDialog.show();
        effectDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }
}
