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

    override fun toString(): String {
        return this.javaClass.simpleName
    }
}
