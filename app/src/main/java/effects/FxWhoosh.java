package effects;

import com.yossibarel.drummap.DrumMapJni;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yossibarel on 16/04/16.
 */
public class FxWhoosh extends Effect {
    public static final int KEY_WHOOSH_WET = 0;
    public static final int KEY_WHOOSH = 1;

    public static final String JKEY_WHOOSH_WET = "JKEY_WHOOSH_WET";
    public static final String JKEY_WHOOSH_FREQ = "JKEY_WHOOSH_FREQ";
    public static final String JKEY_WHOOSH_ENABLE = "JKEY_WHOOSH_ENABLE";
    private static final String JKEY_WHOOSH = "JKEY_WHOOSH";


    @Override
    public int getNumParams() {
        return 2;
    }


    @Override
    public void init() {
        mFxType = FX_WHOOSH;
        mParamsName = new String[]{"Wet", "Freq"};
    }


    @Override
    public void setFx(int fxKeyParam, float val) {
        mDrunMap.setFx(mIndexChannel, FX_WHOOSH, fxKeyParam, val);
    }

    public FxWhoosh(DrumMapJni drumMapJni, int indexChannel) {
        super(drumMapJni, indexChannel);
    }

    @Override
    public int getKeyEffectParam(int keyEffectParam) {
        return keyEffectParam;
    }
    public static void saveToJson(int channel, JSONObject json) throws JSONException {

        DrumMapJni drumMapJni = DrumMapJni.getInstance();
        JSONObject jsonObject=new JSONObject();
        jsonObject.put(JKEY_WHOOSH_ENABLE, drumMapJni.getFxValue(channel, FX_WHOOSH, FX_ENABLED));
        jsonObject.put(JKEY_WHOOSH_WET, drumMapJni.getFxValue(channel, FX_WHOOSH, KEY_WHOOSH_WET));
        jsonObject.put(JKEY_WHOOSH_FREQ, drumMapJni.getFxValue(channel, FX_WHOOSH, KEY_WHOOSH));
        json.put(JKEY_WHOOSH,jsonObject);
    }

    public static void loadFromJson(int channel, JSONObject json) throws JSONException {
        DrumMapJni drumMapJni = DrumMapJni.getInstance();
        JSONObject jsonObject=json.getJSONObject(JKEY_WHOOSH);
        drumMapJni.addEffect(channel,FX_WHOOSH);
        drumMapJni.setFx(channel, FX_WHOOSH, FX_ENABLED, (float) jsonObject.getDouble(JKEY_WHOOSH_ENABLE));
        drumMapJni.setFx(channel, FX_WHOOSH, KEY_WHOOSH_WET, (float) jsonObject.getDouble(JKEY_WHOOSH_WET));
        drumMapJni.setFx(channel, FX_WHOOSH, KEY_WHOOSH, (float) jsonObject.getDouble(JKEY_WHOOSH_FREQ));

    }
}