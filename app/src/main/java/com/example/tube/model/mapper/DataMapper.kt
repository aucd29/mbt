package com.example.tube.model.mapper

import com.example.tube.model.local.recycler.SearchedData
import com.example.tube.model.remote.entity.KakaoLocation

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2020-02-05 <p/>
 */

object DataMapper {
    fun convert(data: KakaoLocation): ArrayList<SearchedData> {
        val list = arrayListOf<SearchedData>()

        data.documents.forEach {
            list.add(SearchedData(
                it.id,
                it.place_name,
                it.road_address_name,
                it.x.toDouble(),
                it.y.toDouble()
            ))
        }

        return list
    }
}