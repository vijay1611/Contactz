package com.vijay.contactz.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts_table")
data class ContactModel(

    val name:String?="",
    @PrimaryKey
    val phone:String,
    val email:String?="",
    val picture: String?="",
    val pictureLarge:String?="",
    val isLocal: Boolean=false
)
