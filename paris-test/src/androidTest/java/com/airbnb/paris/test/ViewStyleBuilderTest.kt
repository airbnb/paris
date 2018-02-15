package com.airbnb.paris.test

import android.graphics.*
import android.support.test.*
import android.support.test.runner.*
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.*

@RunWith(AndroidJUnit4::class)
class ViewStyleBuilderTest {

    private val context = InstrumentationRegistry.getTargetContext()!!
    lateinit var myOtherView: MyOtherView
    lateinit var myViewBuilder: MyOtherViewStyleApplier.StyleBuilder

    init {
        // Necessary to test AppCompat attributes like "?attr/selectableItemBackground"
        // TODO Not working for background() test
        context.setTheme(R.style.Theme_AppCompat)
    }

    @Before
    fun setup() {
        myOtherView = MyOtherView(context)
        myViewBuilder = MyOtherViewStyleApplier.StyleBuilder()
    }

    @Test
    fun subStyleCombination() {
        val style = myViewBuilder
                .titleStyle { builder ->
                    builder.textColor(Color.RED)
                }
                .titleStyle { builder ->
                    builder.textSize(16)
                }
                .build()
        val typedArray = style.obtainStyledAttributes(context, R.styleable.MyView)
        val subStyle = typedArray.getStyle(R.styleable.MyView_titleStyle)
        val subTypedArray = subStyle.obtainStyledAttributes(context, R.styleable.Paris_TextView)

        assertEquals(Color.RED, subTypedArray.getColor(R.styleable.Paris_TextView_android_textColor))
        assertEquals(16, subTypedArray.getDimensionPixelSize(R.styleable.Paris_TextView_android_textSize))
    }
}
