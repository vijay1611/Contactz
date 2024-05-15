package com.vijay.contactz.localDataFragment

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class Contact(
    val displayName: String?,
    val phoneNumbers: List<Number?>?,
    val picture:String? = ""
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.createTypedArrayList(Number.CREATOR),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(displayName)
        parcel.writeTypedList(phoneNumbers)
        parcel.writeString(picture)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Contact> {
        override fun createFromParcel(parcel: Parcel): Contact {
            return Contact(parcel)
        }

        override fun newArray(size: Int): Array<Contact?> {
            return arrayOfNulls(size)
        }
    }
}

data class Number(
    val no: String?
):Parcelable {
    constructor(parcel: Parcel) : this(parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(no)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Number> {
        override fun createFromParcel(parcel: Parcel): Number {
            return Number(parcel)
        }

        override fun newArray(size: Int): Array<Number?> {
            return arrayOfNulls(size)
        }
    }
}
