@file:Suppress(
  "DEPRECATION",
  "Detekt.MaxLineLength",
)

package com.airbnb.paris.extensions

import android.util.AttributeSet
import androidx.`annotation`.StyleRes
import com.airbnb.paris.ExtendableStyleBuilder
import com.airbnb.paris.styles.Style
import com.airbnb.paris.test.MyView
import com.airbnb.paris.test.MyViewStyleApplier
import kotlin.Int
import kotlin.Suppress
import kotlin.Unit

public fun MyView.style(style: Style): Unit {
  MyViewStyleApplier(this).apply(style)
}

public fun MyView.style(@StyleRes styleRes: Int): Unit {
  MyViewStyleApplier(this).apply(styleRes)
}

public fun MyView.style(attrs: AttributeSet?): Unit {
  MyViewStyleApplier(this).apply(attrs)
}

public inline fun MyView.style(builder: ExtendableStyleBuilder<MyView>.() -> Unit): Unit {
  MyViewStyleApplier(this).apply(ExtendableStyleBuilder<MyView>().apply(builder).build())
}

/**
 * @see MyView.testStyle
 */
public fun ExtendableStyleBuilder<MyView>.addTest(): Unit {
  add(MyViewStyleApplier.StyleBuilder().addTest().build())
}

/**
 * Empty style.
 */
public fun ExtendableStyleBuilder<MyView>.addDefault(): Unit {
  add(MyViewStyleApplier.StyleBuilder().addDefault().build())
}

public inline fun myViewStyle(builder: ExtendableStyleBuilder<MyView>.() -> Unit): Style =
    ExtendableStyleBuilder<MyView>().apply(builder).build()
