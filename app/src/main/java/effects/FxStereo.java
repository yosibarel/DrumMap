package effects;

import com.yossibarel.drummap.DrumMapJni;

/**
 * Created by yossibarel on 26/04/16.
 */
public class FxStereo extends Effect {

    public FxStereo(DrumMapJni drumMap, int channel) {
        super(drumMap, channel);

    }

    @Override
    public int getNumParams() {

        return 1;
    }

    @Override
    public void init() {
        mFxType = FX_STEREO;
    }

    @Override
    public void setFx(int fxKeyParam, float val) {
        mDrunMap.setFx(mIndexChannel, FX_STEREO, fxKeyParam, val);
    }

    @Override
    public int getKeyEffectParam(int keyEffectParam) {
        return keyEffectParam;
    }
}
