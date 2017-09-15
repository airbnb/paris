package com.airbnb.paris.proxies;

import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.airbnb.paris.R2;
import com.airbnb.paris.annotations.Attr;
import com.airbnb.paris.annotations.Styleable;

@Styleable(value = "Paris_ImageView")
class ImageViewProxy extends BaseProxy<ImageViewProxy, ImageView> {

    private static final ScaleType[] SCALE_TYPE_ARRAY = {
            ScaleType.MATRIX,
            ScaleType.FIT_XY,
            ScaleType.FIT_START,
            ScaleType.FIT_CENTER,
            ScaleType.FIT_END,
            ScaleType.CENTER,
            ScaleType.CENTER_CROP,
            ScaleType.CENTER_INSIDE
    };

    ImageViewProxy(ImageView view) {
        super(view);
    }

    // TODO Provide a builder-only method
    @Attr(R2.styleable.Paris_ImageView_android_scaleType)
    void setScaleType(int index) {
        if (index >= 0) {
            getView().setScaleType(SCALE_TYPE_ARRAY[index]);
        }
    }
}
