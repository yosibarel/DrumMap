package effects;

import com.yossibarel.drummap.DrumMapJni;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yossibarel on 05/04/16.
 */
public class FxGate extends Effect {


    public static final int KEY_WET = 0;

    public static final int KEY_BEATS = 1;
    public static final String JKEY_GATE_WET = "JKEY_GATE_WET";
    public static final String JKEY_GATE_BEATS = "JKEY_GATE_BEATS";
    public static final String JKEY_GATE_ENABLE = "JKEY_GATE_ENABLE";
    private static final String JKEY_GATE = "JKEY_GATE";


    @Override
    public int getNumParams() {
        return 2;
    }


    @Override
    public void setFx(int fxKeyParam, float val) {
        mDrunMap.setFx(mIndexChannel, FX_GATE, fxKeyParam, val);
    }

    @Override
    public void init() {
        mFxType = FX_GATE;
        mParamsName = new String[]{"Wet", "Beats"};
    }

    public FxGate(DrumMapJni drumMapJni, int indexChannel) {
        super(drumMapJni, indexChannel);
    }
    @Override
    public int getKeyEffectParam(int keyEffectParam) {
        return keyEffectParam;
    }
    public static void saveToJson(int channel, JSONObject json) throws JSONException {

        DrumMapJni drumMapJni = DrumMapJni.getInstance();
        JSONObject jsonObject=new JSONObject();
        jsonObject.put(JKEY_GATE_ENABLE, drumMapJni.getFxValue(channel, FX_GATE, FX_ENABLED));
        jsonObject.put(JKEY_GATE_WET, drumMapJni.getFxValue(channel, FX_GATE, KEY_WET));
        jsonObject.put(JKEY_GATE_BEATS, drumMapJni.getFxValue(channel, FX_GATE, KEY_BEATS));
        json.put(JKEY_GATE,jsonObject);
    }

    public static void loadFromJson(int channel, JSONObject json) throws JSONException {
        DrumMapJni drumMapJni = DrumMapJni.getInstance();
        JSONObject jsonObject=json.getJSONObject(JKEY_GATE);
        drumMapJni.addEffect(channel,FX_GATE);
        drumMapJni.setFx(channel, FX_GATE, FX_ENABLED, (float) jsonObject.getDouble(JKEY_GATE_ENABLE));
        drumMapJni.setFx(channel, FX_GATE, KEY_WET, (float) jsonObject.getDouble(JKEY_GATE_WET));
        drumMapJni.setFx(channel, FX_GATE, KEY_BEATS, (float) jsonObject.getDouble(JKEY_GATE_BEATS));



    }
}

