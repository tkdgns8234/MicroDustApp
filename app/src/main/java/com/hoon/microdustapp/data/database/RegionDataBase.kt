package com.hoon.microdustapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [RegionEntity::class], version = 1)
abstract class RegionDataBase : RoomDatabase(){
    abstract fun regionDao(): RegionDao

    // google room db 페이지에서 싱글톤 생성을 권장
    companion object {
        private var instance: RegionDataBase? = null

        @Synchronized
        fun getInstance(context: Context): RegionDataBase {
            if (instance == null) {
                synchronized(RegionDataBase::class){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        RegionDataBase::class.java,
                        "user_database"
                    ).build()
                }
            }
            return instance!!
        }
    }
}