package views.Custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashSet;
import java.util.Random;

/**
 * Created by yossibarel on 07/04/16.
 */
public class ViewControlXY extends View {
    private static final String TAG = "CirclesDrawingView";
    private OnValueChangeListener mListener;
    int h;
    int w;

    public interface OnValueChangeListener {
        void onValueChange(double x, double y);
    }

    public void setOnValueChangeLitener(OnValueChangeListener l) {
        mListener = l;
    }


    private Rect mMeasuredRect;

    /**
     * Stores data about single circle
     */
    private static class CircleArea {
        int radius;
        int centerX;
        int centerY;

        CircleArea(int centerX, int centerY, int radius) {
            this.radius = radius;
            this.centerX = centerX;
            this.centerY = centerY;
        }

        @Override
        public String toString() {
            return "Circle[" + centerX + ", " + centerY + ", " + radius + "]";
        }

    }

    /**
     * Paint to draw circles
     */
    private Paint mCirclePaint;

    private final Random mRadiusGenerator = new Random();
    // Radius limit in pixels
    private final static int RADIUS_LIMIT = 100;

    private static final int CIRCLES_LIMIT = 3;

    /**
     * All available circles
     */
    private HashSet<CircleArea> mCircles = new HashSet<CircleArea>(CIRCLES_LIMIT);
    private SparseArray<CircleArea> mCirclePointer = new SparseArray<CircleArea>(CIRCLES_LIMIT);

    /**
     * Default constructor
     *
     * @param ct {@link Context}
     */
    public ViewControlXY(final Context ct) {
        super(ct);

        init(ct);
    }

    public ViewControlXY(final Context ct, final AttributeSet attrs) {
        super(ct, attrs);

        init(ct);
    }

    public ViewControlXY(final Context ct, final AttributeSet attrs, final int defStyle) {
        super(ct, attrs, defStyle);

        init(ct);
    }

    private void init(final Context ct) {
        // Generate bitmap used for background
        // mBitmap = BitmapFactory.decodeResource(ct.getResources(), R.drawable.img);

        mCirclePaint = new Paint();


        invalidate();
    }

    @Override
    public void onDraw(final Canvas canv) {
        // background bitmap to cover all area
        //   canv.drawBitmap(mBitmap, null, mMeasuredRect, null);

        for (CircleArea circle : mCircles) {
            mCirclePaint.setColor(0xff003368);
            mCirclePaint.setStrokeWidth(10);
            canv.drawCircle(circle.centerX, circle.centerY, circle.radius, mCirclePaint);
            mCirclePaint.setColor(0xff006699);
            mCirclePaint.setStrokeWidth(4);
            canv.drawLine(0, circle.centerY, mMeasuredRect.width(), circle.centerY, mCirclePaint);
            canv.drawLine(circle.centerX, 0, circle.centerX, mMeasuredRect.height(), mCirclePaint);
        }
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        boolean handled = false;

        CircleArea touchedCircle;
        int xTouch = 0;
        int yTouch = 0;
        int pointerId;
        int actionIndex = event.getActionIndex();

        // get touch event coordinates and make transparent circle from it
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                // it's the first pointer, so clear all existing pointers data
                clearCirclePointer();

                xTouch = (int) event.getX(0);
                yTouch = (int) event.getY(0);

                // check if we've touched inside some circle
                touchedCircle = obtainTouchedCircle(xTouch, yTouch);
                touchedCircle.centerX = xTouch;
                touchedCircle.centerY = yTouch;
                mCirclePointer.put(event.getPointerId(0), touchedCircle);

                invalidate();
                handled = true;
                changeValue(xTouch / (double) mMeasuredRect.width(), yTouch / (double) mMeasuredRect.height());
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                Log.w(TAG, "Pointer down");
                // It secondary pointers, so obtain their ids and check circles
                pointerId = event.getPointerId(actionIndex);

                xTouch = (int) event.getX(actionIndex);
                yTouch = (int) event.getY(actionIndex);

                // check if we've touched inside some circle
                touchedCircle = obtainTouchedCircle(xTouch, yTouch);

                mCirclePointer.put(pointerId, touchedCircle);
                touchedCircle.centerX = xTouch;
                touchedCircle.centerY = yTouch;
                invalidate();
                handled = true;
                changeValue(xTouch / (double) mMeasuredRect.width(), yTouch / (double) mMeasuredRect.height());
                break;

            case MotionEvent.ACTION_MOVE:
                final int pointerCount = event.getPointerCount();

                Log.w(TAG, "Move");

                for (actionIndex = 0; actionIndex < pointerCount; actionIndex++) {
                    // Some pointer has moved, search it by pointer id
                    pointerId = event.getPointerId(actionIndex);

                    xTouch = (int) event.getX(actionIndex);
                    yTouch = (int) event.getY(actionIndex);

                    touchedCircle = mCirclePointer.get(pointerId);

                    if (null != touchedCircle) {
                        touchedCircle.centerX = xTouch;
                        touchedCircle.centerY = yTouch;
                    }
                }
                invalidate();
                handled = true;
                changeValue(xTouch / (double) mMeasuredRect.width(), yTouch / (double) mMeasuredRect.height());
                break;

            case MotionEvent.ACTION_UP:
                clearCirclePointer();
                invalidate();
                handled = true;
                //  changeValue(xTouch/(double)mMeasuredRect.width(),yTouch/(double)mMeasuredRect.height());
                break;

            case MotionEvent.ACTION_POINTER_UP:
                // not general pointer was up
                pointerId = event.getPointerId(actionIndex);

                mCirclePointer.remove(pointerId);
                invalidate();
                handled = true;
                changeValue(xTouch / (double) mMeasuredRect.width(), yTouch / (double) mMeasuredRect.height());
                break;

            case MotionEvent.ACTION_CANCEL:
                handled = true;
                break;

            default:
                // do nothing
                break;
        }

        return true;
    }

    private void changeValue(double x, double y) {
        if (mListener != null)
            mListener.onValueChange(x, y);
    }

    /**
     * Clears all CircleArea - pointer id relations
     */
    private void clearCirclePointer() {
        Log.w(TAG, "clearCirclePointer");

        mCirclePointer.clear();
    }

    /**
     * Search and creates new (if needed) circle based on touch area
     *
     * @param xTouch int x of touch
     * @param yTouch int y of touch
     * @return obtained {@link CircleArea}
     */
    private CircleArea obtainTouchedCircle(final int xTouch, final int yTouch) {
        CircleArea touchedCircle = getTouchedCircle(xTouch, yTouch);

        if (null == touchedCircle) {
            touchedCircle = new CircleArea(xTouch, yTouch, 30);

            if (mCircles.size() == 1) {
                Log.w(TAG, "Clear all circles, size is " + mCircles.size());
                // remove first circle
                mCircles.clear();
            }

            Log.w(TAG, "Added circle " + touchedCircle);
            mCircles.add(touchedCircle);
        }

        return touchedCircle;
    }

    /**
     * Determines touched circle
     *
     * @param xTouch int x touch coordinate
     * @param yTouch int y touch coordinate
     * @return {@link CircleArea} touched circle or null if no circle has been touched
     */
    private CircleArea getTouchedCircle(final int xTouch, final int yTouch) {
        CircleArea touched = null;

        for (CircleArea circle : mCircles) {
            if ((circle.centerX - xTouch) * (circle.centerX - xTouch) + (circle.centerY - yTouch) * (circle.centerY - yTouch) <= circle.radius * circle.radius) {
                touched = circle;
                break;
            }
        }

        return touched;
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        h = getMeasuredHeight();
        w = getMeasuredWidth();
        mMeasuredRect = new Rect(0, 0, getMeasuredWidth(), getMeasuredHeight());
        CircleArea touchedCircle;
        mCirclePaint.setColor(0xff003368);
        mCirclePaint.setStrokeWidth(10);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        touchedCircle = obtainTouchedCircle(0, 0);
        touchedCircle.centerX = w / 2;
        touchedCircle.centerY = h / 2;
        mCirclePointer.put(0, touchedCircle);
    }
}