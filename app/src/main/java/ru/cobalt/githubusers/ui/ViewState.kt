package ru.cobalt.githubusers.ui

sealed class ViewState {
    object Empty : ViewState()
    object Initialization : ViewState()

    object Reloading : ViewState()
    object Loading : ViewState()
    object Loaded : ViewState()

    object Searching : ViewState()
    object Searched : ViewState()

    data class NetworkError(val lastUserId: Long, val errorMessage: String) : ViewState()
    data class SearchError(val errorMessage: String) : ViewState()
    data class DatabaseError(val errorMessage: String) : ViewState()

    override fun toString(): String {
        return this.javaClass.simpleName
    }
}
