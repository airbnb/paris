package com.airbnb.paris;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.content.res.AppCompatResources;

public class StyleUtils {

    public static Drawable getDrawable(Context context, TypedArrayWrapper a, int index) {
        return a.isNull(index) ? null : getDrawableCompat(context, a, index);
    }

    /**
     * Use this to load a vector drawable from a TypedArray in a backwards compatible fashion
     */
    @Nullable
    private static Drawable getDrawableCompat(Context context, TypedArrayWrapper array, int index) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return array.getDrawable(index);
        } else {
            int resourceId = array.getResourceId(index, -1);
            if (resourceId != -1) {
                return AppCompatResources.getDrawable(context, resourceId);
            } else {
                return null;
            }
        }
    }
}
