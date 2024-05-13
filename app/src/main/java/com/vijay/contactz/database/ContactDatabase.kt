package com.vijay.contactz.database

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vijay.contactz.remoteDataFragment.RemoteContactModel

@Database(entities = [RemoteContactModel::class], version = 1, exportSchema = false)
@TypeConverters(TypeConverter::class)
abstract class ContactDatabase:RoomDatabase(){

    abstract fun contactzDao() : ContactzDao
    companion object {
        private val LOCK = Any()
        val DB_NAME = "Contactz.db"

        @Volatile
        public var dbInstance: ContactDatabase? = null

        fun getInstance(application: Application):ContactDatabase{
            synchronized(LOCK) {
                if (dbInstance == null) {
                    dbInstance = Room.databaseBuilder(application, ContactDatabase::class.java, DB_NAME)
                        .allowMainThreadQueries()
                        .build()
                }
            }
            return dbInstance!!
        }
    }
}