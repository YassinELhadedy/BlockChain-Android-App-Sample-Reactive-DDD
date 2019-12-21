package com.n26.yeh.blockchainsample.domain.exception

import com.n26.yeh.blockchainsample.domain.ModelException

/**
 * Exception throw by the application when a there is a network connection exception.
 */
class ChartValuesFoundException : ModelException {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}