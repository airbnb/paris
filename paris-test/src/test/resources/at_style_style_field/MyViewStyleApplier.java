package com.airbnb.paris.test;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.UiThread;
import android.view.ViewStyleApplier;
import com.airbnb.paris.StyleApplier;
import com.airbnb.paris.StyleApplierUtils;
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
    return R.styleable.MyView;
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
  }

  public StyleBuilder builder() {
    return new StyleBuilder(this);
  }

  /**
   * @see MyView#myStyle */
  public void applyMy() {
    apply(MyView.myStyle);
  }

  /**
   * Empty style */
  public void applyDefault() {
  }

  /**
   * For debugging */
  public static void assertStylesContainSameAttributes(Context context) {
    MyView MyView = new MyView(context);
    StyleApplierUtils.Companion.assertSameAttributes(new MyViewStyleApplier(MyView), new StyleBuilder().addMy().build(), new StyleBuilder().addDefault().build());
  }

  public abstract static class BaseStyleBuilder<B extends BaseStyleBuilder<B, A>, A extends StyleApplier<?, ?>> extends ViewStyleApplier.BaseStyleBuilder<B, A> {
    public BaseStyleBuilder(A applier) {
      super(applier);
    }

    public BaseStyleBuilder() {
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
     * @see MyView#myStyle */
    public StyleBuilder addMy() {
      add(MyView.myStyle);
      return this;
    }

    /**
     * Empty style */
    public StyleBuilder addDefault() {
      return this;
    }
  }
}