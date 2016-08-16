package views.Custom;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yossibarel.drummap.R;

/**
 * Created by yossibarel on 17/05/16.
 */
public class RangeBarVertical  extends RelativeLayout {

    private static int PROGRESS_HEIGHT;
    private static int HEIGHT;
    private final Context mContext;
    double mMax = 100.0;
    double mProgress1;
    double mProgress2;
    private View progress1;
    private View progress2;
    private View bar;
    private ValueTopBottomFormatter mFormater;
    private OnRangeChangeListener mListener;
    LayoutParams mLyProgress1;
    LayoutParams mLyProgress2;
    LayoutParams mLyBar;
    private RelativeLayout mContent;
    private TextView tvTop;
    private TextView tvBottom;


    public RangeBarVertical(Context context) {
        super(context);
        mContext = context;
        init();
        
    }


    public RangeBarVertical(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public void setFormater(ValueTopBottomFormatter formater) {
        mFormater = formater;
    }

    public void setOnRangeChangeListener(OnRangeChangeListener l) {
        mListener = l;
    }

    private void init() {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.range_bar_vertical, this, true);
        HEIGHT = 0;
        progress1 = view.findViewById(R.id.progress1);

        progress2 = view.findViewById(R.id.progress2);
        mContent = (RelativeLayout) view.findViewById(R.id.content);
        bar = view.findViewById(R.id.bar);
        progress1.setOnTouchListener(onProgressTouch);
        progress2.setOnTouchListener(onProgressTouch);
        ViewTreeObserver vto = getViewTreeObserver();

        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
                HEIGHT = mContent.getHeight();
                PROGRESS_HEIGHT = progress1.getHeight();


                setProgress1(mProgress1);
                setProgress2(mProgress2);
                setTextValue();
            }
        });
        mLyProgress1 = (LayoutParams) progress1.getLayoutParams();
        mLyProgress2 = (LayoutParams) progress2.getLayoutParams();

        mLyBar = (LayoutParams) bar.getLayoutParams();
        tvTop = (TextView) findViewById(R.id.tvTop);
        tvBottom = (TextView) findViewById(R.id.tvBottom);
        mContent.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                  /*  float x = (event.getRawX() - view.getX());

                    float p1 = Math.abs(progress1.getX() - x);
                    float p2 = Math.abs(progress2.getX() - x);
                    if (!(x - progress1.getWidth() / 2 >= 0 && x + progress1.getWidth() / 2 <= WIDTH))
                        return false;
                    if (p1 < p2) {

                        progress1.setX((x + progress1.getWidth() / 2)-view.getX() );
                    } else {

                            progress2.setX((x + progress2.getWidth() / 2) -view.getX());
                    }

                    updateBar();
                    updateListener();*/


                }
                return false;
            }
        });
    }

    float dY;
    OnTouchListener onProgressTouch = new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {


            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:

                    dY = view.getY() - event.getRawY();

                    break;
                case MotionEvent.ACTION_MOVE:

                    float y = event.getRawY() + dY;

                    Log.e("y", y + "");
                    if (y >= 0.0 &&y + view.getHeight() <= HEIGHT)
                        view.setY(y);
                    else if (y < 0.0)
                        view.setY(0);
                    else
                        view.setY(HEIGHT - PROGRESS_HEIGHT);
                    updateBar();
                    updateListener();


                    break;

                case MotionEvent.ACTION_UP:
                    y= event.getRawY() + dY;

                    Log.e("y", y + "");
                    if (y >= 0.0 && y + view.getHeight() <= HEIGHT)
                        view.setY(y);
                    else if (y < 0.0)
                        view.setY(0);
                    else
                        view.setY(HEIGHT - PROGRESS_HEIGHT);

                    updateBar();
                    updateListener();

                    break;


            }

            return true;

        }
    };

    private void updateListener() {
        setTextValue();
        if (mListener != null) {
            float top = Math.min(progress1.getY(), progress2.getY());
            float bottom = Math.max(progress1.getY(), progress2.getY());

            double rtop = (double) top / (HEIGHT - PROGRESS_HEIGHT) * mMax;
            double rbottom = (double) bottom / (HEIGHT - PROGRESS_HEIGHT) * mMax;

            mListener.onRangeChange(rtop, rbottom);
            if (mFormater != null) {
                tvTop.setText(mFormater.getTop(rtop));
                tvBottom.setText(mFormater.getBottom(rbottom));
            }
        }
    }

    void setTextValue() {

        float top = Math.min(progress1.getY(), progress2.getY());
        float bottom = Math.max(progress1.getY(), progress2.getY());


        double rtop = (double) top / (HEIGHT - PROGRESS_HEIGHT) * mMax;
        double rbottom = (double) bottom / (HEIGHT - PROGRESS_HEIGHT) * mMax;


        if (mFormater != null) {
            tvTop.setText(mFormater.getTop(rtop));
            tvBottom.setText(mFormater.getBottom(rbottom));
        }

    }

    public void setMax(float max) {
        mMax = max;
    }

    public void setProgress2(double progress) {
        mProgress2 = progress;
        if (mFormater != null)
            tvBottom.setText(mFormater.getBottom(progress));
        if (HEIGHT > 0) {
            progress2.setY((float) ((progress / mMax) * (HEIGHT - PROGRESS_HEIGHT)));
            updateBar();
        }
    }

    public void setProgress1(double progress) {
        mProgress1 = progress;
        if (mFormater != null)
            tvTop.setText(mFormater.getTop(progress));
        if (HEIGHT > 0) {
            progress1.setY((float) ((progress / mMax) * (HEIGHT - PROGRESS_HEIGHT)));
            updateBar();
        }
    }

    void updateBar() {
        bar.setY((progress1.getY() < progress2.getY() ? progress1.getY() : progress2.getY()) + PROGRESS_HEIGHT / 2);
        mLyBar.height = (int) Math.abs(progress1.getY() - progress2.getY());
        bar.setLayoutParams(mLyBar);
    }

    public void updateSize() {
        HEIGHT = mContent.getHeight();
        PROGRESS_HEIGHT = progress1.getHeight();
        setProgress1(mProgress1);
        setProgress2(mProgress2);
        setTextValue();
    }

    public float getMax() {
        return (float) mMax;
    }

    public interface OnRangeChangeListener {


        void onRangeChange(double top, double bottom);

    }

    public interface ValueTopBottomFormatter {
        String getTop(double value);

        String getBottom(double value);
    }


}
