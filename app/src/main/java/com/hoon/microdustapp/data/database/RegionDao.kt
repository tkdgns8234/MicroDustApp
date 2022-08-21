package com.hoon.microdustapp.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RegionDao {
    @Query("select * from RegionEntity" )
    suspend fun getAll(): MutableList<RegionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE) // 이미 존재하는경우 replace
    suspend fun insertRegion(region: RegionEntity)

    @Query("select * from RegionEntity where regionId == :regionId")
    suspend fun findRegion(regionId: String): MutableList<RegionEntity>

}