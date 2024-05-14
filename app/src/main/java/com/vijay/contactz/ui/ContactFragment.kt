package com.vijay.contactz.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vijay.contactz.Constant
import com.vijay.contactz.MainActivity
import com.vijay.contactz.ProfileDetailsActivity
import com.vijay.contactz.databinding.FragmentContactBinding
import com.vijay.contactz.localDataFragment.Contact
import com.vijay.contactz.localDataFragment.ContactAdapter
import com.vijay.contactz.localDataFragment.Number
import com.vijay.contactz.localDataFragment.profileListener
import com.vijay.contactz.network.ApiResponse
import com.vijay.contactz.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Callback


class ContactFragment : Fragment(),profileListener {
    private lateinit var binding: FragmentContactBinding
    var adapter: ContactAdapter = ContactAdapter(listOf(),this)
    private val key1 = "key1"
    private var isFromRandom = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentContactBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isFromRandom = arguments?.getBoolean(Constant.ARG_OBJECT) ?: false
        if (isFromRandom) callApi() else permissionCheck()
    }

    private fun permissionCheck() {
        if (context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.READ_CONTACTS
                )
            }
            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CALL_PHONE
            )
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity as MainActivity,
                arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE),
                MainActivity.CONTACTS_PERMISSION_REQUEST_CODE
            )

        } else {
            getContacts()
        }
    }




    private fun callApi() {
        binding.progress.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.apiService.getPosts()
            call.enqueue(object : Callback<ApiResponse> {
                override fun onResponse(
                    call: retrofit2.Call<ApiResponse>,
                    response: retrofit2.Response<ApiResponse>
                ) {
                    Log.e("****", response.body().toString())
                    val contactList: ArrayList<Contact> = arrayListOf()
                    response.body()?.results?.forEach{ result ->
                        val name=result.name?.let { it.title+"."+it.first+" "+it.last }
                        contactList.add(Contact(name, listOf( Number(result.phone)),result.picture?.medium.toString()))
                    }
                    adapter.setData(contactList)
                    binding.rvMain.layoutManager = LinearLayoutManager(requireContext())
                    binding.rvMain.adapter = adapter
                    binding.progress.visibility = View.GONE
                    binding.rvMain.visibility=View.VISIBLE

                }

                override fun onFailure(call: retrofit2.Call<ApiResponse>, t: Throwable) {
                    Log.e("****", "ApiFailed")
                }
            })
        }

    }

    /* private fun getAllContacts(){
         binding.progress.visibility=View.VISIBLE
         CoroutineScope(Dispatchers.Main).launch {
             val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
             val value1 = sharedPreferences.getString(key1, null)
             //val value2 = sharedPreferences.getString(key2, null)
             // return Pair(value1, value2)

             Log.e("shared***", value1.toString())
             val gson = Gson()
             val itemType = object : TypeToken<List<Contact>>() {}.type
             val data1:List<Contact> = if(value1!=null){
                 gson.fromJson(value1, itemType)
             }else {
                 listOf<Contact>()
             }
             adapter.setData(data1)
         }.invokeOnCompletion {

             binding.rvMain.layoutManager = LinearLayoutManager(requireContext())
             binding.rvMain.adapter = adapter
             binding.progress.visibility=View.GONE
         }

     }*/

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MainActivity.CONTACTS_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission was granted, start the camera
                    getContacts()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Permission denied to access the camera",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    @SuppressLint("Range", "SuspiciousIndentation")
    fun getContacts() {
        val contacts = mutableListOf<Contact>()
        val contentResolver: ContentResolver = requireContext().contentResolver
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
                    var displaImage =  cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_URI))

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
                    if (displayName != null && phoneNumbers != null) {
                        val contact = Contact(displayName, phoneNumbers,displayName)
                        contacts.add(contact)
                    }


                }

            }
            val sharedPreferences =
                requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString(key1, Gson().toJson(contacts))
            editor.apply()
            Log.e("cp*", Gson().toJson(contacts))
            cursor?.close()

            withContext(Dispatchers.Main) {
                adapter.setData(contacts)
                binding.rvMain.layoutManager = LinearLayoutManager(requireContext())
                binding.rvMain.adapter = adapter
                binding.progress.visibility = View.GONE
                binding.rvMain.visibility=View.VISIBLE
            }
        }


    }

    override fun profileClick(contact: Contact) {
        var intent = Intent(requireContext(),ProfileDetailsActivity::class.java)
        intent.putExtra("contactData",contact)
        startActivity(intent)

    }
}