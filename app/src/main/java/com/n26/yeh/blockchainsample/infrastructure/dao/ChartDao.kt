package com.n26.yeh.blockchainsample.infrastructure.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.n26.yeh.blockchainsample.infrastructure.cashedmodel.CashedChart

@Dao
interface ChartDao {

    @Query("SELECT * from chart_table ORDER BY unit ASC")
    abstract fun getAlphabetizedWords(): List<CashedChart>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insert(chart: CashedChart)

    @Query("DELETE FROM chart_table")
    abstract fun deleteAll()
}