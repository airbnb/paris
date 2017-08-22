package com.airbnb.paris.test;

import android.content.res.Resources;
import com.airbnb.paris.Style;
import com.airbnb.paris.StyleApplier;
import com.airbnb.paris.TypedArrayWrapper;
import com.airbnb.paris.proxy.ViewProxyStyleApplier;
import java.lang.Override;

public final class MyViewStyleApplier extends StyleApplier<MyViewStyleApplier, MyView, MyView> {
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
        return new int[] {R.styleable.Formats_formatBoolean,R.styleable.Formats_formatColor,R.styleable.Formats_formatDimension,R.styleable.Formats_formatEnum,R.styleable.Formats_formatFlag,R.styleable.Formats_formatFraction,R.styleable.Formats_formatInteger,R.styleable.Formats_formatReference,R.styleable.Formats_formatReference,R.styleable.Formats_formatReference,R.styleable.Formats_formatReference,R.styleable.Formats_formatString,R.styleable.Formats_formatString,};
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
        else {
            getProxy().formatBoolean(res.getBoolean(R.bool.format_boolean));
        }
        if (a.hasValue(R.styleable.Formats_formatColor)) {
            getProxy().formatColor(a.getColor(R.styleable.Formats_formatColor, -1));
        }
        else {
            getProxy().formatColor(res.getColor(R.color.format_color));
        }
        if (a.hasValue(R.styleable.Formats_formatDimension)) {
            getProxy().formatDimension(a.getDimensionPixelSize(R.styleable.Formats_formatDimension, -1));
        }
        else {
            getProxy().formatDimension(res.getDimensionPixelSize(R.dimen.format_dimension));
        }
        if (a.hasValue(R.styleable.Formats_formatEnum)) {
            getProxy().formatEnum(a.getInt(R.styleable.Formats_formatEnum, -1));
        }
        else {
            getProxy().formatEnum(res.getInteger(R.integer.format_enum));
        }
        if (a.hasValue(R.styleable.Formats_formatFlag)) {
            getProxy().formatFlag(a.getInt(R.styleable.Formats_formatFlag, -1));
        }
        else {
            getProxy().formatFlag(res.getInteger(R.integer.format_flag));
        }
        if (a.hasValue(R.styleable.Formats_formatFraction)) {
            getProxy().formatFraction(a.getFraction(R.styleable.Formats_formatFraction, 2, 3, -1f));
        }
        else {
            getProxy().formatFraction(res.getFraction(R.fraction.format_fraction, 2, 3));
        }
        if (a.hasValue(R.styleable.Formats_formatInteger)) {
            getProxy().formatInteger(a.getInt(R.styleable.Formats_formatInteger, -1));
        }
        else {
            getProxy().formatInteger(res.getInteger(R.integer.format_integer));
        }
        if (a.hasValue(R.styleable.Formats_formatReference)) {
            getProxy().formatReference_CharSequenceArray(a.getTextArray(R.styleable.Formats_formatReference));
        }
        else {
            getProxy().formatReference_CharSequenceArray(res.getTextArray(R.array.format_string_array));
        }
        if (a.hasValue(R.styleable.Formats_formatReference)) {
            getProxy().formatReference_ColorStateList(a.getColorStateList(R.styleable.Formats_formatReference));
        }
        else {
            getProxy().formatReference_ColorStateList(res.getColorStateList(R.color.format_color_state_list));
        }
        if (a.hasValue(R.styleable.Formats_formatReference)) {
            getProxy().formatReference_Drawable(a.getDrawable(R.styleable.Formats_formatReference));
        }
        else {
            getProxy().formatReference_Drawable(res.getDrawable(R.drawable.format_drawable));
        }
        if (a.hasValue(R.styleable.Formats_formatReference)) {
            getProxy().formatReference_int(a.getResourceId(R.styleable.Formats_formatReference, -1));
        }
        else {
            getProxy().formatReference_int(R.bool.active);
        }
        if (a.hasValue(R.styleable.Formats_formatString)) {
            getProxy().formatString_CharSequence(a.getText(R.styleable.Formats_formatString));
        }
        else {
            getProxy().formatString_CharSequence(res.getText(R.string.format_char_sequence));
        }
        if (a.hasValue(R.styleable.Formats_formatString)) {
            getProxy().formatString_String(a.getString(R.styleable.Formats_formatString));
        }
        else {
            getProxy().formatString_String(res.getString(R.string.format_string));
        }
    }

    public StyleBuilder builder() {
        return new StyleBuilder(this);
    }

    public abstract static class BaseStyleBuilder<B extends BaseStyleBuilder<B, A>, A extends StyleApplier<?, ?, ?>> extends ViewProxyStyleApplier.BaseStyleBuilder<B, A> {
        public BaseStyleBuilder(A applier) {
            super(applier);
        }

        public BaseStyleBuilder() {
        }

        public B formatBoolean(int res) {
            getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatBoolean], res);
            return (B) this;
        }

        public B formatColor(int res) {
            getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatColor], res);
            return (B) this;
        }

        public B formatDimension(int res) {
            getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatDimension], res);
            return (B) this;
        }

        public B formatEnum(int res) {
            getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatEnum], res);
            return (B) this;
        }

        public B formatFlag(int res) {
            getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatFlag], res);
            return (B) this;
        }

        public B formatFraction(int res) {
            getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatFraction], res);
            return (B) this;
        }

        public B formatInteger(int res) {
            getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatInteger], res);
            return (B) this;
        }

        public B formatReference(int res) {
            getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatReference], res);
            return (B) this;
        }

        public B formatString(int res) {
            getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatString], res);
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