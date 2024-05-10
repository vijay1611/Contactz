package com.vijay.contactz.localDataFragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vijay.contactz.databinding.FragmentLocalContactsBinding


class LocalContactsFragment : Fragment() {
    private var _binding:FragmentLocalContactsBinding? = null
    private val binding get() = _binding!!
    var adapter: ContactAdapter= ContactAdapter(requireContext(),listOf())
    val key1="key1"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
      //  return inflater.inflate(R.layout.fragment_local_contacts, container, false)
        _binding = FragmentLocalContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)





    }

    fun getAllContacts(){
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
        binding.rvMain.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMain.adapter = adapter
    }




}