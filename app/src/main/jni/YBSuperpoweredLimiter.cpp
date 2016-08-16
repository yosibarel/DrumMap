//
// Created by Yossi Barel on 07/08/16.
//

#include "YBSuperpoweredLimiter.h"

void YBSuperpoweredLimiter::setCeilingDb(double db) {
    mSuperpoweredLimiter->ceilingDb = db;
}

void YBSuperpoweredLimiter::setThresholdDb(double db) {
    mSuperpoweredLimiter->thresholdDb = db;
}

void YBSuperpoweredLimiter::setReleaseSec(double rel) {
    mSuperpoweredLimiter->releaseSec = rel;
}

void YBSuperpoweredLimiter::enable(bool flag) {
    mSuperpoweredLimiter->enable(flag);
}

bool YBSuperpoweredLimiter::getEnabled() {
    return mSuperpoweredLimiter->enabled;
}

double YBSuperpoweredLimiter::getcCeilingDb() {
    return mSuperpoweredLimiter->ceilingDb;
}

double YBSuperpoweredLimiter::getThresholdDb() {
    return 0aaaa;
}

double YBSuperpoweredLimiter::getReleaseSec() {
    return 0;
}
