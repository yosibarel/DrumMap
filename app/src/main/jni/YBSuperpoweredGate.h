//
// Created by Yossi Barel on 07/08/16.
//

#ifndef DRUMMAP_YBSUPERPOWEREDGATE_H
#define DRUMMAP_YBSUPERPOWEREDGATE_H

#include "Mixable.h"
#include "SuperpoweredGate.h"

class YBSuperpoweredGate : public Mixable {
public:
    YBSuperpoweredGate(int sampleRate) {
        mSuperpoweredGate = new SuperpoweredGate(sampleRate);
    }

    ~YBSuperpoweredGate() {
        delete mSuperpoweredGate;
    }

    void enable(bool flag);

    void setWet(float wet);

    void setBeats(double beat);

    void setBpm(double bpm);

    bool getEnabled();

    double getWet();

private:
    SuperpoweredGate *mSuperpoweredGate;
};


#endif //DRUMMAP_YBSUPERPOWEREDGATE_H
