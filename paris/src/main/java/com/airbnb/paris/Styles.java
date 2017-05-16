package com.airbnb.paris;

import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.View;

import com.airbnb.paris.Style.Config;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * This is an experimental framework. It's currently being tested on fonts and a couple of DLS
 * Components. In the meantime, please refrain from using it, or talk to nathanael-silverman.
 */
public final class Styles {

    /**
     * Builder-like class to apply styles and options in a chain
     */
    public static class Applier<T extends View> {

        private final T view;

        private Config config = null;//Config.builder().build();

        private Applier(T view) {
            this.view = view;
        }

        /**
         * Will apply this {@link Config.Option} to all subsequent calls that don't include a
         * {@link Config}
         */
        public Applier<T> addOption(Config.Option option) {
            config = null;//config.toBuilder().addOption(option).build();
            return this;
        }

        /**
         * Will apply the attribute values contained in the {@link AttributeSet} by looking up
         * {@link Style} declarations that correspond to the name of the associated {@link View} or
         * any of its parents.
         *
         * For example, changing a {@link android.widget.TextView} will apply both
         * {@link TextViewStyle} and {@link ViewStyle}.
         */
        public Applier<T> apply(@Nullable AttributeSet set) {
            if (set != null) {
                Styles.apply(view, set, 0, config);
            }
            return this;
        }

        /**
         * Will apply the attribute values contained in the {@link AttributeSet} by looking up
         * {@link Style} declarations that correspond to the name of the associated {@link View} or
         * any of its parents.
         *
         * For example, changing a {@link android.widget.TextView} will apply both
         * {@link TextViewStyle} and {@link ViewStyle}.
         */
        public Applier<T> apply(@Nullable AttributeSet set, Config config) {
            if (set != null) {
                Styles.apply(view, set, 0, config);
            }
            return this;
        }

        /**
         * Will apply the attribute values contained in the {@link StyleRes} by looking up
         * {@link Style} declarations that correspond to the name of the associated {@link View} or
         * any of its parents.
         *
         * For example, changing a {@link android.widget.TextView} will apply both
         * {@link TextViewStyle} and {@link ViewStyle}.
         */
        public Applier<T> apply(@StyleRes int styleRes) {
            Styles.apply(view, null, styleRes, config);
            return this;
        }

        /**
         * Will apply the attribute values contained in the {@link StyleRes} by looking up
         * {@link Style} declarations that correspond to the name of the associated {@link View} or
         * any of its parents.
         *
         * For example, changing a {@link android.widget.TextView} will apply both
         * {@link TextViewStyle} and {@link ViewStyle}.
         */
        public Applier<T> apply(@StyleRes int styleRes, Config config) {
            Styles.apply(view, null, styleRes, config);
            return this;
        }
    }

    /**
     * For fast look-ups a tree of {@link Style} constructors is used where each {@link Node} links
     * to the parent {@link Style}.
     *
     * For example:
     *
     *             null
     *              |
     *              |
     *           ViewStyle
     *           /      \
     *          /        \
     *         /          \
     * TextViewStyle   MyCustomViewStyle
     *
     * {@link #VIEW_CLASS_TO_NODE} provides the entry point into the tree depending on the
     * {@link View} that's being modified.
     */
    private static class Node {

        @Nullable final Node parentNode;
        final Method styleConstructor;

        private Node(Node parentNode, Method styleConstructor) {
            this.parentNode = parentNode;
            this.styleConstructor = styleConstructor;
        }
    }

    private static final String PACKAGE_NAME = Styles.class.getPackage().getName();
    private static final String STYLE_CLASS_NAME_SUFFIX = "Style";
    private static final Map<Class<?>, Node> VIEW_CLASS_TO_NODE = new HashMap<>();

    public static <T extends View> Applier<T> change(T view) {
        return new Applier<>(view);
    }

    /**
     * Applies all the {@link Style}s automatically detected base on the {@link View} hierarchy
     */
    private static <T extends View> void apply(T view, AttributeSet set, @StyleRes int styleRes, Config config) {
        Node node = findNodeForViewClass(view.getClass());
        while (node != null) {
            Method styleConstructor = node.styleConstructor;
            StyleUtils.<Style<? super T>>create(styleConstructor, set, styleRes, config).applyTo(view);
            node = node.parentNode;
        }
    }

    @Nullable
    private static Node findNodeForViewClass(Class<?> viewClass) {
        if (viewClass == Object.class) {
            return null;
        }

        Node node = VIEW_CLASS_TO_NODE.get(viewClass);
        if (node != null) {
            return node;
        }

        String className = viewClass.getName();
        if (className.startsWith("android.")) {
            // Look for framework View classes in this package
            className = PACKAGE_NAME + "." + viewClass.getSimpleName();
        }
        try {
            //noinspection unchecked
            Class<? extends Style<?>> styleClass = (Class<? extends Style<?>>) Class.forName(className + STYLE_CLASS_NAME_SUFFIX);
            Method styleConstructor = StyleUtils.getConstructor(styleClass);
            node = new Node(findNodeForViewClass(viewClass.getSuperclass()), styleConstructor);
        } catch (ClassNotFoundException e) {
            node = findNodeForViewClass(viewClass.getSuperclass());
        }

        VIEW_CLASS_TO_NODE.put(viewClass, node);
        return node;
    }
}
