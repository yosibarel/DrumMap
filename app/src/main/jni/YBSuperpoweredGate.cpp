//
// Created by Yossi Barel on 07/08/16.
//

#include "YBSuperpoweredGate.h"

void YBSuperpoweredGate::enable(bool flag) {
    mSuperpoweredGate->enable(flag);
}

void YBSuperpoweredGate::setWet(float wet) {
    mSuperpoweredGate->wet = wet;
}

void YBSuperpoweredGate::setBeats(double beat) {
    mSuperpoweredGate->beats = beat;
}

void YBSuperpoweredGate::setBpm(double bpm) {
    mSuperpoweredGate->bpm = bpm;
}

bool YBSuperpoweredGate::getEnabled() {
    return mSuperpoweredGate->enabled;
}

double YBSuperpoweredGate::getWet() {
    return mSuperpoweredGate->wet;
}
