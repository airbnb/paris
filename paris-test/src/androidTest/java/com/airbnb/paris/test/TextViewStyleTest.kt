package com.airbnb.paris.test

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import com.airbnb.paris.TextViewStyleApplier
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.spy


@RunWith(AndroidJUnit4::class)
class TextViewStyleTest {

    lateinit var context: Context
    lateinit var res: Resources
    lateinit var view: TextView

    @Before
    fun setup() {
        context = InstrumentationRegistry.getTargetContext()
        res = context.resources
        view = TextView(context)
    }

    @Test
    fun textViewDrawables() {
        assertNull(view.compoundDrawables[0])
        assertNull(view.compoundDrawables[1])
        assertNull(view.compoundDrawables[2])
        assertNull(view.compoundDrawables[3])
        TextViewStyleApplier(view).apply(R.style.Test_TextView_drawables)
        assertNotNull(view.compoundDrawables[0])
        assertNotNull(view.compoundDrawables[1])
        assertNotNull(view.compoundDrawables[2])
        assertNotNull(view.compoundDrawables[3])
    }

    @Test
    fun textViewDrawablesNotReset() {
        // If a style is applied that doesn't change the drawable then it should still be there

        assertNull(view.compoundDrawables[0])
        TextViewStyleApplier(view).apply(R.style.Test_TextView_drawables)
        val drawableLeft = view.compoundDrawables[0];
        assertNotNull(drawableLeft)
        TextViewStyleApplier(view).apply(R.style.Test_TextView_no_drawables)
        assertEquals(drawableLeft, view.compoundDrawables[0])
    }

    @Test
    fun textViewEllipsize() {
        assertNull(view.ellipsize)
        TextViewStyleApplier(view).apply(R.style.Test_TextView_ellipsize)
        assertEquals(TextUtils.TruncateAt.END, view.ellipsize)
    }

    @Test
    fun textViewGravity() {
        assertNotEquals(Gravity.CENTER, view.gravity)
        TextViewStyleApplier(view).apply(R.style.Test_TextView_gravity)
        assertEquals(Gravity.CENTER, view.gravity)
    }

    @Test
    fun textViewLetterSpacing() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            assertEquals(0.0f, view.letterSpacing)
            TextViewStyleApplier(view).apply(R.style.Test_TextView_letterSpacing)
            val typedValue = TypedValue()
            res.getValue(R.dimen.test_text_view_letter_spacing, typedValue, true)
            assertEquals(typedValue.float, view.letterSpacing)
        }
    }

    @Test
    fun textViewLines() {
        val spy = spy(view)
        TextViewStyleApplier(spy).apply(R.style.Test_TextView_lines)
        Mockito.verify(spy).setLines(view.resources.getInteger(R.integer.test_text_view_lines))
    }

    @Test
    fun textViewLineSpacingExtra() {
        assertEquals(0.0f, view.lineSpacingExtra)
        TextViewStyleApplier(view).apply(R.style.Test_TextView_lineSpacingExtra)
        assertEquals(res.getDimensionPixelSize(R.dimen.test_text_view_line_spacing_extra) * 1.0f, view.lineSpacingExtra)
    }

    @Test
    fun textViewLineSpacingMultiplier() {
        assertEquals(1.0f, view.lineSpacingMultiplier)
        TextViewStyleApplier(view).apply(R.style.Test_TextView_lineSpacingMultiplier)
        val typedValue = TypedValue()
        res.getValue(R.dimen.test_text_view_line_spacing_multiplier, typedValue, true)
        assertEquals(typedValue.float, view.lineSpacingMultiplier)
    }

    @Test
    fun textViewMaxLines() {
        assertEquals(Integer.MAX_VALUE, view.maxLines)
        TextViewStyleApplier(view).apply(R.style.Test_TextView_maxLines)
        assertEquals(res.getInteger(R.integer.test_text_view_max_lines), view.maxLines)
    }

    @Test
    fun textViewMinLines() {
        assertEquals(0, view.minLines)
        TextViewStyleApplier(view).apply(R.style.Test_TextView_minLines)
        assertEquals(res.getInteger(R.integer.test_text_view_min_lines), view.minLines)
    }

    @Test
    fun textViewMinWidth() {
        assertEquals(0, view.minWidth)
        TextViewStyleApplier(view).apply(R.style.Test_TextView_minWidth)
        assertEquals(res.getDimensionPixelSize(R.dimen.test_text_view_min_width), view.minWidth)
    }

    @Test
    fun textViewSingleLine() {
        val spy = spy(view)
        TextViewStyleApplier(spy).apply(R.style.Test_TextView_singleLine)
        Mockito.verify(spy).setSingleLine(true)
    }

    @Test
    fun textViewTextAllCaps() {
        val spy = spy(view)
        TextViewStyleApplier(spy).apply(R.style.Test_TextView_textAllCaps)
        Mockito.verify(spy).setAllCaps(true)
    }

    @Test
    fun textViewTextSize() {
        val textSize = res.getDimensionPixelSize(R.dimen.test_text_view_text_size) * 1.0f
        assertNotEquals(textSize, view.textSize)
        TextViewStyleApplier(view).apply(R.style.Test_TextView_textSize)
        assertEquals(textSize, view.textSize)
    }
}
