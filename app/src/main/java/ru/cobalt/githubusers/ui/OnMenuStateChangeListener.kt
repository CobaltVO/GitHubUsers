package ru.cobalt.githubusers.ui

import android.view.MenuItem
import ru.cobalt.githubusers.model.UserViewModel

class OnMenuStateChangeListener(
    private val userViewModel: UserViewModel
) : MenuItem.OnActionExpandListener {

    override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
        userViewModel.startSearch()
        return true
    }

    override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
        userViewModel.stopSearch()
        return true
    }

}