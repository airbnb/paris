package com.airbnb.paris.proxies

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.Px
import android.text.InputType
import android.text.TextUtils
import android.text.method.PasswordTransformationMethod
import android.util.TypedValue
import android.widget.TextView

import com.airbnb.paris.R2
import com.airbnb.paris.annotations.AfterStyle
import com.airbnb.paris.annotations.Attr
import com.airbnb.paris.annotations.Styleable
import com.airbnb.paris.styles.Style

@Styleable("Paris_TextView")
class TextViewProxy(view: TextView) : BaseProxy<TextViewProxy, TextView>(view) {

    private var drawableLeft: Drawable? = null
    private var drawableTop: Drawable? = null
    private var drawableRight: Drawable? = null
    private var drawableBottom: Drawable? = null

    /**
     * `null` if not set.
     */
    private var singleLine: Boolean? = null

    /**
     * Value must be a constant from [InputType] or `null` if not set.
     */
    private var inputType: Int? = null

    private var typeface: Typeface? = null

    private var textStyleIndex: Int? = null

    @Attr(R2.styleable.Paris_TextView_android_drawableBottom)
    fun setDrawableBottom(drawable: Drawable?) {
        drawableBottom = drawable
    }

    @Attr(R2.styleable.Paris_TextView_android_drawableLeft)
    fun setDrawableLeft(drawable: Drawable?) {
        drawableLeft = drawable
    }

    @Attr(R2.styleable.Paris_TextView_android_drawableRight)
    fun setDrawableRight(drawable: Drawable?) {
        drawableRight = drawable

    }

    @Attr(R2.styleable.Paris_TextView_android_drawableTop)
    fun setDrawableTop(drawable: Drawable?) {
        drawableTop = drawable
    }

    @Attr(R2.styleable.Paris_TextView_android_ellipsize)
    fun setEllipsize(ellipsize: Int) {
        view.ellipsize = when (ellipsize) {
            1 -> TextUtils.TruncateAt.START
            2 -> TextUtils.TruncateAt.MIDDLE
            3 -> TextUtils.TruncateAt.END
            4 -> TextUtils.TruncateAt.MARQUEE
            else -> throw IllegalStateException("Invalid value for ellipsize.")
        }
    }

    @Attr(R2.styleable.Paris_TextView_android_fontFamily)
    fun setFontFamily(typeface: Typeface?) {
        this.typeface = typeface
    }

    @Attr(R2.styleable.Paris_TextView_android_hint)
    fun setHint(hint: CharSequence?) {
        view.hint = hint
    }

    @Attr(R2.styleable.Paris_TextView_android_inputType)
    fun setInputType(inputType: Int) {
        this.inputType = inputType
        view.inputType = inputType
    }

    @Attr(R2.styleable.Paris_TextView_android_gravity)
    fun setGravity(gravity: Int) {
        view.gravity = gravity
    }

    @Attr(R2.styleable.Paris_TextView_android_letterSpacing)
    fun setLetterSpacing(letterSpacing: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.letterSpacing = letterSpacing
        }
    }

    @Attr(R2.styleable.Paris_TextView_android_lines)
    fun setLines(lines: Int) {
        view.setLines(lines)
    }

    /**
     * View.setLineSpacing(...) takes a float for extra spacing but it's treated as pixels so seems
     * to make more sense to use an int here and mark it as a dimension
     */
    @Attr(R2.styleable.Paris_TextView_android_lineSpacingExtra)
    fun setLineSpacingExtra(@Px lineSpacingExtra: Int) {
        view.setLineSpacing(lineSpacingExtra.toFloat(), view.lineSpacingMultiplier)
    }

    @Attr(R2.styleable.Paris_TextView_android_lineSpacingMultiplier)
    fun setLineSpacingMultiplier(lineSpacingMultiplier: Float) {
        view.setLineSpacing(view.lineSpacingExtra, lineSpacingMultiplier)
    }

    @Attr(R2.styleable.Paris_TextView_android_maxLines)
    fun setMaxLines(maxLines: Int) {
        view.maxLines = maxLines
    }

    @Attr(R2.styleable.Paris_TextView_android_minLines)
    fun setMinLines(minLines: Int) {
        view.minLines = minLines
    }

    @Attr(R2.styleable.Paris_TextView_android_maxWidth)
    fun setMaxWidth(@Px maxWidth: Int) {
        view.maxWidth = maxWidth
    }

    @Attr(R2.styleable.Paris_TextView_android_minWidth)
    fun setMinWidth(@Px minWidth: Int) {
        view.minWidth = minWidth
    }

    @Attr(R2.styleable.Paris_TextView_android_singleLine)
    fun setSingleLine(singleLine: Boolean) {
        this.singleLine = singleLine
    }

    @Attr(R2.styleable.Paris_TextView_android_text)
    fun setText(text: CharSequence?) {
        view.text = text
    }

    @Attr(R2.styleable.Paris_TextView_android_textAllCaps)
    fun setTextAllCaps(textAllCaps: Boolean) {
        view.setAllCaps(textAllCaps)
    }

    /**
     * @param colors If null will set the color to the default (black), same as TextView
     */
    @Attr(R2.styleable.Paris_TextView_android_textColor)
    fun setTextColor(colors: ColorStateList?) {
        view.setTextColor(colors ?: ColorStateList.valueOf(-0x1000000))
    }

    @Attr(R2.styleable.Paris_TextView_android_textColorHint)
    fun setTextColorHint(colors: ColorStateList?) {
        view.setHintTextColor(colors)
    }

    @Attr(R2.styleable.Paris_TextView_android_textSize)
    fun setTextSize(@Px textSize: Int) {
        // TODO Change to SP?
        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
    }

    @Attr(R2.styleable.Paris_TextView_android_textStyle)
    fun setTextStyle(styleIndex: Int) {
        this.textStyleIndex = styleIndex
    }

    @AfterStyle
    fun afterStyle(@Suppress("UNUSED_PARAMETER") style: Style?) {
        val drawables = view.compoundDrawables
        view.setCompoundDrawablesWithIntrinsicBounds(
            drawableLeft ?: drawables[0],
            drawableTop ?: drawables[1],
            drawableRight ?: drawables[2],
            drawableBottom ?: drawables[3]
        )
        drawableLeft = null
        drawableTop = null
        drawableRight = null
        drawableBottom = null

        if (singleLine != null) {
            if (inputType != null) {
                // If set, the input type overrides what was set using the deprecated singleLine
                // attribute
                singleLine = !isMultilineInputType(inputType!!)
            }
            view.setSingleLine(singleLine!!)
        }

        // This copies what TextView is doing although it only seems necessary when singleLine
        // is set to true since that changes the transformation method
        if (inputType != null && isPasswordInputType(inputType!!)) {
            view.transformationMethod = PasswordTransformationMethod.getInstance()
        }

        inputType = null

        if (typeface != null || textStyleIndex != null) {
            val typefaceToSet = typeface ?: view.typeface
            val textStyleToSet = textStyleIndex ?: typefaceToSet.style

            // Removes any style already applied to the typeface and applies the appropriate one instead
            val typeface = Typeface.create(typefaceToSet, textStyleToSet)
            // Purposefully pass in the styleIndex again here because the view will apply "fake" bold
            // and/or italic if the typeface doesn't support it
            view.setTypeface(typeface, textStyleToSet)
        }
    }

    private fun isMultilineInputType(inputType: Int): Boolean {
        return inputType and (InputType.TYPE_MASK_CLASS or InputType.TYPE_TEXT_FLAG_MULTI_LINE) == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
    }

    private fun isPasswordInputType(inputType: Int): Boolean {
        val variation = inputType and (InputType.TYPE_MASK_CLASS or InputType.TYPE_MASK_VARIATION)
        return (variation == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                || variation == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD
                || variation == InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD)
    }
}
