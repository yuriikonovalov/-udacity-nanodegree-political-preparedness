package com.example.android.politicalpreparedness.network

import com.example.android.politicalpreparedness.network.jsonadapter.ElectionAdapter
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.ElectionResponse
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*

private const val BASE_URL = "https://www.googleapis.com/civicinfo/v2/"

// : Add adapters for Java Date and custom adapter ElectionAdapter (included in project)

object CivicsApi {
    fun provideCivicsApi(retrofit: Retrofit): CivicsApiService {
        return retrofit.create(CivicsApiService::class.java)
    }

    fun provideMoshi(): Moshi {
        return Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .add(ElectionAdapter())
                .add(Date::class.java, Rfc3339DateJsonAdapter())
                .build()
    }


    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .client(okHttpClient)
                .baseUrl(BASE_URL)
                .build()
    }
}


/**
 *  Documentation for the Google Civics API Service can be found at https://developers.google.com/civic-information/docs/v2
 */

interface CivicsApiService {
    //: Add elections API Call
    @GET("/elections")
    suspend fun getElections(): ElectionResponse

    //TODO: Add voterinfo API Call
//    @GET("/voterinfo")
//    suspend fun getVoterInfo(@Query("address") address: String)

    //TODO: Add representatives API Call

}


