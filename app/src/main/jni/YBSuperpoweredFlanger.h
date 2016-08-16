//
// Created by Yossi Barel on 07/08/16.
//

#ifndef DRUMMAP_YBSUPERPOWEREDFLANGER_H
#define DRUMMAP_YBSUPERPOWEREDFLANGER_H

#include "SuperpoweredFlanger.h"
#include "Mixable.h"

class YBSuperpoweredFlanger : public Mixable {
public:
    YBSuperpoweredFlanger(int sampleRate) {
        mSuperpoweredFlanger = new SuperpoweredFlanger(sampleRate);
    }

    ~YBSuperpoweredFlanger() {
        delete mSuperpoweredFlanger;
    }

    void enable(bool flag);

    void setWet(float wet);

    void setDepth(float depth);

    void setLFOBeats(double beat);

    void setClipperThresholdDb(float db);

    void setClipperMaximumDb(float db);

    void setBpm(double bpm);

    bool getEnabled();

    double getWet();

    double getDepth();

    double getClipperThresholdDb();

    double getClipperMaximumDb();

private:
    SuperpoweredFlanger *mSuperpoweredFlanger;
};


#endif //DRUMMAP_YBSUPERPOWEREDFLANGER_H
