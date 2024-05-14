package com.vijay.contactz

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.vijay.contactz.databinding.ActivityMainBinding
import com.vijay.contactz.localDataFragment.Contact
import com.vijay.contactz.localDataFragment.Number
import com.vijay.contactz.ui.ContactFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : FragmentActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var adapter: FragmentPageAdapter
    var contactData : List<Contact> = listOf()

    companion object {
        var CONTACTS_PERMISSION_REQUEST_CODE = 101
    }
    val key1="key1"
    private var fragmentList = arrayListOf<ContactFragment>()
    private var listStr = arrayListOf("Phone", "Remote")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fragment1 = ContactFragment()
        fragment1.arguments = Bundle().apply {
            putBoolean(Constant.ARG_OBJECT, false)
        }
       // val fragment2 = RemoteContactsFragment()
        val fragment2=ContactFragment()
        fragment2.arguments = Bundle().apply {
            putBoolean(Constant.ARG_OBJECT, true)
        }
        fragmentList.add(fragment1)
        fragmentList.add(fragment2)
        adapter = FragmentPageAdapter(this, fragmentList)
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = listStr[position]
        }.attach()


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
        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isEmpty()) {
                    adapter.setLocalData(contactData)
                } else {
                    // Filter contacts based on the newText
                    adapter.filterLocalData(newText)
                    adapter.notifyDataSetChanged()
                }
                return true
            }
        })

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position))
            }
        })

    }


}