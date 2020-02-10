package com.example.tube.model.local.recycler

import brigitte.IRecyclerDiff

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2020-02-05 <p/>
 */

data class SearchedData(
    val id: String,
    val title: String,
    val address: String,
    val longitude: Double,
    val latitude: Double
): IRecyclerDiff {
    override fun itemSame(item: IRecyclerDiff): Boolean {
        val newItem = item as SearchedData
        return id == newItem.id
    }

    override fun contentsSame(item: IRecyclerDiff): Boolean {
        val newItem = item as SearchedData
        return item.title == newItem.title && item.address == newItem.address
    }
}

