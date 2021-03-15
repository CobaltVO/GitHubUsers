package ru.cobalt.githubusers.ui

import ru.cobalt.githubusers.model.User

sealed class ViewState {
    object Empty : ViewState()
    object Loading : ViewState()
    object Searching : ViewState()
    data class Loaded(val users: List<User>) : ViewState()
}
