package ru.cobalt.githubusers.repo.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.cobalt.githubusers.model.User

@Dao
interface UserDao {

    @Query("SELECT * FROM users")
    fun getAll(): LiveData<List<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(users: List<User>)

}