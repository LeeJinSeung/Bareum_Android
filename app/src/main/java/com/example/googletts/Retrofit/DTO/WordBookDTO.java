package com.example.googletts.Retrofit.DTO;

import java.util.List;

public class WordBookDTO {
    private int wordbookId;
    private String wordData;
    private List<TestDTO> recommend;

    public int getWordbookId(){return wordbookId;}

    public String getWordData(){return wordData;}

    public List<TestDTO> getRecommend() {
        return recommend;
    }
}
