package com.hoon.microdustapp.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AddressEntity(
    // 각 Entity는 최소 하나 이상의 필드를 기본키로 지정해야 한다!
    @ColumnInfo val addressName: String,
    @ColumnInfo val x: Double,
    @ColumnInfo val y: Double,
    @PrimaryKey(autoGenerate = true) val uid: Long = 0
)