package saves;

import com.yossibarel.drummap.DrumMapJni;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yossibarel on 08/06/16.
 */
public class SequencerSaver {

    private static final int NUM_PATTERN = 16;
    private static final String SEQ_ENABLE_KEY = "SEQ_ENABLE_KEY";
    private static final String SEQ_VOLUME_KEY = "SEQ_VOLUME_KEY";
    private static final String SEQ_PITCH_KEY = "SEQ_PITCH_KEY";
    private static final String SEQ_LENGTH_KEY = "SEQ_LENGTH_KEY";
    private static final String SEQ_STEP_DUR_KEY = "SEQ_STEP_DUR_KEY";
    private static final String SEQ_CURVE_1_KEY = "SEQ_CURVE_1_KEY";
    private static final String SEQ_CURVE_2_KEY = "SEQ_CURVE_2_KEY";
    private static final String SEQ_LIST_PATTERN = "SEQ_LIST_PATTERN";
    private static final String SEQ_INDEX_ENABLE = "SEQ_INDEX_ENABLE";
    private static final String SEQ_RATE_PROGRESS = "SEQ_RATE_PROGRESS";
    private static final String SEQ_STATE = "SEQ_STATE";
    private static final String SEQ_STEP_STATE = "SEQ_STEP_STATE";

    public static final int SEQ_ENABLE = -1, SEQ_VOLUME = 0, SEQ_PITCH = 1, SEQ_LENGTH = 2, SEQ_STEP_DUR = 3, SEQ_CURVE_1 = 4, SEQ_CURVE_2 = 5;
    private static final String SEQUENCER = "SEQUENCER";

    public static void saveToJson(int channel, JSONObject json) throws JSONException {
        DrumMapJni drumMapJni = DrumMapJni.getInstance();

        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < NUM_PATTERN; i++) {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put(SEQ_ENABLE_KEY, drumMapJni.getSeqPattern(channel, i, SEQ_ENABLE));
            jsonObject.put(SEQ_VOLUME_KEY, drumMapJni.getSeqPattern(channel, i, SEQ_VOLUME));
            jsonObject.put(SEQ_PITCH_KEY, drumMapJni.getSeqPattern(channel, i, SEQ_PITCH));
            jsonObject.put(SEQ_LENGTH_KEY, drumMapJni.getSeqPattern(channel, i, SEQ_LENGTH));
            jsonObject.put(SEQ_STEP_DUR_KEY, drumMapJni.getSeqPattern(channel, i, SEQ_STEP_DUR));
            jsonObject.put(SEQ_CURVE_1_KEY, drumMapJni.getSeqPattern(channel, i, SEQ_CURVE_1));
            jsonObject.put(SEQ_CURVE_2_KEY, drumMapJni.getSeqPattern(channel, i, SEQ_CURVE_2));
            jsonArray.put(jsonObject);


        }
        JSONObject retVal = new JSONObject();
        retVal.put(SEQ_LIST_PATTERN, jsonArray);
        retVal.put(SEQ_INDEX_ENABLE, drumMapJni.getSeqIndexEnable(channel));
        retVal.put(SEQ_RATE_PROGRESS, drumMapJni.getSeqRateProgress(channel));
        retVal.put(SEQ_STATE, drumMapJni.getSeqState(channel));
        retVal.put(SEQ_STEP_STATE, drumMapJni.getSeqStepState(channel));

        json.put(SEQUENCER, retVal);
    }

    public static void loadFromJson(int channel, JSONObject jsonObject) throws JSONException {
        JSONObject json = jsonObject.getJSONObject(SEQUENCER);
        DrumMapJni drumMapJni = DrumMapJni.getInstance();
        JSONArray jsonPatternArr = json.getJSONArray(SEQ_LIST_PATTERN);
        for (int i = 0; i < jsonPatternArr.length(); i++) {
            drumMapJni.enableSequencerPattern(channel, i, jsonPatternArr.getJSONObject(i).getDouble(SEQ_ENABLE_KEY) > 0.5);
            drumMapJni.setValueSequencerPattern(channel, i, SEQ_ENABLE, jsonPatternArr.getJSONObject(i).getDouble(SEQ_ENABLE_KEY));
            drumMapJni.setValueSequencerPattern(channel, i, SEQ_VOLUME, jsonPatternArr.getJSONObject(i).getDouble(SEQ_VOLUME_KEY));
            drumMapJni.setValueSequencerPattern(channel, i, SEQ_PITCH, jsonPatternArr.getJSONObject(i).getDouble(SEQ_PITCH_KEY));
            drumMapJni.setValueSequencerPattern(channel, i, SEQ_LENGTH, jsonPatternArr.getJSONObject(i).getDouble(SEQ_LENGTH_KEY));
            drumMapJni.setValueSequencerPattern(channel, i, SEQ_STEP_DUR, jsonPatternArr.getJSONObject(i).getDouble(SEQ_STEP_DUR_KEY));
            drumMapJni.setValueSequencerPattern(channel, i, SEQ_CURVE_1, jsonPatternArr.getJSONObject(i).getDouble(SEQ_CURVE_1_KEY));
            drumMapJni.setValueSequencerPattern(channel, i, SEQ_CURVE_2, jsonPatternArr.getJSONObject(i).getDouble(SEQ_CURVE_2_KEY));
        }
        drumMapJni.setSeqIndexEnable(channel, json.getInt(SEQ_INDEX_ENABLE));
        drumMapJni.setSeqState(channel, json.getInt(SEQ_STATE));
        drumMapJni.setSeqRate(channel, json.getInt(SEQ_RATE_PROGRESS));
        drumMapJni.setSeqStepState(channel, json.getInt(SEQ_STEP_STATE));
    }

}
