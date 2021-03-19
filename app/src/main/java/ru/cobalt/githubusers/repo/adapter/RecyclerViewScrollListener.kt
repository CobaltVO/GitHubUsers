package ru.cobalt.githubusers.repo.adapter

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewScrollListener(
    private val layoutManager: RecyclerView.LayoutManager,
    private val loadingThresholdReachCallback: (position: Int) -> Unit,
) : RecyclerView.OnScrollListener() {

    var isActivated: Boolean = true
    var pageSize: Int = 100
    var loadThreshold: Double = 1.0

    private var isNeedLoading: Boolean = true
    private var lastTotalItemsCount: Int = -1

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        if (layoutManager is LinearLayoutManager) {
            val totalItemCount = layoutManager.itemCount
            val lastItemPosition: Int = layoutManager.findLastVisibleItemPosition()

            if (
                isActivated
                && isNeedLoading
                && lastTotalItemsCount != totalItemCount
                && lastItemPosition >= totalItemCount - pageSize * loadThreshold
            ) {
                loadingThresholdReachCallback.invoke(totalItemCount - 1)
                lastTotalItemsCount = totalItemCount
                isNeedLoading = false
            }
        }
    }

    fun onDataLoaded() {
        isNeedLoading = true
    }
}