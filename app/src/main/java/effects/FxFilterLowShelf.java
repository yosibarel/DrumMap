package effects;

import com.yossibarel.drummap.DrumMapJni;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by yossibarel on 04/04/16.
 */
public class FxFilterLowShelf  extends Effect {
    public static final int KEY_FREQ = 0;
    public static final int KEY_DECIBLE = 2;
    public static final int KEY_SLOPE = 4;

    private static final String JKEY_FILTER_ENABLE = "JKEY_FILTER_ENABLE";
    private static final String JKEY_FREQ = "JKEY_FREQ";
    private static final String JKEY_DECIBLE = "JKEY_DECIBLE";
    private static final String JKEY_SLOPE = "JKEY_SLOPE";
    private static final String JKEY_FILTER_LOW_SHELF = "JKEY_FILTER_LOW_SHELF";


    @Override
    public int getNumParams() {
        return 3;
    }

    public String getName() {
        return "Low Shelf";
    }
    @Override
    public void init() {
        mFxType = FILTER_LOW_SHELF;
        mParamsName = new String[]{"Freq", "Db", "Slope"};
    }


    @Override
    public void setFx(int fxKeyParam, float val) {
        fxKeyParam *= 2;
        mDrunMap.setFx(mIndexChannel, FILTER_LOW_SHELF, fxKeyParam, val);
    }

    public FxFilterLowShelf(DrumMapJni drumMapJni, int indexChannel) {
        super(drumMapJni, indexChannel);
    }
    @Override
    public int getKeyEffectParam(int keyEffectParam) {
        return keyEffectParam*2;
    }
    public static void saveToJson(int channel, JSONObject json) throws JSONException {

        DrumMapJni drumMapJni = DrumMapJni.getInstance();
        JSONObject jsonObject=new JSONObject();
        jsonObject.put(JKEY_FILTER_ENABLE, drumMapJni.getFxValue(channel, FILTER_LOW_SHELF, FX_ENABLED));
        jsonObject.put(JKEY_FREQ, drumMapJni.getFxValue(channel, FILTER_LOW_SHELF, KEY_FREQ));
        jsonObject.put(JKEY_DECIBLE, drumMapJni.getFxValue(channel, FILTER_LOW_SHELF, KEY_DECIBLE));
        jsonObject.put(JKEY_SLOPE, drumMapJni.getFxValue(channel, FILTER_LOW_SHELF, KEY_SLOPE));

        json.put(JKEY_FILTER_LOW_SHELF, jsonObject);
    }

    public static void loadFromJson(int channel, JSONObject json) throws JSONException {
        DrumMapJni drumMapJni = DrumMapJni.getInstance();
        JSONObject jsonObject=json.getJSONObject(JKEY_FILTER_LOW_SHELF);
        drumMapJni.setFx(channel, FILTER_LOW_SHELF, FX_ENABLED, (float) jsonObject.getDouble(JKEY_FILTER_ENABLE));
        drumMapJni.setFx(channel, FILTER_LOW_SHELF, KEY_FREQ, (float) jsonObject.getDouble(JKEY_FREQ));
        drumMapJni.setFx(channel, FILTER_LOW_SHELF, KEY_DECIBLE, (float) jsonObject.getDouble(JKEY_DECIBLE));
        drumMapJni.setFx(channel, FILTER_LOW_SHELF, KEY_SLOPE, (float) jsonObject.getDouble(JKEY_SLOPE));

    }
}