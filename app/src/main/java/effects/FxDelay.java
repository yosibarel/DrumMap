package effects;

import com.yossibarel.drummap.DrumMapJni;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yossibarel on 03/04/16.
 */


public class FxDelay extends Effect {
    public static final int KEY_DELAY_MIX = 0;
    public static final int KEY_DELAY_BEATS = 1;
    public static final int KEY_DELAY_DECAY = 2;
    public static final String JKEY_DELAY_MIX = "JKEY_DELAY_MIX";
    public static final String JKEY_DELAY_BEATS = "JKEY_DELAY_BEATS";
    public static final String JKEY_DELAY_DECAY = "JKEY_DELAY_DECAY";
    public static final String JKEY_DELAY_ENABLED = "JKEY_DELAY_ENABLED";
    private static final String JKEY_DELAY = "JKEY_DELAY";


    @Override
    public int getNumParams() {
        return 3;
    }


    @Override
    public void init() {
        mFxType = FX_DELAY;
        mParamsName = new String[]{"Mix", "Beats", "Decay"};
    }


    @Override
    public void setFx(int fxKeyParam, float val) {
        mDrunMap.setFx(mIndexChannel, FX_DELAY, fxKeyParam, val);
    }

    public FxDelay(DrumMapJni drumMapJni, int indexChannel) {
        super(drumMapJni, indexChannel);
    }

    @Override
    public int getKeyEffectParam(int keyEffectParam) {
        return keyEffectParam;
    }


    public static void saveToJson(int channel, JSONObject json) throws JSONException {

        DrumMapJni drumMapJni = DrumMapJni.getInstance();
        JSONObject jsonObject=new JSONObject();
        jsonObject.put(JKEY_DELAY_ENABLED, drumMapJni.getFxValue(channel, FX_DELAY, FX_ENABLED));
        jsonObject.put(JKEY_DELAY_MIX, drumMapJni.getFxValue(channel, FX_DELAY, KEY_DELAY_MIX));
        jsonObject.put(JKEY_DELAY_BEATS, drumMapJni.getFxValue(channel, FX_DELAY, KEY_DELAY_BEATS));
        jsonObject.put(JKEY_DELAY_DECAY, drumMapJni.getFxValue(channel, FX_DELAY, KEY_DELAY_DECAY));
        json.put(JKEY_DELAY,jsonObject);
    }

    public static void loadFromJson(int channel, JSONObject json) throws JSONException {
        DrumMapJni drumMapJni = DrumMapJni.getInstance();
        drumMapJni.addEffect(channel,FX_DELAY);
        JSONObject jsonObject=json.getJSONObject(JKEY_DELAY);

        drumMapJni.setFx(channel, FX_DELAY, FX_ENABLED, (float) jsonObject.getDouble(JKEY_DELAY_ENABLED));
        drumMapJni.setFx(channel, FX_DELAY, KEY_DELAY_MIX, (float) jsonObject.getDouble(JKEY_DELAY_MIX));
        drumMapJni.setFx(channel, FX_DELAY, KEY_DELAY_BEATS, (float) jsonObject.getDouble(JKEY_DELAY_BEATS));
        drumMapJni.setFx(channel, FX_DELAY, KEY_DELAY_DECAY, (float) jsonObject.getDouble(JKEY_DELAY_DECAY));
    }

}
