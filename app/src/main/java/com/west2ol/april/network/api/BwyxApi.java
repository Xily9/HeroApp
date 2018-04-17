package com.west2ol.april.network.api;

import com.west2ol.april.entity.receive.AnswerInfo;
import com.west2ol.april.entity.receive.LoginInfo;
import com.west2ol.april.entity.receive.LogoutInfo;
import com.west2ol.april.entity.receive.PrizeInfo;
import com.west2ol.april.entity.receive.PrizeListInfo;
import com.west2ol.april.entity.receive.QuestionsInfo;
import com.west2ol.april.entity.receive.TimeInfo;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rx.Observable;

public interface BwyxApi {
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("/auth/login")
    Observable<LoginInfo> login(@Body RequestBody body);

    @GET("/auth/login")
    Observable<LoginInfo> guestLogin();

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("/auth/refresh")
    Observable<LoginInfo> refreshToken(@Body RequestBody body);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("/auth/register")
    Observable<LoginInfo> reg(@Body RequestBody body);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("/auth/transfer")
    Observable<LoginInfo> transfer(@Body RequestBody body);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("/auth/logout")
    Observable<LogoutInfo> logout(@Body RequestBody body);

    @GET("/get_time")
    Observable<TimeInfo> getTime();

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("/get_question")
    Observable<QuestionsInfo> getQuestion(@Body RequestBody body);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("/post_answer")
    Observable<AnswerInfo> postQuestion(@Body RequestBody body);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("/prize/list")
    Observable<PrizeListInfo> getPrizeList(@Body RequestBody body);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("/prize/get")
    Observable<PrizeInfo> getPrize(@Body RequestBody body);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("/prize/own")
    Observable<PrizeListInfo> getOwnPrize(@Body RequestBody body);
}
