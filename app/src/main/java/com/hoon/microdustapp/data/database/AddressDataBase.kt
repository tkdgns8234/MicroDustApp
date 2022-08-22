package com.hoon.microdustapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AddressEntity::class], version = 1)
abstract class AddressDataBase : RoomDatabase() {
    abstract fun addressDao(): AddressDao

    // google 에서 싱글톤 생성을 권장
    companion object {
        private var instance: AddressDataBase? = null

        @Synchronized
        fun getInstance(context: Context): AddressDataBase {
            if (instance == null) {
                synchronized(AddressDataBase::class) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AddressDataBase::class.java,
                        "user_database"
                    ).build()
                }
            }
            return instance!!
        }
    }
}