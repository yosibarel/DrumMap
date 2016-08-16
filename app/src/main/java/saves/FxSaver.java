package saves;

import com.yossibarel.drummap.DrumMapJni;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import effects.Effect;
import effects.FxDelay;
import effects.FxFlanger;
import effects.FxGate;
import effects.FxLimiter;
import effects.FxReverb;
import effects.FxRoll;
import effects.FxWhoosh;

/**
 * Created by yossibarel on 08/06/16.
 */
public class FxSaver {


    private static final String FX_ACTIVE_KEY = "FX_ACTIVE_KEY";
    private static final String JFX_KEY = "JFX_KEY";

   public static void saveToJson(int channel, JSONObject json) throws JSONException {
        DrumMapJni drumMap = DrumMapJni.getInstance();
        int[] fx = drumMap.getActiveEffect(channel);
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < fx.length; ++i) {
            jsonArray.put(i, fx[i]);
            saveFxByType(channel, jsonObject, fx[i]);
        }
        jsonObject.put(FX_ACTIVE_KEY, jsonArray);
        json.put(JFX_KEY, jsonObject);

    }

    private static void saveFxByType(int channelIndex, JSONObject jsonObject, int type) throws JSONException {

        switch (type) {
            case Effect.FX_REVERB:
                FxReverb.saveToJson(channelIndex, jsonObject);
                break;
            case Effect.FX_DELAY:
                FxDelay.saveToJson(channelIndex, jsonObject);
                break;
            case Effect.FX_FLANGER:
                FxFlanger.saveToJson(channelIndex, jsonObject);
                break;
            case Effect.FX_LIMITER:
                FxLimiter.saveToJson(channelIndex, jsonObject);
                break;
            case Effect.FX_ROLL:
                FxRoll.saveToJson(channelIndex, jsonObject);
                break;
            case Effect.FX_GATE:
                FxGate.saveToJson(channelIndex, jsonObject);
                break;
            case Effect.FX_WHOOSH:
                FxWhoosh.saveToJson(channelIndex, jsonObject);
                break;


        }

    }


    public static void loadFromJson(int channel, JSONObject json) throws JSONException {
        DrumMapJni drumMap = DrumMapJni.getInstance();
        JSONObject jsonObject = json.getJSONObject(JFX_KEY);
        JSONArray jsonArray = jsonObject.getJSONArray(FX_ACTIVE_KEY);
        for (int i=0;i<jsonArray.length();++i)
        {
            loadFxByType(channel,jsonObject,jsonArray.getInt(i));
        }
    }
    private static void loadFxByType(int channelIndex, JSONObject jsonObject, int type) throws JSONException {

        switch (type) {
            case Effect.FX_REVERB:
                FxReverb.loadFromJson(channelIndex, jsonObject);
                break;
            case Effect.FX_DELAY:
                FxDelay.loadFromJson(channelIndex, jsonObject);
                break;
            case Effect.FX_FLANGER:
                FxFlanger.loadFromJson(channelIndex, jsonObject);
                break;
            case Effect.FX_LIMITER:
                FxLimiter.loadFromJson(channelIndex, jsonObject);
                break;
            case Effect.FX_ROLL:
                FxRoll.loadFromJson(channelIndex, jsonObject);
                break;
            case Effect.FX_GATE:
                FxGate.loadFromJson(channelIndex, jsonObject);
                break;
            case Effect.FX_WHOOSH:
                FxWhoosh.loadFromJson(channelIndex, jsonObject);
                break;


        }

    }
}
