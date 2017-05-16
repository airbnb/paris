package com.airbnb.paris;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.content.res.AppCompatResources;
import android.util.AttributeSet;
import android.view.View;

import com.airbnb.paris.Style.Config;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class StyleUtils {

    public static int NOT_SET = -10;

    /**
     * Unfortunately Android doesn't support reading @null resources from a style resource like it
     * does from an AttributeSet so this trickery is required
     */
    private static final Set<Integer> NULL_RESOURCE_IDS = new HashSet<>(Arrays.asList(R.anim.null_, R.color.null_));

    private static final Map<Class<?>, Method> STYLE_CLASS_TO_METHOD = new LinkedHashMap<>();

    public static int ifSetElse(int value, int ifNotSet) {
        return value != NOT_SET ? value : ifNotSet;
    }

    public static boolean isAnySet(int... values) {
        for (int value : values) {
            if (value != NOT_SET) {
                return true;
            }
        }
        return false;
    }

    /**
     * <p>Returns a TypedArray holding the attribute values in {@code set} and/or {@code styleRes} that are listed in attrs. If both {@code set} and
     * {@code styleRes} are specified then {@code set} attribute values take precedence over {@code styleRes}'s.</p>
     *
     * <p>In other words, this method behaves similarly to
     * {@link android.content.res.Resources.Theme#obtainStyledAttributes(AttributeSet, int[], int, int)}. It differs in that it allows for
     * {@code set} to be {@code null}.</p>
     */
    @Nullable
    public static TypedArray obtainStyledAttributes(Context context, @Nullable AttributeSet set, @StyleRes int styleRes, int[] attrs) {
        if (set != null) {
            return context.obtainStyledAttributes(set, attrs, 0, styleRes);
        } else if (styleRes != 0) {
            return context.obtainStyledAttributes(styleRes, attrs);
        } else {
            return null;
        }
    }

    public static void setPadding(View view, int px) {
        view.setPadding(px, px, px, px);
    }

    public static void setPaddingBottom(View view, int px) {
        view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), px);
    }

    public static void setPaddingLeft(View view, int px) {
        view.setPadding(px, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
    }

    public static void setPaddingRight(View view, int px) {
        view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), px, view.getPaddingBottom());
    }

    public static void setPaddingTop(View view, int px) {
        view.setPadding(view.getPaddingLeft(), px, view.getPaddingRight(), view.getPaddingBottom());
    }

    public static Drawable getDrawable(Context context, TypedArray a, int index) {
        return isNull(a, index) ? null : getDrawableCompat(context, a, index);
    }

    /**
     * Use this to load a vector drawable from a TypedArray in a backwards compatible fashion
     */
    @Nullable
    static Drawable getDrawableCompat(Context context, TypedArray array, int index) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return array.getDrawable(index);
        } else {
            int resourceId = array.getResourceId(index, -1);
            if (resourceId != -1) {
                return AppCompatResources.getDrawable(context, resourceId);
            } else {
                return null;
            }
        }
    }

    public static int getResourceId(TypedArray a, int index, int defValue) {
        return isNull(a, index) ? 0 : a.getResourceId(index, 0);
    }

    private static boolean isNull(TypedArray a, int index) {
        return NULL_RESOURCE_IDS.contains(a.getResourceId(index, 0));
    }

    static <T extends Style<?>> T create(Class<T> styleClass, AttributeSet set, @StyleRes int styleRes, Config config) {
        return create(getConstructor(styleClass), set, styleRes, config);
    }

    static <T extends Style<?>> T create(Method method, AttributeSet set, @StyleRes int styleRes, Config config) {
        if (method == null) {
            //noinspection unchecked
            return (T) new Style() {

                @Override
                public void applyTo(View view) {

                }
            };
        }

        try {
            //noinspection unchecked
            return (T) method.invoke(null, set, styleRes, config);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to invoke " + method, e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            if (cause instanceof Error) {
                throw (Error) cause;
            }
            throw new RuntimeException("Unable to create binding instance.", cause);
        }
    }

    @Nullable
    static <T extends Style<?>> Method getConstructor(Class<T> styleClass) {
        Method method = STYLE_CLASS_TO_METHOD.get(styleClass);
        if (method != null) {
            return method;
        }
        try {
            method = styleClass.getMethod("from", AttributeSet.class, Integer.TYPE, Config.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unable to find static constructor for " + styleClass.getSimpleName(), e);
        }
        STYLE_CLASS_TO_METHOD.put(styleClass, method);
        return method;
    }
}
