//
// Created by Yossi Barel on 07/08/16.
//

#include "YBSuperpoweredEcho.h"

void YBSuperpoweredEcho::enable(bool flag) {
    mSuperpoweredEcho->enable(flag);
}

void YBSuperpoweredEcho::setMix(double mix) {
    mSuperpoweredEcho->setMix(mix);
}

void YBSuperpoweredEcho::setBeats(double beats) {
    mSuperpoweredEcho->beats = beats;
}

void YBSuperpoweredEcho::setDecay(float decay) {
    mSuperpoweredEcho->decay = decay;
}

void YBSuperpoweredEcho::setBpm(double bpm) {
    mSuperpoweredEcho->bpm = bpm;
}

double YBSuperpoweredEcho::getDecay() {
    return mSuperpoweredEcho->decay;
}

bool YBSuperpoweredEcho::getEnabled() {
    return mSuperpoweredEcho->enabled;
}
