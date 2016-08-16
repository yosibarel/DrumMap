//
// Created by Yossi Barel on 07/08/16.
//

#ifndef DRUMMAP_YBSUPERPOWEREDECHO_H
#define DRUMMAP_YBSUPERPOWEREDECHO_H

#include "SuperpoweredEcho.h"
#include "Mixable.h"

class YBSuperpoweredEcho : public Mixable {
public:
    YBSuperpoweredEcho(int sampleRate) {
        mSuperpoweredEcho = new SuperpoweredEcho(sampleRate);
    }

    ~YBSuperpoweredEcho() {
        delete mSuperpoweredEcho;
    }

    void enable(bool flag);

    void setMix(double mix);

    void setBeats(double beats);

    void setDecay(float decay);

    void setBpm(double bpm);

    double getDecay();

    bool getEnabled();

private:
    SuperpoweredEcho *mSuperpoweredEcho;
};


#endif //DRUMMAP_YBSUPERPOWEREDECHO_H
