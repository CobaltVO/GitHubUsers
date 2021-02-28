package ru.cobalt.githubusers.repo

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.cobalt.githubusers.model.User
import ru.cobalt.githubusers.repo.room.UserDao

class ListOfUsersCallback(private val userDao: UserDao) : Callback<List<User>> {

    override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
        if (response.isSuccessful) response.body()?.let { Thread { userDao.add(it) }.start() }
        else response.errorBody()?.let { Log.d("wtf", it.string()) }
    }

    override fun onFailure(call: Call<List<User>>, t: Throwable) {
        Log.d("wtf", "can not make response: ${t.message}")
    }

}