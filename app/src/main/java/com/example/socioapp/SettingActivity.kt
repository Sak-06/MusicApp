package com.example.socioapp

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.socioapp.databinding.ActivitySettingBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.internal.GsonBuildConfig

class SettingActivity : AppCompatActivity() {
    lateinit var binding:ActivitySettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currNavTheme[MainActivity.themeId])
        binding=ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title="Settings"
        when(MainActivity.themeId){
            0->{binding.lavendarthemebtn.setBackgroundColor(Color.CYAN)}
            1->{binding.pinkthemebtn.setBackgroundColor(Color.CYAN)}
            2->{binding.bluethemebtn.setBackgroundColor(Color.CYAN)}
            3->{binding.greenthemebtn.setBackgroundColor(Color.CYAN)}
            4->{binding.blackthemebtn.setBackgroundColor(Color.CYAN)}
        }
        binding.lavendarthemebtn.setOnClickListener { saveTheme(0) }
        binding.pinkthemebtn.setOnClickListener { saveTheme(1) }
        binding.bluethemebtn.setOnClickListener { saveTheme(2) }
        binding.blackthemebtn.setOnClickListener { saveTheme(4) }
        binding.greenthemebtn.setOnClickListener { saveTheme(3) }

        binding.version.text=versionDetails()
        binding.sortbtn.setOnClickListener {
            val menuList= arrayOf("Recently Added","Song Title","File Size")
            var currSortOrder=MainActivity.sortOrder
            val builder= MaterialAlertDialogBuilder(this)
            builder.setTitle("SORT BY")
                .setPositiveButton("YES"){_,_ ->
                    val editor = getSharedPreferences("SORTING", MODE_PRIVATE).edit()
                    editor.putInt("sortOrder",currSortOrder)
                    editor.apply()

                }.setSingleChoiceItems(menuList,currSortOrder){_,which ->
                    currSortOrder=which
                }
            val customdialogue = builder.create()
            customdialogue.show()
            customdialogue.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE)

        }
    }
    private fun saveTheme(index:Int){
        if(MainActivity.themeId!=index){
            val editor = getSharedPreferences("THEMES", MODE_PRIVATE).edit()
            editor.putInt("ThemeIndex",index)
            editor.apply()
            val builder= MaterialAlertDialogBuilder(this)
            builder.setTitle("THEME")
                .setMessage("Want to apply this Theme?")
                .setPositiveButton("YES"){_,_ ->

                    exitActivity()

                }
                .setNegativeButton("NO") { dialogue, _ ->
                    dialogue.dismiss()
                }
            val customdialogue = builder.create()
            customdialogue.show()
            customdialogue.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE)
            customdialogue.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLUE)


        }
    }
    private fun versionDetails():String{
        return "Version Name: ${GsonBuildConfig.VERSION}"

    }
}