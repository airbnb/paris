package com.airbnb.paris.test;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.AnyRes;
import android.support.annotation.ArrayRes;
import android.support.annotation.BoolRes;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.FractionRes;
import android.support.annotation.IntegerRes;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import com.airbnb.paris.Style;
import com.airbnb.paris.StyleApplier;
import com.airbnb.paris.TypedArrayWrapper;
import com.airbnb.paris.proxies.ViewProxyStyleApplier;
import com.airbnb.paris.utils.ResourcesExtensionsKt;
import java.lang.CharSequence;
import java.lang.Override;
import java.lang.String;

@UiThread
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
    return new int[] {R.styleable.Formats_formatBoolean,R.styleable.Formats_formatColor,R.styleable.Formats_formatDimension,R.styleable.Formats_formatEnum,R.styleable.Formats_formatFlag,R.styleable.Formats_formatFloat,R.styleable.Formats_formatFraction,R.styleable.Formats_formatInteger,R.styleable.Formats_formatReference,R.styleable.Formats_formatReference2,R.styleable.Formats_formatReference3,R.styleable.Formats_formatString,R.styleable.Formats_formatString2,};
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
      getProxy().formatDimension_px(a.getDimensionPixelSize(R.styleable.Formats_formatDimension, -1));
    }
    else {
      getProxy().formatDimension_px(res.getDimensionPixelSize(R.dimen.format_dimension));
    }
    if (a.hasValue(R.styleable.Formats_formatDimension)) {
      getProxy().formatDimension_LayoutDimension(a.getLayoutDimension(R.styleable.Formats_formatDimension, -1));
    }
    else {
      getProxy().formatDimension_LayoutDimension(ResourcesExtensionsKt.getLayoutDimension(res, R.dimen.format_dimension));
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
    if (a.hasValue(R.styleable.Formats_formatFloat)) {
      getProxy().formatFloat(a.getFloat(R.styleable.Formats_formatFloat, -1f));
    }
    else {
      getProxy().formatFloat(ResourcesExtensionsKt.getFloat(res, R.dimen.format_float));
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
      getProxy().formatReference_res(a.getResourceId(R.styleable.Formats_formatReference, -1));
    }
    else {
      getProxy().formatReference_res(R.bool.active);
    }
    if (a.hasValue(R.styleable.Formats_formatReference2)) {
      getProxy().formatReference_ColorStateList(a.getColorStateList(R.styleable.Formats_formatReference2));
    }
    else {
      getProxy().formatReference_ColorStateList(res.getColorStateList(R.color.format_color_state_list));
    }
    if (a.hasValue(R.styleable.Formats_formatReference3)) {
      getProxy().formatReference_Drawable(a.getDrawable(R.styleable.Formats_formatReference3));
    }
    else {
      getProxy().formatReference_Drawable(res.getDrawable(R.drawable.format_drawable));
    }
    if (a.hasValue(R.styleable.Formats_formatString)) {
      getProxy().formatString_CharSequence(a.getText(R.styleable.Formats_formatString));
    }
    else {
      getProxy().formatString_CharSequence(res.getText(R.string.format_char_sequence));
    }
    if (a.hasValue(R.styleable.Formats_formatString2)) {
      getProxy().formatString_String(a.getString(R.styleable.Formats_formatString2));
    }
    else {
      getProxy().formatString_String(res.getString(R.string.format_string));
    }
  }

  public StyleBuilder builder() {
    return new StyleBuilder(this);
  }

  public void applyDefault() {
  }

  public abstract static class BaseStyleBuilder<B extends BaseStyleBuilder<B, A>, A extends StyleApplier<?, ?>> extends ViewProxyStyleApplier.BaseStyleBuilder<B, A> {
    public BaseStyleBuilder(A applier) {
      super(applier);
    }

    public BaseStyleBuilder() {
    }

    public B formatBoolean(boolean value) {
      getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatBoolean], value);
      return (B) this;
    }

    public B formatBoolean(@BoolRes int resId) {
      getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatBoolean], resId);
      return (B) this;
    }

    public B formatColor(int value) {
      getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatColor], value);
      return (B) this;
    }

    public B formatColorRes(@ColorRes int resId) {
      getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatColor], resId);
      return (B) this;
    }

    public B formatDimension(int value) {
      getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatDimension], value);
      return (B) this;
    }

    public B formatDimensionRes(@DimenRes int resId) {
      getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatDimension], resId);
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

    public B formatEnumRes(@IntegerRes int resId) {
      getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatEnum], resId);
      return (B) this;
    }

    public B formatFlag(int value) {
      getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatFlag], value);
      return (B) this;
    }

    public B formatFlagRes(@IntegerRes int resId) {
      getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatFlag], resId);
      return (B) this;
    }

    public B formatFloat(float value) {
      getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatFloat], value);
      return (B) this;
    }

    public B formatFloat(@AnyRes int resId) {
      getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatFloat], resId);
      return (B) this;
    }

    public B formatFraction(float value) {
      getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatFraction], value);
      return (B) this;
    }

    public B formatFraction(@FractionRes int resId) {
      getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatFraction], resId);
      return (B) this;
    }

    public B formatInteger(int value) {
      getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatInteger], value);
      return (B) this;
    }

    public B formatIntegerRes(@IntegerRes int resId) {
      getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatInteger], resId);
      return (B) this;
    }

    public B formatReference(CharSequence[] value) {
      getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatReference], value);
      return (B) this;
    }

    public B formatReference(@ArrayRes int resId) {
      getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatReference], resId);
      return (B) this;
    }

    public B formatReference2(ColorStateList value) {
      getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatReference2], value);
      return (B) this;
    }

    public B formatReference2(@ColorRes int resId) {
      getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatReference2], resId);
      return (B) this;
    }

    public B formatReference3(Drawable value) {
      getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatReference3], value);
      return (B) this;
    }

    public B formatReference3(@DrawableRes int resId) {
      getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatReference3], resId);
      return (B) this;
    }

    public B formatString(CharSequence value) {
      getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatString], value);
      return (B) this;
    }

    public B formatString(@StringRes int resId) {
      getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatString], resId);
      return (B) this;
    }

    public B formatString2(String value) {
      getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatString2], value);
      return (B) this;
    }

    public B formatString2(@StringRes int resId) {
      getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatString2], resId);
      return (B) this;
    }

    public B applyTo(MyView view) {
      new MyViewStyleApplier(view).apply(build());
      return (B) this;
    }
  }

  @UiThread
  public static final class StyleBuilder extends BaseStyleBuilder<StyleBuilder, MyViewStyleApplier> {
    public StyleBuilder(MyViewStyleApplier applier) {
      super(applier);
    }

    public StyleBuilder() {
    }

    public StyleBuilder addDefault() {
      return this;
    }
  }
}