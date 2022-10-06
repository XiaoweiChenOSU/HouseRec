package com.okstate.VisualComputingandImageProcessingLab.HouseRec.DataBase;

public class Layout {
    private String name;
    private String description;
    private String remindTime;


    public Layout() {}

    public Layout(String name, String description, String remindTime) {
        this.name = name;
        this.description = description;
        this.remindTime = remindTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return remindTime;
    }

    public void setTime(String remindTime) {
        this.remindTime = remindTime;
    }
}
