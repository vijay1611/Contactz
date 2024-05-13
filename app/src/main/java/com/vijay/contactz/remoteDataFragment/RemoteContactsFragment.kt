package com.vijay.contactz.remoteDataFragment

import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.vijay.contactz.R
import com.vijay.contactz.database.ContactDatabase


class RemoteContactsFragment : Fragment() {

    private lateinit var vm: RemoteDataViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val vm = ViewModelProvider(this)[RemoteDataViewModel::class.java]
        return inflater.inflate(R.layout.fragment_remote_contacts, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = ContactDatabase.getInstance(Application())


        vm.getData()
//        vm.data.observe(this) { it ->
//            Log.e("****",it.toString())
//            vm.saveData(db)
//        }
        vm.data1.observe(viewLifecycleOwner) { it ->
            Log.e("****",it.toString())
            if (it.isEmpty()){
                vm.getDataFromRoom(db)
            }else{
               // adapter.setvalue(it)
                Log.e("123*", it.toString())
            }

        }
    }


}