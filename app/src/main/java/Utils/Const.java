package Utils;

import android.content.Context;
import android.os.Environment;

/**
 * Created by yossibarel on 08/05/16.
 */
public class Const {

    public static String getAppFolder(Context context) {
        return Environment.getExternalStorageDirectory().toString() + "/" + Environment.DIRECTORY_MUSIC + "/SynthPro";
    }
}
