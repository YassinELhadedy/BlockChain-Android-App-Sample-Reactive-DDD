package com.n26.yeh.blockchainsample.domain.exception

import com.n26.yeh.blockchainsample.domain.ModelException

class UnauthorizedException : ModelException {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}