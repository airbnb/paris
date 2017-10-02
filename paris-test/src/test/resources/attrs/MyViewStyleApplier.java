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
    ViewProxyStyleApplier applier = new ViewProxyStyleApplier(getView());
    applier.setDebugListener(getDebugListener());
    applier.apply(style);
  }

  @Override
  protected int[] attributes() {
    return R.styleable.Formats;
  }

  @Override
  protected void processStyleableFields(Style style, TypedArrayWrapper a) {
    Resources res = getView().getContext().getResources();
  }

  @Override
  protected void processAttributes(Style style, TypedArrayWrapper a) {
    Resources res = getView().getContext().getResources();
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

    public B formatBooleanRes(@BoolRes int resId) {
      getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatBoolean], resId);
      return (B) this;
    }

    public B formatBoolean2Res(@AnyRes int resId) {
      getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatBoolean2], resId);
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

    public B formatFloatRes(@AnyRes int resId) {
      getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatFloat], resId);
      return (B) this;
    }

    public B formatFraction(float value) {
      getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatFraction], value);
      return (B) this;
    }

    public B formatFractionRes(@FractionRes int resId) {
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

    public B formatReferenceRes(@ArrayRes int resId) {
      getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatReference], resId);
      return (B) this;
    }

    public B formatReference2(ColorStateList value) {
      getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatReference2], value);
      return (B) this;
    }

    public B formatReference2Res(@ColorRes int resId) {
      getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatReference2], resId);
      return (B) this;
    }

    public B formatReference3(Drawable value) {
      getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatReference3], value);
      return (B) this;
    }

    public B formatReference3Res(@DrawableRes int resId) {
      getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatReference3], resId);
      return (B) this;
    }

    public B formatString(CharSequence value) {
      getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatString], value);
      return (B) this;
    }

    public B formatStringRes(@StringRes int resId) {
      getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatString], resId);
      return (B) this;
    }

    public B formatString2(String value) {
      getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatString2], value);
      return (B) this;
    }

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

    public StyleBuilder addDefault() {
      return this;
    }
  }
}