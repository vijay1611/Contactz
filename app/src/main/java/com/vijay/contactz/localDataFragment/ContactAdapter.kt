package com.vijay.contactz.localDataFragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vijay.contactz.databinding.ContactLlistItemBinding


class ContactAdapter(private var context: Context,private var contacts:List<Contact>) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    fun filterContacts(query: String) {
        contacts = contacts.filter { contact ->
            contact.displayName!!.contains(query, ignoreCase = true) //||
//                    contact.phoneNumbers?.get(0)?.no!!.contains(query)
        }
        notifyDataSetChanged()
        
    }

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

        (if(contact.phoneNumbers!!.isNotEmpty()) contact.phoneNumbers[0] else "")?.let {
            holder.bind(contact.displayName ?: "",
                it
            )
        }
        holder.calloption.setOnClickListener {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:${contact.phoneNumbers[0]?.no.toString()}")
            context.startActivity(intent)
        }
        holder.calloption.setOnLongClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${contact.phoneNumbers[0]?.no.toString()}")
            context.startActivity(intent)
            true
        }


    }
    fun setData(contacts: List<Contact>){
       this.contacts=contacts
        notifyDataSetChanged()
    }

    class ContactViewHolder(private val binding:ContactLlistItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(name: String, number: Any) {
            binding.name.text = name
            binding.number.text = number.toString()
        }

        var calloption = binding.callIcon




    }
}