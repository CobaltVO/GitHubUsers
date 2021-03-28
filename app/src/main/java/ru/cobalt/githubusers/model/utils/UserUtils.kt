package ru.cobalt.githubusers.model.utils

import ru.cobalt.githubusers.model.User
import ru.cobalt.githubusers.ui.user.adapter.ViewType

fun List<User>.toUserViewType() = this.map { ViewType.UserViewType(it) }