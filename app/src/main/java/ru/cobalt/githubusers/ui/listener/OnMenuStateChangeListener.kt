package ru.cobalt.githubusers.ui.listener

import android.content.Context
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import ru.cobalt.githubusers.model.UserViewModel

class OnMenuStateChangeListener(
    private val context: Context,
    private val userViewModel: UserViewModel
) : MenuItem.OnActionExpandListener {

    private fun showKeyboard(context: Context, item: MenuItem) {
        item.actionView.requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
        if (item != null) showKeyboard(context, item)
        userViewModel.startUsersSearch()
        return true
    }

    override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
        userViewModel.stopUsersSearch()
        return true
    }

}