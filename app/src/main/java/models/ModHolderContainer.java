package models;

import com.yossibarel.drummap.DrumMapJni;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by yossibarel on 09/06/16.
 */
public class ModHolderContainer {
    static final int LFO = 1, CONTROL_XY = 2, ADSR = 3, CURVE = 4;
    private static final String SEND_MOD = "SEND_MOD";
    private static final int CLEAR_ALL = 100;

    static HashMap<String, ModHolder> mModContainer = new HashMap<>();

    public static void addOrUpdateLfo(String mKey, int mIndexChannel, int mFxType, int keyEffectParam, int lfo1, int lfo2, double start, double end, boolean isUpdate) {
        DrumMapJni.getInstance().sendFxToLFO(mKey, mIndexChannel, mFxType, keyEffectParam, lfo1, lfo2, start, end, isUpdate);

        if (!isUpdate) {
            ModHolder modHolder = new ModHolder();
            modHolder.mModType = LFO;
            modHolder.mKey = mKey;
            modHolder.mFxType = mFxType;
            modHolder.mIndexChannel = mIndexChannel;
            modHolder.mKeyEffectParam = keyEffectParam;
            modHolder.mLfo1 = lfo1;
            modHolder.mLfo2 = lfo2;
            modHolder.mStart = start;
            modHolder.mEnd = end;
            mModContainer.put(mKey, modHolder);
        } else {
            ModHolder modHolder = mModContainer.get(mKey);
            modHolder.mStart = start;
            modHolder.mEnd = end;
        }
    }

    public static void addOrUpdateControlXY(String mKey, int mIndexChannel, int control, int mFxType, int keyEffectParam, double start, double end, boolean isUpdate) {
        DrumMapJni.getInstance().sendFxToControlXY(mKey, mIndexChannel, control, mFxType, keyEffectParam, start, end, isUpdate);
        if (!isUpdate) {
            ModHolder modHolder = new ModHolder();
            modHolder.mModType = CONTROL_XY;
            modHolder.mKey = mKey;
            modHolder.mFxType = mFxType;
            modHolder.mIndexChannel = mIndexChannel;
            modHolder.mKeyEffectParam = keyEffectParam;
            modHolder.mControlXY = control;
            modHolder.mStart = start;
            modHolder.mEnd = end;
            mModContainer.put(mKey, modHolder);
        } else {
            ModHolder modHolder = mModContainer.get(mKey);
            modHolder.mStart = start;
            modHolder.mEnd = end;
        }
    }

    public static void addOrUpdateCurve(String mKey, int mIndexChannel, int curve, int mFxType, int keyEffectParam, double start, double end, boolean isUpdate) {
        DrumMapJni.getInstance().sendFxToCurve(mKey, mIndexChannel, curve, mFxType, keyEffectParam, start, end, isUpdate);

        if (!isUpdate) {
            ModHolder modHolder = new ModHolder();
            modHolder.mModType = CURVE;
            modHolder.mKey = mKey;
            modHolder.mFxType = mFxType;
            modHolder.mIndexChannel = mIndexChannel;
            modHolder.mKeyEffectParam = keyEffectParam;
            modHolder.mCurve = curve;
            modHolder.mStart = start;
            modHolder.mEnd = end;
            mModContainer.put(mKey, modHolder);
        } else {
            ModHolder modHolder = mModContainer.get(mKey);
            modHolder.mStart = start;
            modHolder.mEnd = end;
        }
    }

    public static void addOrUpdateADSR(String mKey, int mIndexChannel, int mFxType, int keyEffectParam, double start, double end, boolean isUpdate) {
        DrumMapJni.getInstance().sendFxToADSR(mKey, mIndexChannel, mFxType, keyEffectParam, start, end, isUpdate);
        if (!isUpdate) {
            ModHolder modHolder = new ModHolder();
            modHolder.mModType = ADSR;
            modHolder.mKey = mKey;
            modHolder.mFxType = mFxType;
            modHolder.mIndexChannel = mIndexChannel;
            modHolder.mKeyEffectParam = keyEffectParam;
            modHolder.mStart = start;
            modHolder.mEnd = end;
            mModContainer.put(mKey, modHolder);
        } else {
            ModHolder modHolder = mModContainer.get(mKey);
            modHolder.mStart = start;
            modHolder.mEnd = end;
        }
    }

    public static void remove(String key) {
        mModContainer.remove(key);
    }

    public static void saveToJson(int channel, JSONObject json) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (ModHolder modHolder : mModContainer.values()) {
            if (modHolder.mIndexChannel == channel)
                jsonArray.put(ModHolder.toJson(modHolder));
        }
        json.put(SEND_MOD, jsonArray);
    }

    public static void loadFromJson(int channel ,JSONObject jsonObject) throws JSONException {
        mModContainer.clear();
        DrumMapJni.getInstance().removeModFx(channel,"",0,CLEAR_ALL);
        JSONArray jsonArray = jsonObject.getJSONArray(SEND_MOD);
        for (int i = 0; i < jsonArray.length(); i++) {

            ModHolder modHolder = ModHolder.fromJson(jsonArray.getJSONObject(i));
            addByTypeMod(modHolder);
        }

    }

    private static void addByTypeMod(ModHolder modHolder) {
        switch (modHolder.mModType) {
            case LFO:
                addOrUpdateLfo(modHolder.mKey, modHolder.mIndexChannel, modHolder.mFxType, modHolder.mKeyEffectParam, modHolder.mLfo1, modHolder.mLfo2, modHolder.mStart, modHolder.mEnd, false);
                break;
            case CONTROL_XY:
                addOrUpdateControlXY(modHolder.mKey, modHolder.mIndexChannel, modHolder.mControlXY, modHolder.mFxType, modHolder.mKeyEffectParam, modHolder.mStart, modHolder.mEnd, false);
                break;
            case ADSR:
                addOrUpdateADSR(modHolder.mKey, modHolder.mIndexChannel, modHolder.mFxType, modHolder.mKeyEffectParam, modHolder.mStart, modHolder.mEnd, false);
                break;
            case CURVE:
                addOrUpdateADSR(modHolder.mKey, modHolder.mIndexChannel, modHolder.mFxType, modHolder.mKeyEffectParam, modHolder.mStart, modHolder.mEnd, false);
                break;
        }
    }
}
