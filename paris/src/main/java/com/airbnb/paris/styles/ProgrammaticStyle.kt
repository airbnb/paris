package com.airbnb.paris.styles

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.AnyRes
import android.support.annotation.AttrRes
import android.support.annotation.ColorInt
import com.airbnb.paris.attribute_values.ColorValue
import com.airbnb.paris.attribute_values.DpValue
import com.airbnb.paris.attribute_values.ResourceId
import com.airbnb.paris.attribute_values.Styles
import com.airbnb.paris.typed_array_wrappers.MapTypedArrayWrapper
import com.airbnb.paris.typed_array_wrappers.TypedArrayWrapper
import java.util.*

data class ProgrammaticStyle internal constructor(
    private val attributeMap: Map<Int, Any?>,
    private var name: String? = null
) : Style {

    private constructor(builder: Builder) : this(builder.attrResToValueResMap, builder.name)

    data class Builder internal constructor(
        internal val attrResToValueResMap: MutableMap<Int, Any?> = HashMap(),
        internal var name: String = "a programmatic style"
    ) {

        fun isEmpty(): Boolean = attrResToValueResMap.isEmpty()

        fun debugName(name: String): Builder {
            this.name = name
            return this
        }

        fun putRes(@AttrRes attrRes: Int, @AnyRes valueRes: Int): Builder =
            put(attrRes, ResourceId(valueRes))

        fun putDp(@AttrRes attrRes: Int, dps: Int): Builder =
            put(attrRes, DpValue(dps))

        fun putColor(@AttrRes attrRes: Int, @ColorInt color: Int): Builder =
            put(attrRes, ColorValue(color))

        fun put(@AttrRes attrRes: Int, value: Any?): Builder {
            attrResToValueResMap[attrRes] = value
            return this
        }

        fun putStyle(@AttrRes attrRes: Int, @AnyRes valueRes: Int): Builder =
            putStyle(attrRes, ResourceStyle(valueRes))

        fun putStyle(@AttrRes attrRes: Int, style: Style): Builder {
            val styles: Styles
            if (attrResToValueResMap.containsKey(attrRes)) {
                styles = attrResToValueResMap[attrRes] as Styles
            } else {
                styles = Styles()
                attrResToValueResMap[attrRes] = styles
            }
            styles.list.add(style)
            return this
        }

        fun build(): ProgrammaticStyle = ProgrammaticStyle(this)
    }

    companion object {
        fun builder(): Builder = Builder()
    }

    override val shouldApplyParent = true

    override val shouldApplyDefaults = true

    override fun name(context: Context): String = when {
        name != null -> name!!
        else -> "a programmatic style"
    }

    @SuppressLint("Recycle")
    override fun obtainStyledAttributes(context: Context, attrs: IntArray): TypedArrayWrapper =
        MapTypedArrayWrapper(context, attrs, attributeMap)
}
