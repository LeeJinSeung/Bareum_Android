package com.example.googletts.Retrofit.DTO;

import java.io.Serializable;
import java.util.ArrayList;

public class SentenceDTO implements Serializable {
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
