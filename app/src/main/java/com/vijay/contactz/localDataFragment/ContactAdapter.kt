package com.vijay.contactz.localDataFragment

import android.content.Context
import android.content.Intent
import android.media.Image
import android.net.Uri
import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.vijay.contactz.R
import com.vijay.contactz.database.ContactModel
import com.vijay.contactz.databinding.ContactLlistItemBinding
import com.vijay.contactz.ui.ContactFragment
import java.util.regex.Pattern


class ContactAdapter(private var contacts:List<ContactModel>,val profileListener: profileListener) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

//    fun filterContacts(query: String) {
//        contacts = contacts.filter { contact ->
//            contact.displayName!!.contains(query, ignoreCase = true) //||
////                    contact.phoneNumbers?.get(0)?.no!!.contains(query)
//        }
//        notifyDataSetChanged()
//
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ContactLlistItemBinding.inflate(inflater, parent, false)
        return ContactViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]

//        (if(contact.phone!!.isNotEmpty()) contact.phone[0]?.no else "")?.let {
//            holder.bind(contact.name ?: "",
//                it
//            )
//        }
       holder.name.text = contact.name
       holder.number.text = contact.phone

        val requestOptions = RequestOptions().transform(RoundedCorners(50))
       if(contact.picture!=null){
           Glide.with(holder.image)
               .load(contact.picture)
               .apply(requestOptions)
               .into(holder.image)
       }else{
           holder.image.setImageResource(R.drawable.profile_ic)
       }

        holder.calloption.setOnClickListener {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:${contact.phone}")
            holder.itemView.context.startActivity(intent)
        }
        holder.fullLayout.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${contact.phone}")
            holder.itemView.context.startActivity(intent)

        }
        holder.itemView.setOnClickListener{
            profileListener.profileClick(contact)
        }


    }
    fun setData(contacts: List<ContactModel>){
       this.contacts=contacts
        notifyDataSetChanged()
    }


    class ContactViewHolder(private val binding:ContactLlistItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(name: String, number: Any) {

        }
       var name =  binding.name
       var number = binding.number

        var calloption = binding.callIcon
        var fullLayout = binding.adapterLayout
         var image =  binding.imageView



    }

}
interface profileListener{
    fun profileClick(contact: ContactModel)
}