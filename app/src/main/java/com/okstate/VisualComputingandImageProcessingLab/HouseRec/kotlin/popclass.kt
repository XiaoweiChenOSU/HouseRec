package com.okstate.VisualComputingandImageProcessingLab.HouseRec.kotlin

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Pop : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.popupwindow)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        var width = displayMetrics.widthPixels
        var height = displayMetrics.heightPixels
        window.setLayout(900, 500)
        val intentclose = Intent(this, com.example.petfeederapphome.AppScreen::class.java)
        val message = findViewById<TextView>(R.id.textView)
        val codearea = findViewById<EditText>(R.id.CodeArea)
        val okbutton = findViewById<Button>(R.id.okbutton)
        message.visibility = View.GONE
        okbutton.setOnClickListener {
            val code = codearea.text.toString()
            if(!code.equals("")){
                    for (i in DataBase.codes.indices)
                    {
                        if (code.equals(DataBase.codes[i])){
                        val intentok = Intent(this, PetFeederControl::class.java)
                        startActivity(intentok)
                            message.visibility = View.GONE
                    }
                    else {
                        message.visibility = View.VISIBLE
                        message.setText("Wrong Password")


                    }}
                }



            }

        }



    }


















