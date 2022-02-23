package com.alfraza.app.models;

public class SelectedSpinnerData {

    private int spid, selid;
    private String stag;

    public SelectedSpinnerData(int spid, int selid, String stag) {
        this.spid = spid;
        this.selid = selid;
        this.stag = stag;
    }


    public int getSpid() {
        return spid;
    }

    public int getSelid() {
        return selid;
    }

    public String getStag() {
        return stag;
    }


}