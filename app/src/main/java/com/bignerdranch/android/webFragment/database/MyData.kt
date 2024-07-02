package com.bignerdranch.android.webFragment.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Mydata")
data class MyData(
    @PrimaryKey(autoGenerate = true)
    val PrimaryKey: Int,
    val Image: ByteArray?,
    val name: String,
    val surname: String,
    val group: String
)
