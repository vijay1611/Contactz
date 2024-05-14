package com.vijay.contactz

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.vijay.contactz.databinding.ActivityProfileDetailsBinding
import com.vijay.contactz.localDataFragment.Contact

class ProfileDetailsActivity : AppCompatActivity() {
    lateinit var binding: ActivityProfileDetailsBinding
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

       val data:Contact?= intent.getParcelableExtra("contactData",Contact::class.java)
        Glide.with(binding.imagePd)
            .load(data?.picture)
            .centerCrop()
            .into(binding.imagePd)

        binding.namePd.setText(data?.displayName.toString())
        binding.numberPd.setText(data?.phoneNumbers?.get(0)?.no.toString())

    }
}