package com.vijay.contactz.network

import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("https://randomuser.me/api/?results=25&inc=gender,name,picture,phone,cell,id,email")
    fun getPosts(): Call<ApiResponse>
}