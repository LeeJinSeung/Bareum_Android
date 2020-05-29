package com.example.googletts.Retrofit;

import java.util.ArrayList;

public class SentenceDTO {
    ArrayList<TestDTO> sentenceList;

    public SentenceDTO(ArrayList<TestDTO> sentenceList) {
        this.sentenceList = sentenceList;
    }

    public ArrayList<TestDTO> getSentenceList() {
        return sentenceList;
    }

    public void setSentenceList(ArrayList<TestDTO> sentenceList) {
        this.sentenceList = sentenceList;
    }
}
