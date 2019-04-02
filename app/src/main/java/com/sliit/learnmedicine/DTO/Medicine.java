package com.sliit.learnmedicine.DTO;

public class Medicine {

    public Medicine(String name, String description, String sideEffects){
        this.name = name;
        this.description = description;
        this.sideEffects = sideEffects;
    }

    private String name;
    private String description;
    private String sideEffects;
}
