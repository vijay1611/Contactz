package com.vijay.contactz.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
@Database(entities = [ContactModel::class], version = 1)
abstract class ContactDatabase:RoomDatabase() {

    abstract fun contactDao(): ContactDao

    companion object {
        private var instance: ContactDatabase? = null

        @Synchronized
        fun getInstance(ctx: Context): ContactDatabase {
            if (instance == null)
                instance = Room.databaseBuilder(
                    ctx.applicationContext, ContactDatabase::class.java,
                    "contact_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

            return instance!!

        }


    }
}