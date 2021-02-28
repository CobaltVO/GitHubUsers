package ru.cobalt.githubusers.repo.room

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.cobalt.githubusers.model.User

@Database(
    entities = [User::class],
    version = 1,
)
abstract class UserDatabase : RoomDatabase() {
    abstract val userDao: UserDao
}