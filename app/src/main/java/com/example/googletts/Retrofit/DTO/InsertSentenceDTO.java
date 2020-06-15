package com.example.googletts.Retrofit.DTO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InsertSentenceDTO {
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public TestDTO getSentence() {
        return sentence;
    }

    public void setSentence(TestDTO sentence) {
        this.sentence = sentence;
    }

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("newSentence")
    @Expose
    private TestDTO sentence;
}
