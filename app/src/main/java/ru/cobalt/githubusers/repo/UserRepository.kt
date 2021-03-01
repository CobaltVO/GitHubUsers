package ru.cobalt.githubusers.repo

import ru.cobalt.githubusers.api.UserApi
import ru.cobalt.githubusers.repo.room.UserDao
import java.util.concurrent.ExecutorService

class UserRepository(
    private val executor: ExecutorService,
    private val userApi: UserApi,
    private val userDao: UserDao
) {

    private val callback = ListOfUsersCallback(executor, userDao)

    val users = userDao.getAll()

    fun init() = userApi.getAll().enqueue(callback)

    fun load(usersSinceId: Long, usersPerPage: Int) =
        userApi.getAll(usersSinceId, usersPerPage).enqueue(callback)

    fun deleteAll() = executor.execute { userDao.deleteAll() }
}