package ru.cobalt.githubusers.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: Long,

    val login: String,

    @SerializedName("avatar_url")
    val photoUrl: String,

    @SerializedName("html_url")
    val userPageUrl: String,
)