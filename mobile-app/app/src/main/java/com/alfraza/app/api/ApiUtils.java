package com.alfraza.app.api;

public class ApiUtils {
    public static RequestInterface getService() {
        return RetrofitClient.getClient().create(RequestInterface.class);
    }
}