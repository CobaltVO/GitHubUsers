package ru.cobalt.githubusers.domain.user

import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.cobalt.githubusers.api.UserApi
import ru.cobalt.githubusers.model.User
import ru.cobalt.githubusers.repo.room.UserDao
import ru.cobalt.githubusers.utils.log

class UserInteractor(
    private val userApi: UserApi,
    private val userDao: UserDao,
) {

    private fun downloadAndSave(): Single<List<User>> =
        userApi.getAll()
            .doOnSuccess { userDao.addSync(it) }
            .doAfterSuccess { log("${it.size} new users were downloaded from server") }

    private fun downloadAndSave(idFrom: Long, count: Int): Single<List<User>> =
        userApi.get(idFrom, count)
            .doOnSuccess { userDao.addSync(it) }
            .doAfterSuccess { log("${it.size} new users were downloaded from server") }

    fun get(): Maybe<List<User>> =
        Single.concat(userDao.getAll(), downloadAndSave())
            .filter { list -> list.isNotEmpty() }
            .firstElement()
            .subscribeOn(Schedulers.io())

    fun get(idFrom: Long, count: Int): Maybe<List<User>> =
        Single.concat(userDao.get(idFrom, count), downloadAndSave(idFrom, count))
            .filter { list -> list.isNotEmpty() }
            .firstElement()
            .subscribeOn(Schedulers.io())

    fun get(login: String): Single<User> =
        userDao.get(login)
            .subscribeOn(Schedulers.io())

    fun search(query: String): Single<List<User>> =
        userDao.search("$query%")
            .subscribeOn(Schedulers.io())

    fun deleteAll() = userDao.deleteAll()

}