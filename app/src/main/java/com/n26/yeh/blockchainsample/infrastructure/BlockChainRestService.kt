package com.n26.yeh.blockchainsample.infrastructure

import com.n26.yeh.blockchainsample.infrastructure.dto.BcChart
import com.n26.yeh.blockchainsample.infrastructure.dto.BcState
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BlockChainRestService {
    @GET("charts/{transactionsRate}")
    fun getChart(
        @Path("transactionsRate") transactionsRate: String?,
        @Query("timespan", encoded = true) timespan: String? = null, //FIXME : we don't need all this query since we have a paginator can expose as query string like "timespan=5weeks&rollingAverage=8hours&format=json"
        @Query("rollingAverage") rollingAverage: String? = null,
        @Query("start", encoded = true) start: String? = null,
        @Query("format",encoded = false) format: String? = null,
        @Query("sampled") sampled: Boolean? = null
    ): Observable<BcChart>

    @GET("stats")
    fun getState(): Observable<BcState>
}