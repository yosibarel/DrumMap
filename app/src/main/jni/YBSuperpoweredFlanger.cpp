//
// Created by Yossi Barel on 07/08/16.
//

#include "YBSuperpoweredFlanger.h"

void YBSuperpoweredFlanger::enable(bool flag) {
    mSuperpoweredFlanger->enable(flag);
}

void YBSuperpoweredFlanger::setWet(float wet) {
    mSuperpoweredFlanger->setWet(wet);
}

void YBSuperpoweredFlanger::setDepth(float depth) {
    mSuperpoweredFlanger->setDepth(depth);
}

void YBSuperpoweredFlanger::setLFOBeats(double beat) {
    mSuperpoweredFlanger->setLFOBeats(beat);
}

void YBSuperpoweredFlanger::setClipperThresholdDb(float db) {
    mSuperpoweredFlanger->clipperThresholdDb = db;
}

void YBSuperpoweredFlanger::setClipperMaximumDb(float db) {
    mSuperpoweredFlanger->clipperMaximumDb = db;
}

void YBSuperpoweredFlanger::setBpm(double bpm) {
    mSuperpoweredFlanger->bpm = bpm;
}

bool YBSuperpoweredFlanger::getEnabled() {
    return mSuperpoweredFlanger->enabled;
}

double YBSuperpoweredFlanger::getWet() {
    return mSuperpoweredFlanger->wet;
}

double YBSuperpoweredFlanger::getDepth() {
    return mSuperpoweredFlanger->depth;
}

double YBSuperpoweredFlanger::getClipperThresholdDb() {
    return mSuperpoweredFlanger->clipperThresholdDb;
}

double YBSuperpoweredFlanger::getClipperMaximumDb() {
    return mSuperpoweredFlanger->clipperMaximumDb;
}
