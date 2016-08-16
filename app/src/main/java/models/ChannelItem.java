package models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yossibarel on 05/06/16.
 */
public class ChannelItem {
    private double mLength;
    public double mStart;
    public int mChannel;
    public double mEnd;
    public boolean mIsPlay;
    public int mIndex;


    public ChannelItem(double posBeat, float len, int channel) {
        mStart = (float) posBeat;
        mLength = len;
        mEnd = (float) (posBeat + len);
        mChannel = channel;
    }

    public static void setPlay(ChannelItem channelItem, float mPositionBeat) {

    }

    public boolean updateEnd(double posBeatEnd) {
        if (posBeatEnd > mStart && posBeatEnd != mEnd) {
            mEnd = posBeatEnd;
            mLength = mEnd - mStart;
            return true;
        }
        return false;
    }

    public static JSONObject toJson(ChannelItem channelItem) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("mChannel", channelItem.mChannel);
        jsonObject.put("mEnd", channelItem.mEnd);
        jsonObject.put("mIndex", channelItem.mIndex);
        jsonObject.put("mStart", channelItem.mStart);
        jsonObject.put("mLength", channelItem.mLength);
        return jsonObject;
    }

    public static ChannelItem fromJson(JSONObject jsonObject) throws JSONException {

        ChannelItem channelItem=new ChannelItem(jsonObject.getDouble("mStart"),jsonObject.getInt("mLength"),jsonObject.getInt("mChannel"));
        return channelItem;

    }
}
