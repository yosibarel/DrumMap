package saves;

import com.yossibarel.drummap.DrumMapJni;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import models.ChannelItem;

/**
 * Created by yossibarel on 11/06/16.
 */
public class MainSaver {

    private static final int NUM_CHANNELS = 16;
    private static final String CHANNEL_ITEMS_GRID = "CHANNEL_ITEMS_GRID";
    private static final String BPM = "BPM";
    private static final String MAIN_DURATION_BEAT = "MAIN_DURATION_BEAT";
    private static final String MAIN_START_PLAY_POSITION = "MAIN_START_PLAY_POSITION";
    private static final String MAIN_STOP_PLAY_POSITION = "MAIN_STOP_PLAY_POSITION";
    private static final String MAIN_POSITION_BEAT = "MAIN_POSITION_BEAT";
    private static final String CHANNEL_INDEX = "CHANNEL_INDEX";
    private static final String CHANNELS = "CHANNELS";
    private static LoadListener mListener;
    private static String mPath;

    public interface LoadListener {
        void onSuccess();

        void onFailed(String message);
    }

    public interface SaveListener extends LoadListener {


    }

    private static ChannelSaver.LoadListener loadListener = new ChannelSaver.LoadListener() {
        int numChannelLoaded = 0;

        @Override
        synchronized public void onLoadSuccess(int channel) {
            numChannelLoaded++;
            if (numChannelLoaded == 16)
                mListener.onSuccess();
        }

        @Override
        synchronized public void onNotifyChannelEmpty(int channel) {
            numChannelLoaded++;
            if (numChannelLoaded == 16)
                mListener.onSuccess();
        }

        @Override
        synchronized public void onLoadFailed(int channel, String message) {
            numChannelLoaded++;
            if (numChannelLoaded == 16)
                mListener.onSuccess();
        }
    };

    public static void save(String filePath, SaveListener saveListener) {
        mListener = saveListener;
        mPath = filePath;

        new Thread() {
            @Override
            public void run() {

                JSONObject jsonObject = new JSONObject();

                try {
                    saveToJson(jsonObject);
                    FileWriter file = new FileWriter(mPath);
                    file.write(jsonObject.toString());
                    file.flush();
                    file.close();
                } catch (IOException e) {
                    mListener.onFailed(e.getMessage());
                } catch (JSONException e) {
                    mListener.onFailed(e.getMessage());
                }
                mListener.onSuccess();
            }
        }.start();

    }

    public static void saveToJson(JSONObject json) throws JSONException {


        JSONArray jsonArrayChannel = new JSONArray();
        for (int i = 0; i < NUM_CHANNELS; i++) {

            JSONObject jChannel = new JSONObject();
            jChannel.put(CHANNEL_INDEX, i);
            ChannelSaver.saveToJson(i, jChannel);

            jsonArrayChannel.put(jChannel);
        }
        json.put(CHANNELS, jsonArrayChannel);
        DrumMapJni drumMap = DrumMapJni.getInstance();
        json.put(BPM, drumMap.getBpm());
        json.put(MAIN_DURATION_BEAT, drumMap.getMainDurationBeat());
        json.put(MAIN_START_PLAY_POSITION, drumMap.getMainGridStartPlayPosition());
        json.put(MAIN_STOP_PLAY_POSITION, drumMap.getMainGridStopPlayPosition());
        json.put(MAIN_POSITION_BEAT, drumMap.getMainPositionBeat());

        JSONArray jsonArray = new JSONArray();
        float[] channelItemsFloat = drumMap.getChannelItemsFloat();
        for (int i = 0; i < channelItemsFloat.length; i += 4) {
            int channel = (int) channelItemsFloat[i + 2];

            ChannelItem channelItem = new ChannelItem(channelItemsFloat[i], (float) (channelItemsFloat[i + 1]), channel);
            channelItem.mIndex = (int) channelItemsFloat[i + 3];
            JSONObject jItem = ChannelItem.toJson(channelItem);
            jsonArray.put(jItem);

        }
        json.put(CHANNEL_ITEMS_GRID, jsonArray);


    }

    public static void load(final String filePath, LoadListener listener) {
        mListener = listener;
        new Thread() {
            @Override
            public void run() {
                try {
                    File f = new File(filePath);
                    FileInputStream is = new FileInputStream(f);
                    int size = is.available();
                    byte[] buffer = new byte[size];
                    is.read(buffer);
                    is.close();
                    String mResponse = new String(buffer);
                    JSONObject jsonObject = new JSONObject(mResponse);
                    loadFromJson(jsonObject);
                } catch (IOException e) {
                    mListener.onFailed(e.getMessage());
                    e.printStackTrace();
                } catch (JSONException e) {
                    mListener.onFailed(e.getMessage());
                }

            }
        }.start();

    }

    public static void loadFromJson(JSONObject json) throws JSONException {

        DrumMapJni drumMap = DrumMapJni.getInstance();

        drumMap.setMainGridBeatsDuration((float) json.getDouble(MAIN_DURATION_BEAT));
        drumMap.setStartEndLoopPositionPrecentMainGrid(json.getDouble(MAIN_START_PLAY_POSITION) / json.getDouble(MAIN_DURATION_BEAT), json.getDouble(MAIN_STOP_PLAY_POSITION) / json.getDouble(MAIN_DURATION_BEAT));
        drumMap.setMainGridPositionBeat(json.getDouble(MAIN_POSITION_BEAT));
        JSONArray jsonArray = json.getJSONArray(CHANNELS);
        for (int i = 0; i < jsonArray.length(); i++) {
            ChannelSaver.loadFromJson(i, jsonArray.getJSONObject(i), loadListener);
        }
        JSONArray jChannelItems = json.getJSONArray(CHANNEL_ITEMS_GRID);
        for (int i = 0; i < jChannelItems.length(); ++i) {
            ChannelItem channelItem = ChannelItem.fromJson(jChannelItems.getJSONObject(i));
            drumMap.addChannelItem(channelItem.mChannel, channelItem.mStart, channelItem.mEnd);
        }
        drumMap.setBpm(json.getDouble(BPM));
    }
}
