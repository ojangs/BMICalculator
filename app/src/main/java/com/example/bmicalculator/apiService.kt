package com.example.bmicalculator

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface apiService {

        @GET("weight-category")
        suspend fun getWeightCategory(@Query("bmi") bmi: Double): Response<ResponseModelWeigtCategory>

}