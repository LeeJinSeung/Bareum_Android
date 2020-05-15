package com.example.googletts.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TestDTO {

    @SerializedName("sentenceId")
    @Expose
    int sid;

    @SerializedName("sentenceData")
    @Expose
    String sentence;

    @SerializedName("standard")
    @Expose
    String standard;

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public String getStandard() {
        return standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }
}
