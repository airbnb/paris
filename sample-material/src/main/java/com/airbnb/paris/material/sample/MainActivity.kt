package com.airbnb.paris.material.sample

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.airbnb.paris.extensions.*
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<MaterialButton>(R.id.parisButton)

        button.style {
            text("Kotlin")
            textColor(Color.BLACK)
            /*layoutHeightDp(200)
            layoutWidthDp(300)*/
            textAppearanceRes(R.style.TextAppearance_MaterialComponents_Headline1)
        }

        //button.text = "Kotlin"
    }
}
