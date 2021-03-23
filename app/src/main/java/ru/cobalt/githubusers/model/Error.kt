package ru.cobalt.githubusers.model

import com.google.gson.annotations.SerializedName

data class Error(
    @SerializedName("message")
    val message: String,

    @SerializedName("documentation_url")
    val docUrl: String,
)