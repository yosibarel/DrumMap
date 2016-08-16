package Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yossibarel on 10/04/16.
 */
public class TimeUtils {
    public static String getStringPosition(double postionMs) {

        return String.format("%02d:%02d.%03d", (int) (postionMs / 60000), (int) ((postionMs % 60000) / 1000), (int) postionMs % 1000);
    }

    public static String getFileTimeString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss");
        Date now = new Date();
        String strDate = sdf.format(now);
        return "sp_" + strDate;
    }

   public static double getMsFromSizeArray(double sampleRate, int channels, int bps, int sizeArr) {
        return sizeArr / (sampleRate * (bps / 8) * channels);
    }

    public static String getBpmFromMs(double ms, float bpm) {
        return String.format("%.1f",(ms/1000.0) / bpm);
    }
}
