package com.messenger.messengerclient.Models.Communication;

import org.json.simple.JSONObject;
import retrofit2.Call;
import retrofit2.http.*;

public interface RestAPIFace {
    @POST("sendmessage/{threadId}")
    Call<JSONObject> sendMessage(@Header("token") String token,@Path("threadId") long threadId, @Body JSONObject jsonObject);
    @GET("getthread/{threadId}")
    Call<JSONObject> getMessages(@Path("threadId") long threadId);
    @GET("getlastmessage/{threadId}")
    Call<JSONObject> getLastMessage(@Path("threadId") long threadId);
    @GET("getfirstmessage/{threadId}")
    Call<JSONObject> getFirstMessage(@Path("threadId") long threadId);
    @POST("authorization")
    Call<JSONObject> auth(@Body JSONObject jsonObject);
    @GET("getsubscriptions")
    Call<JSONObject> getSubscriptions(@Header("token") String token);
    @GET("listen")
    Call<JSONObject> initNotifications();
    @POST("subscribe/{threadId}")
    Call<JSONObject> subscribe(@Header("token") String token,@Path("threadId") long threadId);
    @POST("unsubscribe/{threadId}")
    Call<JSONObject> unsubscribe(@Header("token") String token,@Path("threadId") long threadId);
    @POST("read/{threadId}")
    Call<JSONObject> read(@Header("token") String token,@Path("threadId") long threadId);
}
