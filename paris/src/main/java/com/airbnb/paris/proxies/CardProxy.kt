package com.airbnb.paris.proxies

import android.content.res.ColorStateList
import androidx.annotation.Px
import androidx.cardview.widget.CardView
import com.airbnb.paris.R2
import com.airbnb.paris.annotations.Attr
import com.airbnb.paris.annotations.LayoutDimension
import com.airbnb.paris.annotations.Styleable

@Styleable("Paris_CardView")
class CardViewProxy(view: CardView) : BaseProxy<CardViewProxy, CardView>(view) {

    @Attr(R2.styleable.Paris_CardView_cardCornerRadius)
    fun setRadius(@Px radius: Float) {
        view.radius = radius
    }

    @Attr(R2.styleable.Paris_CardView_cardBackgroundColor)
    fun setBackgroundColor(colorStateList: ColorStateList?) {
        view.setCardBackgroundColor(colorStateList)
    }

    @Attr(R2.styleable.Paris_CardView_cardElevation)
    fun setElevation(@Px elevation: Float) {
        view.cardElevation = elevation
    }

    @Attr(R2.styleable.Paris_CardView_contentPadding)
    fun setContentPadding(@Px contentPadding: Int) {
        view.setContentPadding(contentPadding, contentPadding, contentPadding, contentPadding)
    }
}
