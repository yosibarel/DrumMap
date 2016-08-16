package models;

/**
 * Created by yossibarel on 10/05/16.
 */
public class Midi {
    public int mKey;
    public float mStart;
    public float mLength;
    public float mEnd;
    public int mIndex;
    public boolean mIsPlay;
    public boolean mIsSelected;
    public float mSaveStart;
    public int mSaveKey;

    public Midi(double pos, float len, int key) {
        mStart = (float) pos;
        mLength = len;
        mEnd = (float) (pos + len);
        mKey = key;
    }

    public static void setPlay(Midi midi, float beatPositon) {
        midi.mIsPlay = midi.mStart <= beatPositon && midi.mEnd > beatPositon;
    }


    public boolean updateEnd(double posBeatEnd) {
        if (posBeatEnd > mStart && posBeatEnd != mEnd) {
            mEnd = (float) posBeatEnd;
            mLength = mEnd - mStart;
            return true;
        }
        return false;
    }

    public Midi copy() {
        Midi midi = new Midi(mStart, mLength, mKey);
        mIsSelected = false;
        midi.mIsSelected = true;
        return midi;

    }
}
