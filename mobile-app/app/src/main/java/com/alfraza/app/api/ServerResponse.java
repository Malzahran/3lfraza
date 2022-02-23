package com.alfraza.app.api;

import com.alfraza.app.models.ActionData;
import com.alfraza.app.models.Cart;
import com.alfraza.app.models.Category;
import com.alfraza.app.models.City;
import com.alfraza.app.models.IntentData;
import com.alfraza.app.models.ItemData;
import com.alfraza.app.models.LayoutData;
import com.alfraza.app.models.LocationData;
import com.alfraza.app.models.MessagesData;
import com.alfraza.app.models.NewFormData;
import com.alfraza.app.models.NewsInfo;
import com.alfraza.app.models.NotificationData;
import com.alfraza.app.models.Order;
import com.alfraza.app.models.PlacesSearch;
import com.alfraza.app.models.Product;
import com.alfraza.app.models.StoreData;
import com.alfraza.app.models.User;

import java.util.ArrayList;
import java.util.List;

public class ServerResponse {

    private String result;
    private String message;
    private int success_action = 0;
    private String notes;
    public int total = 0;
    private boolean error;
    private int lastid, firstid, totalcount;
    private User user;
    private ActionData action;
    private List<Category> cats;
    private List<ItemData> items;
    private ItemData item;
    private NotificationData notification;
    private MessagesData[] messages;
    private LayoutData layout;
    private NewFormData form;
    private IntentData intentData;
    private StoreData store;
    private LocationData[] locations;
    public Product product;
    public Cart cart;
    public City city;
    public List<Order> orders;
    public List<PlacesSearch> places;
    public List<NewsInfo> news_infos = new ArrayList<>();
    public NewsInfo news_info;


    public StoreData getStore() {
        return store;
    }

    public List<Category> getCats() {
        return cats;
    }

    public String getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public int getSuccessAction() {
        return success_action;
    }

    public String getNotes() {
        return notes;
    }

    public Boolean getError() {
        return error;
    }

    public IntentData getIntent() {
        return intentData;
    }

    public User getUser() {
        return user;
    }

    public List<ItemData> getItemsListing() {
        return items;
    }

    public ItemData getItemInfo() {
        return item;
    }

    public NewFormData getFormInfo() {
        return form;
    }

    public ActionData getAction() {
        return action;
    }

    public NotificationData getNotification() {
        return notification;
    }

    public MessagesData[] getMessages() {
        return messages;
    }

    public LayoutData getLayoutInfo() {
        return layout;
    }

    public LocationData[] getLocations() {
        return locations;
    }

    public int getFirstid() {
        return firstid;
    }

    public int getLastid() {
        return lastid;
    }

    public int getTotalcount() {
        return totalcount;
    }
}