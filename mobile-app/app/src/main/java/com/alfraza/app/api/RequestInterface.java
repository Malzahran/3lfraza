package com.alfraza.app.api;

import com.alfraza.app.data.Constant;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface RequestInterface {
    String CACHE = "Cache-Control: max-age=0";
    String AGENT = "User-Agent: Markeet";
    String SECURITY = "Security: " + Constant.SECURITY_CODE;

    @Headers({CACHE, AGENT, SECURITY})
    @POST("api.php")
    Call<ServerResponse> operation(@Body ServerRequest request);

    @Multipart
    @POST("api.php")
    Call<ServerResponse> uploadImage(@PartMap Map<String, RequestBody> data,
                                     @Part MultipartBody.Part files);

}