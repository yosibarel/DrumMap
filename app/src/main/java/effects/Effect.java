package effects;

import com.yossibarel.drummap.DrumMapJni;

import models.ModHolderContainer;

/**
 * Created by yossibarel on 02/04/16.
 */
public abstract class Effect {


    public static final int FX_ENABLED = -1;

    public static final int C1X = 0;
    public static final int C1Y = 1;
    public static final int C2X = 2;
    public static final int C2Y = 3;
    public static final int SNSRCX = 4;
    public static final int SNSRCY = 5;

    public static final int FX_PITCH = -3;
    public static final int FX_STEREO = -2;
    public static final int FILTER_ENABLE = -1;
    public static String[] FX_NAMES = {"Reverb", "Delay", "Flanger", "Roll", "Limiter", "Gate", "Whoosh"};
    public static String[] FILTER_NAME = {"Low pass", "High pass", "Band pass", "Notch", "Low self", "High shelf"};
    public static final int FX_REVERB = 0;
    public static final int FX_DELAY = 1;
    public static final int FX_FLANGER = 2;
    public static final int FX_ROLL = 3;
    public static final int FX_LIMITER = 4;
    public static final int FX_GATE = 5;
    public static final int FX_WHOOSH = 6;

    public static final int FILTER_LOW_PASS = 10;
    public static final int FILTER_HIGH_PASS = 11;
    public static final int FILTER_BAND_PASS = 12;
    public static final int FILTER_NOTCH = 13;
    public static final int FILTER_LOW_SHELF = 14;
    public static final int FILTER_HIGH_SHELF = 15;
    public static final int FILTER_PARAMETRIC = 16;
    protected final DrumMapJni mDrunMap;
    protected String[] mParamsName = null;
    private int mIndexCollection;

    public abstract int getNumParams();

    public abstract void init();


    public String getParaName(int paramIndexChannel) {
        return mParamsName[paramIndexChannel];
    }

    public abstract void setFx(int fxKeyParam, float val);


    public String getName() {
        return FX_NAMES[mFxType];
    }


    Effect(DrumMapJni drumMapJni, int indexChannels) {
        this.mDrunMap = drumMapJni;
        mIndexChannel = indexChannels;
        init();
    }

    public void keyDown() {
        mDrunMap.keyDown(mIndexChannel);
    }

    public void keyRelese() {
        mDrunMap.keyRelese(mIndexChannel);
    }

    public int mIndexChannel;
    public int mFxType;
    public boolean mIsEnable;
    String mKey;

    public abstract int getKeyEffectParam(int keyEffectParam);

    public void setLFO(int keyEffectParam, int lfo1, int lfo2, int which, double start, double end, boolean isUpdate) {
        mKey = "_" + mIndexChannel + "_" + mFxType + "_" + getKeyEffectParam(keyEffectParam);
        ModHolderContainer.addOrUpdateLfo(mKey, mIndexChannel, mFxType, getKeyEffectParam(keyEffectParam), lfo1, lfo2, start, end, isUpdate);
    }

    public void setControlXY(int keyEffectParam, int control, int which, double start, double end, boolean isUpdate) {
        mKey = "_" + mIndexChannel + "_" + mFxType + "_" + getKeyEffectParam(keyEffectParam);
        ModHolderContainer.addOrUpdateControlXY(mKey, mIndexChannel, control, mFxType, getKeyEffectParam(keyEffectParam), start, end, isUpdate);

    }

    public void setAdsrEnvelope(int keyEffectParam, int control, int which, double start, double end, boolean isUpdate) {
        mKey = "_" + mIndexChannel + "_" + mFxType + "_" + getKeyEffectParam(keyEffectParam);
        ModHolderContainer.addOrUpdateADSR(mKey, mIndexChannel, mFxType, getKeyEffectParam(keyEffectParam), start, end, isUpdate);

    }

    public void setCurve(int keyEffectParam, int curve, int which, double start, double end, boolean isUpdate) {
        mKey = "_" + mIndexChannel + "_" + mFxType + "_" + getKeyEffectParam(keyEffectParam);
        ModHolderContainer.addOrUpdateCurve(mKey, mIndexChannel, curve, mFxType, getKeyEffectParam(keyEffectParam), start, end, isUpdate);
    }

    public void setEnable(boolean isChecked) {
        mDrunMap.setFx(mIndexChannel, mFxType, Effect.FX_ENABLED, isChecked ? 1 : 0);
    }

    public double getFxValue(int fxKeyParam) {
        return mDrunMap.getFxValue(mIndexChannel, mFxType, fxKeyParam);
    }

    public int getIsInModeState(int keyEffectParam) {
        return mDrunMap.getModState(mIndexChannel, "_" + mIndexChannel + "_" + mFxType + "_" + getKeyEffectParam(keyEffectParam));
    }

    public void removeModFx(int keyEffectParam, int selected) {
        String key = "_" + mIndexChannel + "_" + mFxType + "_" + getKeyEffectParam(keyEffectParam);
        ModHolderContainer.remove(key);
        mDrunMap.removeModFx(mIndexChannel, key, keyEffectParam, selected);
    }


    public void close(int index) {
        mDrunMap.deleteEffectChannel(mIndexChannel, index);
    }

    public void setIndex(int index) {
        this.mIndexCollection = index;
    }

    public int getIndex() {
        return this.mIndexCollection;
    }
}
