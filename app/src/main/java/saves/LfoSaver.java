package saves;

import com.yossibarel.drummap.DrumMapJni;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yossibarel on 08/06/16.
 */
public class LfoSaver {

    private static final int LFO_1 = 0;
    private static final int LFO_2 = 1;
    private static final String LFO1_AMP_KEY = "LFO1_AMP_KEY";
    private static final String LFO2_AMP_KEY = "LFO2_AMP_KEY";
    private static final String LFO1_TYPE_KEY = "LFO1_TYPE_KEY";
    private static final String LFO2_TYPE_KEY = "LFO2_TYPE_KEY";
    private static final String LFO1_RATE_KEY = "LFO1_RATE_KEY";
    private static final String LFO2_RATE_KEY = "LFO2_RATE_KEY";
    private static final String LFO_PERCENT_KEY = "LFO_PERCENT_KEY";


    public static void saveToJson(int channel, JSONObject json) throws JSONException {
        DrumMapJni drumMap = DrumMapJni.getInstance();

        json.put(LFO1_AMP_KEY, drumMap.getLFOAmplitudeWave(channel, LFO_1));
        json.put(LFO2_AMP_KEY, drumMap.getLFOAmplitudeWave(channel, LFO_2));
        json.put(LFO1_TYPE_KEY, drumMap.getLFOTypeWave(channel, LFO_1));
        json.put(LFO2_TYPE_KEY, drumMap.getLFOTypeWave(channel, LFO_2));
        json.put(LFO1_RATE_KEY, drumMap.getLFORateWave(channel, LFO_1));
        json.put(LFO2_RATE_KEY, drumMap.getLFORateWave(channel, LFO_2));
        json.put(LFO_PERCENT_KEY, drumMap.getLfoWavePresent(channel));
    }

    public static   void loadFromJson(int channel, JSONObject json) throws JSONException {
        DrumMapJni drumMap = DrumMapJni.getInstance();
        drumMap.setLFOAmplitudeWave(channel, LFO_1, json.getDouble(LFO1_AMP_KEY));
        drumMap.setLFOAmplitudeWave(channel, LFO_2, json.getDouble(LFO2_AMP_KEY));
        drumMap.setLFOTypeWave(channel, LFO_1, json.getInt(LFO1_TYPE_KEY));
        drumMap.setLFOTypeWave(channel, LFO_2, json.getInt(LFO2_TYPE_KEY));
        drumMap.setLFORateWave(channel, LFO_1, json.getDouble(LFO1_RATE_KEY));
        drumMap.setLFORateWave(channel, LFO_2, json.getDouble(LFO2_RATE_KEY));
        drumMap.setLFOPresentWave(channel, json.getDouble(LFO_PERCENT_KEY));
    }
}
