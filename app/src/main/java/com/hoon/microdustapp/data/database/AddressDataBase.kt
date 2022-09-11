package com.hoon.microdustapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AddressEntity::class], version = 1)
abstract class AddressDataBase : RoomDatabase() {
    abstract fun addressDao(): AddressDao

    companion object {
        const val DATABASE_NAME = "AddressDataBase"

        fun build(context: Context): AddressDataBase =
            Room.databaseBuilder(context, AddressDataBase::class.java, DATABASE_NAME).build()
    }
}