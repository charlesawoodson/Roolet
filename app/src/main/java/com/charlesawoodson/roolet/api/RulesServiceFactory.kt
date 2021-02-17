package com.charlesawoodson.roolet.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

object RulesServiceFactory {

    private const val BASE_URL = "https://s3.amazonaws.com"

    //Creating Auth Interceptor to add api_key query in front of all the requests.
//    private val authInterceptor = Interceptor { chain ->
//        val newUrl = chain.request().url()
//            .newBuilder()
//            .build()
//
//        val newRequest = chain.request()
//            .newBuilder()
//            .url(newUrl)
//            .build()
//
//        chain.proceed(newRequest)
//    }

//    private val rulesClient = OkHttpClient().newBuilder()
//        .build()

    private fun retrofit(): Retrofit = Retrofit.Builder()
        // .client(rulesClient)
        .baseUrl(BASE_URL)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val rulesServiceApi: RulesService = retrofit().create(RulesService::class.java)
}