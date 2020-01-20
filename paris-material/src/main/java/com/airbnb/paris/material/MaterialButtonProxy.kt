package com.airbnb.paris.material

import android.content.res.ColorStateList
import android.util.TypedValue
import androidx.annotation.StyleRes
import com.airbnb.paris.annotations.AfterStyle
import com.airbnb.paris.annotations.Attr
import com.airbnb.paris.annotations.Styleable
import com.airbnb.paris.proxies.BaseProxy
import com.airbnb.paris.styles.Style
import com.google.android.material.button.MaterialButton

@Styleable("Paris_MaterialButton")
class MaterialButtonProxy(view: MaterialButton) : BaseProxy<MaterialButtonProxy, MaterialButton>(view) {

    private var mColors: ColorStateList? = null
    private val defaultTextAppearance:Int = 16974317 //todo this might not be the default for every android version or phone
    private var mTextAppearance = defaultTextAppearance


    @Attr(R2.styleable.Paris_MaterialButton_android_textColor)
    fun setTextColor(colors: ColorStateList?) {
        this.mColors = colors
    }

    @Attr(R2.styleable.Paris_MaterialButton_android_textAppearance)
    fun setTextAppearance(@StyleRes textAppearance: Int) {
        mTextAppearance = textAppearance
    }


    @AfterStyle
    fun afterStyle(@Suppress("UNUSED_PARAMETER") style: Style?) {



        if(mTextAppearance == defaultTextAppearance){
            view.setTextAppearance(view.context, R.style.TextAppearance_MaterialComponents_Button)
        } else{
            view.setTextAppearance(view.context, mTextAppearance)
        }

        if(mColors == null){
            val typedValue = TypedValue()
            view.context.theme.resolveAttribute(R.attr.colorOnPrimary, typedValue, true)
            view.setTextColor(typedValue.data)
        } else{
            view.setTextColor(mColors)
        }

    }
}