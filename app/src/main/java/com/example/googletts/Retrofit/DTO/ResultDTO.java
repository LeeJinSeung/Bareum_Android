package com.example.googletts.Retrofit.DTO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.FormatFlagsConversionMismatchException;
import java.util.List;

public class ResultDTO implements Serializable {


    public List<Float> getScore() {
        return score;
    }

    public void setScore(List<Float> score) {
        this.score = score;
    }

    public List<ArrayList<String>> getMostPhoneme() {
        return mostPhoneme;
    }

    public void setMostPhoneme(List<ArrayList<String>> mostPhoneme) {
        this.mostPhoneme = mostPhoneme;
    }

    public ResultDTO(List<Float> score, List<ArrayList<String>> mostPhoneme) {
        this.score = score;
        this.mostPhoneme = mostPhoneme;
    }

    public ResultDTO() {

    }

    @SerializedName("recentScore")
    @Expose
    List<Float> score;

    @SerializedName("mostPhoneme")
    @Expose
    List<ArrayList<String>> mostPhoneme;
}
