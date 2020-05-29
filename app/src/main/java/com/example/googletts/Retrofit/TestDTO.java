package com.example.googletts.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TestDTO implements Serializable {

    @SerializedName("sentenceId")
    @Expose
    int sentenceId;

    @SerializedName("sentenceData")
    @Expose
    String sentenceData;

    @SerializedName("standard")
    @Expose
    String standard;

    public int getSid() {
        return sentenceId;
    }

    public void setSid(int sentenceId) {
        this.sentenceId = sentenceId;
    }

    public String getSentence() {
        return sentenceData;
    }

    public void setSentence(String sentenceData) {
        this.sentenceData = sentenceData;
    }

    public String getStandard() {
        return standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }
}
