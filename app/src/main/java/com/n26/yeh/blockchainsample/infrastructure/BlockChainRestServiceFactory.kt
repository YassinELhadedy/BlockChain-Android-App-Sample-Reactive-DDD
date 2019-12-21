package com.n26.yeh.blockchainsample.infrastructure

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * SuperGlideRestServiceFactory
 */
class BlockChainRestServiceFactory(private val baseUrl: String) {


    val service: BlockChainRestService by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(SafeOkHttpClientFactory.okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BlockChainRestService::class.java)
    }
}