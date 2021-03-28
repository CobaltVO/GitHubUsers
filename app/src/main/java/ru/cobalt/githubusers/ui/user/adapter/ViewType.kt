package ru.cobalt.githubusers.ui.user.adapter

import ru.cobalt.githubusers.model.User

const val TYPE_USER = 0
const val TYPE_LOADER = 1

sealed class ViewType {
    abstract val viewType: Int

    data class UserViewType(val user: User) : ViewType() {
        override val viewType = TYPE_USER
    }

    object LoaderViewType : ViewType() {
        override val viewType = TYPE_LOADER
    }
}