package com.hoon.microdustapp.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AddressDao {
    @Query("select * from AddressEntity")
    fun getAll(): Flow<List<AddressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE) // 이미 존재하는경우 replace
    suspend fun insert(address: AddressEntity)

    @Delete
    suspend fun delete(address: AddressEntity)

    @Query("select * from AddressEntity where addressName == :addressName")
    suspend fun find(addressName: String): AddressEntity?

}