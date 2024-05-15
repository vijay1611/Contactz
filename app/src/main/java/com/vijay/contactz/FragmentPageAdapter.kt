package com.vijay.contactz

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.vijay.contactz.database.ContactModel
import com.vijay.contactz.localDataFragment.Contact
import com.vijay.contactz.ui.ContactFragment

class FragmentPageAdapter(fa: FragmentActivity, private var fragmentList:ArrayList<ContactFragment>
): FragmentStateAdapter(fa) {

    var contacts:List<Contact> = listOf()
    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }



    fun setLocalData(contact: List<ContactModel>){
        fragmentList[0].adapter.setData(contact)
    }
    fun filterLocalData(query:String){
        fun filterContacts(query: String) {
            contacts = contacts.filter { contact ->
                contact.displayName!!.contains(query, ignoreCase = true) //||
//                    contact.phoneNumbers?.get(0)?.no!!.contains(query)
            }
            notifyDataSetChanged()

        }

    }
}