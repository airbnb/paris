package com.airbnb.paris.material.proxies

import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.util.Log
import android.util.TypedValue
import androidx.annotation.Px
import androidx.annotation.StyleRes
import com.airbnb.paris.annotations.AfterStyle
import com.airbnb.paris.annotations.Attr
import com.airbnb.paris.annotations.Styleable
import com.airbnb.paris.material.R
import com.airbnb.paris.material.R2
import com.airbnb.paris.material.utils.ViewUtils
import com.airbnb.paris.proxies.BaseProxy
import com.airbnb.paris.styles.Style
import com.google.android.material.button.MaterialButton

@Styleable("Paris_MaterialButton")
class MaterialButtonProxy(view: MaterialButton) : BaseProxy<MaterialButtonProxy, MaterialButton>(view) {

    private var mColors: ColorStateList? = null
    private val defaultTextAppearance:Int = 16974317 //todo this might not be the default for every android version or phone
    private var mTextAppearance = defaultTextAppearance

    companion object{

        @com.airbnb.paris.annotations.Style
        val RED_STYLE = R.style.Widget_MaterialComponents_Button_OutlinedButton
    }

    @Attr(R2.styleable.Paris_MaterialButton_android_textColor)
    fun setTextColor(colors: ColorStateList?) {
        this.mColors = colors
    }

    @Attr(R2.styleable.Paris_MaterialButton_android_textAppearance)
    fun setTextAppearance(@StyleRes textAppearance: Int) {
        mTextAppearance = textAppearance
    }


    @Attr(R2.styleable.Paris_MaterialButton_backgroundTint)
    fun setBackgroundTintList(tintList:ColorStateList?){
        view.backgroundTintList = tintList

        Log.d("MaterialProxy", "tintList: $tintList")

    }

    @Attr(R2.styleable.Paris_MaterialButton_backgroundTintMode)
    fun setBackgroundTintMode(mode: Int){
        val tintMode = ViewUtils.parseTintMode(mode, PorterDuff.Mode.SRC_IN)
        view.backgroundTintMode = tintMode
    }

    @Attr(R2.styleable.Paris_MaterialButton_cornerRadius)
    fun setCornerRadius(@Px cornerRadius: Int) {
        view.cornerRadius = cornerRadius
    }

    @Attr(R2.styleable.Paris_MaterialButton_elevation)
    fun setElevation(@Px elevation: Int){
        view.elevation = elevation.toFloat()
    }

    @Attr(R2.styleable.Paris_MaterialButton_icon)
    fun setIcon(icon: Drawable){
        view.icon = icon
    }

    @Attr(R2.styleable.Paris_MaterialButton_iconSize)
    fun setIconSize(@Px size: Int){
        view.iconSize = size
    }

    @Attr(R2.styleable.Paris_MaterialButton_iconGravity)
    fun setIconGravity(gravity: Int){
        view.iconGravity = gravity
    }

    @Attr(R2.styleable.Paris_MaterialButton_iconPadding)
    fun setIconPadding(@Px padding: Int){
        view.iconPadding = padding
    }

    @Attr(R2.styleable.Paris_MaterialButton_iconTint)
    fun setIconTintList(tintList:ColorStateList?){
        view.iconTint = tintList
    }

    @Attr(R2.styleable.Paris_MaterialButton_iconTintMode)
    fun setIconTintModeMode(mode: Int){
        val tintMode = ViewUtils.parseTintMode(mode, PorterDuff.Mode.SRC_IN)
        view.iconTintMode = tintMode
    }

    @Attr(R2.styleable.Paris_MaterialButton_rippleColor)
    fun setRippleColor(ripple:ColorStateList?){
        view.rippleColor = ripple

        Log.d("MaterialProxy", "ripple: $ripple")

    }

    //todo add shapeAppearance & shapeAppearanceOverlay

    @Attr(R2.styleable.Paris_MaterialButton_strokeColor)
    fun setStrokeColor(strokeColor: ColorStateList?){
        view.strokeColor = strokeColor

        Log.d("MaterialProxy", "strokeColor: $strokeColor")
    }

    @Attr(R2.styleable.Paris_MaterialButton_strokeWidth)
    fun setStrokeWidth(@Px width: Int){
        view.strokeWidth = width

        Log.d("MaterialProxy", "strokeWidth: $width")
    }

    @AfterStyle
    fun afterStyle(@Suppress("UNUSED_PARAMETER") style: Style?) {
        /*
        * Material component has default textAppearance.
        * But if we do not pass any textAppearance then
        * android default textAppearance is set. To keep the
        * material theme textAppearance I have checked
        * whether it is the default android textAppearance.
        * If it's default android textAppearance then I have
        * assumed that user did not set the textAppearance
        * */
        if(mTextAppearance == defaultTextAppearance){

            if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
                view.setTextAppearance(R.style.TextAppearance_MaterialComponents_Button)
            } else{
                view.setTextAppearance(view.context, R.style.TextAppearance_MaterialComponents_Button)
            }

        } else{

            if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
                view.setTextAppearance(mTextAppearance)
            } else{
                view.setTextAppearance(view.context, mTextAppearance)
            }

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