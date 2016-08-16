package views.Custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;


/**
 * Created by yossibarel on 25/01/16.
 */
public class ViewMeter extends View {
    private float UNIT_HEIGHT = 20;
    private Paint paint = new Paint();
    private int mColor1;
    private int mColor2;
    private int mColor3;
    private int mColorBG;
    boolean mIsDraw = false;
    private float val;
    Shader mShaderMute;
    Shader mShaderNotmal;

    private boolean mIsMute = false;

    public ViewMeter(Context context) {
        super(context);
        paint.setColor(Color.BLACK);
        mColor1 = 0xff00ff00;
        mColor2 = 0xffffff00;
        mColor2 = 0xffff0000;
        val = 0.0f;

    }

    public ViewMeter(Context context, AttributeSet attributes) {
        super(context, attributes);
        paint.setColor(Color.BLACK);
        val = 0.0f;
    }

    public void setValue(float value) {
        val = value;
    }

    void initDraw(Canvas canvas) {
        mShaderMute = new LinearGradient(0, 0, canvas.getWidth(), canvas.getHeight(), Color.DKGRAY, Color.GRAY, Shader.TileMode.CLAMP);
        mShaderNotmal = new LinearGradient(0, 0, canvas.getWidth(), canvas.getHeight(), Color.RED, Color.YELLOW, Shader.TileMode.CLAMP);
    }


    @Override
    public void onDraw(Canvas canvas) {

        if (!mIsDraw) {
            mIsDraw = true;
            initDraw(canvas);
        }
        if (mIsMute)
            paint.setShader(mShaderMute);
        else
            paint.setShader(mShaderNotmal);
        canvas.drawRect(0, canvas.getHeight() - (int) ((double) canvas.getHeight() * val), canvas.getWidth(), canvas.getHeight(), paint);
    }


    public void setMuteState(boolean isMute) {
        mIsMute = isMute;
    }
}
