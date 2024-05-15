package com.vijay.contactz

import android.content.ContentProviderOperation
import android.content.ContentResolver
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.vijay.contactz.database.ContactDatabase
import com.vijay.contactz.database.ContactModel
import com.vijay.contactz.databinding.ActivityProfileDetailsBinding
import com.vijay.contactz.localDataFragment.Contact
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileDetailsActivity : AppCompatActivity() {
    lateinit var binding: ActivityProfileDetailsBinding
    lateinit var database : ContactDatabase
    var oldData:ContactModel?=null
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = ContactDatabase.getInstance(this)

       val data= intent.getStringExtra("contactData")
        var newData = Gson().fromJson(data,ContactModel::class.java)
        oldData=newData
        val requestOptions = RequestOptions().transform(RoundedCorners(100))
       if(newData.isLocal){
           if(newData?.picture==null){
              binding.imagePd.setImageResource(R.drawable.profile_ic)
           }else{
               Glide.with(binding.imagePd)
                   .load(newData?.picture)
                   .apply(requestOptions)
                   .into(binding.imagePd)
           }

           binding.mailPd.visibility = View.GONE
       }else{
           Glide.with(binding.imagePd)
               .load(newData?.pictureLarge)
               .apply(requestOptions)
               .into(binding.imagePd)
       }

        binding.namePd.setText(newData?.name)
        binding.numberPd.setText(newData?.phone)
        binding.mailPd.setText(newData?.email)

        binding.namePd.setOnClickListener{
            binding.modifyBtn.visibility = View.VISIBLE
            binding.cancelBtn.visibility = View.VISIBLE
        }
        binding.numberPd.setOnClickListener{
            binding.modifyBtn.visibility = View.VISIBLE
            binding.cancelBtn.visibility = View.VISIBLE
        }

        binding.modifyBtn.setOnClickListener {
           val name = binding.namePd.text.toString().trim()
            val number = binding.numberPd.text.toString().trim()
            if(oldData?.isLocal == true){
                modifyContact(name,number)
            }else {
                val con = ContactModel(name = name, phone = number)
                CoroutineScope(Dispatchers.IO).launch {
                    database.contactDao().updateContact(con)
                }.invokeOnCompletion {
                    runOnUiThread {
                        Toast.makeText(this, "Contact modified", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        binding.cancelBtn.setOnClickListener {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
            finish()
        }


    }

    private fun modifyContact( newName: String, newPhoneNumber: String) {
        val contentResolver: ContentResolver = contentResolver

        // Get the contact ID of the contact
        val contactUri = ContactsContract.Contacts.CONTENT_URI
        val projection = arrayOf(ContactsContract.Contacts._ID)
        val selection = "${ContactsContract.Contacts.DISPLAY_NAME} = ? "
        val selectionArgs = arrayOf(oldData?.name)

        val cursor = contentResolver.query(contactUri, projection, selection, selectionArgs, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val contactId = it.getLong(it.getColumnIndexOrThrow(ContactsContract.Contacts._ID))

                val operations = ArrayList<ContentProviderOperation>()

                // Update the display name
                val whereName = "${ContactsContract.Data.CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?"
                val nameWhereArgs = arrayOf(contactId.toString(), ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                operations.add(
                    ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(whereName, nameWhereArgs)
                        .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, newName)
                        .build()
                )

                // Update the phone number
                val wherePhone = "${ContactsContract.Data.CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?"
                val phoneWhereArgs = arrayOf(contactId.toString(), ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                operations.add(
                    ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(wherePhone, phoneWhereArgs)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, newPhoneNumber)
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                        .build()
                )

                // Apply the operations
                try {
                    contentResolver.applyBatch(ContactsContract.AUTHORITY, operations)
                    Toast.makeText(this, "Contact updated successfully", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Failed to update contact", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Contact not found", Toast.LENGTH_SHORT).show()
            }
        }
    }

}