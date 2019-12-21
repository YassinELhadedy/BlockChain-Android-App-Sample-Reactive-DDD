package com.n26.yeh.blockchainsample.ui.exception

import android.content.Context
import com.n26.yeh.blockchainsample.R
import com.n26.yeh.blockchainsample.domain.exception.*


/**
 * Factory used to create error messages from an Exception as a condition.
 */
class ErrorMessageFactory {
    companion object {
        fun create(context: Context, t: Throwable): String {
            var message = context.getString(R.string.exception_message_generic)

            when (t) {
                is UnauthorizedException -> context.getString(R.string.unauthorized_person)
                is TokenExpiredException -> message =
                    context.getString(R.string.exception_message_user_not_found)
                is EmptyPageException -> message = context.getString(R.string.data_not_existed)
                is ChartValuesFoundException -> message =
                    context.getString(R.string.values_not_existed)
            }
            return message
        }
    }
}

enum class ErrorCategory {
    EMPTYPAGE, SESSIONEXPIRED, NONE
}
