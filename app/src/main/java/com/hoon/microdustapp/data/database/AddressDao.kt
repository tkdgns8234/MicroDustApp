package com.hoon.microdustapp.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AddressDao {
    @Query("select * from AddressEntity")
    suspend fun getAll(): MutableList<AddressEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE) // 이미 존재하는경우 replace
    suspend fun insert(address: AddressEntity)

    @Query("select * from AddressEntity where addressName == :addressName")
    suspend fun find(addressName: String): MutableList<AddressEntity>

}