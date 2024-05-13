package com.vijay.contactz.remoteDataFragment

import retrofit2.Call
import retrofit2.http.GET

interface ContactzRetrofitInterface {

    @GET("api")
    fun getAllContactz():Call<List<RemoteContactModel>>
}