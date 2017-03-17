package com.lenovo.silentrecognition.data.bean;

import java.io.Serializable;

/**
 * Created by mary on 2016/11/4.
 */
public class PersonInfo implements Serializable {
    private int id ;
    private int recognitionId;
    private String name;
    private String imagePath;
    private String voicePath;

    public PersonInfo() {
    }

    public PersonInfo(int recognitionId, String name, String imagePath, String voicePath) {
        this.recognitionId = recognitionId;
        this.name = name;
        this.imagePath = imagePath;
        this.voicePath = voicePath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRecognitionId() {
        return recognitionId;
    }

    public void setRecognitionId(int recognitionId) {
        this.recognitionId = recognitionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getVoicePath() {
        return voicePath;
    }

    public void setVoicePath(String voicePath) {
        this.voicePath = voicePath;
    }
}
