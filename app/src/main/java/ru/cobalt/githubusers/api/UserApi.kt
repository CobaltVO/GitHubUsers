package ru.cobalt.githubusers.api

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query
import ru.cobalt.githubusers.model.User

interface UserApi {

    @GET("users")
    fun getAll(): Single<List<User>>

    @GET("users")
    fun get(
        @Query("since") usersFromId: Long,
        @Query("per_page") usersPerPage: Int,
    ): Single<List<User>>

}