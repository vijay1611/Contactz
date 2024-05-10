package com.vijay.contactz

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.vijay.contactz.databinding.ActivityMainBinding
import com.vijay.contactz.databinding.FragmentLocalContactsBinding
import com.vijay.contactz.localDataFragment.Contact
import com.vijay.contactz.localDataFragment.Number
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var adapter: FragmentPageAdapter

    companion object {
        var CONTACTS_PERMISSION_REQUEST_CODE = 101
    }
    val key1="key1"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = FragmentPageAdapter(supportFragmentManager,lifecycle)

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Phone"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Remote"))

        binding.viewPager.adapter = adapter

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
               if(tab != null){
                   binding.viewPager.currentItem =tab.position
               }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position))
            }
        })
        permissionCheck()



    }

    private fun permissionCheck() {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED  ) {
                // Permission is not granted
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE),
                    CONTACTS_PERMISSION_REQUEST_CODE
                )
                // setadapter()
                getContacts()
               // binding.btnSubmit.visibility = View.GONE

            } else {
                // Permission has already been granted
                // You can proceed with accessing contacts here
//       setadapter()
              //  binding.btnSubmit.visibility = View.GONE
                getContacts()
            }

    }
    @SuppressLint("Range", "SuspiciousIndentation")
    fun getContacts() {
        val contacts = mutableListOf<Contact>()

        // Query the contacts content provider
        val contentResolver: ContentResolver = this.contentResolver
        var cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        CoroutineScope(Dispatchers.IO).launch {

          //  binding.progress.visibility = View.VISIBLE

            cursor?.use { cursor ->

                while (cursor.moveToNext()) {
                    val contactId =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                    val displayName =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))

                    // Retrieve phone numbers for the contact
                    val phoneCursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(contactId),
                        null
                    )

                    val phoneNumbers = mutableListOf<Number>()
                    phoneCursor?.use { phoneCursor ->
                        while (phoneCursor.moveToNext()) {
                            val phoneNumber =
                                phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            phoneNumbers.add(Number(phoneNumber))
                        }
                        }

                    phoneCursor?.close()
                    if(displayName!=null && phoneNumbers!=null){
                        val contact = Contact(displayName, phoneNumbers)
                        contacts.add(contact)
                    }

                    // value1 = displayName
                    // value2 = phoneNumbers[0]



                }

            }
            val sharedPreferences =applicationContext.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString(key1, Gson().toJson(contacts))
            editor.apply()
            Log.e("cp*", Gson().toJson(contacts))
            cursor?.close()

        }.invokeOnCompletion {
            runOnUiThread{
               // binding.progress.visibility = View.GONE
               // adapter.setData(contacts)
            }

        }

    }

}