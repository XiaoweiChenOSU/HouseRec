package com.okstate.VisualComputingandImageProcessingLab.HouseRec.content;

import java.io.Serializable;

public class HomeContext implements Serializable {
    public final String id;
    public String layout;
    public String details;
    public String text;
    public String videoURL;
    public String audioURL;
    public String name;
    public PromptType type;

    public enum PromptType implements Serializable {
        TEXT,
        VIDEO,
        AUDIO
    }

    public HomeContext(String id, String layout, String name, String details) {
        this.id = id;
        this.layout = layout;
        this.name = name;
        this.details = details;
        this.type = PromptType.TEXT;
        this.text = "";
        this.videoURL = "";
        this.audioURL = "";

    }

    @Override
    public String toString() {
        return name;
    }
}