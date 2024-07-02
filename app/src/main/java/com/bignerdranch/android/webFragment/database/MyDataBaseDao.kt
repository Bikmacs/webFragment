package com.bignerdranch.android.webFragment.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MyDataBaseDao {

    @Query("SELECT * FROM Mydata")
    fun query(): Flow<List<MyData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: MyData)

    @Update
    fun update(data: MyData)

}
