package com.example.tube.model.remote.entity

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2020-02-05 <p/>
 */

/* JSON SAMPLE
{
  "meta": {
    "same_name": null,
    "pageable_count": 11,
    "total_count": 11,
    "is_end": true
  },
  "documents": [
    {
      "place_name": "장생당약국",
      "distance": "",
      "place_url": "http://place.map.kakao.com/16618597",
      "category_name": "의료,건강 > 약국",
      "address_name": "서울 강남구 대치동 943-16",
      "road_address_name": "서울 강남구 테헤란로84길 17",
      "id": "16618597",
      "phone": "02-558-5476",
      "category_group_code": "PM9",
      "category_group_name": "약국",
      "x": "127.05897078335246",
      "y": "37.506051888130386"
    },
    ...
  ]
}
 */

data class KakaoLocation(
    val meta: KakaoLocationMeta,
    val documents: ArrayList<KakaoLocationDocument>
)

data class KakaoLocationMeta(
    val same_name: String?,
    val pageable_count: Int,
    val total_count: Int,
    val is_end: Boolean
)

data class KakaoLocationDocument(
    val place_name: String,
    val distance: String,
    val place_url: String,
    val category_name: String,
    val address_name: String,
    val road_address_name: String,
    val id: String,
    val phone: String,
    val category_group_code: String,
    val category_group_name: String,
    val x: String,
    val y: String
)