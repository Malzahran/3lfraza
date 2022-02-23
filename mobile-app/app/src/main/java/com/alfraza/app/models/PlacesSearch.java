package com.alfraza.app.models;

import androidx.annotation.NonNull;

public class PlacesSearch {

    public String name;
    public String address;
    public String more;
    public double lat = 0D;
    public double lon = 0D;

    //to display object as a string in spinner
    @NonNull
    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PlacesSearch) {
            PlacesSearch c = (PlacesSearch) obj;
            return c.name.equals(name);
        }

        return false;
    }
}
