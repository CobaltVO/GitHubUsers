package ru.cobalt.githubusers.ui.listener

import android.view.MenuItem
import ru.cobalt.githubusers.model.UserViewModel

class OnMenuStateChangeListener(
    private val userViewModel: UserViewModel
) : MenuItem.OnActionExpandListener {

    override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
        userViewModel.startUsersSearch()
        return true
    }

    override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
        userViewModel.stopUsersSearch()
        return true
    }

}