package com.example.googletts.Retrofit.DTO;

import java.io.Serializable;
import java.util.List;

public class WordBookDTO implements Serializable {
    private int wordbookId;
    private String wordData;

    public int getWordbookId(){return wordbookId;}

    public String getWordData(){return wordData;}
}
