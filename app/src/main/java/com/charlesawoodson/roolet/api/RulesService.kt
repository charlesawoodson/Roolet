package com.charlesawoodson.roolet.api

import retrofit2.Call
import retrofit2.http.GET

/**
 * Created by charles.adams on 05/24/2020
 */

interface RulesService {

    @GET("/roolet-rules/rules.json")
    fun getRules(): Call<RulesResponse>

}