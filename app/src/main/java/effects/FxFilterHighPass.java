package effects;

/**
 * Created by yossibarel on 04/04/16.
 */

import com.yossibarel.drummap.DrumMapJni;

import org.json.JSONException;
import org.json.JSONObject;


public class FxFilterHighPass  extends Effect {
    public static final int KEY_FREQ = 0;
    public static final int KEY_RES = 1;
    private static final String JKEY_FILTER_ENABLE = "JKEY_FILTER_ENABLE";
    private static final String JKEY_FILTER_FREQ = "JKEY_FILTER_FREQ";
    private static final String JKEY_FILTER_RES = "JKEY_FILTER_RES";
    private static final String JKEY_FILTER_HIGH_PASS = "JKEY_FILTER_HIGH_PASS";


    @Override
    public int getNumParams() {
        return 2;
    }
    public String getName() {
        return "High Pass";
    }

    @Override
    public void init() {
        mFxType = FILTER_HIGH_PASS;
        mParamsName = new String[]{"Freq", "Res"};
    }


    @Override
    public void setFx(int fxKeyParam, float val) {
        mDrunMap.setFx(mIndexChannel, FILTER_HIGH_PASS, fxKeyParam, val);
    }

    public FxFilterHighPass(DrumMapJni drumMapJni, int indexChannel) {
        super(drumMapJni, indexChannel);
    }
    @Override
    public int getKeyEffectParam(int keyEffectParam) {
        return keyEffectParam;
    }

    public static void saveToJson(int channel, JSONObject json) throws JSONException {

        DrumMapJni drumMapJni = DrumMapJni.getInstance();
        JSONObject jsonObject=new JSONObject();
        jsonObject.put(JKEY_FILTER_ENABLE, drumMapJni.getFxValue(channel, FILTER_HIGH_PASS, FX_ENABLED));
        jsonObject.put(JKEY_FILTER_FREQ, drumMapJni.getFxValue(channel, FILTER_HIGH_PASS, KEY_FREQ));
        jsonObject.put(JKEY_FILTER_RES, drumMapJni.getFxValue(channel, FILTER_HIGH_PASS, KEY_RES));

        json.put(JKEY_FILTER_HIGH_PASS, jsonObject);
    }

    public static void loadFromJson(int channel, JSONObject json) throws JSONException {
        DrumMapJni drumMapJni = DrumMapJni.getInstance();

        JSONObject jsonObject=json.getJSONObject(JKEY_FILTER_HIGH_PASS);
        drumMapJni.setFx(channel, FILTER_HIGH_PASS, FX_ENABLED, (float) jsonObject.getDouble(JKEY_FILTER_ENABLE));
        drumMapJni.setFx(channel, FILTER_HIGH_PASS, KEY_FREQ, (float) jsonObject.getDouble(JKEY_FILTER_FREQ));
        drumMapJni.setFx(channel, FILTER_HIGH_PASS, KEY_RES, (float) jsonObject.getDouble(JKEY_FILTER_RES));
    }
}
