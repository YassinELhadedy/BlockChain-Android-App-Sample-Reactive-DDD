package com.n26.yeh.blockchainsample.infrastructure

import com.n26.yeh.blockchainsample.infrastructure.ChartMapper.toChart
import com.n26.yeh.blockchainsample.domain.Pagination
import com.n26.yeh.blockchainsample.domain.SupportTransaction
import com.n26.yeh.blockchainsample.domain.model.Chart
import com.n26.yeh.blockchainsample.domain.model.Configuration
import com.n26.yeh.blockchainsample.domain.repository.Repository
import com.n26.yeh.blockchainsample.infrastructure.dao.ChartDao
import io.reactivex.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.n26.yeh.blockchainsample.infrastructure.ChartMapper.toCashedChart
import kotlinx.coroutines.runBlocking


/**
 * Chart Disk Repository
 * This class is open for Mockito to be able to mock it.
 */
open class ChartDiskRepository(
    private val chartDao: ChartDao,
    private val configurationRepository: ConfigurationRepository
) : Repository<Chart, Chart>,
    SupportTransaction {

    override fun <T> doInTransaction(operation: () -> T): T =
        runBlocking {
            //FIXME : we shouldn't block streams when we have a reactive full cycle. so i need alternative way to fix exception "withcontext should be called from a cortouine or suspend function".
            withContext(Dispatchers.IO) {
                try {
                    val result = operation()
                    result
                } catch (error: Exception) {
                    throw InfrastructureException(error)
                }
            }
        }

    override fun get(id: Int): Observable<out Chart> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(id: Int): Observable<Unit> =
        Observable.fromCallable {
            doInTransaction {
                chartDao.deleteAll()
            }
        }

    /**
     * Hint we need later use {@link Paginator} Custom here to filter where query sql like in {@link BlockChainRepository}
     */
    override fun getAll(pagination: Pagination): Observable<out List<Chart>> {
        return Observable.fromCallable {
            chartDao.getAlphabetizedWords().map {
                configurationRepository.insert(
                    Configuration(
                        it.description,
                        it.size,
                        null
                    )
                )
                it.toChart()
            }

        }.onErrorResumeNext { throwable: Throwable ->
            if (throwable is NullPointerException) {
                Observable.empty()
            } else {
                Observable.error(InfrastructureException(throwable))
            }
        }
    }

    override fun insert(entity: Chart): Observable<out Chart> = Observable.fromCallable {
        doInTransaction {
            chartDao.insert(entity.toCashedChart())
            entity
        }
    }

    override fun update(entity: Chart): Observable<Unit> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun insertOrUpdate(entity: Chart): Observable<out Chart> =
        Observable.fromCallable {
            doInTransaction {
                chartDao.deleteAll()
                chartDao.insert(entity.toCashedChart())
                entity
            }
        }
}