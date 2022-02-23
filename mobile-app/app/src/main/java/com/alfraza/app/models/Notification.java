package com.alfraza.app.models;

import java.io.Serializable;

public class Notification implements Serializable {

    public Long id;
    public String title;
    public String content;
    public String type;
    public Long obj_id;
    public String image;
    public String code;
    public String status;

    // extra attribute
    public Boolean read = false;
    public String created_at;

}
