package views.Custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yossibarel.drummap.R;

/**
 * Created by yossibarel on 11/04/16.
 */
public class ViewPitchBand extends RelativeLayout {

    private static int VIEW_HIGHT;
    private static int HIGHT;

    private final Context mContext;
    double mMax = 100.0;
    double mProgress1;
    double mProgress2;
    private View progress1;
    private View progress2;
    private View bar;

    private OnPichChangeListener mListener;
    LayoutParams mLyProgress1;
    LayoutParams mLyProgress2;
    LayoutParams mLyBar;
    private RelativeLayout mContent;
    private TextView tvLeft;
    private TextView tvRight;
    private View cursor;
    private View mVPich;
    private RelativeLayout pichBandContainer;

    public ViewPitchBand(Context context) {
        super(context);
        mContext = context;
        init();
    }


    public ViewPitchBand(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public interface OnPichChangeListener {
        void OnPichChange(double pich);

    }


    public void setOnPichChangeListener(OnPichChangeListener l) {
        mListener = l;
    }

    private void init() {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.pich_band_view, this, true);

        mVPich = view.findViewById(R.id.vPitchBand);
        pichBandContainer = (RelativeLayout) view.findViewById(R.id.pichBandContainer);

        ViewTreeObserver vto = pichBandContainer.getViewTreeObserver();

        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                pichBandContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                HIGHT = pichBandContainer.getMeasuredHeight();
                VIEW_HIGHT = mVPich.getMeasuredHeight();


                mVPich.setY(HIGHT / 2 - VIEW_HIGHT / 2);
            }
        });

        mVPich.setOnTouchListener(onViewTouch);
    }

    private float dY;
    OnTouchListener onViewTouch = new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {


            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:

                    dY = view.getY() - event.getRawY();

                    break;
                case MotionEvent.ACTION_MOVE:

                    float y = event.getRawY() + dY;


                    if (y >= 0.0 && y + view.getHeight() <= HIGHT) {
                        view.setY(y);
                        if (mListener != null) {
                            mListener.OnPichChange(y / (HIGHT - VIEW_HIGHT));
                        }
                    }


                    break;

                case MotionEvent.ACTION_UP:
                    mVPich.animate().setDuration(200).translationY(0).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            if (mListener != null) {
                                mListener.OnPichChange(-1);
                            }
                        }
                    });


                    break;


            }

            return true;

        }
    };
}
