package com.example.googletts.Retrofit.DTO;

import com.example.googletts.Retrofit.DTO.TestDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class analysisDTO {
    private TestDTO recommendSentence;
    private String recommendWord;
    private float score;
    private List<Integer> standardBlank;
    private List<Integer> userBlank;
    private List<Integer> wrongIndex;
    private String status;

    public analysisDTO(TestDTO recommendSentence, String recommendWord, float score,
                       List<Integer> standardBlank, List<Integer> userBlank, List<Integer> wrongIndex, String status)
    {
        this.recommendSentence = recommendSentence;
        this.recommendWord = recommendWord;
        this.score = score;
        this.standardBlank = standardBlank;
        this.userBlank = userBlank;
        this.wrongIndex = wrongIndex;
        this.status = status;
    }

    public TestDTO getRecommendSentence(){
        return recommendSentence;
    }

    public String getRecommendWord(){
        return recommendWord;
    }

    public String getStatus(){
        return status;
    }

    public float getScore(){
        return score;
    }

    public List<Integer> getStandardBlank(){
        return standardBlank;
    }

    public List<Integer> getUserBlank(){
        return userBlank;
    }

    public List<Integer> getWrongIndex(){
        return wrongIndex;
    }
}
