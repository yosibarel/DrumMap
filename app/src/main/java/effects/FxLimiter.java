package effects;

/**
 * Created by yossibarel on 04/04/16.
 */

import com.yossibarel.drummap.DrumMapJni;

import org.json.JSONException;
import org.json.JSONObject;


public class FxLimiter extends Effect {


    public static final int KEY_CEILLING_DB = 0;
    public static final int KEY_THRESHHOLD_DB = 1;
    public static final int KEY_RELEASE_SEC = 2;

    public static final String JKEY_LIMITER_CEILLING_DB = "JKEY_LIMITER_CEILLING_DB";
    public static final String JKEY_LIMITER_THRESHHOLD_DB = "JKEY_LIMITER_THRESHHOLD_DB";
    public static final String JKEY_LIMITER_RELEASE_SEC = "JKEY_LIMITER_RELEASE_SEC";
    public static final String JKEY_LIMITER_ENABLE = "JKEY_LIMITER_ENABLE";
    private static final String JKEY_LIMITER = "JKEY_LIMITER";


    @Override
    public int getNumParams() {
        return 3;
    }


    @Override
    public void setFx(int fxKeyParam, float val) {
        mDrunMap.setFx(mIndexChannel, FX_LIMITER, fxKeyParam, val);
    }

    @Override
    public void init() {
        mFxType = FX_LIMITER;
        mParamsName = new String[]{"Ceilling", "THold", "Rel sec"};
    }

    public FxLimiter(DrumMapJni drumMapJni, int indexChannel) {
        super(drumMapJni, indexChannel);
    }

    @Override
    public int getKeyEffectParam(int keyEffectParam) {
        return keyEffectParam;
    }

    public static void saveToJson(int channel, JSONObject json) throws JSONException {

        DrumMapJni drumMapJni = DrumMapJni.getInstance();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JKEY_LIMITER_ENABLE, drumMapJni.getFxValue(channel, FX_LIMITER, FX_ENABLED));
        jsonObject.put(JKEY_LIMITER_CEILLING_DB, drumMapJni.getFxValue(channel, FX_LIMITER, KEY_CEILLING_DB));
        jsonObject.put(JKEY_LIMITER_THRESHHOLD_DB, drumMapJni.getFxValue(channel, FX_LIMITER, KEY_THRESHHOLD_DB));
        jsonObject.put(JKEY_LIMITER_RELEASE_SEC, drumMapJni.getFxValue(channel, FX_LIMITER, KEY_RELEASE_SEC));
        json.put(JKEY_LIMITER, jsonObject);
    }

    public static void loadFromJson(int channel, JSONObject json) throws JSONException {
        DrumMapJni drumMapJni = DrumMapJni.getInstance();
        JSONObject jsonObject=json.getJSONObject(JKEY_LIMITER);
        drumMapJni.addEffect(channel,FX_LIMITER);
        drumMapJni.setFx(channel, FX_LIMITER, FX_ENABLED, (float) jsonObject.getDouble(JKEY_LIMITER_ENABLE));
        drumMapJni.setFx(channel, FX_LIMITER, KEY_CEILLING_DB, (float) jsonObject.getDouble(JKEY_LIMITER_CEILLING_DB));
        drumMapJni.setFx(channel, FX_LIMITER, KEY_THRESHHOLD_DB, (float) jsonObject.getDouble(JKEY_LIMITER_THRESHHOLD_DB));
        drumMapJni.setFx(channel, FX_LIMITER, KEY_RELEASE_SEC, (float) jsonObject.getDouble(JKEY_LIMITER_RELEASE_SEC));


    }

}
