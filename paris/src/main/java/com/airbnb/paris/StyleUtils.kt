package com.airbnb.paris

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.StyleRes
import android.support.v7.content.res.AppCompatResources
import android.util.AttributeSet
import android.view.View

import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.*

object StyleUtils {

    var NOT_SET = -10

    /**
     * Unfortunately Android doesn't support reading @null resources from a style resource like it
     * does from an AttributeSet so this trickery is required
     */
    private val NULL_RESOURCE_IDS = hashSetOf(R.anim.null_, R.color.null_)

    private val STYLE_CLASS_TO_METHOD = LinkedHashMap<Class<*>, Method>()

    fun ifSetElse(value: Int, ifNotSet: Int): Int {
        return if (value != NOT_SET) value else ifNotSet
    }

    fun isAnySet(vararg values: Int): Boolean {
        for (value in values) {
            if (value != NOT_SET) {
                return true
            }
        }
        return false
    }

    /**
     *
     * Returns a TypedArray holding the attribute values in `set` and/or `styleRes` that are listed in attrs. If both `set` and
     * `styleRes` are specified then `set` attribute values take precedence over `styleRes`'s.

     *
     * In other words, this method behaves similarly to
     * [android.content.res.Resources.Theme.obtainStyledAttributes]. It differs in that it allows for
     * `set` to be `null`.
     */
    fun obtainStyledAttributes(context: Context, set: AttributeSet?, @StyleRes styleRes: Int, attrs: IntArray?): TypedArray? {
        if (set != null) {
            return context.obtainStyledAttributes(set, attrs, 0, styleRes)
        } else if (styleRes != 0) {
            return context.obtainStyledAttributes(styleRes, attrs)
        } else {
            return null
        }
    }

    fun setPadding(view: View, px: Int) {
        view.setPadding(px, px, px, px)
    }

    fun setPaddingBottom(view: View, px: Int) {
        view.setPadding(view.paddingLeft, view.paddingTop, view.paddingRight, px)
    }

    fun setPaddingLeft(view: View, px: Int) {
        view.setPadding(px, view.paddingTop, view.paddingRight, view.paddingBottom)
    }

    fun setPaddingRight(view: View, px: Int) {
        view.setPadding(view.paddingLeft, view.paddingTop, px, view.paddingBottom)
    }

    fun setPaddingTop(view: View, px: Int) {
        view.setPadding(view.paddingLeft, px, view.paddingRight, view.paddingBottom)
    }

    fun getDrawable(context: Context, a: TypedArray, index: Int): Drawable? {
        return if (isNull(a, index)) null else getDrawableCompat(context, a, index)
    }

    /**
     * Use this to load a vector drawable from a TypedArray in a backwards compatible fashion
     */
    internal fun getDrawableCompat(context: Context, array: TypedArray, index: Int): Drawable? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return array.getDrawable(index)
        } else {
            val resourceId = array.getResourceId(index, -1)
            if (resourceId != -1) {
                return AppCompatResources.getDrawable(context, resourceId)
            } else {
                return null
            }
        }
    }

    fun getResourceId(a: TypedArray, index: Int, defValue: Int): Int {
        return if (isNull(a, index)) 0 else a.getResourceId(index, 0)
    }

    private fun isNull(a: TypedArray, index: Int): Boolean {
        return NULL_RESOURCE_IDS.contains(a.getResourceId(index, 0))
    }

    internal fun <T : Style<*>> create(styleClass: Class<T>, set: AttributeSet?, @StyleRes styleRes: Int, config: Style.Config?): T {
        return create(getConstructor(styleClass), set, styleRes, config)
    }

    internal fun <T : Style<*>> create(method: Method?, set: AttributeSet?, @StyleRes styleRes: Int, config: Style.Config?): T {
        if (method == null) {

            return { view: View -> } as Style<*> as T
        }

        try {

            return method.invoke(null, set, styleRes, config) as T
        } catch (e: IllegalAccessException) {
            throw RuntimeException("Unable to invoke " + method, e)
        } catch (e: InvocationTargetException) {
            val cause = e.cause
            if (cause is RuntimeException) {
                throw cause
            }
            if (cause is Error) {
                throw cause
            }
            throw RuntimeException("Unable to create binding instance.", cause)
        }

    }

    internal fun <T : Style<*>> getConstructor(styleClass: Class<T>): Method? {
        var method: Method? = STYLE_CLASS_TO_METHOD[styleClass]
        if (method != null) {
            return method
        }
        try {
            method = styleClass.getMethod("from", AttributeSet::class.java, Integer.TYPE, Style.Config::class.java)
        } catch (e: NoSuchMethodException) {
            throw RuntimeException("Unable to find static constructor for " + styleClass.simpleName, e)
        }

        STYLE_CLASS_TO_METHOD.put(styleClass, method)
        return method
    }
}
