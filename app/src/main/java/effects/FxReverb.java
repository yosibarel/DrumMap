package effects;

import com.yossibarel.drummap.DrumMapJni;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yossibarel on 02/04/16.
 */
public class FxReverb extends Effect {




    public static final int KEY_REVERB_MIX = 0;
    public static final int KEY_REVERB_DUMP = 1;
    public static final int KEY_REVERB_WIDTH = 2;
    public static final int KEY_REVERB_DRY = 3;
    public static final int KEY_REVERB_WET = 4;
    public static final int KEY_REVERB_ROOMSIZE = 5;


    public static final String JKEY_REVERB_MIX = "JKEY_REVERB_MIX";
    public static final String JKEY_REVERB_DUMP = "JKEY_REVERB_DUMP";
    public static final String JKEY_REVERB_WIDTH = "JKEY_REVERB_WIDTH";
    public static final String JKEY_REVERB_DRY = "JKEY_REVERB_DRY";
    public static final String JKEY_REVERB_WET = "JKEY_REVERB_WET";
    public static final String JKEY_REVERB_ROOMSIZE = "JKEY_REVERB_ROOMSIZE";
    public static final String JKEY_REVERB_ENABLE = "JKEY_REVERB_ENABLE";
    private static final String JKEY_REVERB = "JKEY_REVERB";


    @Override
    public int getNumParams() {
        return 6;
    }



    @Override
    public void setFx(int fxKeyParam, float val) {
        mDrunMap.setFx(mIndexChannel, FX_REVERB, fxKeyParam, val);
    }

    @Override
    public void init() {
        mFxType= FX_REVERB;
        mParamsName=new String[] {"Mix", "Dump", "Width", "Dry", "Wet", "Room Size"};
    }

    public FxReverb(DrumMapJni drumMapJni,int indexChannel) {
        super(drumMapJni, indexChannel);
    }
    @Override
    public int getKeyEffectParam(int keyEffectParam) {
        return keyEffectParam;
    }

    public static void saveToJson(int channel, JSONObject json) throws JSONException {

        DrumMapJni drumMapJni = DrumMapJni.getInstance();
        JSONObject jsonObject=new JSONObject();
        jsonObject.put(JKEY_REVERB_ENABLE, drumMapJni.getFxValue(channel, FX_REVERB, FX_ENABLED));
        jsonObject.put(JKEY_REVERB_MIX, drumMapJni.getFxValue(channel, FX_REVERB, KEY_REVERB_MIX));
        jsonObject.put(JKEY_REVERB_DUMP, drumMapJni.getFxValue(channel, FX_REVERB, KEY_REVERB_DUMP));
        jsonObject.put(JKEY_REVERB_WIDTH, drumMapJni.getFxValue(channel, FX_REVERB, KEY_REVERB_WIDTH));
        jsonObject.put(JKEY_REVERB_DRY, drumMapJni.getFxValue(channel, FX_REVERB, KEY_REVERB_DRY));
        jsonObject.put(JKEY_REVERB_WET, drumMapJni.getFxValue(channel, FX_REVERB, KEY_REVERB_WET));
        jsonObject.put(JKEY_REVERB_ROOMSIZE, drumMapJni.getFxValue(channel, FX_REVERB, KEY_REVERB_ROOMSIZE));
        json.put(JKEY_REVERB,jsonObject);
    }

    public static void loadFromJson(int channel, JSONObject json) throws JSONException {
        DrumMapJni drumMapJni = DrumMapJni.getInstance();
        JSONObject jsonObject=json.getJSONObject(JKEY_REVERB);
        drumMapJni.addEffect(channel,FX_REVERB);
        drumMapJni.setFx(channel, FX_REVERB, FX_ENABLED, (float) jsonObject.getDouble(JKEY_REVERB_ENABLE));
        drumMapJni.setFx(channel, FX_REVERB, KEY_REVERB_MIX, (float) jsonObject.getDouble(JKEY_REVERB_MIX));
        drumMapJni.setFx(channel, FX_REVERB, KEY_REVERB_DUMP, (float) jsonObject.getDouble(JKEY_REVERB_DUMP));
        drumMapJni.setFx(channel, FX_REVERB, KEY_REVERB_WIDTH, (float) jsonObject.getDouble(JKEY_REVERB_WIDTH));
        drumMapJni.setFx(channel, FX_REVERB, KEY_REVERB_DRY, (float) jsonObject.getDouble(JKEY_REVERB_DRY));
        drumMapJni.setFx(channel, FX_REVERB, KEY_REVERB_WET, (float) jsonObject.getDouble(JKEY_REVERB_WET));
        drumMapJni.setFx(channel, FX_REVERB, KEY_REVERB_ROOMSIZE, (float) jsonObject.getDouble(JKEY_REVERB_ROOMSIZE));

    }


}
