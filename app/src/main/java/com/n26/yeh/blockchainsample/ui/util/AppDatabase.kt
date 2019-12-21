package com.n26.yeh.blockchainsample.ui.util

import androidx.room.Database
import androidx.room.RoomDatabase
import com.n26.yeh.blockchainsample.infrastructure.cashedmodel.CashedChart
import com.n26.yeh.blockchainsample.infrastructure.dao.ChartDao

@Database(entities = [CashedChart::class], version = 1,exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chartDao(): ChartDao
}