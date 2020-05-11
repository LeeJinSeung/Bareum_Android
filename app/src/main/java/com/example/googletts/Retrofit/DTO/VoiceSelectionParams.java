package com.example.googletts.Retrofit.DTO;

public class VoiceSelectionParams {
    String languageCode;
    String name;

    public VoiceSelectionParams() {
        languageCode = "ko-KR";
        name = "ko-KR-Standard-C";
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
