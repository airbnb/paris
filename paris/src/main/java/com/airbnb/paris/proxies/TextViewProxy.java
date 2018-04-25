package com.airbnb.paris.proxies;

import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.text.TextUtils;
import android.util.TypedValue;
import android.widget.TextView;

import com.airbnb.paris.R2;
import com.airbnb.paris.annotations.AfterStyle;
import com.airbnb.paris.annotations.Attr;
import com.airbnb.paris.annotations.Styleable;
import com.airbnb.paris.styles.Style;

@Styleable(value = "Paris_TextView")
public class TextViewProxy extends BaseProxy<TextViewProxy, TextView> {

    @Nullable
    private Drawable drawableLeft;
    @Nullable
    private Drawable drawableTop;
    @Nullable
    private Drawable drawableRight;
    @Nullable
    private Drawable drawableBottom;

    public TextViewProxy(TextView view) {
        super(view);
    }

    @AfterStyle
    public void afterStyle(Style style) {
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
    public void setDrawableBottom(@Nullable Drawable drawable) {
        drawableBottom = drawable;
    }

    @Attr(R2.styleable.Paris_TextView_android_drawableLeft)
    public void setDrawableLeft(@Nullable Drawable drawable) {
        drawableLeft = drawable;
    }

    @Attr(R2.styleable.Paris_TextView_android_drawableRight)
    public void setDrawableRight(@Nullable Drawable drawable) {
        drawableRight = drawable;

    }
    @Attr(R2.styleable.Paris_TextView_android_drawableTop)
    public void setDrawableTop(@Nullable Drawable drawable) {
        drawableTop = drawable;
    }

    @Attr(R2.styleable.Paris_TextView_android_ellipsize)
    public void setEllipsize(int ellipsize) {
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

    @Attr(R2.styleable.Paris_TextView_android_hint)
    public void setHint(@Nullable CharSequence hint) {
        getView().setHint(hint);
    }

    @Attr(R2.styleable.Paris_TextView_android_inputType)
    public void setInputType(int inputType) {
        getView().setInputType(inputType);
    }

    @Attr(R2.styleable.Paris_TextView_android_gravity)
    public void setGravity(int gravity) {
        getView().setGravity(gravity);
    }

    @Attr(R2.styleable.Paris_TextView_android_letterSpacing)
    public void setLetterSpacing(float letterSpacing) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getView().setLetterSpacing(letterSpacing);
        }
    }

    @Attr(R2.styleable.Paris_TextView_android_lines)
    public void setLines(int lines) {
        getView().setLines(lines);
    }

    /**
     * View.setLineSpacing(...) takes a float for extra spacing but it's treated as pixels so seems
     * to make more sense to use an int here and mark it as a dimension
     */
    @Attr(R2.styleable.Paris_TextView_android_lineSpacingExtra)
    public void setLineSpacingExtra(@Px int lineSpacingExtra) {
        getView().setLineSpacing(lineSpacingExtra, getView().getLineSpacingMultiplier());
    }

    @Attr(R2.styleable.Paris_TextView_android_lineSpacingMultiplier)
    public void setLineSpacingMultiplier(float lineSpacingMultiplier) {
        getView().setLineSpacing(getView().getLineSpacingExtra(), lineSpacingMultiplier);
    }

    @Attr(R2.styleable.Paris_TextView_android_maxLines)
    public void setMaxLines(int maxLines) {
        getView().setMaxLines(maxLines);
    }

    @Attr(R2.styleable.Paris_TextView_android_minLines)
    public void setMinLines(int minLines) {
        getView().setMinLines(minLines);
    }

    @Attr(R2.styleable.Paris_TextView_android_minWidth)
    public void setMinWidth(@Px int minWidth) {
        getView().setMinWidth(minWidth);
    }

    @Attr(R2.styleable.Paris_TextView_android_singleLine)
    public void setSingleLine(boolean singleLine) {
        getView().setSingleLine(singleLine);
    }

    @Attr(R2.styleable.Paris_TextView_android_text)
    public void setText(@Nullable CharSequence text) {
        getView().setText(text);
    }

    @Attr(R2.styleable.Paris_TextView_android_textAllCaps)
    public void setTextAllCaps(boolean textAllCaps) {
        getView().setAllCaps(textAllCaps);
    }

    /**
     * @param colors If null will set the color to the default (black), same as TextView
     */
    @Attr(R2.styleable.Paris_TextView_android_textColor)
    public void setTextColor(@Nullable ColorStateList colors) {
        getView().setTextColor(colors != null ? colors : ColorStateList.valueOf(0xFF000000));
    }

    @Attr(R2.styleable.Paris_TextView_android_textColorHint)
    public void setTextColorHint(@Nullable ColorStateList colors) {
        getView().setHintTextColor(colors);
    }

    @Attr(R2.styleable.Paris_TextView_android_textSize)
    public void setTextSize(@Px int textSize) {
        // TODO Change to SP?
        getView().setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }

    @Attr(R2.styleable.Paris_TextView_android_textStyle)
    public void setTextStyle(int styleIndex) {
        // Removes any style already applied to the typeface and applies the appropriate one instead
        Typeface typeface = Typeface.create(getView().getTypeface(), styleIndex);
        // Purposefully pass in the styleIndex again here because the view will apply "fake" bold
        // and/or italic if the typeface doesn't support it
        getView().setTypeface(typeface, styleIndex);
    }
}
