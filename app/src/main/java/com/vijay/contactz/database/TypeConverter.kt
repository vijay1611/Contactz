package com.vijay.contactz.database

import androidx.room.TypeConverter

class TypeConverter {

        @TypeConverter
        fun fromString(value: String): List<String> {
            return value.split(",")
        }

        @TypeConverter
        fun listToString(list: List<String>): String {
            return list.joinToString(",")
        }
    }

