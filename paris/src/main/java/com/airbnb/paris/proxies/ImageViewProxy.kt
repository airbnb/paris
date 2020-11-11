package com.airbnb.paris.proxies

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Build
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import androidx.annotation.RequiresApi
import com.airbnb.paris.R2
import com.airbnb.paris.annotations.Attr
import com.airbnb.paris.annotations.Styleable

@Styleable("Paris_ImageView")
class ImageViewProxy(view: ImageView) : BaseProxy<ImageViewProxy, ImageView>(view) {

    // TODO Provide a builder-only method
    @Attr(R2.styleable.Paris_ImageView_android_scaleType)
    fun setScaleType(index: Int) {
        view.scaleType = when (index) {
            in SCALE_TYPE_ARRAY.indices -> SCALE_TYPE_ARRAY[index]
            // Default scale type for an ImageView
            // https://stackoverflow.com/questions/2951923/whats-the-default-scaletype-of-imageview
            else -> ScaleType.FIT_CENTER
        }
    }

    @Attr(R2.styleable.Paris_ImageView_android_tint)
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun setTint(color: ColorStateList?) {
        view.imageTintList = color
    }

    @Attr(R2.styleable.Paris_ImageView_android_src)
    fun setSrc(drawable: Drawable?) {
        view.setImageDrawable(drawable)
    }

    companion object {

        private val SCALE_TYPE_ARRAY = arrayOf(
            ScaleType.MATRIX,
            ScaleType.FIT_XY,
            ScaleType.FIT_START,
            ScaleType.FIT_CENTER,
            ScaleType.FIT_END,
            ScaleType.CENTER,
            ScaleType.CENTER_CROP,
            ScaleType.CENTER_INSIDE
        )
    }
}
