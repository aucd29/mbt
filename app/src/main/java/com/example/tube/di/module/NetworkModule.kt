package com.example.tube.di.module

import brigitte.AuthorizationInterceptor
import brigitte.di.dagger.module.OkhttpModule
import com.example.tube.model.remote.KakaoLocationService
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 5. <p/>
 *
 * - 카테고리로 장소 검색
 * https://developers.kakao.com/docs/restapi/local#%EC%B9%B4%ED%85%8C%EA%B3%A0%EB%A6%AC%EB%A1%9C-%EC%9E%A5%EC%86%8C-%EA%B2%80%EC%83%89
 */

@Module(includes = [OkhttpModule::class])
class NetworkModule {
    // 다수개의 retrofit 을 이용해야 하므로 Retrofit.Builder 를 전달 받은 후
    // 이곳에서 baseurl 을 설정하는 방식을 이용한다.

    companion object {
        val LOG_CLASS = NetworkModule::class.java

        const val KAKAO_DAPI_URL = "https://dapi.kakao.com/"

        const val KAKAO_AK          = "KakaoAK"
        const val AUTHORIZATION     = "Authorization"
        const val KAKAO_REST_AUTH   = "057fe89a88de5f16335b4025e3b9aaf4"
    }

    @Provides
    @Singleton
    fun provideKakaoApiService(retrofitBuilder: Retrofit.Builder): KakaoLocationService =
        retrofitBuilder.baseUrl(KAKAO_DAPI_URL).build()
            .create(KakaoLocationService::class.java)

    @Provides
    @Singleton
    fun provideLogger(): Logger =
        LoggerFactory.getLogger(LOG_CLASS)

    @Provides
    @Singleton
    fun provideLogLevel() =
        HttpLoggingInterceptor.Level.BODY

    @Provides
    @Singleton
    fun provideAuthorizationInterceptor(): AuthorizationInterceptor =
        object: AuthorizationInterceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                return chain.proceed(
                    chain.request().newBuilder()
                        .addHeader(AUTHORIZATION, "$KAKAO_AK $KAKAO_REST_AUTH")
                        .build()
                )
            }
        }

}
