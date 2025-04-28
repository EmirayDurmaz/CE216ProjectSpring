package com.example.ce216project;

import org.json.JSONObject;

public class Artifact {
    private String artifactId;
    private String artifactName;
    private String category;
    private String civilization;
    private String discoveryLocation;
    private String composition;
    private String discoveryDate;
    private String currentPlace;
    private JSONObject dimensions;
    private int weight;
    private String tags;

    public Artifact(String artifactId, String artifactName, String category, String civilization, String discoveryLocation, String composition, String discoveryDate, String currentPlace, JSONObject dimensions, int weight, String tags) {
        this.artifactId = artifactId;
        this.artifactName = artifactName;
        this.category = category;
        this.civilization = civilization;
        this.discoveryLocation = discoveryLocation;
        this.composition = composition;
        this.discoveryDate = discoveryDate;
        this.currentPlace = currentPlace;
        this.dimensions = dimensions;
        this.weight = weight;
        this.tags = tags;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getArtifactName() {
        return artifactName;
    }

    public void setArtifactName(String artifactName) {
        this.artifactName = artifactName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCivilization() {
        return civilization;
    }

    public void setCivilization(String civilization) {
        this.civilization = civilization;
    }

    public String getDiscoveryLocation() {
        return discoveryLocation;
    }

    public void setDiscoveryLocation(String discoveryLocation) {
        this.discoveryLocation = discoveryLocation;
    }

    public String getComposition() {
        return composition;
    }

    public void setComposition(String composition) {
        this.composition = composition;
    }

    public String getDiscoveryDate() {
        return discoveryDate;
    }

    public void setDiscoveryDate(String discoveryDate) {
        this.discoveryDate = discoveryDate;
    }

    public String getCurrentPlace() {
        return currentPlace;
    }

    public void setCurrentPlace(String currentPlace) {
        this.currentPlace = currentPlace;
    }

    public JSONObject getDimensions() {
        return dimensions;
    }

    public void setDimensions(JSONObject dimensions) {
        this.dimensions = dimensions;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
