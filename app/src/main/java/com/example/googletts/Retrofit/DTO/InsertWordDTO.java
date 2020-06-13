package com.example.googletts.Retrofit.DTO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class InsertWordDTO implements Serializable {
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @SerializedName("message")
    @Expose
    private String message;

    public WordBookDTO getWord() {
        return word;
    }

    public void setWord(WordBookDTO word) {
        this.word = word;
    }

    @SerializedName("newWordBook")
    @Expose
    private WordBookDTO word;
}
