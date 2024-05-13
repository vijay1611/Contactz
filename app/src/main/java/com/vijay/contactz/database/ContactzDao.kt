package com.vijay.contactz.database

import androidx.room.Dao
import androidx.room.Query
import com.vijay.contactz.remoteDataFragment.RemoteContactModel


@Dao
interface ContactzDao {
    @Query("Select * from remote_contactz")
    suspend fun getAllContactzUsers(): List<RemoteContactModel>

}
