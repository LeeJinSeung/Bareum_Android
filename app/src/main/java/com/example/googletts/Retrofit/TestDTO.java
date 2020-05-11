package com.example.googletts.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TestDTO {

    @SerializedName("userid")
    @Expose
    int uid;

    @SerializedName("id")
    @Expose
    int id;

    @SerializedName("title")
    @Expose
    String title;

    @SerializedName("body")
    @Expose
    String completed;

    public TestDTO(int uid, int id, String title, String completed) {
        this.uid = uid;
        this.id = id;
        this.title = title;
        this.completed = completed;
    }

    public int getUid() { return uid; }

    public void setUid(int uid) { this.uid = uid; }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String isCompleted() { return completed; }

    public void setCompleted(String completed) { this.completed = completed; }
}
