package saves;

import com.yossibarel.drummap.DrumMapJni;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import models.ModHolderContainer;

/**
 * Created by yossibarel on 08/06/16.
 */
public class ChannelSaver {
    private static final int A = 0;
    private static final int D = 0;
    private static final int S = 0;
    private static final int R = 0;

    private static final String CHANNEL_FILE_PATH = "CHANNEL_FILE_PATH";
    private static final String CHANNEL_COLOR = "CHANNEL_COLOR";
    private static final String CHANNEL_VOLUME = "CHANNEL_VOLUME";

    private static final String CHANNEL_ADSR_A = "CHANNEL_ADSR_A";
    private static final String CHANNEL_ADSR_D = "CHANNEL_ADSR_D";
    private static final String CHANNEL_ADSR_S = "CHANNEL_ADSR_S";
    private static final String CHANNEL_ADSR_R = "CHANNEL_ADSR_R";
    private static final String CHANNEL_CURREN_FILTER = "CHANNEL_CURREN_FILTER";
    private static final String CHANNEL_IS_LOOP = "CHANNEL_IS_LOOP";
    private static final String CHANNEL_START_POSITION = "CHANNEL_START_POSITION";
    private static final String CHANNEL_STOP_POSITION = "CHANNEL_STOP_POSITION";
    private static final String CHANNEL_STEREO_PROGRESS = "CHANNEL_STEREO_PROGRESS";
    private static final String CHANNEL_IS_STOP_ON_RELEASE = "CHANNEL_IS_STOP_ON_RELEASE";
    private static final String CHANNEL_PITCH = "CHANNEL_PITCH";
    private static final String CHANNEL_IS_LOADED = "CHANNEL_IS_LOADED";
    private static final String CHANNEL = "CHANNEL";

    interface LoadListener {
        void onLoadSuccess(int channel);

        void onNotifyChannelEmpty(int channel);

        void onLoadFailed(int channel, String message);
    }

    public static void saveToJson(int channel, JSONObject json) throws JSONException {
        DrumMapJni drumMap = DrumMapJni.getInstance();
        JSONObject jsonObject = new JSONObject();
        boolean isLoaded = drumMap.getIsLoaded(channel);
        jsonObject.put(CHANNEL_IS_LOADED, isLoaded);

        if (isLoaded) {
            jsonObject.put(CHANNEL_FILE_PATH, new String(drumMap.getFilePath(channel)));
            jsonObject.put(CHANNEL_COLOR, drumMap.getChannelColor(channel));
            jsonObject.put(CHANNEL_VOLUME, drumMap.getVolume(channel));
            jsonObject.put(CHANNEL_ADSR_A, drumMap.getEnvADSR(0, channel, A));
            jsonObject.put(CHANNEL_ADSR_D, drumMap.getEnvADSR(0, channel, D));
            jsonObject.put(CHANNEL_ADSR_S, drumMap.getEnvADSR(0, channel, S));
            jsonObject.put(CHANNEL_ADSR_R, drumMap.getEnvADSR(0, channel, R));
            jsonObject.put(CHANNEL_CURREN_FILTER, drumMap.getCurrentFilter(channel));
            jsonObject.put(CHANNEL_IS_LOOP, drumMap.getIsLoop(channel));
            jsonObject.put(CHANNEL_START_POSITION, drumMap.getStartPlayPosition(channel));
            jsonObject.put(CHANNEL_STOP_POSITION, drumMap.getStopPlayPosition(channel));
            jsonObject.put(CHANNEL_STEREO_PROGRESS, drumMap.getStereoProgress(channel));
            jsonObject.put(CHANNEL_IS_STOP_ON_RELEASE, drumMap.getStopPlayOnRelease(channel));
            jsonObject.put(CHANNEL_PITCH, drumMap.getMainChannelPitch(channel));
            LfoSaver.saveToJson(channel, jsonObject);
            FxSaver.saveToJson(channel, jsonObject);
            PianoSaver.saveToJson(channel, jsonObject);
            SequencerSaver.saveToJson(channel, jsonObject);
            FilterSaver.saveToJson(channel, jsonObject);
            ModHolderContainer.saveToJson(channel, jsonObject);

        }
        json.put(CHANNEL, jsonObject);


    }

    public static void loadFromJson(final int channel, JSONObject json, final LoadListener listener) throws JSONException {
        final JSONObject jsonObject = json.getJSONObject(CHANNEL);
        final DrumMapJni drumMapJni = DrumMapJni.getInstance();
        if (jsonObject.getBoolean(CHANNEL_IS_LOADED)) {
            DrumMapJni.JniListener jniListener = new DrumMapJni.JniListener() {
                @Override
                public void onEOF(int channel) {

                }

                @Override
                public void onFileLoadSuccess(int _channel) {
                    if (channel == _channel) {
                        try {
                            drumMapJni.removeJniListener(this);
                            drumMapJni.setChannelColor(channel, jsonObject.getInt(CHANNEL_COLOR));
                            drumMapJni.setVolume(channel, jsonObject.getDouble(CHANNEL_VOLUME));
                            drumMapJni.setEnvADSR(0, channel, A, jsonObject.getDouble(CHANNEL_ADSR_A));
                            drumMapJni.setEnvADSR(0, channel, D, jsonObject.getDouble(CHANNEL_ADSR_D));
                            drumMapJni.setEnvADSR(0, channel, S, jsonObject.getDouble(CHANNEL_ADSR_S));
                            drumMapJni.setEnvADSR(0, channel, R, jsonObject.getDouble(CHANNEL_ADSR_R));
                            drumMapJni.addEffect(channel, jsonObject.getInt(CHANNEL_CURREN_FILTER));
                            drumMapJni.setLoop(channel, jsonObject.getBoolean(CHANNEL_IS_LOOP));
                            drumMapJni.loopBetween(channel, jsonObject.getDouble(CHANNEL_START_POSITION), jsonObject.getDouble(CHANNEL_STOP_POSITION), false);

                            double left;
                            double right;
                            int progress = jsonObject.getInt(CHANNEL_STEREO_PROGRESS);
                            if (progress > 100) {

                                left = (double) (100 - (progress - 100)) / 100;
                                right = 1.0;
                            } else {
                                left = 1.0;
                                right = (double) progress / 100;
                            }
                            drumMapJni.setStereo(channel, left, right, progress);
                            drumMapJni.setStopPlayOnRelease(channel, jsonObject.getBoolean(CHANNEL_IS_STOP_ON_RELEASE));
                            drumMapJni.setMainChannelPitch(channel, jsonObject.getDouble(CHANNEL_PITCH));
                            LfoSaver.loadFromJson(channel, jsonObject);
                            FxSaver.loadFromJson(channel, jsonObject);
                            PianoSaver.loadFromJson(channel, jsonObject);
                            SequencerSaver.loadFromJson(channel, jsonObject);
                            FilterSaver.loadFromJson(channel, jsonObject);
                            ModHolderContainer.loadFromJson(channel, jsonObject);
                            listener.onLoadSuccess(channel);
                        } catch (JSONException e) {

                            listener.onLoadFailed(channel, "Error file format");
                        }
                    }
                }

                @Override
                public void onLoadFileError(int channel) {
                    drumMapJni.removeJniListener(this);
                    try {
                        listener.onLoadFailed(channel, "Error load file " + jsonObject.getString(CHANNEL_FILE_PATH));
                    } catch (JSONException e) {
                        listener.onLoadFailed(channel, "Error file format");
                    }
                }

                @Override
                public void onBufferEditLoaded(int channel) {

                }
            };
            drumMapJni.addJniListener(jniListener);
            File file = new File(jsonObject.getString(CHANNEL_FILE_PATH));
            drumMapJni.openChannelFile(channel, file.getPath(), file.length());

        } else
            listener.onNotifyChannelEmpty(channel);
    }
}
