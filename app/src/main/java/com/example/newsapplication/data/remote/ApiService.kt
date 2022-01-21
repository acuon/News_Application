package com.example.newsapplication.data.remote

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

interface ApiService {

//    @GET("everything?q=trending&from=2021-10-08&sortBy=publishedAt&apiKey=287da81201e84513866473c106bac1af")
    @GET("everything?q=trending&sortBy=publishedAt&apiKey=287da81201e84513866473c106bac1af")
    fun getTrendingNews(
        @Query("from") date: String
    ): Observable<ResponseModel>

    @GET("everything?from=2021-10-08&sortBy=publishedAt")
    fun getUserQueryNews(
        @Query("apiKey") apiKey: String,
        @Query("q") q: String
    ): Observable<ResponseModel>

}