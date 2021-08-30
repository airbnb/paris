package com.airbnb.paris.test;

import android.content.Context;
import android.view.ViewStyleApplier;
import androidx.annotation.UiThread;
import com.airbnb.paris.StyleApplier;
import com.airbnb.paris.styles.Style;
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
