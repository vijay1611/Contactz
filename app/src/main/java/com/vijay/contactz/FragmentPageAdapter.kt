package com.vijay.contactz

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.vijay.contactz.localDataFragment.Contact
import com.vijay.contactz.localDataFragment.LocalContactsFragment
import com.vijay.contactz.remoteDataFragment.RemoteContactsFragment

class FragmentPageAdapter(fa: FragmentActivity, private var fragmentList:ArrayList<Fragment>
): FragmentStateAdapter(fa) {
    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }


fun changeContacts(){
    (fragmentList[0] as LocalContactsFragment).getAllContacts()
}
}