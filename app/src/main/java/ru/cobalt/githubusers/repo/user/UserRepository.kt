package ru.cobalt.githubusers.repo.user

import android.content.Context
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.cobalt.githubusers.api.UserApi
import ru.cobalt.githubusers.di.app.App
import ru.cobalt.githubusers.model.User
import ru.cobalt.githubusers.repo.room.UserDao
import ru.cobalt.githubusers.utils.log

class UserRepository(
    private val appContext: Context,
    private val userApi: UserApi,
    private val userDao: UserDao,
) {
    private fun saveToDatabase(list: List<User>) =
        (appContext as App).activityComponent?.compositeDisposable?.add(
            userDao.add(list)
                .subscribeOn(Schedulers.io())
                .subscribe { log("${list.size} new users were saved to database") }
        )

    private fun downloadAndSave() =
        userApi.getAll()
            .doOnSuccess { saveToDatabase(it) }
            .doAfterSuccess { log("${it.size} new users were downloaded from server") }

    private fun downloadAndSave(idFrom: Long, count: Int) =
        userApi.get(idFrom, count)
            .doOnSuccess { saveToDatabase(it) }
            .doAfterSuccess { log("${it.size} new users were downloaded from server") }

    fun get(): Maybe<List<User>> =
        Single.concat(userDao.getAll(), downloadAndSave())
            .filter { list -> list.isNotEmpty() }
            .firstElement()
            .subscribeOn(Schedulers.io())

    fun get(idFrom: Long, count: Int): Maybe<List<User>> =
        Single.concat(userDao.get(idFrom, count), downloadAndSave(idFrom, 100))
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