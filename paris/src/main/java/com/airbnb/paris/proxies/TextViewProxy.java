package com.airbnb.paris.proxies;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.text.TextUtils;
import android.util.TypedValue;
import android.widget.TextView;

import com.airbnb.paris.R2;
import com.airbnb.paris.Style;
import com.airbnb.paris.annotations.AfterStyle;
import com.airbnb.paris.annotations.Attr;
import com.airbnb.paris.annotations.Styleable;

@Styleable(value = "Paris_TextView")
class TextViewProxy extends BaseProxy<TextViewProxy, TextView> {

    @Nullable
    private Drawable drawableLeft;
    @Nullable
    private Drawable drawableTop;
    @Nullable
    private Drawable drawableRight;
    @Nullable
    private Drawable drawableBottom;

    TextViewProxy(TextView view) {
        super(view);
    }

    @AfterStyle
    void afterStyle(Style style) {
        Drawable[] drawables = getView().getCompoundDrawables();
        getView().setCompoundDrawables(
                drawableLeft != null ? drawableLeft : drawables[0],
                drawableTop != null ? drawableTop : drawables[1],
                drawableRight != null ? drawableRight : drawables[2],
                drawableBottom != null ? drawableBottom : drawables[3]);
        drawableLeft = null;
        drawableTop = null;
        drawableRight = null;
        drawableBottom = null;
    }

    @Attr(R2.styleable.Paris_TextView_android_drawableBottom)
    void setDrawableBottom(@Nullable Drawable drawable) {
        drawableBottom = drawable;
    }

    @Attr(R2.styleable.Paris_TextView_android_drawableLeft)
    void setDrawableLeft(@Nullable Drawable drawable) {
        drawableLeft = drawable;
    }

    @Attr(R2.styleable.Paris_TextView_android_drawableRight)
    void setDrawableRight(@Nullable Drawable drawable) {
        drawableRight = drawable;

    }
    @Attr(R2.styleable.Paris_TextView_android_drawableTop)
    void setDrawableTop(@Nullable Drawable drawable) {
        drawableTop = drawable;
    }

    @Attr(R2.styleable.Paris_TextView_android_ellipsize)
    void setEllipsize(int ellipsize) {
        TextUtils.TruncateAt where;
        switch (ellipsize) {
            case 1:
                where = TextUtils.TruncateAt.START;
                break;
            case 2:
                where = TextUtils.TruncateAt.MIDDLE;
                break;
            case 3:
                where = TextUtils.TruncateAt.END;
                break;
            case 4:
                where = TextUtils.TruncateAt.MARQUEE;
                break;
            default:
                throw new IllegalStateException("Wrong value for ellipsize");
        }
        getView().setEllipsize(where);
    }

    @Attr(R2.styleable.Paris_TextView_android_gravity)
    void setGravity(int gravity) {
        getView().setGravity(gravity);
    }

    @Attr(R2.styleable.Paris_TextView_android_letterSpacing)
    void setLetterSpacing(float letterSpacing) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getView().setLetterSpacing(letterSpacing);
        }
    }

    @Attr(R2.styleable.Paris_TextView_android_lines)
    void setLines(int lines) {
        getView().setLines(lines);
    }

    /**
     * View.setLineSpacing(...) takes a float for extra spacing but it's treated as pixels so seems
     * to make more sense to use an int here and mark it as a dimension
     */
    @Attr(R2.styleable.Paris_TextView_android_lineSpacingExtra)
    void setLineSpacingExtra(@Px int lineSpacingExtra) {
        getView().setLineSpacing(lineSpacingExtra, getView().getLineSpacingMultiplier());
    }

    @Attr(R2.styleable.Paris_TextView_android_lineSpacingMultiplier)
    void setLineSpacingMultiplier(float lineSpacingMultiplier) {
        getView().setLineSpacing(getView().getLineSpacingExtra(), lineSpacingMultiplier);
    }

    @Attr(R2.styleable.Paris_TextView_android_maxLines)
    void setMaxLines(int maxLines) {
        getView().setMaxLines(maxLines);
    }

    @Attr(R2.styleable.Paris_TextView_android_minLines)
    void setMinLines(int minLines) {
        getView().setMinLines(minLines);
    }

    @Attr(R2.styleable.Paris_TextView_android_minWidth)
    void setMinWidth(@Px int minWidth) {
        getView().setMinWidth(minWidth);
    }

    @Attr(R2.styleable.Paris_TextView_android_singleLine)
    void setSingleLine(boolean singleLine) {
        getView().setSingleLine(singleLine);
    }

    @Attr(R2.styleable.Paris_TextView_android_textAllCaps)
    void setTextAllCaps(boolean textAllCaps) {
        getView().setAllCaps(textAllCaps);
    }

    @Attr(R2.styleable.Paris_TextView_android_textColor)
    void setTextColor(ColorStateList colors) {
        getView().setTextColor(colors);
    }

    @Attr(R2.styleable.Paris_TextView_android_textColorHint)
    void setTextColorHint(ColorStateList colors) {
        getView().setHintTextColor(colors);
    }

    @Attr(R2.styleable.Paris_TextView_android_textSize)
    void setTextSize(@Px int textSize) {
        // TODO Change to SP?
        getView().setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }
}
