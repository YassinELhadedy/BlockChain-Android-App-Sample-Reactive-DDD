package com.n26.yeh.blockchainsample.domain.services

import com.n26.yeh.blockchainsample.domain.ModelException
import com.n26.yeh.blockchainsample.domain.model.Configuration
import com.n26.yeh.blockchainsample.infrastructure.ConfigurationRepository
import io.reactivex.Observable

class SaveMetaData(private val configurationRepository: ConfigurationRepository) {

    fun saveMetaConfigurationData(configuration: Configuration): Observable<out Configuration> {
        return configurationRepository.insert(configuration)
            .map { it }
            .onErrorResumeNext { e: Throwable ->
                Observable.error(ModelException(e))
            }
    }
}