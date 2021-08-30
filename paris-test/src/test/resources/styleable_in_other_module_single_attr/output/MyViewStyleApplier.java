package com.airbnb.paris.test;

import android.content.Context;
import android.content.res.Resources;
import android.view.ViewStyleApplier;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.UiThread;
import com.airbnb.paris.StyleApplier;
import com.airbnb.paris.styles.Style;
import com.airbnb.paris.test.lib.R;
import com.airbnb.paris.typed_array_wrappers.TypedArrayWrapper;
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
    return R.styleable.MyLibView;
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
    if (a.hasValue(R.styleable.MyLibView_title)) {
      getProxy().setTitle(a.getString(R.styleable.MyLibView_title));
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
     * @see MyView#setTitle(String)
     */
    public B title(@Nullable String value) {
      getBuilder().put(R.styleable.MyLibView[R.styleable.MyLibView_title], value);
      return (B) this;
    }

    /**
     * @see MyView#setTitle(String)
     */
    public B titleRes(@StringRes int resId) {
      getBuilder().putRes(R.styleable.MyLibView[R.styleable.MyLibView_title], resId);
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
