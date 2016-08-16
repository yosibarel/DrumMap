//
// Created by Yossi Barel on 07/08/16.
//

#include "YBSuperpoweredRoll.h"

void YBSuperpoweredRoll::setBpm(int param) {
    mSuperpoweredRoll->bpm = param;
}

void YBSuperpoweredRoll::enable(bool flag) {
    mSuperpoweredRoll->enable(flag);
}

double YBSuperpoweredRoll::getWet() {
    return mSuperpoweredRoll->wet;
}

bool YBSuperpoweredRoll::getEnable() {
    return mSuperpoweredRoll->enabled;
}

void YBSuperpoweredRoll::setBeat(double beats) {
    mSuperpoweredRoll->beats;
}

void YBSuperpoweredRoll::setWet(float wet) {
    mSuperpoweredRoll->wet = wet;
}
