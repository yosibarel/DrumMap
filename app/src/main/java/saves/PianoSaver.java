package saves;

import com.yossibarel.drummap.DrumMapJni;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import models.Midi;

/**
 * Created by yossibarel on 08/06/16.
 */
public class PianoSaver {
    private static final String MIDI_START_KEY = "MIDI_START_KEY";
    private static final String MIDI_END_KEY = "MIDI_END_KEY";
    private static final String MIDI_INDEX_KEY = "MIDI_INDEX_KEY";
    private static final String MIDI_KEY_KEY = "MIDI_KEY_KEY";
    private static final String MIDI_LEN_KEY = "MIDI_LEN_KEY";
    private static final String MIDI_LIST_KEY = "MIDI_LIST_KEY";
    private static final String PIANO_START_LOOP_POSITION = "PIANO_START_LOOP_POSITION";
    private static final String PIANO_STOP_LOOP_POSITION = "PIANO_STOP_LOOP_POSITION";
    private static final String PIANO_BEAT_DURATION = "PIANO_BEAT_DURATION";
    private static final String PIANO = "PIANO";
    private static final int REMOVAE_ALL = -1;

    public static void saveToJson(int channel, JSONObject json) throws JSONException {
        DrumMapJni drumMapJni = DrumMapJni.getInstance();
        float[] midiFloat = drumMapJni.getMidiFloat(channel);

        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < midiFloat.length; i += 4) {

            JSONObject jsonObject = new JSONObject();
            int key = (int) midiFloat[i + 2];

            Midi midi = new Midi(midiFloat[i], (float) (midiFloat[i + 1]), key);
            midi.mIndex = (int) midiFloat[i + 3];
            jsonObject.put(MIDI_START_KEY, midi.mStart);
            jsonObject.put(MIDI_END_KEY, midi.mEnd);
            jsonObject.put(MIDI_INDEX_KEY, midi.mIndex);
            jsonObject.put(MIDI_KEY_KEY, midi.mKey);
            jsonObject.put(MIDI_LEN_KEY, midi.mLength);
            jsonArray.put(jsonObject);


        }
        JSONObject retVal = new JSONObject();
        retVal.put(MIDI_LIST_KEY, jsonArray);
        retVal.put(PIANO_START_LOOP_POSITION, drumMapJni.getPianoStartPlayPosition(channel));
        retVal.put(PIANO_STOP_LOOP_POSITION, drumMapJni.getPianoStopPlayPosition(channel));
        retVal.put(PIANO_BEAT_DURATION, drumMapJni.getPianoDurationBeat(channel));
        json.put(PIANO, retVal);

    }

    public static void loadFromJson(int channel, JSONObject jsonObject) throws JSONException {
        JSONObject json=jsonObject.getJSONObject(PIANO);
        DrumMapJni drumMapJni = DrumMapJni.getInstance();
        drumMapJni.removeMidiPiano(channel,REMOVAE_ALL);
        JSONArray jsonArray = json.getJSONArray(MIDI_LIST_KEY);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jMidi = jsonArray.getJSONObject(i);
            drumMapJni.addMidiPiano(channel, jMidi.getInt(MIDI_KEY_KEY), (float) jMidi.getDouble(MIDI_START_KEY), (float) jMidi.getDouble(MIDI_END_KEY));
        }

        double duration = json.getDouble(PIANO_BEAT_DURATION);
        drumMapJni.setPianoBeatsDuration(channel, (float) duration);
        drumMapJni.setStartEndLoopPositionPrecentPiano(channel, json.getDouble(PIANO_START_LOOP_POSITION) / duration, json.getDouble(PIANO_STOP_LOOP_POSITION) / duration);

    }


}
