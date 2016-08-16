package Utils;

import android.content.Context;

/**
 * Created by yossibarel on 24/04/16.
 */
public class SizeCalc {
    public static float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }
}
