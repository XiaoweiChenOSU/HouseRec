package com.okstate.VisualComputingandImageProcessingLab.HouseRec.Layout;

public class Dog {


    private String id;
    private String name;
    private String age;
    private String breed;
    private String weight;

    public Dog(String id, String name, String age, String breed, String weight) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.breed = breed;
        this.weight = weight;
    }

    public String getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getAge() {
        return this.age;
    }

    public String getWeight() {
        return this.weight;
    }

    public String getBreed() {
        return this.breed;
    }
}
