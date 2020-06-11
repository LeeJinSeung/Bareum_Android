package com.example.googletts.Retrofit.DTO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class messageDTO {

    @SerializedName("message")
    @Expose
    private String message;

    public messageDTO(String message){
        this.message = message;
    }

    public String getMessage(){
        return message;
    }
}