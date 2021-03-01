package ru.cobalt.githubusers.model

import androidx.lifecycle.ViewModel
import ru.cobalt.githubusers.repo.UserRepository

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {
    val users = userRepository.users

    init {
        userRepository.init()
    }

    fun update() {
        val id = userRepository.users.value?.last()?.id ?: 0
        userRepository.load(id, 10)
    }

    fun deleteAll() {
        userRepository.deleteAll()
    }
}