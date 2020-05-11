package com.example.googletts.Retrofit.DTO;

import java.util.ArrayList;

public class AudioConfig {
    enum AudioEncoding {
        AUDIO_ENCODING_UNSPECIFIED,
        LINEAR16,
        MP3,
        OGG_OPUS
    }

    AudioEncoding audioEncoding;
    Number speakingRate;
    Number pitch;
    Number volumnGainDb;
    Number sampleRateHertz;
    ArrayList<String> effectsProfileId;

    public AudioConfig() {
        audioEncoding = AudioEncoding.MP3;
    }

    public AudioEncoding getAudioEncoding() {
        return audioEncoding;
    }

    public void setAudioEncoding(AudioEncoding audioEncoding) {
        this.audioEncoding = audioEncoding;
    }

    public Number getSpeakingRate() {
        return speakingRate;
    }

    public void setSpeakingRate(Number speakingRate) {
        this.speakingRate = speakingRate;
    }

    public Number getPitch() {
        return pitch;
    }

    public void setPitch(Number pitch) {
        this.pitch = pitch;
    }

    public Number getVolumnGainDb() {
        return volumnGainDb;
    }

    public void setVolumnGainDb(Number volumnGainDb) {
        this.volumnGainDb = volumnGainDb;
    }

    public Number getSampleRateHertz() {
        return sampleRateHertz;
    }

    public void setSampleRateHertz(Number sampleRateHertz) {
        this.sampleRateHertz = sampleRateHertz;
    }

    public ArrayList<String> getEffectsProfileId() {
        return effectsProfileId;
    }

    public void setEffectsProfileId(ArrayList<String> effectsProfileId) {
        this.effectsProfileId = effectsProfileId;
    }
}
