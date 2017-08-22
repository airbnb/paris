package com.airbnb.paris

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.AttrRes
import android.support.annotation.StyleRes
import android.util.AttributeSet
import com.airbnb.paris.Style.DebugListener
import java.util.*

// TODO Can all the parameters be private?
data class SimpleStyle internal constructor(
        private val attributeMap: Map<Int, Any>?,
        val attributeSet: AttributeSet?,
        @StyleRes val styleRes: Int) : Style {

    private constructor(builder: Builder) : this(builder.attrResToValueResMap, null, 0)
    constructor(attributeSet: AttributeSet) : this(null, attributeSet, 0)
    constructor(@StyleRes styleRes: Int) : this(null, null, styleRes)

    class Builder internal constructor() {

        internal val attrResToValueResMap = HashMap<Int, Any>()

        fun isEmpty(): Boolean = attrResToValueResMap.isEmpty()

        fun put(@AttrRes attrRes: Int, valueRes: Int): Builder =
                put(attrRes, ResourceId(valueRes))

        fun put(@AttrRes attrRes: Int, value: Any): Builder {
            attrResToValueResMap.put(attrRes, value)
            return this
        }

        fun build(): SimpleStyle = SimpleStyle(this)
    }

    companion object {
        internal val EMPTY = SimpleStyle(null, null, 0)

        fun builder(): Builder = Builder()
    }

    override val shouldApplyParent = attributeSet == null

    /**
     * Visible for debug
     */
    override var debugListener: DebugListener? = null

    override fun name(context: Context): String = when {
        styleRes != 0 -> context.resources.getResourceEntryName(styleRes)
        else -> "unknown name"
    }

    @SuppressLint("Recycle")
    override fun obtainStyledAttributes(context: Context, attrs: IntArray): TypedArrayWrapper = when {
        attributeMap != null -> MapTypedArrayWrapper(context.resources, attrs, attributeMap)
        attributeSet != null -> TypedArrayTypedArrayWrapper(context.obtainStyledAttributes(attributeSet, attrs, 0, styleRes))
        styleRes != 0 -> TypedArrayTypedArrayWrapper(context.obtainStyledAttributes(styleRes, attrs))
        else -> EmptyTypedArrayWrapper
    }
}
