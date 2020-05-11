package com.example.googletts.Retrofit.DTO;

public class SynthesizeRequestDTO {
    SynthesisInput input;
    VoiceSelectionParams voice;
    AudioConfig audioConfig;

    public SynthesizeRequestDTO(SynthesisInput input, VoiceSelectionParams voice, AudioConfig audioConfig) {
        this.input = input;
        this.voice = voice;
        this.audioConfig = audioConfig;
    }

    public SynthesisInput getInput() {
        return input;
    }

    public void setInput(SynthesisInput input) {
        this.input = input;
    }

    public VoiceSelectionParams getVoice() {
        return voice;
    }

    public void setVoice(VoiceSelectionParams voice) {
        this.voice = voice;
    }

    public AudioConfig getAudio() {
        return audioConfig;
    }

    public void setAudio(AudioConfig audio) {
        this.audioConfig = audio;
    }
}
