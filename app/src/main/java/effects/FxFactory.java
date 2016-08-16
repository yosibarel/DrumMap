package effects;

import com.yossibarel.drummap.DrumMapJni;

/**
 * Created by yossibarel on 02/04/16.
 */

public class FxFactory {


    public static Effect createEffect(int channelIndex, DrumMapJni drumMap, int type) {
        Effect effect = null;
        switch (type) {
            case Effect.FX_REVERB:
                effect = new FxReverb(drumMap, channelIndex);
                break;
            case Effect.FX_DELAY:
                effect = new FxDelay(drumMap, channelIndex);
                break;
            case Effect.FX_FLANGER:
                effect = new FxFlanger(drumMap, channelIndex);
                break;
            case Effect.FX_LIMITER:
                effect = new FxLimiter(drumMap, channelIndex);
                break;
            case Effect.FX_ROLL:
                effect = new FxRoll(drumMap, channelIndex);
                break;
            case Effect.FX_GATE:
                effect = new FxGate(drumMap, channelIndex);
                break;
            case Effect.FX_WHOOSH:
                effect = new FxWhoosh(drumMap, channelIndex);
                break;
            case Effect.FILTER_BAND_PASS:
                effect = new FxFilterBandPass(drumMap, channelIndex);
                break;
            case Effect.FILTER_HIGH_PASS:
                effect = new FxFilterHighPass(drumMap, channelIndex);
                break;
            case Effect.FILTER_HIGH_SHELF:
                effect = new FxFilterHighShelf(drumMap, channelIndex);
                break;
            case Effect.FILTER_LOW_PASS:
                effect = new FxFilterLowPass(drumMap, channelIndex);
                break;
            case Effect.FILTER_LOW_SHELF:
                effect = new FxFilterLowShelf(drumMap, channelIndex);
                break;
            case Effect.FILTER_NOTCH:
                effect = new FxFilterNotch(drumMap, channelIndex);
                break;


        }
        return effect;

    }


}
