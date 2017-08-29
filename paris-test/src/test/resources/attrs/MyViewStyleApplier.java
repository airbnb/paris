package com.airbnb.paris.test;

import android.content.res.Resources;
import android.support.annotation.AnyRes;
import com.airbnb.paris.Style;
import com.airbnb.paris.StyleApplier;
import com.airbnb.paris.TypedArrayWrapper;
import com.airbnb.paris.proxy.ViewProxyStyleApplier;
import java.lang.CharSequence;
import java.lang.Override;

public final class MyViewStyleApplier extends StyleApplier<MyView, MyView> {
    public MyViewStyleApplier(MyView view) {
        super(view);
    }

    @Override
    protected void applyParent(Style style) {
        new ViewProxyStyleApplier(getView()).apply(style);
    }

    @Override
    protected int[] attributes() {
        return R.styleable.Formats;
    }

    @Override
    public int[] attributesWithDefaultValue() {
        return new int[]{};
    }

    @Override
    protected void processStyleableFields(Style style, TypedArrayWrapper a) {
        Resources res = getView().getContext().getResources();
    }

    @Override
    protected void processAttributes(Style style, TypedArrayWrapper a) {
        Resources res = getView().getContext().getResources();
        if (a.hasValue(R.styleable.Formats_formatBoolean)) {
            getProxy().formatBoolean(a.getBoolean(R.styleable.Formats_formatBoolean, false));
        }
        if (a.hasValue(R.styleable.Formats_formatColor)) {
            getProxy().formatColor(a.getColor(R.styleable.Formats_formatColor, -1));
        }
        if (a.hasValue(R.styleable.Formats_formatDimension)) {
            getProxy().formatDimension(a.getDimensionPixelSize(R.styleable.Formats_formatDimension, -1));
        }
        if (a.hasValue(R.styleable.Formats_formatEnum)) {
            getProxy().formatEnum(a.getInt(R.styleable.Formats_formatEnum, -1));
        }
        if (a.hasValue(R.styleable.Formats_formatFlag)) {
            getProxy().formatFlag(a.getInt(R.styleable.Formats_formatFlag, -1));
        }
        if (a.hasValue(R.styleable.Formats_formatFloat)) {
            getProxy().formatFloat(a.getFloat(R.styleable.Formats_formatFloat, -1f));
        }
        if (a.hasValue(R.styleable.Formats_formatFraction)) {
            getProxy().formatFraction(a.getFraction(R.styleable.Formats_formatFraction, 2, 3, -1f));
        }
        if (a.hasValue(R.styleable.Formats_formatInteger)) {
            getProxy().formatInteger(a.getInt(R.styleable.Formats_formatInteger, -1));
        }
        if (a.hasValue(R.styleable.Formats_formatReference)) {
            getProxy().formatReference_CharSequenceArray(a.getTextArray(R.styleable.Formats_formatReference));
        }
        if (a.hasValue(R.styleable.Formats_formatReference)) {
            getProxy().formatReference_ColorStateList(a.getColorStateList(R.styleable.Formats_formatReference));
        }
        if (a.hasValue(R.styleable.Formats_formatReference)) {
            getProxy().formatReference_Drawable(a.getDrawable(R.styleable.Formats_formatReference));
        }
        if (a.hasValue(R.styleable.Formats_formatReference)) {
            getProxy().formatReference_int(a.getResourceId(R.styleable.Formats_formatReference, -1));
        }
        if (a.hasValue(R.styleable.Formats_formatString)) {
            getProxy().formatString_CharSequence(a.getText(R.styleable.Formats_formatString));
        }
        if (a.hasValue(R.styleable.Formats_formatString)) {
            getProxy().formatString_String(a.getString(R.styleable.Formats_formatString));
        }
    }

    public StyleBuilder builder() {
        return new StyleBuilder(this);
    }

    public abstract static class BaseStyleBuilder<B extends BaseStyleBuilder<B, A>, A extends StyleApplier<?, ?>> extends ViewProxyStyleApplier.BaseStyleBuilder<B, A> {
        public BaseStyleBuilder(A applier) {
            super(applier);
        }

        public BaseStyleBuilder() {
        }

        public B formatBoolean(@AnyRes int resId) {
            getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatBoolean], resId);
            return (B) this;
        }

        public B formatColorRes(@AnyRes int resId) {
            getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatColor], resId);
            return (B) this;
        }

        public B formatDimensionRes(@AnyRes int resId) {
            getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatDimension], resId);
            return (B) this;
        }

        public B formatEnumRes(@AnyRes int resId) {
            getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatEnum], resId);
            return (B) this;
        }

        public B formatFlagRes(@AnyRes int resId) {
            getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatFlag], resId);
            return (B) this;
        }

        public B formatFloat(@AnyRes int resId) {
            getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatFloat], resId);
            return (B) this;
        }

        public B formatFraction(@AnyRes int resId) {
            getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatFraction], resId);
            return (B) this;
        }

        public B formatIntegerRes(@AnyRes int resId) {
            getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatInteger], resId);
            return (B) this;
        }

        public B formatReference(@AnyRes int resId) {
            getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatReference], resId);
            return (B) this;
        }

        public B formatString(@AnyRes int resId) {
            getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatString], resId);
            return (B) this;
        }

        public B formatBoolean(boolean value) {
            getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatBoolean], value);
            return (B) this;
        }

        public B formatColor(int value) {
            getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatColor], value);
            return (B) this;
        }

        public B formatDimension(int value) {
            getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatDimension], value);
            return (B) this;
        }

        public B formatDimensionDp(int value) {
            getBuilder().putDp(R.styleable.Formats[R.styleable.Formats_formatDimension], value);
            return (B) this;
        }

        public B formatEnum(int value) {
            getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatEnum], value);
            return (B) this;
        }

        public B formatFlag(int value) {
            getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatFlag], value);
            return (B) this;
        }

        public B formatFloat(float value) {
            getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatFloat], value);
            return (B) this;
        }

        public B formatFraction(float value) {
            getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatFraction], value);
            return (B) this;
        }

        public B formatInteger(int value) {
            getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatInteger], value);
            return (B) this;
        }

        public B formatReference(CharSequence[] value) {
            getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatReference], value);
            return (B) this;
        }

        public B formatString(CharSequence value) {
            getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatString], value);
            return (B) this;
        }

        public B applyTo(MyView view) {
            new MyViewStyleApplier(view).apply(build());
            return (B) this;
        }
    }

    public static final class StyleBuilder extends BaseStyleBuilder<StyleBuilder, MyViewStyleApplier> {
        public StyleBuilder(MyViewStyleApplier applier) {
            super(applier);
        }

        public StyleBuilder() {
        }
    }
}