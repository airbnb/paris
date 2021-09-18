package com.airbnb.paris.test;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.view.ViewStyleApplier;
import androidx.annotation.BoolRes;
import androidx.annotation.RequiresApi;
import androidx.annotation.UiThread;
import com.airbnb.paris.StyleApplier;
import com.airbnb.paris.styles.Style;
import com.airbnb.paris.typed_array_wrappers.TypedArrayWrapper;
import java.lang.Override;

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
    return new int[] {R.styleable.Formats_formatBoolean,};
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
    if (Build.VERSION.SDK_INT >= 21) {
      if (a.hasValue(R.styleable.Formats_formatBoolean)) {
        getProxy().formatBoolean(a.getBoolean(R.styleable.Formats_formatBoolean));
      }
      else if (style.getShouldApplyDefaults()) {
        getProxy().formatBoolean(res.getBoolean(R.bool.format_boolean));
      }
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
    @RequiresApi(21)
    public B formatBoolean(boolean value) {
      getBuilder().put(R.styleable.Formats[R.styleable.Formats_formatBoolean], value);
      return (B) this;
    }

    /**
     * @see MyView#formatBoolean(boolean)
     */
    @RequiresApi(21)
    public B formatBooleanRes(@BoolRes int resId) {
      getBuilder().putRes(R.styleable.Formats[R.styleable.Formats_formatBoolean], resId);
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
