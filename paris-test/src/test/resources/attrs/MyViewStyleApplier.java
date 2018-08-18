package com.airbnb.paris.test;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.AnyRes;
import android.support.annotation.ArrayRes;
import android.support.annotation.BoolRes;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.Dimension;
import android.support.annotation.DrawableRes;
import android.support.annotation.FontRes;
import android.support.annotation.FractionRes;
import android.support.annotation.IntegerRes;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.view.ViewStyleApplier;
import com.airbnb.paris.StyleApplier;
import com.airbnb.paris.styles.Style;
import com.airbnb.paris.typed_array_wrappers.TypedArrayWrapper;
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
    ViewStyleApplier applier = new ViewStyleApplier(getView());
    applier.setDebugListener(getDebugListener());
    applier.apply(style);
  }

  @Override
  protected int[] attributes() {
    return R.styleable.Formats;
  }

  @Override
  protected void processStyleableFields(Style style, TypedArrayWrapper a) {
    Context context = getView().getContext();
    Resources res = context.getResources();
  }

  @Override
  protected void processAttributes(Style style, TypedArrayWrapper a) {
    Context context = getView().getContext()
    Resources res = context.getResources();
    if (a.hasValue(R.styleable.Formats_formatBoolean)) {
      getProxy().formatBoolean(a.getBoolean(R.styleable.Formats_formatBoolean));
    }
    if (a.hasValue(R.styleable.Formats_formatBoolean)) {
      getProxy().formatBoolean(a.getResourceId(R.styleable.Formats_formatBoolean));
    }
    if (a.hasValue(R.styleable.Formats_formatBoolean2)) {
      getProxy().formatBoolean2(a.getResourceId(R.styleable.Formats_formatBoolean2));
    }
    if (a.hasValue(R.styleable.Formats_formatColor)) {
      getProxy().formatColor(a.getColor(R.styleable.Formats_formatColor));
    }
    if (a.hasValue(R.styleable.Formats_formatDimension)) {
      getProxy().formatDimension_res(a.getResourceId(R.styleable.Formats_formatDimension));
    }
    if (a.hasValue(R.styleable.Formats_formatDimension)) {
      getProxy().formatDimension_px(a.getDimensionPixelSize(R.styleable.Formats_formatDimension));
    }
    if (a.hasValue(R.styleable.Formats_formatDimension)) {
      getProxy().formatDimension_LayoutDimension(a.getLayoutDimension(R.styleable.Formats_formatDimension));
    }
    if (a.hasValue(R.styleable.Formats_formatEnum)) {
      getProxy().formatEnum(a.getInt(R.styleable.Formats_formatEnum));
    }
    if (a.hasValue(R.styleable.Formats_formatFlag)) {
      getProxy().formatFlag(a.getInt(R.styleable.Formats_formatFlag));
    }
    if (a.hasValue(R.styleable.Formats_formatFloat)) {
      getProxy().formatFloat(a.getFloat(R.styleable.Formats_formatFloat));
    }
    if (a.hasValue(R.styleable.Formats_formatFraction)) {
      getProxy().formatFraction(a.getFraction(R.styleable.Formats_formatFraction, 2, 3));
    }
    if (a.hasValue(R.styleable.Formats_formatInteger)) {
      getProxy().formatInteger(a.getInt(R.styleable.Formats_formatInteger));
    }
    if (a.hasValue(R.styleable.Formats_formatReference)) {
      getProxy().formatReference_CharSequenceArray(a.getTextArray(R.styleable.Formats_formatReference));
    }
    if (a.hasValue(R.styleable.Formats_formatReference)) {
      getProxy().formatReference_res(a.getResourceId(R.styleable.Formats_formatReference));
    }
    if (a.hasValue(R.styleable.Formats_formatReference2)) {
      getProxy().formatReference2_ColorStateList(a.getColorStateList(R.styleable.Formats_formatReference2));
    }
    if (a.hasValue(R.styleable.Formats_formatReference3)) {
      getProxy().formatReference3_Drawable(a.getDrawable(R.styleable.Formats_formatReference3));
    }
    if (a.hasValue(R.styleable.Formats_formatReference4)) {
      getProxy().formatReference4_Font(a.getFont(R.styleable.Formats_formatReference4));
    }
    if (a.hasValue(R.styleable.Formats_formatString)) {
      getProxy().formatString_CharSequence(a.getText(R.styleable.Formats_formatString));
    }
    if (a.hasValue(R.styleable.Formats_formatString2)) {
      getProxy().formatString2_String(a.getString(R.styleable.Formats_formatString2));
    }
  }

  public StyleBuilder builder() {
    return new StyleBuilder(this);
  }

  /**
   * Empty style */
  public void applyDefault() {
  }

  /**
   * For debugging */
  public static void assertStylesContainSameAttributes(Context context) {
  }

  public abstract static class BaseStyleBuilder<B extends BaseStyleBuilder<B, A>, A extends StyleApplier<?, ?>> extends ViewStyleApplier.BaseStyleBuilder<B, A> {
    public BaseStyleBuilder(A applier) {
      super(applier);
    }

    public BaseStyleBuilder() {
    }

    /**
     * @see MyView#formatBoolean(boolean) */
    public B formatBoolean(boolean value) {
      getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatBoolean], value);
      return (B) this;
    }

    /**
     * @see MyView#formatBoolean(boolean) */
    public B formatBooleanRes(@BoolRes int resId) {
      getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatBoolean], resId);
      return (B) this;
    }

    /**
     * @see MyView#formatBoolean2(int) */
    public B formatBoolean2Res(@AnyRes int resId) {
      getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatBoolean2], resId);
      return (B) this;
    }

    /**
     * @see MyView#formatColor(int) */
    public B formatColor(@ColorInt int value) {
      getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatColor], value);
      return (B) this;
    }

    /**
     * @see MyView#formatColor(int) */
    public B formatColorRes(@ColorRes int resId) {
      getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatColor], resId);
      return (B) this;
    }

    /**
     * @see MyView#formatDimension_px(int) */
    public B formatDimension(@Px int value) {
      getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatDimension], value);
      return (B) this;
    }

    /**
     * @see MyView#formatDimension_px(int) */
    public B formatDimensionRes(@DimenRes int resId) {
      getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatDimension], resId);
      return (B) this;
    }

    /**
     * @see MyView#formatDimension_px(int) */
    public B formatDimensionDp(@Dimension(unit = Dimension.DP) int value) {
      getBuilder().putDp(R.styleable.Formats[R.styleable.Formats_formatDimension], value);
      return (B) this;
    }

    /**
     * @see MyView#formatEnum(int) */
    public B formatEnum(int value) {
      getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatEnum], value);
      return (B) this;
    }

    /**
     * @see MyView#formatEnum(int) */
    public B formatEnumRes(@IntegerRes int resId) {
      getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatEnum], resId);
      return (B) this;
    }

    /**
     * @see MyView#formatFlag(int) */
    public B formatFlag(int value) {
      getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatFlag], value);
      return (B) this;
    }

    /**
     * @see MyView#formatFlag(int) */
    public B formatFlagRes(@IntegerRes int resId) {
      getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatFlag], resId);
      return (B) this;
    }

    /**
     * @see MyView#formatFloat(float) */
    public B formatFloat(float value) {
      getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatFloat], value);
      return (B) this;
    }

    /**
     * @see MyView#formatFloat(float) */
    public B formatFloatRes(@AnyRes int resId) {
      getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatFloat], resId);
      return (B) this;
    }

    /**
     * @see MyView#formatFraction(float) */
    public B formatFraction(float value) {
      getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatFraction], value);
      return (B) this;
    }

    /**
     * @see MyView#formatFraction(float) */
    public B formatFractionRes(@FractionRes int resId) {
      getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatFraction], resId);
      return (B) this;
    }

    /**
     * @see MyView#formatInteger(int) */
    public B formatInteger(int value) {
      getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatInteger], value);
      return (B) this;
    }

    /**
     * @see MyView#formatInteger(int) */
    public B formatIntegerRes(@IntegerRes int resId) {
      getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatInteger], resId);
      return (B) this;
    }

    /**
     * @see MyView#formatReference_CharSequenceArray(CharSequence[]) */
    public B formatReference(@Nullable CharSequence[] value) {
      getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatReference], value);
      return (B) this;
    }

    /**
     * @see MyView#formatReference_CharSequenceArray(CharSequence[]) */
    public B formatReferenceRes(@ArrayRes int resId) {
      getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatReference], resId);
      return (B) this;
    }

    /**
     * @see MyView#formatReference2_ColorStateList(ColorStateList) */
    public B formatReference2(@Nullable ColorStateList value) {
      getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatReference2], value);
      return (B) this;
    }

    /**
     * @see MyView#formatReference2_ColorStateList(ColorStateList) */
    public B formatReference2Res(@ColorRes int resId) {
      getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatReference2], resId);
      return (B) this;
    }

    /**
     * @see MyView#formatReference2_ColorStateList(ColorStateList) */
    public B formatReference2(@ColorInt int color) {
      getBuilder().putColor(R.styleable.Formats[R.styleable.Formats_formatReference2], color);
      return (B) this;
    }

    /**
     * @see MyView#formatReference3_Drawable(Drawable) */
    public B formatReference3(@Nullable Drawable value) {
      getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatReference3], value);
      return (B) this;
    }

    /**
     * @see MyView#formatReference3_Drawable(Drawable) */
    public B formatReference3Res(@DrawableRes int resId) {
      getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatReference3], resId);
      return (B) this;
    }

    /**
     * @see MyView#formatReference4_Font(Typeface) */
    public B formatReference4(@Nullable Typeface value) {
      getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatReference4], value);
      return (B) this;
    }

    /**
     * @see MyView#formatReference4_Font(Drawable) */
    public B formatReference4Res(@FontRes int resId) {
      getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatReference4], resId);
      return (B) this;
    }

    /**
     * @see MyView#formatString_CharSequence(CharSequence) */
    public B formatString(@Nullable CharSequence value) {
      getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatString], value);
      return (B) this;
    }

    /**
     * @see MyView#formatString_CharSequence(CharSequence) */
    public B formatStringRes(@StringRes int resId) {
      getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatString], resId);
      return (B) this;
    }

    /**
     * @see MyView#formatString2_String(String) */
    public B formatString2(@Nullable String value) {
      getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatString2], value);
      return (B) this;
    }

    /**
     * @see MyView#formatString2_String(String) */
    public B formatString2Res(@StringRes int resId) {
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

    /**
     * Empty style */
    public StyleBuilder addDefault() {
      return this;
    }
  }
}