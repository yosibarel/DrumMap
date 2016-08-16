package effects;

import com.yossibarel.drummap.DrumMapJni;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yossibarel on 04/04/16.
 */
public class FxFlanger extends Effect {
    public static final int KEY_FLANGER_WET = 0;
    public static final int KEY_FLANGER_DEPTH = 1;
    public static final int KEY_FLANGER_LFO_BEATS = 2;
    public static final int KEY_FLANGER_CLIPPER_DB = 3;
    public static final int KEY_FLANGER_CLIPPER_MAX_DB = 4;

    public static final String JKEY_FLANGER_WET = "JKEY_FLANGER_WET";
    public static final String JKEY_FLANGER_DEPTH = "JKEY_FLANGER_DEPTH";
    public static final String JKEY_FLANGER_LFO_BEATS = "JKEY_FLANGER_LFO_BEATS";
    public static final String JKEY_FLANGER_CLIPPER_DB = "JKEY_FLANGER_CLIPPER_DB";
    public static final String JKEY_FLANGER_CLIPPER_MAX_DB = "JKEY_FLANGER_CLIPPER_MAX_DB";
    public static final String JKEY_FLANGER_ENABLED = "JKEY_FLANGER_ENABLED";
    private static final String JKEY_FLANGER = "JKEY_FLANGER";


    @Override
    public int getNumParams() {
        return 5;
    }


    @Override
    public void init() {
        mFxType = FX_FLANGER;
        mParamsName = new String[]{"Wet", "Depth", "LFO Beats", "Clpr db", "Clpr MAX db"};
    }


    @Override
    public void setFx(int fxKeyParam, float val) {
        mDrunMap.setFx(mIndexChannel, FX_FLANGER, fxKeyParam, val);
    }

    public FxFlanger(DrumMapJni drumMapJni, int indexChannel) {
        super(drumMapJni, indexChannel);
    }

    @Override
    public int getKeyEffectParam(int keyEffectParam) {
        return keyEffectParam;
    }

    public static void saveToJson(int channel, JSONObject json) throws JSONException {

        DrumMapJni drumMapJni = DrumMapJni.getInstance();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JKEY_FLANGER_ENABLED, drumMapJni.getFxValue(channel, FX_FLANGER, FX_ENABLED));
        jsonObject.put(JKEY_FLANGER_WET, drumMapJni.getFxValue(channel, FX_FLANGER, KEY_FLANGER_WET));
        jsonObject.put(JKEY_FLANGER_DEPTH, drumMapJni.getFxValue(channel, FX_FLANGER, KEY_FLANGER_DEPTH));
        jsonObject.put(JKEY_FLANGER_LFO_BEATS, drumMapJni.getFxValue(channel, FX_FLANGER, KEY_FLANGER_LFO_BEATS));
        jsonObject.put(JKEY_FLANGER_CLIPPER_DB, drumMapJni.getFxValue(channel, FX_FLANGER, KEY_FLANGER_CLIPPER_DB));
        jsonObject.put(JKEY_FLANGER_CLIPPER_MAX_DB, drumMapJni.getFxValue(channel, FX_FLANGER, KEY_FLANGER_CLIPPER_MAX_DB));
        json.put(JKEY_FLANGER, jsonObject);

    }

    public static void loadFromJson(int channel, JSONObject json) throws JSONException {
        DrumMapJni drumMapJni = DrumMapJni.getInstance();
        drumMapJni.addEffect(channel,FX_FLANGER);
        JSONObject jsonObject = json.getJSONObject(JKEY_FLANGER);
        drumMapJni.setFx(channel, FX_FLANGER, FX_ENABLED, (float) jsonObject.getDouble(JKEY_FLANGER_ENABLED));
        drumMapJni.setFx(channel, FX_FLANGER, KEY_FLANGER_WET, (float) jsonObject.getDouble(JKEY_FLANGER_WET));
        drumMapJni.setFx(channel, FX_FLANGER, KEY_FLANGER_DEPTH, (float) jsonObject.getDouble(JKEY_FLANGER_DEPTH));
        drumMapJni.setFx(channel, FX_FLANGER, KEY_FLANGER_LFO_BEATS, (float) jsonObject.getDouble(JKEY_FLANGER_LFO_BEATS));
        drumMapJni.setFx(channel, FX_FLANGER, KEY_FLANGER_CLIPPER_DB, (float) jsonObject.getDouble(JKEY_FLANGER_CLIPPER_DB));
        drumMapJni.setFx(channel, FX_FLANGER, KEY_FLANGER_CLIPPER_MAX_DB, (float) jsonObject.getDouble(JKEY_FLANGER_CLIPPER_MAX_DB));
    }
}
