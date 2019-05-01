package com.sliit.learnmedicine.DTO;

public class Medicine {

    public Medicine() {

    }

    public Medicine(String id, String name, String description, boolean favourite) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.favourite = favourite;
    }

    private String id;
    private String name;
    private String description;
    private boolean favourite;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }
}
