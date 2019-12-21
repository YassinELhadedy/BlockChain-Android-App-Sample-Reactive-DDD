package com.n26.yeh.blockchainsample.domain.repository

/**
 * Read Repository with Read Only Methods
 */
interface ReadRepository<out T> : GetRepository<T>, GetAllRepository<T>