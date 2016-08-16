package views.Custom;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import interfaces.CloseAnimationListener;
import interfaces.AnimtionListener;

/**
 * Created by yossibarel on 29/04/16.
 */
public class LinearLayoutTouched extends LinearLayout implements CloseAnimationListener {
    private float mPivotX;
    private float mPivotY;
    private float mScaleX;
    private float mScaleY;
    private Runnable mAnimationListener;

    private enum StateTouch {
        TOUCH, NOT_TOUCH, NEED_TO_CLOSE
    }

    private StateTouch mStateTouch;

    private boolean mIsOpen;
    private int mTimeCloseMs;
    private AnimtionListener mOnOpenAnimtionListener;

    public LinearLayoutTouched(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LinearLayoutTouched(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LinearLayoutTouched(Context context) {
        super(context);

    }

    @Override
    public void closeAnimation() {
        mHandlerPostDelay.removeCallbacks(mRunnablePostDelay);
        animate().scaleX(1).scaleY(1).setDuration(250).withEndAction(new Runnable() {
            @Override
            public void run() {

                mOnOpenAnimtionListener.onCloseAnimation(LinearLayoutTouched.this);
            }
        });

        mIsOpen = false;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d("touch", "touch");
        mStateTouch = StateTouch.TOUCH;


        if (!mIsOpen) {

            bringToFront();

            animate().scaleX(mScaleX).scaleY(mScaleY).setDuration(250);
            mIsOpen = true;
            mHandlerPostDelay.postDelayed(mRunnablePostDelay, mTimeCloseMs);
            mOnOpenAnimtionListener.onOpenAnimation(this);
            return true;
        }
        return false;
    }

    Handler mHandlerPostDelay = new Handler();
    Runnable mRunnablePostDelay = new Runnable() {
        @Override
        public void run() {
            if (mStateTouch == StateTouch.NEED_TO_CLOSE) {
                closeAnimation();
            } else
                mHandlerPostDelay.postDelayed(this, mTimeCloseMs);
            if (mStateTouch == StateTouch.TOUCH)
                mStateTouch = StateTouch.NOT_TOUCH;
            else if (mStateTouch == StateTouch.NOT_TOUCH)
                mStateTouch = StateTouch.NEED_TO_CLOSE;
        }
    };

    public void setAnimationParam(float pivotX, float pivotY, float scaleX, float scaleY, int timeCloseMs, AnimtionListener l) {
        mIsOpen = false;
        mPivotX = pivotX;
        mPivotY = pivotY;
        mScaleX = scaleX;
        mScaleY = scaleY;
        setPivotX(pivotX);
        setPivotY(pivotY);
        mTimeCloseMs = timeCloseMs;
        mOnOpenAnimtionListener = l;
    }


}
