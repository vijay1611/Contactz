package com.vijay.contactz.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ContactDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contactModel: List<ContactModel>)

    @Query("select * from contacts_table where isLocal= :isLocalData")
    fun getContacts(isLocalData:Boolean):List<ContactModel>

    @Update
    fun updateContact(contactModel: ContactModel)
}