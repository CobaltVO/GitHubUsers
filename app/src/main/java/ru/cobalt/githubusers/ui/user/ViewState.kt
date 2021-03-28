package ru.cobalt.githubusers.ui.user

import ru.cobalt.githubusers.model.User

sealed class ViewState(open val users: List<User>) {
    object Initialization : ViewState(listOf())
    object Cleared : ViewState(listOf())

    data class Reloading(override val users: List<User>) : ViewState(users) {
        override fun toString(): String {
            return "${javaClass.simpleName}, ${users.size} users"
        }
    }

    data class Loading(override val users: List<User>) : ViewState(users) {
        override fun toString(): String {
            return "${javaClass.simpleName}, ${users.size} users"
        }
    }

    data class Loaded(override val users: List<User>) : ViewState(users) {
        override fun toString(): String {
            return "${javaClass.simpleName}, ${users.size} users"
        }
    }

    data class Searching(override val users: List<User>) : ViewState(users) {
        override fun toString(): String {
            return "${javaClass.simpleName}, ${users.size} users"
        }
    }

    data class Searched(override val users: List<User>, val searchedUsers: List<User>) :
        ViewState(users) {
        override fun toString(): String {
            return "${javaClass.simpleName}, ${users.size}+${searchedUsers.size} users"
        }
    }

    data class NetworkError(
        override val users: List<User>,
        val errorMessage: String,
        val lastUserId: Long
    ) : ViewState(users)

    data class ApiLimitError(
        override val users: List<User>,
        val errorMessage: String,
        val docUrl: String
    ) : ViewState(users)

    data class SearchError(override val users: List<User>, val errorMessage: String) :
        ViewState(users)

    data class DatabaseError(override val users: List<User>, val errorMessage: String) :
        ViewState(users)
}
