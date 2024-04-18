package com.d4rk.cleaner.ui.whitelist
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.d4rk.cleaner.databinding.ActivityWhitelistBinding

class WhitelistActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWhitelistBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWhitelistBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}