package com.airbnb.paris.styles

import android.annotation.*
import android.content.*
import android.support.annotation.*
import com.airbnb.paris.*
import com.airbnb.paris.typed_array_wrappers.*
import java.util.*

data class ProgrammaticStyle internal constructor(
        private val attributeMap: Map<Int, Any>,
        private var name: String? = null) : Style {

    private constructor(builder: Builder) : this(builder.attrResToValueResMap, builder.name)

    data class Builder internal constructor(
            internal val attrResToValueResMap: HashMap<Int, Any> = HashMap<Int, Any>(),
            internal var name: String = "a_ProgrammaticStyleBuilder") {

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

        fun put(@AttrRes attrRes: Int, value: Any): Builder {
            attrResToValueResMap.put(attrRes, value)
            return this
        }

        fun build(): ProgrammaticStyle = ProgrammaticStyle(this)
    }

    companion object {
        fun builder(): Builder = Builder()
    }

    override val shouldApplyParent = true

    override fun name(context: Context): String = when {
        name != null -> name!!
        else -> "a_ProgrammaticStyle"
    }

    @SuppressLint("Recycle")
    override fun obtainStyledAttributes(context: Context, attrs: IntArray): TypedArrayWrapper =
        MapTypedArrayWrapper(context.resources, attrs, attributeMap)
}
