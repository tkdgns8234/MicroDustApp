package com.hoon.microdustapp.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RegionEntity(
    // 각 Entity는 최소 하나 이상의 필드를 기본키로 지정해야 한다!
    @ColumnInfo val regionId: String,
    @ColumnInfo val description: String,
    @PrimaryKey(autoGenerate = true) val uid: Long = 0
)