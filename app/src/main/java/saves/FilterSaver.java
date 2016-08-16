package saves;

import com.yossibarel.drummap.DrumMapJni;

import org.json.JSONException;
import org.json.JSONObject;

import effects.Effect;
import effects.FxFilterBandPass;
import effects.FxFilterHighPass;
import effects.FxFilterHighShelf;
import effects.FxFilterLowPass;
import effects.FxFilterLowShelf;
import effects.FxFilterNotch;

/**
 * Created by yossibarel on 09/06/16.
 */
public class FilterSaver {

    private static final String FILTER = "FILTER";
    private static final String FILTER_ACTIVE = "FILTER_ACTIVE";

    public static void saveToJson(int channel, JSONObject json) throws JSONException {
        DrumMapJni drumMap = DrumMapJni.getInstance();
        int currentFilter = drumMap.getCurrentFilter(channel);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(FILTER_ACTIVE, currentFilter);
        saveFilterByType(channel, jsonObject, currentFilter);

        json.put(FILTER, jsonObject);

    }

    private static void saveFilterByType(int channelIndex, JSONObject jsonObject, int type) throws JSONException {

        switch (type) {
            case Effect.FILTER_HIGH_PASS:
                FxFilterHighPass.saveToJson(channelIndex, jsonObject);
                break;
            case Effect.FILTER_BAND_PASS:
                FxFilterBandPass.saveToJson(channelIndex, jsonObject);
                break;
            case Effect.FILTER_LOW_PASS:
                FxFilterLowPass.saveToJson(channelIndex, jsonObject);
                break;
            case Effect.FILTER_HIGH_SHELF:
                FxFilterHighShelf.saveToJson(channelIndex, jsonObject);
                break;
            case Effect.FILTER_LOW_SHELF:
                FxFilterLowShelf.saveToJson(channelIndex, jsonObject);
                break;
            case Effect.FILTER_NOTCH:
                FxFilterNotch.saveToJson(channelIndex, jsonObject);
                break;


        }

    }


    public static void loadFromJson(int channel, JSONObject json) throws JSONException {

        JSONObject jsonObject = json.getJSONObject(FILTER);
        loadFxByType(channel, jsonObject, jsonObject.getInt(FILTER_ACTIVE));

    }

    private static void loadFxByType(int channelIndex, JSONObject jsonObject, int type) throws JSONException {

        switch (type) {
            case Effect.FILTER_HIGH_PASS:
                FxFilterHighPass.loadFromJson(channelIndex, jsonObject);
                break;
            case Effect.FILTER_BAND_PASS:
                FxFilterBandPass.loadFromJson(channelIndex, jsonObject);
                break;
            case Effect.FILTER_LOW_PASS:
                FxFilterLowPass.loadFromJson(channelIndex, jsonObject);
                break;
            case Effect.FILTER_HIGH_SHELF:
                FxFilterHighShelf.loadFromJson(channelIndex, jsonObject);
                break;
            case Effect.FILTER_LOW_SHELF:
                FxFilterLowShelf.loadFromJson(channelIndex, jsonObject);
                break;
            case Effect.FILTER_NOTCH:
                FxFilterNotch.loadFromJson(channelIndex, jsonObject);
                break;


        }

    }
}
