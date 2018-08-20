package com.airbnb.paris.utils

import android.content.Context
import android.graphics.Typeface
import android.support.annotation.FontRes
import android.support.v4.content.res.ResourcesCompat
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements

@Implements(ResourcesCompat::class)
class ShadowResourcesCompat {

    companion object {

        @JvmStatic
        @Implementation
        fun getFont(context: Context, @FontRes id: Int): Typeface {
            return Typeface.create(id.toString(), Typeface.NORMAL)
        }

    }

}
