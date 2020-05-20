package com.airbnb.paris.test

import android.graphics.Color
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.airbnb.paris.styles.EmptyStyle
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

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

    @Test
    fun subStyleCombinationPrecedence() {
        val style = myViewBuilder
            .titleStyle { builder ->
                builder.textColor(Color.RED)
            }
            .titleStyle { builder ->
                builder.textColor(Color.GREEN)
            }
            .build()
        val typedArray = style.obtainStyledAttributes(context, R.styleable.MyView)
        val subStyle = typedArray.getStyle(R.styleable.MyView_titleStyle)
        val subTypedArray = subStyle.obtainStyledAttributes(context, R.styleable.Paris_TextView)

        assertEquals(Color.GREEN, subTypedArray.getColor(R.styleable.Paris_TextView_android_textColor))
    }

    @Test
    fun subStyleCombinationPrecedenceMultiWrappers() {
        // This tests the substyle combination logic in the case where all the attributes are
        // defined in different typed array wrappers. There used to be an optimization where if all
        // attributes were declared by a wrapper the rest would be ignored but following new
        // substyle combinatory rules it had to be removed

        val style = myViewBuilder
            .titleStyle { builder ->
                builder.textColor(Color.RED)
            }
            .add(EmptyStyle)
            .titleStyle { builder ->
                builder.textColor(Color.GREEN)
            }
            .build()
        val typedArray = style.obtainStyledAttributes(context, intArrayOf(R.attr.titleStyle))
        val subStyle = typedArray.getStyle(0)
        val subTypedArray = subStyle.obtainStyledAttributes(context, R.styleable.Paris_TextView)

        assertEquals(Color.GREEN, subTypedArray.getColor(R.styleable.Paris_TextView_android_textColor))
    }
}
