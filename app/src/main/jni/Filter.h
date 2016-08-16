//
// Created by Yossi Barel on 04/04/16.
//

#ifndef DRUMMAP_FILTER_H
#define DRUMMAP_FILTER_H

#include "LFOControler.h"


class Filter : public SuperpoweredFX
{
public:
	Filter(int sampleRate);

	~Filter();

	bool process(float *input, float *output, unsigned int numberOfSamples);

	void setType(SuperpoweredFilterType _filterType);

	void setFreq(float freq);

	void setRes(float res);

	void setDecibal(float dec);

	void setOctave(float octave);

	void setSlope(float slope);

	void enable(bool flag);

	void setSamplerate(unsigned int samplerate);

	void reset();

	void setMix(float value);

	double getFreq();

	double getRes();

	double getDecibal();

	double getOctave();

	double getSlope();

private:

	SuperpoweredFilterType filterType;
	SuperpoweredFilter *filter;
	float mFrequency;
	float mDecibel;
	float mResonance;
	float mOctave;
	float mSlope;
	double mFrequencyVal;
};


#endif //DRUMMAP_FILTER_H
