package effects;

import com.yossibarel.drummap.DrumMapJni;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yossibarel on 04/04/16.
 */
public class FxFilterBandPass  extends Effect {
    public static final int KEY_FREQ = 0;
    public static final int KEY_OCTAVE = 3;
    private static final String JKEY_FILTER_BAND_PASS = "JKEY_FILTER_BAND_PASS";
    private static final String JKEY_FILTER_ENABLE = "JKEY_FILTER_ENABLE";
    private static final String JKEY_FILTER_FREQ = "JKEY_FILTER_FREQ";
    private static final String JKEY_FILTER_OCTAVE = "JKEY_FILTER_OCTAVE";


    @Override
    public int getNumParams() {
        return 2;
    }

    public String getName() {
        return "Band Pass";
    }
    @Override
    public void init() {
        mFxType = FILTER_BAND_PASS;
        mParamsName = new String[]{"Freq", "Oct"};
    }


    @Override
    public void setFx(int fxKeyParam, float val) {
        mDrunMap.setFx(mIndexChannel, Effect.FILTER_BAND_PASS, fxKeyParam, val);
    }

    public FxFilterBandPass(DrumMapJni drumMapJni, int indexChannel) {
        super(drumMapJni, indexChannel);
    }
    @Override
    public int getKeyEffectParam(int keyEffectParam) {
        return keyEffectParam;
    }
    public static void saveToJson(int channel, JSONObject json) throws JSONException {

        DrumMapJni drumMapJni = DrumMapJni.getInstance();
        JSONObject jsonObject=new JSONObject();
        jsonObject.put(JKEY_FILTER_ENABLE, drumMapJni.getFxValue(channel, FILTER_BAND_PASS, FX_ENABLED));
        jsonObject.put(JKEY_FILTER_FREQ, drumMapJni.getFxValue(channel, FILTER_BAND_PASS, KEY_FREQ));
        jsonObject.put(JKEY_FILTER_OCTAVE, drumMapJni.getFxValue(channel, FILTER_BAND_PASS, KEY_OCTAVE));

        json.put(JKEY_FILTER_BAND_PASS, jsonObject);
    }

    public static void loadFromJson(int channel, JSONObject json) throws JSONException {
        DrumMapJni drumMapJni = DrumMapJni.getInstance();
        JSONObject jsonObject=json.getJSONObject(JKEY_FILTER_BAND_PASS);
        drumMapJni.setFx(channel, FILTER_BAND_PASS, FX_ENABLED, (float) jsonObject.getDouble(JKEY_FILTER_ENABLE));
        drumMapJni.setFx(channel, FILTER_BAND_PASS, KEY_FREQ, (float) jsonObject.getDouble(JKEY_FILTER_FREQ));
        drumMapJni.setFx(channel, FILTER_BAND_PASS, KEY_OCTAVE, (float) jsonObject.getDouble(JKEY_FILTER_OCTAVE));
    }
}
