package com.west2ol.april.network.api;

import com.west2ol.april.entity.AnswerInfo;
import com.west2ol.april.entity.LoginInfo;
import com.west2ol.april.entity.PrizeInfo;
import com.west2ol.april.entity.PrizeListInfo;
import com.west2ol.april.entity.QuestionsInfo;
import com.west2ol.april.entity.TimeInfo;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rx.Observable;

public interface BwyxApi {
    @GET("/auth/login")
    Observable<LoginInfo> getToken();
    @GET("/get_time")
    Observable<TimeInfo> getTime();
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/get_question")
    Observable<QuestionsInfo> getQuestion(@Body RequestBody body);
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/post_answer")
    Observable<AnswerInfo> postQuestion(@Body RequestBody body);
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/prize/list")
    Observable<PrizeListInfo> getPrizeList(@Body RequestBody body);
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/prize/get")
    Observable<PrizeInfo> getPrize(@Body RequestBody body);
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/prize/own")
    Observable<PrizeListInfo> getOwnPrize(@Body RequestBody body);
}
