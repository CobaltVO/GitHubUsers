package ru.cobalt.githubusers.ui

import ru.cobalt.githubusers.model.User

sealed class ViewState {
    object Empty : ViewState()
    object Loading : ViewState()
    object Searching : ViewState()

    data class Loaded(val users: List<User>) : ViewState() {
        override fun toString(): String {
            return "${this.javaClass.simpleName}, ${users.size} users"
        }
    }

    data class Searched(val users: List<User>) : ViewState() {
        override fun toString(): String {
            return "${this.javaClass.simpleName}, ${users.size} users"
        }
    }

    data class NetworkError(val lastUserId: Long, val errorMessage: String) : ViewState()
    data class SearchError(val errorMessage: String) : ViewState()
    data class DatabaseError(val errorMessage: String) : ViewState()

    override fun toString(): String {
        return this.javaClass.simpleName
    }
}
