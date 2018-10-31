package com.airbnb.paris.utils

import android.content.Context
import android.graphics.Typeface
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements

@Implements(ResourcesCompat::class)
class ShadowResourcesCompat {

    companion object {

        /**
         * Since Robolectric has some
         * <a href="https://github.com/robolectric/robolectric/issues/3590">issues</a>
         * with loading font resources, we need to provide our own method of
         * constructing Typeface object from font resource. It's enough to create Typeface
         * object with resource id as a font family name. Since Typeface object equality check
         * depends on font family name and style id, objects created from the same resource
         * will be considered equal. And they won't clash with typefaces created from predefined
         * names.
         */
        @JvmStatic
        @Implementation
        fun getFont(context: Context, @FontRes id: Int): Typeface {
            return Typeface.create(id.toString(), Typeface.NORMAL)
        }

    }

}
