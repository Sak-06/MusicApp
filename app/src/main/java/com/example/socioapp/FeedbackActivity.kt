package com.example.socioapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.socioapp.databinding.ActivityFeedbackBinding

class FeedbackActivity : AppCompatActivity() {
    lateinit var binding:ActivityFeedbackBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currNavTheme[MainActivity.themeId])
        binding=ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title="Feedback"
        binding.submit.setOnClickListener {
            val feedmsg=binding.feedback.text.toString() +"\n" + binding.email.text.toString()+"\n"+
            binding.subject.text.toString()
            Toast.makeText(this,feedmsg.toString(),Toast.LENGTH_SHORT).show()

        }


    }
}