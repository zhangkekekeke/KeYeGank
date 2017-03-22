package com.dih.keye.keyegank.retrofitUtils;


import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by zsj on 2015/11/20 0020.
 */
public class GirlRetrofit {

    private final GirlApi girlApi;

    private static final String GANK_URL = "http://gank.io/api/";


    public GirlRetrofit() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(20, TimeUnit.SECONDS);
        builder.readTimeout(15, TimeUnit.SECONDS);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GANK_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(builder.build())
                .build();
        girlApi = retrofit.create(GirlApi.class);
    }

    public GirlApi getGirlApi() {
        return girlApi;
    }
}