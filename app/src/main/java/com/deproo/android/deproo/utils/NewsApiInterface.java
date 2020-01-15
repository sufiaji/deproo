package com.deproo.android.deproo.utils;

import com.deproo.android.deproo.model.News;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Pradhono Rakhmono Aji on 28/10/2019
 */
public interface NewsApiInterface {

//    @GET("top-headlines")
    @GET("everything")
    Call<News> getNews(

//            @Query("country") String country,
//            @Query("apiKey") String apiKey
        @Query("q") String q,
        @Query("language") String language,
        @Query("pageSize") int pageSize,
        @Query("page") int page,
        @Query("apiKey") String apiKey

    );

    @GET("everything")
    Call<News> getNews2(
        @Query("q") String q,
        @Query("language") String language,
        @Query("pageSize") int pageSize,
        @Query("apiKey") String apiKey
    );
}
