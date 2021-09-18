package com.airbnb.paris.test;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.ViewStyleApplier;
import androidx.annotation.AnyRes;
import androidx.annotation.ArrayRes;
import androidx.annotation.BoolRes;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.Dimension;
import androidx.annotation.DrawableRes;
import androidx.annotation.FontRes;
import androidx.annotation.FractionRes;
import androidx.annotation.IntegerRes;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.annotation.StringRes;
import androidx.annotation.UiThread;
import com.airbnb.paris.StyleApplier;
import com.airbnb.paris.styles.Style;
import com.airbnb.paris.typed_array_wrappers.TypedArrayWrapper;
import com.airbnb.paris.utils.ContextExtensionsKt;
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
    ViewStyleApplier applier = new ViewStyleApplier(getView());
    applier.setDebugListener(getDebugListener());
    applier.apply(style);
  }

  @Override
  protected int[] attributes() {
    return R.styleable.Formats;
  }

  @Override
  public int[] attributesWithDefaultValue() {
    return new int[] {R.styleable.Formats_formatBoolean,R.styleable.Formats_formatColor,R.styleable.Formats_formatDimension,R.styleable.Formats_formatEnum,R.styleable.Formats_formatFlag,R.styleable.Formats_formatFloat,R.styleable.Formats_formatFraction,R.styleable.Formats_formatInteger,R.styleable.Formats_formatReference,R.styleable.Formats_formatReference2,R.styleable.Formats_formatReference3,R.styleable.Formats_formatReference4,R.styleable.Formats_formatString,R.styleable.Formats_formatString2,};
  }

  @Override
  protected void processStyleableFields(Style style, TypedArrayWrapper a) {
    Context context = getView().getContext();
    Resources res = context.getResources();
  }

  @Override
  protected void processAttributes(Style style, TypedArrayWrapper a) {
    Context context = getView().getContext();
    Resources res = context.getResources();
    if (a.hasValue(R.styleable.Formats_formatBoolean)) {
      getProxy().formatBoolean(a.getBoolean(R.styleable.Formats_formatBoolean));
    }
    else if (style.getShouldApplyDefaults()) {
      getProxy().formatBoolean(res.getBoolean(R.bool.format_boolean));
    }
    if (a.hasValue(R.styleable.Formats_formatColor)) {
      getProxy().formatColor(a.getColor(R.styleable.Formats_formatColor));
    }
    else if (style.getShouldApplyDefaults()) {
      getProxy().formatColor(res.getColor(R.color.format_color));
    }
    if (a.hasValue(R.styleable.Formats_formatDimension)) {
      getProxy().formatDimension_px(a.getDimensionPixelSize(R.styleable.Formats_formatDimension));
    }
    else if (style.getShouldApplyDefaults()) {
      getProxy().formatDimension_px(res.getDimensionPixelSize(R.dimen.format_dimension));
    }
    if (a.hasValue(R.styleable.Formats_formatDimension)) {
      getProxy().formatDimension_LayoutDimension(a.getLayoutDimension(R.styleable.Formats_formatDimension));
    }
    else if (style.getShouldApplyDefaults()) {
      getProxy().formatDimension_LayoutDimension(ResourcesExtensionsKt.getLayoutDimension(res, R.dimen.format_dimension));
    }
    if (a.hasValue(R.styleable.Formats_formatEnum)) {
      getProxy().formatEnum(a.getInt(R.styleable.Formats_formatEnum));
    }
    else if (style.getShouldApplyDefaults()) {
      getProxy().formatEnum(res.getInteger(R.integer.format_enum));
    }
    if (a.hasValue(R.styleable.Formats_formatFlag)) {
      getProxy().formatFlag(a.getInt(R.styleable.Formats_formatFlag));
    }
    else if (style.getShouldApplyDefaults()) {
      getProxy().formatFlag(res.getInteger(R.integer.format_flag));
    }
    if (a.hasValue(R.styleable.Formats_formatFloat)) {
      getProxy().formatFloat(a.getFloat(R.styleable.Formats_formatFloat));
    }
    else if (style.getShouldApplyDefaults()) {
      getProxy().formatFloat(ResourcesExtensionsKt.getFloat(res, R.dimen.format_float));
    }
    if (a.hasValue(R.styleable.Formats_formatFraction)) {
      getProxy().formatFraction(a.getFraction(R.styleable.Formats_formatFraction, 2, 3));
    }
    else if (style.getShouldApplyDefaults()) {
      getProxy().formatFraction(res.getFraction(R.fraction.format_fraction, 2, 3));
    }
    if (a.hasValue(R.styleable.Formats_formatInteger)) {
      getProxy().formatInteger(a.getInt(R.styleable.Formats_formatInteger));
    }
    else if (style.getShouldApplyDefaults()) {
      getProxy().formatInteger(res.getInteger(R.integer.format_integer));
    }
    if (a.hasValue(R.styleable.Formats_formatReference)) {
      getProxy().formatReference_CharSequenceArray(a.getTextArray(R.styleable.Formats_formatReference));
    }
    else if (style.getShouldApplyDefaults()) {
      getProxy().formatReference_CharSequenceArray(res.getTextArray(R.array.format_string_array));
    }
    if (a.hasValue(R.styleable.Formats_formatReference)) {
      getProxy().formatReference_res(a.getResourceId(R.styleable.Formats_formatReference));
    }
    else if (style.getShouldApplyDefaults()) {
      getProxy().formatReference_res(R.bool.active);
    }
    if (a.hasValue(R.styleable.Formats_formatReference2)) {
      getProxy().formatReference_ColorStateList(a.getColorStateList(R.styleable.Formats_formatReference2));
    }
    else if (style.getShouldApplyDefaults()) {
      getProxy().formatReference_ColorStateList(res.getColorStateList(R.color.format_color_state_list));
    }
    if (a.hasValue(R.styleable.Formats_formatReference3)) {
      getProxy().formatReference_Drawable(a.getDrawable(R.styleable.Formats_formatReference3));
    }
    else if (style.getShouldApplyDefaults()) {
      getProxy().formatReference_Drawable(res.getDrawable(R.drawable.format_drawable));
    }
    if (a.hasValue(R.styleable.Formats_formatReference4)) {
      getProxy().formatReference_Font(a.getFont(R.styleable.Formats_formatReference4));
    }
    else if (style.getShouldApplyDefaults()) {
      getProxy().formatReference_Font(ContextExtensionsKt.getFont(context, R.font.format_font));
    }
    if (a.hasValue(R.styleable.Formats_formatString)) {
      getProxy().formatString_CharSequence(a.getText(R.styleable.Formats_formatString));
    }
    else if (style.getShouldApplyDefaults()) {
      getProxy().formatString_CharSequence(res.getText(R.string.format_char_sequence));
    }
    if (a.hasValue(R.styleable.Formats_formatString2)) {
      getProxy().formatString_String(a.getString(R.styleable.Formats_formatString2));
    }
    else if (style.getShouldApplyDefaults()) {
      getProxy().formatString_String(res.getString(R.string.format_string));
    }
  }

  public StyleBuilder builder() {
    return new StyleBuilder(this);
  }

  /**
   * Empty style.
   */
  public void applyDefault() {
  }

  /**
   * For debugging
   */
  public static void assertStylesContainSameAttributes(Context context) {
  }

  public abstract static class BaseStyleBuilder<B extends BaseStyleBuilder<B, A>, A extends StyleApplier<?, ?>> extends ViewStyleApplier.BaseStyleBuilder<B, A> {
    public BaseStyleBuilder(A applier) {
      super(applier);
    }

    public BaseStyleBuilder() {
    }

    /**
     * @see MyView#formatBoolean(boolean)
     */
    public B formatBoolean(boolean value) {
      getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatBoolean], value);
      return (B) this;
    }

    /**
     * @see MyView#formatBoolean(boolean)
     */
    public B formatBooleanRes(@BoolRes int resId) {
      getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatBoolean], resId);
      return (B) this;
    }

    /**
     * @see MyView#formatColor(int)
     */
    public B formatColor(@ColorInt int value) {
      getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatColor], value);
      return (B) this;
    }

    /**
     * @see MyView#formatColor(int)
     */
    public B formatColorRes(@ColorRes int resId) {
      getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatColor], resId);
      return (B) this;
    }

    /**
     * @see MyView#formatDimension_px(int)
     */
    public B formatDimension(@Px int value) {
      getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatDimension], value);
      return (B) this;
    }

    /**
     * @see MyView#formatDimension_px(int)
     */
    public B formatDimensionRes(@DimenRes int resId) {
      getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatDimension], resId);
      return (B) this;
    }

    /**
     * @see MyView#formatDimension_px(int)
     */
    public B formatDimensionDp(@Dimension(unit = Dimension.DP) int value) {
      getBuilder().putDp(R.styleable.Formats[R.styleable.Formats_formatDimension], value);
      return (B) this;
    }

    /**
     * @see MyView#formatEnum(int)
     */
    public B formatEnum(int value) {
      getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatEnum], value);
      return (B) this;
    }

    /**
     * @see MyView#formatEnum(int)
     */
    public B formatEnumRes(@IntegerRes int resId) {
      getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatEnum], resId);
      return (B) this;
    }

    /**
     * @see MyView#formatFlag(int)
     */
    public B formatFlag(int value) {
      getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatFlag], value);
      return (B) this;
    }

    /**
     * @see MyView#formatFlag(int)
     */
    public B formatFlagRes(@IntegerRes int resId) {
      getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatFlag], resId);
      return (B) this;
    }

    /**
     * @see MyView#formatFloat(float)
     */
    public B formatFloat(float value) {
      getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatFloat], value);
      return (B) this;
    }

    /**
     * @see MyView#formatFloat(float)
     */
    public B formatFloatRes(@AnyRes int resId) {
      getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatFloat], resId);
      return (B) this;
    }

    /**
     * @see MyView#formatFraction(float)
     */
    public B formatFraction(float value) {
      getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatFraction], value);
      return (B) this;
    }

    /**
     * @see MyView#formatFraction(float)
     */
    public B formatFractionRes(@FractionRes int resId) {
      getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatFraction], resId);
      return (B) this;
    }

    /**
     * @see MyView#formatInteger(int)
     */
    public B formatInteger(int value) {
      getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatInteger], value);
      return (B) this;
    }

    /**
     * @see MyView#formatInteger(int)
     */
    public B formatIntegerRes(@IntegerRes int resId) {
      getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatInteger], resId);
      return (B) this;
    }

    /**
     * @see MyView#formatReference_CharSequenceArray(CharSequence[])
     */
    public B formatReference(@Nullable CharSequence[] value) {
      getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatReference], value);
      return (B) this;
    }

    /**
     * @see MyView#formatReference_CharSequenceArray(CharSequence[])
     */
    public B formatReferenceRes(@ArrayRes int resId) {
      getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatReference], resId);
      return (B) this;
    }

    /**
     * @see MyView#formatReference_ColorStateList(ColorStateList)
     */
    public B formatReference2(@Nullable ColorStateList value) {
      getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatReference2], value);
      return (B) this;
    }

    /**
     * @see MyView#formatReference_ColorStateList(ColorStateList)
     */
    public B formatReference2Res(@ColorRes int resId) {
      getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatReference2], resId);
      return (B) this;
    }

    /**
     * @see MyView#formatReference_ColorStateList(ColorStateList)
     */
    public B formatReference2(@ColorInt int color) {
      getBuilder().putColor(R.styleable.Formats[R.styleable.Formats_formatReference2], color);
      return (B) this;
    }

    /**
     * @see MyView#formatReference_Drawable(Drawable)
     */
    public B formatReference3(@Nullable Drawable value) {
      getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatReference3], value);
      return (B) this;
    }

    /**
     * @see MyView#formatReference_Drawable(Drawable)
     */
    public B formatReference3Res(@DrawableRes int resId) {
      getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatReference3], resId);
      return (B) this;
    }

    /**
     * @see MyView#formatReference_Font(Typeface)
     */
    public B formatReference4(@Nullable Typeface value) {
      getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatReference4], value);
      return (B) this;
    }

    /**
     * @see MyView#formatReference_Font(Typeface)
     */
    public B formatReference4Res(@FontRes int resId) {
      getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatReference4], resId);
      return (B) this;
    }

    /**
     * @see MyView#formatString_CharSequence(CharSequence)
     */
    public B formatString(@Nullable CharSequence value) {
      getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatString], value);
      return (B) this;
    }

    /**
     * @see MyView#formatString_CharSequence(CharSequence)
     */
    public B formatStringRes(@StringRes int resId) {
      getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatString], resId);
      return (B) this;
    }

    /**
     * @see MyView#formatString_String(String)
     */
    public B formatString2(@Nullable String value) {
      getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatString2], value);
      return (B) this;
    }

    /**
     * @see MyView#formatString_String(String)
     */
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
     * Empty style.
     */
    public StyleBuilder addDefault() {
      return this;
    }
  }
}
