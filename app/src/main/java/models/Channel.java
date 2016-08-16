package models;

import com.yossibarel.drummap.DrumMapJni;

/**
 * Created by yossibarel on 29/03/16.
 */
public class Channel {
    private final DrumMapJni mDrumMap;
    private int mIndex;
    private String mName;
    private boolean isStopPlayOnRelese;

    public Channel(int index, DrumMapJni drumMap) {
        mIndex = index;
        mDrumMap = drumMap;
    }

    public int getIndex() {
        return mIndex;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    boolean isScratchEnable() {
        return false;
    }

    public void setScratchEnable(boolean checked) {
        mDrumMap.setScratchEnable(mIndex, checked);
    }

    public void setIsStopPlayOnRelease(boolean isStop) {
        isStopPlayOnRelese = isStop;
    }
    public boolean getIsStopPlayOnRelease() {
       return mDrumMap.getStopPlayOnRelease(mIndex);
    }


}
