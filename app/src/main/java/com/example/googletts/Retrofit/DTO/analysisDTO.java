package com.example.googletts.Retrofit.DTO;

import com.example.googletts.Retrofit.DTO.TestDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class analysisDTO {
    private float score;
    private List<Integer> standardBlank;
    private List<Integer> userBlank;
    private List<Integer> wrongIndex;
    private List<String> wordList;
    private String resultData;
    private String status;

    public analysisDTO(List<String> wordList, float score,
                       List<Integer> standardBlank, List<Integer> userBlank, List<Integer> wrongIndex,
                       String status, String resultData)
    {
        this.score = score;
        this.wordList = wordList;
        this.standardBlank = standardBlank;
        this.userBlank = userBlank;
        this.wrongIndex = wrongIndex;
        this.status = status;
        this.resultData = resultData;
    }

    public List<String> getWordList() {return wordList;}

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

    public String getResultData(){return resultData;}
}
