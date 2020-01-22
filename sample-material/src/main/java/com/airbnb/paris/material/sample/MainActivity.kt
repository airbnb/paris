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

        /*
        * After setting style from the resources file
        * I can't add text directly to the button
        * However I can add text using the style builder
        * method
        * I don't know why this happen
        * */

        /*
        * Setting style using the style resource does not work correctly
        * I can see the value in the corresponding methods but just
        * don't work
        * */

        button.style(R.style.Widget_MaterialComponents_Button_OutlinedButton)

        //button.style { addRed() }

        /*button.style {
            text("Alu potol")

            backgroundTintRes(R.color.white)
            backgroundTintMode(9)
            cornerRadius(50)
            elevationDp(0)
            iconRes(R.drawable.app)
            iconSizeDp(14)
            iconGravity(MaterialButton.ICON_GRAVITY_END)
            iconPaddingDp(23)
            iconTintRes(R.color.colorPrimaryDark)
            iconTintMode(9)
            rippleColorRes(R.color.colorPrimaryDark)
            strokeColorRes(R.color.colorPrimaryDark)
            strokeWidthDp(2)
            //textColor(Color.BLACK)
            //textAppearanceRes(R.style.TextAppearance_MaterialComponents_Headline1)
        }*/


        //button.text = "Kotlin"
    }
}
