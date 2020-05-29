package com.example.googletts.Retrofit.DTO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ScoreDTO {


    public ArrayList<Float> getScore() {
        return score;
    }

    public void setScore(ArrayList<Float> score) {
        this.score = score;
    }

    public ArrayList<PhonemeDTO> getMostPhoneme() {
        return mostPhoneme;
    }

    public void setMostPhoneme(ArrayList<PhonemeDTO> mostPhoneme) {
        this.mostPhoneme = mostPhoneme;
    }

    public ScoreDTO(ArrayList<Float> score, ArrayList<PhonemeDTO> mostPhoneme) {
        this.score = score;
        this.mostPhoneme = mostPhoneme;
    }

    @SerializedName("recentScore")
    @Expose
    ArrayList<Float> score;

    @SerializedName("mostPhoneme")
    @Expose
    ArrayList<PhonemeDTO> mostPhoneme;


}
