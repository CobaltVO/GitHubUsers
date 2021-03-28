package ru.cobalt.githubusers.repo.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import ru.cobalt.githubusers.model.User

@Dao
interface UserDao {

    @Query("SELECT * FROM users")
    fun getAll(): Single<List<User>>

    @Query("SELECT * FROM users WHERE id > :idFrom LIMIT :count")
    fun get(idFrom: Long = 0, count: Int = 100): Maybe<List<User>>

    @Query("SELECT * FROM users WHERE login = :login")
    fun get(login: String): Maybe<User>

    @Query("SELECT * FROM users WHERE login LIKE :likeQuery")
    fun search(likeQuery: String): Maybe<List<User>>

    @Query("SELECT COUNT(*) FROM users")
    fun getCount(): Single<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(users: List<User>): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addSync(users: List<User>)

    @Query("DELETE FROM users")
    fun deleteAll(): Completable

}