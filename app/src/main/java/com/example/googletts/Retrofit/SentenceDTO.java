package com.example.googletts.Retrofit;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

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

    public boolean empty() {
        if(sentenceList == null) return true;
        return sentenceList.size() == 0;
    }

    public TestDTO getFront() {
        TestDTO ret = sentenceList.get(0);
        sentenceList.remove(0);
        return ret;
    }
}
