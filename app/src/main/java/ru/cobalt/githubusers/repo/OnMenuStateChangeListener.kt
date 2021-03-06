package ru.cobalt.githubusers.repo

import android.view.MenuItem
import ru.cobalt.githubusers.model.UserViewModel
import ru.cobalt.githubusers.utils.log

class OnMenuStateChangeListener(
    private val userViewModel: UserViewModel
) : MenuItem.OnActionExpandListener {

    override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
        userViewModel.startSearch()
        log("search started")
        return true
    }

    override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
        userViewModel.stopSearch()
        log("search stopped")
        return true
    }

}