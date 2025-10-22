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

public fun MyView.style(style: Style) {
  MyViewStyleApplier(this).apply(style)
}

public fun MyView.style(@StyleRes styleRes: Int) {
  MyViewStyleApplier(this).apply(styleRes)
}

public fun MyView.style(attrs: AttributeSet?) {
  MyViewStyleApplier(this).apply(attrs)
}

public inline fun <V : MyView> V.style(builder: ExtendableStyleBuilder<V>.() -> Unit) {
  MyViewStyleApplier(this).apply(ExtendableStyleBuilder<V>().apply(builder).build())
}

/**
 * @see MyView.RED_STYLE
 */
public fun ExtendableStyleBuilder<MyView>.addRed() {
  add(MyViewStyleApplier.StyleBuilder().addRed().build())
}

/**
 * @see MyView.greenStyle
 */
public fun ExtendableStyleBuilder<MyView>.addGreen() {
  add(MyViewStyleApplier.StyleBuilder().addGreen().build())
}

/**
 * @see MyView.RED_STYLE
 */
public fun ExtendableStyleBuilder<MyView>.addDefault() {
  add(MyViewStyleApplier.StyleBuilder().addDefault().build())
}

public inline fun myViewStyle(builder: ExtendableStyleBuilder<MyView>.() -> Unit): Style = ExtendableStyleBuilder<MyView>().apply(builder).build()
