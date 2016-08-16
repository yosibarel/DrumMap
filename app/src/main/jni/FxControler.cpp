//
// Created by Yossi Barel on 02/04/16.
//

#include "FxControler.h"


void FxControler::process(float *audio, float mix) {
    if (mIsFilterEnable)
        mFilter->process(audio, audio, mBufferSize);
    for (std::vector<Fx *>::iterator it = vecFx.begin(); it != vecFx.end(); ++it) {
        if ((*it)->getFx()->enabled) {
            //(*it)->getFx()->setMix(mix);
            (*it)->getFx()->process(audio, audio, mBufferSize);
        }
    }
}

FxControler::FxControler(int sampleRate, int bufferSize) {
    mSampleRate = sampleRate;
    mBufferSize = bufferSize;
    fxReverb = NULL;
    fxDelay = NULL;
    fxFlanger = NULL;
    fxLimiter = NULL;
    fxRoll = NULL;
    fxGate = NULL;
    fxWhoosh = NULL;
    mFilter = new Filter(mSampleRate);
    mFilter->enable(true);
    //vecFx.insert(vecFx.end(), mFilter);
    mIsFilterEnable = false;
    fxRollBeats = 0.5;
    fxGateBeats = 0.5;
    fxWhooshFreq = 0.5;
    fxFlangerLfoBeats = 0.5;
    fxDelayBeats = 0.5;
    mCurrentFilter = 10;

}

void FxControler::setFx(int keyFx, int fxKeyParam, float value) {
    switch (keyFx) {

        case FX_DELAY:
            setfxDeley(fxKeyParam, value);
            break;
        case FX_FLANGER:
            setFxFlanger(fxKeyParam, value);
            break;
        case FX_LIMITER:
            setFxLimiter(fxKeyParam, value);
            break;
        case FX_REVERB:
            setFxReverb(fxKeyParam, value);
            break;
        case FX_ROLL:
            setFxRoll(fxKeyParam, value);
            break;
        case FX_GATE:
            setFxGate(fxKeyParam, value);
            break;
        case FX_WHOOSH:
            setFxWhoosh(fxKeyParam, value);
            break;
    }
    if (keyFx >= 10) {
        //	mIsFilterEnable = true;
        setFxFilter(fxKeyParam, value);
    }
}

void FxControler::setFxFilter(int fxKeyParam, float value) {
    switch (fxKeyParam) {

        case FILTER_FREQ:
            mFilter->setFreq(value);
            break;
        case FILTER_RES:
            mFilter->setRes(value);
            break;
        case FILTER_DECIBEL:
            mFilter->setDecibal(value);
            break;
        case FILTER_OCTAVE:
            mFilter->setOctave(value);
            break;
        case FILTER_SLOPE:
            mFilter->setSlope(value);
            break;

    }
}

void FxControler::setfxDeley(int fxKeyParam, float value) {

    switch (fxKeyParam) {
        case DELAY_DISABLE:
            fxDelay->enable(value > 0.5);
            break;
        case DELAY_MIX:
            fxDelay->setMix(mDeleayMix = value);
            break;
        case BEATS:
            fxDelayBeats = value;
            fxDelay->setBeats(2.0 / ((double) (int) (value * 15.0 + 1.0)));
            break;
        case DECAY:
            fxDelay->setDecay(value);
            break;

    }

}

void FxControler::setFxFlanger(int fxKeyParam, float value) {
    switch (fxKeyParam) {

        case FLANGER_DISABLE:
            fxFlanger->enable(value > 0.5);
            break;
        case FLNG_WET:
            fxFlanger->setWet(value);
            break;
        case FLNG_DEPTH:
            fxFlanger->setDepth(value);
            break;
        case FLNG_LFO_BEATS:
            fxFlangerLfoBeats = value;
            fxFlanger->setLFOBeats(
                    64.0 / pow(2, (double((int) (value * 8.0)))));//  value * 63.75 + 0.25);
            break;
        case FLNG_CLIPPER_DB:
            fxFlanger->setClipperThresholdDb(value);
            break;
        case FLNG_CLIPPER_MAX_DB:
            fxFlanger->setClipperMaximumDb(value);
            break;


    }
}

void FxControler::setFxLimiter(int fxKeyParam, float value) {

    switch (fxKeyParam) {
        case LIMITER_DISABLE:
            fxLimiter->enable(value > 0.5);
            break;

        case LIMITER_CEILLING_DB:
            fxLimiter->setCeilingDb((value - 1.0) * 40.0);
            break;
        case LIMITER_THRESHHOLD_DB:
            fxLimiter->setThresholdDb((value - 1.0) * 40.0);
            break;
        case LIMITER_RELEASE_SEC:
            fxLimiter->setReleaseSec(value * 1.5 + 0.1);
            break;

    }
}

void FxControler::setFxReverb(int fxKeyParam, float value) {
    switch ((KeyReverb) fxKeyParam) {
        case REVERB_DISABLE:
            fxReverb->enable(value > 0.5);
            break;
        case DUMP:
            fxReverb->setDamp(value);
            break;
        case WIDTH:
            fxReverb->setWidth(value);
            break;
        case MIX:
            fxReverb->setMix(value);
            break;
        case DRY:
            fxReverb->setDry(value);
            break;
        case WET:
            fxReverb->setWet(value);
            break;
        case ROOMSIZE:
            fxReverb->setRoomSize(value);
            break;
    }


}

void FxControler::setFxRoll(int fxKeyParam, float value) {
    switch ((KeyRoll) fxKeyParam) {
        case ROLL_DISABLE:
            fxRoll->enable(value > 0.5);
            break;
        case ROLL_WET:
            fxRoll->setWet(value);

            break;
        case ROLL_BEATS:

            fxRollBeats = value;

            fxRoll->setBeat(4.0 / ((double) (int) pow(2, (double) (int) (value * 8))));

            break;
    }
}

void FxControler::setFxGate(int fxKeyParam, float value) {

    switch ((KeyGate) fxKeyParam) {
        case GATE_DISABLE:
            fxGate->enable(value > 0.5);
            break;
        case GATE_WET:
            fxGate->setWet(value);
            break;
        case GATE_BEATS:
            fxGateBeats = value;
            fxGate->setBeats(4.0 / ((double) (int) pow(2, (double) (int) (value *
                                                                          8)))); //* 3.984375f + 0.015625f;
            break;
    }
    //__android_log_print(ANDROID_LOG_DEBUG, "ndk", "beats %f",temp);
}

void FxControler::setFxWhoosh(int fxKeyParam, float value) {
    switch ((KeyWhoosh) fxKeyParam) {
        case WHOOSH_DISABLE:
            fxWhoosh->enable(value > 0.5);
            break;
        case WHOOSH_WET:
            fxWhoosh->setWet(value);
            break;
        case WHOOSH_FREQ:
            fxWhooshFreq = value;
            fxWhoosh->setFrequency(19980 * value + 20.0f);
            break;
    }
}

bool FxControler::addFx(int fxType) {
    bool res = false;
    switch (fxType) {

        case FX_DELAY:
            if (fxDelay == NULL) {
                fxDelay = new YBSuperpoweredEcho(mSampleRate);
                vecFxOrder.insert(vecFxOrder.end(), fxType);
                fxDelay->enable(true);
                fxDelay->setBpm (mBpm);
                Fx *fx = new Fx((SuperpoweredFX **) &fxDelay, "Delay", vecFx.size());

                vecFx.insert(vecFx.end(), fx);
                res = true;
            }
            break;
        case FX_FLANGER:
            if (fxFlanger == NULL) {
                fxFlanger = new YBSuperpoweredFlanger(mSampleRate);
                vecFxOrder.insert(vecFxOrder.end(), fxType);
                fxFlanger->enable(true);
                fxFlanger->setBpm (mBpm);
                Fx *fx = new Fx((SuperpoweredFX **) &fxFlanger, "Flanger", vecFx.size());

                vecFx.insert(vecFx.end(), fx);
                res = true;
            }
            break;
        case FX_LIMITER:
            if (fxLimiter == NULL) {
                fxLimiter = new YBSuperpoweredLimiter(mSampleRate);
                vecFxOrder.insert(vecFxOrder.end(), fxType);
                fxLimiter->enable(true);
                Fx *fx = new Fx((SuperpoweredFX **) &fxLimiter, "Limiter", vecFx.size());

                vecFx.insert(vecFx.end(), fx);
                res = true;
            }
            break;
        case FX_REVERB:
            if (fxReverb == NULL) {
                fxReverb = new YBSuperpoweredReverb(mSampleRate);
                vecFxOrder.insert(vecFxOrder.end(), fxType);
                fxReverb->enable(true);
                Fx *fx = new Fx((SuperpoweredFX **) &fxReverb, "Reverb", vecFx.size());

                vecFx.insert(vecFx.end(), fx);
                res = true;
            }
            break;
        case FX_ROLL:
            if (fxRoll == NULL) {
                fxRoll = new YBSuperpoweredRoll(mSampleRate);
                vecFxOrder.insert(vecFxOrder.end(), fxType);
                fxRoll->setBpm(mBpm);
                fxRoll->enable(true);
                Fx *fx = new Fx((SuperpoweredFX **) &fxRoll, "Roll", vecFx.size());
                vecFx.insert(vecFx.end(), fx);
                res = true;
            }
            break;
        case FX_GATE:
            if (fxGate == NULL) {
                fxGate = new YBSuperpoweredGate(mSampleRate);
                vecFxOrder.insert(vecFxOrder.end(), fxType);
                fxGate->setBpm (mBpm);
                fxGate->enable(true);
                Fx *fx = new Fx((SuperpoweredFX **) &fxGate, "Gate", vecFx.size());

                vecFx.insert(vecFx.end(), fx);
                res = true;
            }
            break;
        case FX_WHOOSH:
            if (fxWhoosh == NULL) {
                fxWhoosh = new YBSuperpoweredWhoosh(mSampleRate);
                vecFxOrder.insert(vecFxOrder.end(), fxType);
                fxWhoosh->enable(true);
                Fx *fx = new Fx((SuperpoweredFX **) &fxWhoosh, "Whoosh", vecFx.size());

                vecFx.insert(vecFx.end(), fx);
                res = true;
            }
            break;
        case FILTER_BAND_PASS:
            mCurrentFilter = fxType;
            mFilter->enable(true);
            mFilter->setType(SuperpoweredFilter_Bandlimited_Bandpass);

            res = true;
            break;
        case FILTER_HIGH_PASS:
            mCurrentFilter = fxType;
            mFilter->enable(true);
            mFilter->setType(SuperpoweredFilter_Resonant_Highpass);

            res = true;
            break;
        case FILTER_HIGH_SHELF:
            mCurrentFilter = fxType;
            mFilter->enable(true);
            mFilter->setType(SuperpoweredFilter_HighShelf);

            res = true;
            break;
        case FILTER_LOW_PASS:
            mCurrentFilter = fxType;
            mFilter->enable(true);
            mFilter->setType(SuperpoweredFilter_Resonant_Lowpass);

            res = true;
            break;
        case FILTER_LOW_SHELF:
            mCurrentFilter = fxType;
            mFilter->enable(true);
            mFilter->setType(SuperpoweredFilter_LowShelf);

            res = true;
            break;
        case FILTER_NOTCH:
            mCurrentFilter = fxType;
            mFilter->enable(true);
            mFilter->setType(SuperpoweredFilter_Bandlimited_Notch);

            res = true;
            break;
        case FILTER_PARAMETRIC:
            mCurrentFilter = fxType;
            mFilter->enable(true);
            mFilter->setType(SuperpoweredFilter_Parametric);
            res = true;
            break;


    }
    __android_log_print(ANDROID_LOG_DEBUG, "ndk", "%s %d", res ? "Trus" : "False", fxType);
    return res;
}


vector<int> FxControler::getActiveEffect() {
    return vecFxOrder;
}

void FxControler::setBpm(double bpm) {
    mBpm = bpm;
    if (fxGate != NULL)
        fxGate->setBpm (mBpm);
    if (fxDelay != NULL)
        fxDelay->setBpm (mBpm);
    if (fxFlanger != NULL)
        fxFlanger->setBpm (mBpm);
    if (fxRoll != NULL)
        fxRoll->setBpm(mBpm);

}


void FxControler::enableFilter(bool enable) {
    mIsFilterEnable = enable;
}

double FxControler::getFxValue(int fxType, int fxKeyParam) {
    double res = 0.0;
    switch (fxType) {

        case FX_DELAY:
            res = getfxDeley(fxKeyParam);
            break;
        case FX_FLANGER:
            res = getFxFlanger(fxKeyParam);
            break;
        case FX_LIMITER:
            res = getFxLimiter(fxKeyParam);
            break;
        case FX_REVERB:
            res = getFxReverb(fxKeyParam);
            break;
        case FX_ROLL:
            res = getFxRoll(fxKeyParam);
            break;
        case FX_GATE:
            res = getFxGate(fxKeyParam);
            break;
        case FX_WHOOSH:
            res = getFxWhoosh(fxKeyParam);
            break;
    }
    if (fxType >= 10) {
        //	mIsFilterEnable = true;
        res = getFxFilter(fxKeyParam);
    }
    return res;
}

double FxControler::getfxDeley(int fxKeyParam) {
    double res = 0;
    switch (fxKeyParam) {
        case DELAY_DISABLE:
            res = fxDelay->getEnabled() ? 1 : 0;
            break;
        case DELAY_MIX:
            res = mDeleayMix;
            break;
        case BEATS:
            res = fxDelayBeats;
            break;
        case DECAY:
            res = fxDelay->getDecay();
            break;

    }
    return res;
}

double FxControler::getFxFlanger(int fxKeyParam) {
    double res = 0;
    switch (fxKeyParam) {

        case FLANGER_DISABLE:
            res = fxFlanger->getEnabled() ? 1 : 0;
            break;
        case FLNG_WET:
            res = fxFlanger->getWet();
            break;
        case FLNG_DEPTH:
            res = fxFlanger->getDepth();
            break;
        case FLNG_LFO_BEATS:
            res = fxFlangerLfoBeats;
            break;
        case FLNG_CLIPPER_DB:
            res = fxFlanger->getClipperThresholdDb();
            break;
        case FLNG_CLIPPER_MAX_DB:
            res = fxFlanger->getClipperMaximumDb();
            break;


    }

    return res;
}

double FxControler::getFxLimiter(int fxKeyParam) {
    double res = 0;
    switch (fxKeyParam) {
        case LIMITER_DISABLE:
            res = fxLimiter->getEnabled() ? 1 : 0;
            break;

        case LIMITER_CEILLING_DB:
            res = fxLimiter->getcCeilingDb() / 40.0 + 1;
            break;
        case LIMITER_THRESHHOLD_DB:
            res = fxLimiter->getThresholdDb() / 40.0 + 1;
            break;
        case LIMITER_RELEASE_SEC:
            res = (fxLimiter->getReleaseSec() - 0.1) / 1.5;
            break;

    }

    return res;
}

double FxControler::getFxReverb(int fxKeyParam) {

    double res = 0;
    switch ((KeyReverb) fxKeyParam) {
        case REVERB_DISABLE:
            res = fxReverb->getEnabled() ? 1 : 0;
            break;
        case DUMP:
            res = fxReverb->getDamp();
            break;
        case WIDTH:
            res = fxReverb->getWidth();
            break;
        case MIX:
            res = fxReverb->getMix();
            break;
        case DRY:
            res = fxReverb->getDry();
            break;
        case WET:
            res = fxReverb->getWet();
            break;
        case ROOMSIZE:
            res = fxReverb->getRoomSize();
            break;
    }

    return res;
}

double FxControler::getFxRoll(int fxKeyParam) {
    double res = 0;
    switch ((KeyRoll) fxKeyParam) {
        case ROLL_DISABLE:
            res = fxRoll->getEnable() ? 1 : 0;
            break;
        case ROLL_WET:
            res = fxRoll->getWet();
            break;
        case ROLL_BEATS:
            res = fxRollBeats;


            break;
    }
    return res;
}

double FxControler::getFxGate(int fxKeyParam) {
    double res = 0;
    switch ((KeyGate) fxKeyParam) {
        case GATE_DISABLE:
            res = fxGate->getEnabled() ? 1 : 0;
            break;
        case GATE_WET:
            res = fxGate->getWet();
            break;
        case GATE_BEATS:
            res = fxGateBeats;
            break;
    }
    return res;
}

double FxControler::getFxWhoosh(int fxKeyParam) {
    double res = 0;

    switch ((KeyWhoosh) fxKeyParam) {
        case WHOOSH_DISABLE:
            res = fxWhoosh->getEnabled() ? 1 : 0;
            break;
        case WHOOSH_WET:
            res = fxWhoosh->getWet();
            break;
        case WHOOSH_FREQ:
            res = fxWhooshFreq;
            break;
    }
    return res;
}

double FxControler::getFxFilter(int fxKeyParam) {
    double res = 0;
    switch (fxKeyParam) {

        case FILTER_FREQ:
            res = mFilter->getFreq();
            break;
        case FILTER_RES:
            res = mFilter->getRes();
            break;
        case FILTER_DECIBEL:
            res = mFilter->getDecibal();
            break;
        case FILTER_OCTAVE:
            res = mFilter->getOctave();
            break;
        case FILTER_SLOPE:
            res = mFilter->getSlope();
            break;
        case FILTER_DISABLE:
            res = mIsFilterEnable ? 1 : 0;
            break;

    }

    return res;
}

int FxControler::getCurrentFilter() {
    return mCurrentFilter;
}

void FxControler::reset() {
    for (std::vector<Fx *>::iterator it = vecFx.begin(); it != vecFx.end(); ++it) {

        (*it)->getFx()->reset();
    }
}

void FxControler::replaceFxPosition(int from, int to) {


    Fx *temp = vecFx[from];
    vecFx.erase(vecFx.begin() + from);
    vecFx.insert(vecFx.begin() + (to), temp);

    int tempOrder = vecFxOrder[from];
    vecFxOrder.erase(vecFxOrder.begin() + from);
    vecFxOrder.insert(vecFxOrder.begin() + (to), tempOrder);

}

void FxControler::deleteEffectChannel(int index) {

    Fx *temp = vecFx[index];
    __android_log_print(ANDROID_LOG_DEBUG, "delete", "fx  %s index  %d", temp->getName().c_str(),
                        index);

    delete temp;
    vecFx.erase(vecFx.begin() + index);
    vecFxOrder.erase(vecFxOrder.begin() + index);

}
