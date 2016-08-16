package effects;

/**
 * Created by yossibarel on 04/04/16.
 */

import com.yossibarel.drummap.DrumMapJni;

import org.json.JSONException;
import org.json.JSONObject;


public class FxRoll extends Effect {


    public static final int KEY_WET = 0;
    public static final int KEY_BEATS = 1;
    public static final String JKEY_ROLL_WET = "JKEY_ROLL_WET";
    public static final String JKEY_ROLL_BEATS = "JKEY_ROLL_BEATS";
    public static final String JKEY_ROLL_ENABLE = "JKEY_ROLL_ENABLE";
    private static final String JFX_ROLL = "JFX_ROLL";


    @Override
    public int getNumParams() {
        return 2;
    }


    @Override
    public void setFx(int fxKeyParam, float val) {
        mDrunMap.setFx(mIndexChannel, FX_ROLL, fxKeyParam, val);
    }

    @Override
    public void init() {
        mFxType = FX_ROLL;
        mParamsName = new String[]{"Wet", "Beats"};
    }

    public FxRoll(DrumMapJni drumMapJni, int indexChannel) {
        super(drumMapJni, indexChannel);
    }

    @Override
    public int getKeyEffectParam(int keyEffectParam) {
        return keyEffectParam;
    }

    public static void saveToJson(int channel, JSONObject json) throws JSONException {

        JSONObject jsonObject = new JSONObject();
        DrumMapJni drumMapJni = DrumMapJni.getInstance();
        jsonObject.put(JKEY_ROLL_ENABLE, drumMapJni.getFxValue(channel, FX_ROLL, FX_ENABLED));
        jsonObject.put(JKEY_ROLL_WET, drumMapJni.getFxValue(channel, FX_ROLL, KEY_WET));
        jsonObject.put(JKEY_ROLL_BEATS, drumMapJni.getFxValue(channel, FX_ROLL, KEY_BEATS));
        json.put(JFX_ROLL, jsonObject);
    }

    public static void loadFromJson(int channel, JSONObject json) throws JSONException {
        DrumMapJni drumMapJni = DrumMapJni.getInstance();
        JSONObject jsonObject = json.getJSONObject(JFX_ROLL);
        drumMapJni.addEffect(channel,FX_LIMITER);
        drumMapJni.addEffect(channel,FX_ROLL);
        drumMapJni.setFx(channel, FX_ROLL, FX_ENABLED, (float) jsonObject.getDouble(JKEY_ROLL_ENABLE));
        drumMapJni.setFx(channel, FX_ROLL, KEY_WET, (float) jsonObject.getDouble(JKEY_ROLL_WET));
        drumMapJni.setFx(channel, FX_ROLL, KEY_BEATS, (float) jsonObject.getDouble(JKEY_ROLL_BEATS));

    }


}
