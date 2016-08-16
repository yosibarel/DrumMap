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
 * Created by yossibarel on 10/04/16.
 */
public class RangeBar extends RelativeLayout {

    private static int PROGRESS_WIDTH;
    private static int WIDTH;
    private final Context mContext;
    double mMax = 100.0;
    double mProgress1;
    double mProgress2;
    private View progress1;
    private View progress2;
    private View bar;
    private ValueLeftRightFormatter mFormater;
    private OnRangeChangeListener mListener;
    LayoutParams mLyProgress1;
    LayoutParams mLyProgress2;
    LayoutParams mLyBar;
    private RelativeLayout mContent;
    private TextView tvLeft;
    private TextView tvRight;
    private View cursor;

    public RangeBar(Context context) {
        super(context);
        mContext = context;
        init();
    }


    public RangeBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public void setFormater(ValueLeftRightFormatter formater) {
        mFormater = formater;
    }

    public void setOnRangeChangeListener(OnRangeChangeListener l) {
        mListener = l;
    }

    private void init() {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.range_bar, this, true);
        WIDTH = 0;
        progress1 = view.findViewById(R.id.progress1);
        cursor = view.findViewById(R.id.cursor);
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
                updateSize();
            }
        });
        mLyProgress1 = (LayoutParams) progress1.getLayoutParams();
        mLyProgress2 = (LayoutParams) progress2.getLayoutParams();

        mLyBar = (LayoutParams) bar.getLayoutParams();
        tvLeft = (TextView) findViewById(R.id.tvleft);
        tvRight = (TextView) findViewById(R.id.tvRight);
        mContent.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                  /* float x = (event.getRawX() - view.getX());

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

    float dX;
    OnTouchListener onProgressTouch = new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {


            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:

                    dX = view.getX() - event.getRawX();

                    break;
                case MotionEvent.ACTION_MOVE:

                    float x = event.getRawX() + dX;

                    Log.e("x", x + "");
                    if (x >= 0.0 && x + view.getWidth() <= WIDTH)
                        view.setX(x);
                    else if (x < 0.0)
                        view.setX(0);
                    else
                        view.setX(WIDTH - PROGRESS_WIDTH);
                    updateBar();
                    updateListener();


                    break;

                case MotionEvent.ACTION_UP:
                    x = event.getRawX() + dX;

                    Log.e("x", x + "");
                    if (x >= 0.0 && x + view.getWidth() <= WIDTH)
                        view.setX(x);
                    else if (x < 0.0)
                        view.setX(0);
                    else
                        view.setX(WIDTH - PROGRESS_WIDTH);

                    updateBar();
                    updateListener();

                    break;


            }

            return true;

        }
    };

    public void updateSize() {
        WIDTH = mContent.getWidth();
        PROGRESS_WIDTH = progress1.getWidth();
        setProgress1(mProgress1);
        setProgress2(mProgress2);
        setTextValue();
    }

    public void setCursorPresent(double precent) {
        cursor.setX((float) ((WIDTH - PROGRESS_WIDTH * 2.0) * precent + (PROGRESS_WIDTH - cursor.getWidth() / 2)));
    }

    private void updateListener() {
        setTextValue();
        if (mListener != null) {
            float left = Math.min(progress1.getX(), progress2.getX());
            float right = Math.max(progress1.getX(), progress2.getX());

            double rleft = (double) left / (WIDTH - PROGRESS_WIDTH) * mMax;
            double rright = (double) right / (WIDTH - PROGRESS_WIDTH) * mMax;

            mListener.onRangeChange(rleft, rright);
            if (mFormater != null) {
                tvLeft.setText(mFormater.getLeft(rleft));
                tvRight.setText(mFormater.getRight(rright));
            }
        }
    }

    void setTextValue() {

        float left = Math.min(progress1.getX(), progress2.getX());
        float right = Math.max(progress1.getX(), progress2.getX());


        double rleft = (double) left / (WIDTH - PROGRESS_WIDTH) * mMax;
        double rright = (double) right / (WIDTH - PROGRESS_WIDTH) * mMax;


        if (mFormater != null) {
            tvLeft.setText(mFormater.getLeft(rleft));
            tvRight.setText(mFormater.getRight(rright));
        }

    }

    public void setMax(float max) {
        mMax = max;
    }

    public void setProgress2(double progress) {
        mProgress2 = progress;
        if (WIDTH > 0) {
            progress2.setX((float) ((progress / mMax) * (WIDTH - PROGRESS_WIDTH)));
            updateBar();


        }
        setTextValue();
    }

    public void setProgress1(double progress) {
        mProgress1 = progress;
        if (WIDTH > 0) {
            progress1.setX((float) ((progress / mMax) * (WIDTH - PROGRESS_WIDTH)));
            updateBar();

        }
        setTextValue();
    }

    void updateBar() {
        bar.setX((progress1.getX() < progress2.getX() ? progress1.getX() : progress2.getX()) + PROGRESS_WIDTH / 2);
        mLyBar.width = (int) Math.abs(progress1.getX() - progress2.getX());
        bar.setLayoutParams(mLyBar);
    }

    public float getMax() {
        return (float) mMax;
    }

    public interface OnRangeChangeListener {
        void onRangeChange(double left, double right);

    }

    public interface ValueLeftRightFormatter {
        String getLeft(double value);

        String getRight(double value);
    }


}
