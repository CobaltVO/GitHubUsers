package ru.cobalt.githubusers.ui.user.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView

private const val ID_SEARCH_CONTAINER = "android:id/search_plate"
private const val ID_SEARCH_CLOSE_BUTTON = "android:id/search_close_btn"

data class SearchViews(
    val searchLoaderView: View,
    val closeButtonView: View
)

fun SearchView.showSearchLoader(context: Context, loaderRes: Int): SearchViews {
    val searchViewGroupId = context.resources
        .getIdentifier(ID_SEARCH_CONTAINER, null, null)

    val closeSearchId = context.resources
        .getIdentifier(ID_SEARCH_CLOSE_BUTTON, null, null)

    val searchViewGroup = findViewById<View>(searchViewGroupId) as ViewGroup

    val closeSearchView = searchViewGroup.findViewById<View>(closeSearchId)

    val searchLoader = LayoutInflater
        .from(context)
        .inflate(loaderRes, searchViewGroup, false)
    searchLoader.minimumWidth = closeSearchView.width
    searchLoader.minimumHeight = closeSearchView.height

    searchViewGroup.removeView(closeSearchView)
    searchViewGroup.addView(searchLoader, 1)

    return SearchViews(searchLoader, closeSearchView)
}

fun SearchView.hideSearchLoader(views: SearchViews) {
    val searchViewGroupId = context.resources
        .getIdentifier(ID_SEARCH_CONTAINER, null, null)

    val searchViewGroup = findViewById<View>(searchViewGroupId) as ViewGroup
    searchViewGroup.removeView(views.searchLoaderView)
    searchViewGroup.addView(views.closeButtonView, 1)
}
