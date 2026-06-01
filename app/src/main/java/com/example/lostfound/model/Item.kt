package com.example.lostfound.model

import com.google.gson.annotations.SerializedName

data class Item(
    var id: String? = null,
    var title: String? = null,
    var description: String? = null,
    var category: String? = null,
    var status: String? = null,
    var location: String? = null,
    @SerializedName("imageUrl")
    var imageUrl: String? = null,
    var date: String? = null,
    var time: String? = null,
    @SerializedName("postedBy")
    var reporterName: String? = null,
    @SerializedName("createdAt")
    var createdAt: String? = null,
    @SerializedName("contactInfo")
    var contactInfo: String? = null
)
