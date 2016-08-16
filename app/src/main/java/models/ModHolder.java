package models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yossibarel on 09/06/16.
 */
public class ModHolder {
    public String mKey;
    public int mIndexChannel;
    public int mKeyEffectParam;
    public int mLfo1;
    public int mLfo2;
    public double mStart;
    public double mEnd;
    public int mFxType;
    public int mControlXY;
    public int mModType;
    public int mCurve;

    public static JSONObject toJson(ModHolder holder) throws JSONException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("mKey", holder.mKey);
        jsonObject.put("mIndexChannel", holder.mIndexChannel);
        jsonObject.put("mKeyEffectParam", holder.mKeyEffectParam);
        jsonObject.put("mLfo1", holder.mLfo1);
        jsonObject.put("mLfo2", holder.mLfo2);
        jsonObject.put("mStart", holder.mStart);
        jsonObject.put("mEnd", holder.mEnd);
        jsonObject.put("mFxType", holder.mFxType);
        jsonObject.put("mControlXY", holder.mControlXY);
        jsonObject.put("mModType", holder.mModType);
        jsonObject.put("mCurve", holder.mCurve);
        return jsonObject;
    }

  public static  ModHolder fromJson(JSONObject jsonObject) throws JSONException {
        ModHolder modHolder = new ModHolder();
        modHolder.mKey = jsonObject.getString("mKey");
        modHolder.mIndexChannel = jsonObject.getInt("mIndexChannel");
        modHolder.mKeyEffectParam = jsonObject.getInt("mKeyEffectParam");
        modHolder.mLfo1 = jsonObject.getInt("mLfo1");
        modHolder.mLfo2 = jsonObject.getInt("mLfo2");
        modHolder.mStart = jsonObject.getDouble("mStart");
        modHolder.mEnd = jsonObject.getDouble("mEnd");
        modHolder.mFxType = jsonObject.getInt("mFxType");
        modHolder.mControlXY = jsonObject.getInt("mControlXY");
        modHolder.mModType = jsonObject.getInt("mModType");
        modHolder.mCurve = jsonObject.getInt("mCurve");
        return modHolder;

    }
}
