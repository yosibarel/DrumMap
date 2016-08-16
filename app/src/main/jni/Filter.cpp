//
// Created by Yossi Barel on 04/04/16.
//

#include "Filter.h"

#define MINFREQ 60.0f
#define MAXFREQ 30000.0f


static inline float floatToFrequency(float value)
{
	if (value > 0.97f)
		return MAXFREQ;
	if (value < 0.03f)
		return MINFREQ;
	value = powf(10.0f, (value + ((0.4f - fabsf(value - 0.4f)) * 0.3f)) * log10f(MAXFREQ - MINFREQ)) + MINFREQ;
	return value < MAXFREQ ? value : MAXFREQ;

}

Filter::Filter(int sampleRate)
{
	filter = new SuperpoweredFilter(SuperpoweredFilter_Resonant_Lowpass, sampleRate);
	mFrequencyVal = 0.5;

}

Filter::~Filter()
{
	delete(filter);
}

void Filter::setType(SuperpoweredFilterType _filterType)
{
	filterType = _filterType;
	switch (filterType)
	{
		case    SuperpoweredFilter_Resonant_Lowpass:
			filter->setResonantParametersAndType(mFrequency, mResonance, SuperpoweredFilter_Resonant_Lowpass);
			break;
		case        SuperpoweredFilter_Resonant_Highpass:
			filter->setResonantParametersAndType(mFrequency, mResonance, SuperpoweredFilter_Resonant_Highpass);
			break;
		case        SuperpoweredFilter_Bandlimited_Bandpass:
			filter->setBandlimitedParametersAndType(mFrequency, mOctave, SuperpoweredFilter_Bandlimited_Bandpass);
			break;
		case        SuperpoweredFilter_Bandlimited_Notch:
			filter->setBandlimitedParametersAndType(mFrequency, mOctave, SuperpoweredFilter_Bandlimited_Notch);
			break;
		case        SuperpoweredFilter_LowShelf:
			filter->setShelfParametersAndType(mFrequency, mSlope, mDecibel, SuperpoweredFilter_LowShelf);
			break;
		case        SuperpoweredFilter_HighShelf:
			filter->setShelfParametersAndType(mFrequency, mSlope, mDecibel, SuperpoweredFilter_HighShelf);
			break;
		case        SuperpoweredFilter_Parametric:
			filter->setShelfParametersAndType(mFrequency, mSlope, mDecibel, SuperpoweredFilter_Parametric);

			break;

	}
}

void Filter::setFreq(float freq)
{
	mFrequencyVal = freq;
	mFrequency = floatToFrequency(freq);
	if (filterType == SuperpoweredFilter_Resonant_Lowpass || filterType == SuperpoweredFilter_Resonant_Highpass)
		filter->setResonantParameters(mFrequency, mResonance);
	else if (filterType == SuperpoweredFilter_LowShelf || filterType == SuperpoweredFilter_HighShelf)

		filter->setShelfParameters(mFrequency, mSlope, mDecibel);
	else if (filterType == SuperpoweredFilter_Bandlimited_Notch || filterType == SuperpoweredFilter_Bandlimited_Bandpass)
		filter->setBandlimitedParameters(mFrequency, mOctave);
	__android_log_print(ANDROID_LOG_DEBUG, "filter", "freq %f res %f", mFrequency, mResonance);

	//filter->setParametricParameters(mFrequency, mOctave, mDecibel);
}

void Filter::setRes(float res)
{
	mResonance = res;
	filter->setResonantParameters(mFrequency, mResonance);
}

void Filter::setDecibal(float dec)
{
	mDecibel = (dec - 0.5) * 50.0;
	if (filterType == SuperpoweredFilter_LowShelf || filterType == SuperpoweredFilter_HighShelf)

		filter->setShelfParameters(mFrequency, mSlope, mDecibel);

	//filter->setParametricParameters(mFrequency, mOctave, mDecibel);
}

void Filter::setOctave(float octave)
{

	mOctave = (octave) * 100.0;
	filter->setBandlimitedParameters(mFrequency, mOctave);
	//filter->setParametricParameters(mFrequency, mOctave, mDecibel);

}


void Filter::setSlope(float slope)
{
	mSlope = slope * 4980.0 + 20;
	filter->setShelfParameters(mFrequency, mSlope, mDecibel);
}

double Filter::getFreq()
{
	return mFrequencyVal;
}

double Filter::getRes()
{
	return filter->resonance;
}

double Filter::getDecibal()
{
	return mDecibel / 5.0 + 0.5;
}

double Filter::getOctave()
{
	return mOctave / 100.0;
}

double Filter::getSlope()
{
	return (mSlope - 20) / 4980.0;
}

void Filter::enable(bool flag)
{
	filter->enable(flag);
}

bool Filter::process(float *input, float *output, unsigned int numberOfSamples)
{
	filter->process(input, output, numberOfSamples);
}

void Filter::reset()
{
	filter->reset();
}

void Filter::setSamplerate(unsigned int samplerate)
{
	filter->setSamplerate(samplerate);
}

void Filter::setMix(float value)
{
}
