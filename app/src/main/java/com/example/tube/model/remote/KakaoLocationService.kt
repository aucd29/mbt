package com.example.tube.model.remote

import com.example.tube.model.remote.entity.KakaoLocation
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2020-02-05 <p/>
 *
 * https://developers.kakao.com/docs/restapi/local#%EC%B9%B4%ED%85%8C%EA%B3%A0%EB%A6%AC%EB%A1%9C-%EC%9E%A5%EC%86%8C-%EA%B2%80%EC%83%89
 *
 * ----
 * CATEGORY CODE
 * ----
    HP8	병원
    PM9	약국
    OL7	주유소, 충전소
 */

interface KakaoLocationService {
    //curl -v -X GET "https://dapi.kakao.com/v2/local/search/category.json?category_group_code=PM9&rect=127.0561466,37.5058277,127.0602340,37.5142554" \
    //-H "Authorization: KakaoAK kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk"
    @GET("v2/local/search/category.json")
    fun searchCategory(@Query("category_group_code") category: String,
                       @Query("x") longitude: String,   // long
                       @Query("y") latitude: String,
                       @Query("page") page: Int = 1,
                       @Query("radius") radius: Int = 5000,
                       @Query("size") size: Int = 15): Single<KakaoLocation>
}