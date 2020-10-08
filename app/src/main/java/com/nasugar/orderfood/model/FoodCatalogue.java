package com.nasugar.orderfood.model;

import java.io.Serializable;

public class FoodCatalogue implements Serializable {
    String id;
    String name;
    String linkimage;

    public FoodCatalogue(String id, String name, String linkimage) {
        this.id = id;
        this.name = name;
        this.linkimage = linkimage;
    }

    public FoodCatalogue() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLinkimage() {
        return linkimage;
    }

    public void setLinkimage(String linkimage) {
        this.linkimage = linkimage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
