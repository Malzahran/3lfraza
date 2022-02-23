package com.alfraza.app.api;

import com.alfraza.app.models.BuyerProfile;
import com.alfraza.app.models.ContactData;
import com.alfraza.app.models.ItemData;
import com.alfraza.app.models.MiscData;
import com.alfraza.app.models.Tracker;
import com.alfraza.app.models.User;

import java.util.ArrayList;

public class ServerRequest {

    private String operation;
    private String parenttype;
    private String subtype;
    private User user;
    private MiscData misc;
    private Tracker tracker;
    private ContactData contact;
    private ArrayList<ItemData> items;
    private BuyerProfile buyer;

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public void setParent(String parenttype) {
        this.parenttype = parenttype;
    }

    public void setSubType(String subtype) {
        this.subtype = subtype;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setMisc(MiscData misc) {
        this.misc = misc;
    }

    public void setTracker(Tracker tracker) {
        this.tracker = tracker;
    }

    public void setContact(ContactData contact) {
        this.contact = contact;
    }

    public void setCart(ArrayList<ItemData> items) {
        this.items = items;
    }

    public void setBuyer(BuyerProfile buyer) {
        this.buyer = buyer;
    }
}