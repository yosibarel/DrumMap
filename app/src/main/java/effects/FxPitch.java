package effects;

import com.yossibarel.drummap.DrumMapJni;

/**
 * Created by yossibarel on 27/04/16.
 */
public class FxPitch   extends Effect {




    @Override
    public int getNumParams() {
        return 1;
    }


    @Override
    public void init() {
        mFxType= FX_PITCH;

    }



    @Override
    public void setFx(int fxKeyParam, float val) {
        mDrunMap.setFx(mIndexChannel, FX_DELAY, fxKeyParam, val);
    }

    public FxPitch(DrumMapJni drumMapJni, int indexChannel) {
        super(drumMapJni, indexChannel);
    }

    @Override
    public int getKeyEffectParam(int keyEffectParam) {
        return keyEffectParam;
    }

}