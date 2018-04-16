package com.west2ol.april.network;


import com.west2ol.april.network.api.BwyxApi;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHelper {
   private static String baseUrl="https://api.hack.rtxux.xyz/";
    //private static String baseUrl="http://192.168.43.153:8080";
    public static BwyxApi getBwyxApi(){
        return createApi(BwyxApi.class,baseUrl);
    }

    /**
     * 根据传入的baseUrl，和api创建retrofit
     */
    private static <T> T createApi(Class<T> clazz, String url) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(clazz);
    }

}
