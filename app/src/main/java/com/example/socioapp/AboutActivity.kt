package com.example.socioapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.socioapp.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {
    lateinit var binding:ActivityAboutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currNavTheme[MainActivity.themeId])
        binding=ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title="About Us"
        binding.abouttext.text=abouttext()

    }

    private fun abouttext(): String {
        return "Developed by: Sakshi Gangwar" +
                "\n \n Please provide feedback " +
                "\n \n Thank you! "
    }
}