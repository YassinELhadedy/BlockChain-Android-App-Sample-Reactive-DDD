package com.n26.yeh.blockchainsample.domain

/**
 * Pagination represents filtering, sorting, and limiting.
 */
enum class Operator(@Suppress("unused") val op: String) {
    Equal("="),
    NotEqual("!="),
    LessThan("<"),
    LessThanOrEqual("<="),
    GreaterThan(">"),
    GreaterThanOrEqual(">="),
    Like("~")
}

enum class Feild(val feild: String) {
    DateTime("start"),
    Format("format"),
    Duration("rollingAverage")
}

enum class Sort(val sort: String) {
    DateTime("start")
}


sealed class Expr
data class OrExpr(val lhs: Expr, val rhs: Expr) : Expr()
data class AndExpr(val lhs: Expr, val rhs: Expr) : Expr()
data class Condition<out T>(
    val field: Feild,
    val operator: Operator,
    val constant: T
) : Expr()

sealed class Direction
class Ascending : Direction()
class Descending : Direction()

// our abstract syntax tree model
data class SortBy(val sortExpression: Sort, val direction: Direction)

data class Pagination(
    val filter: Expr? = null,
    val sort: SortBy? = null,
    val offset: Int = 0,
    val pageSize: Int = 10
)

const val UNSUPPORTED_FIlED = "Unsupported field"
const val UNSUPPORTED_OPERATION = "Unsupported operation"
const val UNSUPPORTED_CONSTANT = "Unsupported constant"