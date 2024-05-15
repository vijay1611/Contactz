package com.vijay.contactz.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.RoomDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vijay.contactz.Constant
import com.vijay.contactz.MainActivity
import com.vijay.contactz.ProfileDetailsActivity
import com.vijay.contactz.database.ContactDatabase
import com.vijay.contactz.database.ContactModel
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
   lateinit var database : ContactDatabase
   lateinit var sharedPreferences: SharedPreferences
    var allContactList = listOf<ContactModel>()
   var inserted =""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentContactBinding.inflate(layoutInflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database =ContactDatabase.getInstance(requireContext())
        isFromRandom = arguments?.getBoolean(Constant.ARG_OBJECT) ?: false
         sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        val value1 = sharedPreferences.getString(inserted, null)
        if(value1=="saved"){
           CoroutineScope(Dispatchers.IO).launch {
               var localDataFromRoom = database.contactDao().getContacts(!isFromRandom)
               allContactList=localDataFromRoom
               withContext(Dispatchers.Main) {
                   Log.e("11*", localDataFromRoom.toString())
                   adapter.setData(localDataFromRoom)
//                   binding.rvMain.layoutManager = LinearLayoutManager(requireContext())
                   binding.rvMain.adapter = adapter
                   binding.progress.visibility = View.GONE
                   binding.rvMain.visibility=View.VISIBLE
               }
           }

        }else{

        }
        if (isFromRandom) callApi() else permissionCheck()
        binding.btnSetting.setOnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", "com.vijay.contactz", null)
            }
            openSettingsLauncher.launch(intent)
        }

    }

    private fun permissionCheck() {
        when {
            context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.READ_CONTACTS
                )
            } == PackageManager.PERMISSION_GRANTED || context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.CALL_PHONE
                )
            } == PackageManager.PERMISSION_GRANTED -> {
                binding.progress.visibility=View.VISIBLE
                binding.linSettings.visibility=View.GONE
                getContacts()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)||shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CONTACTS)||shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE) -> {
                binding.progress.visibility=View.GONE
                binding.linSettings.visibility=View.VISIBLE
            }
            else -> {
                // No explanation needed, we can request the permission.
                requestMultiplePermissionsLauncher.launch(
                    arrayOf(Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_CONTACTS, Manifest.permission.CALL_PHONE)
                )
            }
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
                    val contactList: ArrayList<ContactModel> = arrayListOf()
                    response.body()?.results?.forEach{ result ->
                        val name=result.name?.let { it.title+"."+it.first+" "+it.last }
                        contactList.add(ContactModel(name =name, phone = result.phone.toString(), picture = result.picture?.medium.toString(), email = result.email, pictureLarge = result.picture?.large, isLocal = false))
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        database.contactDao().insertContact(contactList)
                    }
                    allContactList =contactList
                    adapter.setData(contactList)
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
    private val requestMultiplePermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        var isPermissionGranted=false
        Log.e("****",permissions.entries.toString())
        permissions.entries.forEach {
            val permissionName = it.key
            val isGranted = it.value
            isPermissionGranted = isGranted
            if(!it.value){
                return@forEach
            }
        }
        if (isPermissionGranted){
            Log.e("****granted",permissions.entries.toString())
            getContacts()
        }else{
            Log.e("****denied",permissions.entries.toString())
            binding.progress.visibility=View.GONE
            binding.linSettings.visibility=View.VISIBLE
        }
    }
    private val openSettingsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        // This is called when the user returns from the settings activity
       permissionCheck()
    }


    @SuppressLint("Range", "SuspiciousIndentation")
    fun getContacts() {
        val contacts = mutableListOf<ContactModel>()
        val contentResolver: ContentResolver = requireContext().contentResolver
        var cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        CoroutineScope(Dispatchers.IO).launch {

            cursor?.use { cursor ->

                while (cursor.moveToNext()) {
                    val contactId =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                    val displayName =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    var displayImage =
                        cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_URI))

                    val phoneCursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(contactId),
                        null
                    )

                    phoneCursor?.use { phoneCursor ->
                        while (phoneCursor.moveToNext()) {
                            val phoneNumber =
                                phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            val contact = ContactModel(name=displayName, phone=phoneNumber, picture = displayImage,isLocal = true)
                            contacts.add(contact)

                        }
                    }
                    phoneCursor?.close()
                }
                database.contactDao().insertContact(contacts)

            val editor = sharedPreferences.edit()
            editor.putString(inserted,"saved")
            editor.apply()


            }

            cursor?.close()


            withContext(Dispatchers.Main) {
                allContactList=contacts
                adapter.setData(contacts)

                binding.rvMain.adapter = adapter
                binding.progress.visibility = View.GONE
                binding.rvMain.visibility=View.VISIBLE
            }
        }


    }

    override fun profileClick(contact: ContactModel) {
        var intent = Intent(requireContext(),ProfileDetailsActivity::class.java)
        intent.putExtra("contactData",Gson().toJson(contact))
        startActivity(intent)

    }
    fun search(str:String){
        if (str.isNotEmpty()) {
            var filterContacts = allContactList.filter { cm ->
                cm.name?.startsWith(str, true) == true
            }
            adapter.setData(filterContacts)
        }else{
            adapter.setData(allContactList)
        }

    }
}