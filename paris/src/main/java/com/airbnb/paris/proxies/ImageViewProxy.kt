package com.airbnb.paris.proxies

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Build
import android.widget.ImageView
import android.widget.ImageView.ScaleType

import com.airbnb.paris.R2
import com.airbnb.paris.annotations.Attr
import com.airbnb.paris.annotations.Styleable

@Styleable("Paris_ImageView")
class ImageViewProxy(view: ImageView) : BaseProxy<ImageViewProxy, ImageView>(view) {

    // TODO Provide a builder-only method
    @Attr(R2.styleable.Paris_ImageView_android_scaleType)
    fun setScaleType(index: Int) {
        if (index >= 0) {
            view.scaleType = SCALE_TYPE_ARRAY[index]
        }
    }

    @Attr(R2.styleable.Paris_ImageView_android_tint)
    fun setTint(color: ColorStateList?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.imageTintList = color
        }
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
