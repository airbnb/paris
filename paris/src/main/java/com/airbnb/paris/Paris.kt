package com.airbnb.paris

import android.support.annotation.StyleRes
import android.util.AttributeSet
import android.view.View
import com.airbnb.paris.Style.Config
import java.lang.reflect.Method
import java.util.*

/**
 * This is an experimental framework. It's currently being tested on fonts and a couple of DLS
 * Components. In the meantime, please refrain from using it, or talk to nathanael-silverman.
 */
object Paris {

    /**
     * Builder-like class to apply styles and options in a chain
     */
    class Applier<T : View> constructor(private val view: T) {

        private var config = null//Config.builder().build()

        /**
         * Will apply this [Config.Option] to all subsequent calls that don't include a
         * [Config]
         */
        fun addOption(option: Config.Option): Applier<T> {
            config = null//config.toBuilder().addOption(option).build()
            return this
        }

        /**
         * Will apply the attribute values contained in the [AttributeSet] by looking up
         * [Style] declarations that correspond to the name of the associated [View] or
         * any of its parents.

         * For example, changing a [android.widget.TextView] will apply both
         * [TextViewStyle] and [ViewStyle].
         */
        fun apply(set: AttributeSet?): Applier<T> {
            if (set != null) {
                Paris.apply(view, set, 0, config)
            }
            return this
        }

        /**
         * Will apply the attribute values contained in the [AttributeSet] by looking up
         * [Style] declarations that correspond to the name of the associated [View] or
         * any of its parents.

         * For example, changing a [android.widget.TextView] will apply both
         * [TextViewStyle] and [ViewStyle].
         */
        fun apply(set: AttributeSet?, config: Config): Applier<T> {
            if (set != null) {
                Paris.apply(view, set, 0, config)
            }
            return this
        }

        /**
         * Will apply the attribute values contained in the [StyleRes] by looking up
         * [Style] declarations that correspond to the name of the associated [View] or
         * any of its parents.

         * For example, changing a [android.widget.TextView] will apply both
         * [TextViewStyle] and [ViewStyle].
         */
        fun apply(@StyleRes styleRes: Int): Applier<T> {
            Paris.apply(view, null, styleRes, config)
            return this
        }

        /**
         * Will apply the attribute values contained in the [StyleRes] by looking up
         * [Style] declarations that correspond to the name of the associated [View] or
         * any of its parents.

         * For example, changing a [android.widget.TextView] will apply both
         * [TextViewStyle] and [ViewStyle].
         */
        fun apply(@StyleRes styleRes: Int, config: Config): Applier<T> {
            Paris.apply(view, null, styleRes, config)
            return this
        }
    }

    /**
     * For fast look-ups a tree of [Style] constructors is used where each [Node] links
     * to the parent [Style].

     * For example:

     * null
     * |
     * |
     * ViewStyle
     * /      \
     * /        \
     * /          \
     * TextViewStyle   MyCustomViewStyle

     * [.VIEW_CLASS_TO_NODE] provides the entry point into the tree depending on the
     * [View] that's being modified.
     */
    private class Node constructor(internal val parentNode: Node?, internal val styleConstructor: Method?)

    private val PACKAGE_NAME = Paris::class.java.`package`.name
    private val STYLE_CLASS_NAME_SUFFIX = "Style"
    private val VIEW_CLASS_TO_NODE = HashMap<Class<*>, Node?>()

    fun <T : View> change(view: T): Applier<T> {
        return Applier(view)
    }

    /**
     * Applies all the [Style]s automatically detected base on the [View] hierarchy
     */
    private fun <T : View> apply(view: T, set: AttributeSet?, @StyleRes styleRes: Int, config: Config?) {
        var node = findNodeForViewClass(view.javaClass)
        while (node != null) {
            val styleConstructor = node.styleConstructor
            StyleUtils.create<Style<in T>>(styleConstructor, set, styleRes, config).applyTo(view)
            node = node.parentNode
        }
    }

    private fun findNodeForViewClass(viewClass: Class<*>): Node? {
        if (viewClass == Any::class.java) {
            return null
        }

        var node: Node? = VIEW_CLASS_TO_NODE[viewClass]
        if (node != null) {
            return node
        }

        var className = viewClass.name
        if (className.startsWith("android.")) {
            // Look for framework View classes in this package
            className = PACKAGE_NAME + "." + viewClass.simpleName
        }
        try {

            val styleClass = Class.forName(className + STYLE_CLASS_NAME_SUFFIX) as Class<out Style<*>>
            val styleConstructor = StyleUtils.getConstructor(styleClass)
            node = Node(findNodeForViewClass(viewClass.superclass), styleConstructor)
        } catch (e: ClassNotFoundException) {
            node = findNodeForViewClass(viewClass.superclass)
        }

        VIEW_CLASS_TO_NODE.put(viewClass, node)
        return node
    }
}