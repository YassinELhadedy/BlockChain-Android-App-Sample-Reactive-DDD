package com.n26.yeh.blockchainsample.infrastructure.cashedmodel

import androidx.room.*

@Entity(tableName = "chart_table")
data class CashedChart(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    @ColumnInfo(name = "unit")
    val unit: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "status")
    val status: String,
    @ColumnInfo(name = "period")
    val period: String,
    @ColumnInfo(name = "size")
    val size: Int
)
