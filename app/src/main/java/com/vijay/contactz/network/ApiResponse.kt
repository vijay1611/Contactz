package com.vijay.contactz.network

import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("results") var results: ArrayList<Results> = arrayListOf(),
    @SerializedName("info") var info: Info? = Info()
)

data class Name(

    @SerializedName("title") var title: String? = null,
    @SerializedName("first") var first: String? = null,
    @SerializedName("last") var last: String? = null

)

data class Id(

    @SerializedName("name") var name: String? = null,
    @SerializedName("value") var value: String? = null

)

data class Picture(

    @SerializedName("large") var large: String? = null,
    @SerializedName("medium") var medium: String? = null,
    @SerializedName("thumbnail") var thumbnail: String? = null

)

data class Results(

    @SerializedName("gender") var gender: String? = null,
    @SerializedName("name") var name: Name? = Name(),
    @SerializedName("email") var email: String? = null,
    @SerializedName("phone") var phone: String? = null,
    @SerializedName("cell") var cell: String? = null,
    @SerializedName("id") var id: Id? = Id(),
    @SerializedName("picture") var picture: Picture? = Picture()

)

data class Info(

    @SerializedName("seed") var seed: String? = null,
    @SerializedName("results") var results: Int? = null,
    @SerializedName("page") var page: Int? = null,
    @SerializedName("version") var version: String? = null

)