package ru.cobalt.githubusers.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import ru.cobalt.githubusers.model.User

interface UserApi {

    @GET("users")
    fun getAll(): Call<List<User>>

    @GET("users")
    fun getAll(
        @Query("since") usersSinceId: Long,
        @Query("per_page") usersPerPage: Int,
    ): Call<List<User>>

}