package com.sliit.learnmedicine.DTO;

/**
 * Medicine Data Transfer Object
 * Used to hold information about a single medicine (drug)
 */
public class Medicine {

    private String id;
    private String name;
    private String description;
    private boolean favourite;

    /**
     * Standard Empty Constructor
     * Allows object to be instantiated without any data and be filled later or
     */
    public Medicine() {

    }

    /**
     * Constructor which creates the object with complete information about the medicine
     *
     * @param id          the unique ID of the medicine/drug
     * @param name        the name of the medicine/drug
     * @param description description of the medicine/drug
     * @param favourite   is the medicine marked as favourite
     */
    public Medicine(String id, String name, String description, boolean favourite) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.favourite = favourite;
    }

    // Getters and Setters

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
