package com.airbnb.paris.test;

import android.content.Context;
import android.view.ViewStyleApplier;
import androidx.annotation.UiThread;
import com.airbnb.paris.StyleApplier;
import com.airbnb.paris.StyleApplierUtils;
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
   * @see MyView#RED_STYLE
   */
  public void applyRed() {
    apply(MyView.RED_STYLE);
  }

  /**
   * @see MyView#greenStyle
   */
  public void applyGreen() {
    apply(MyView.greenStyle);
  }

  /**
   * @see MyView#blue(StyleBuilder)
   */
  public void applyBlue() {
    StyleBuilder builder = new StyleBuilder();
    MyView.blue(builder);
    apply(builder.build());
  }

  /**
   * @see MyView#RED_STYLE
   */
  public void applyDefault() {
    apply(MyView.RED_STYLE);
  }

  /**
   * For debugging
   */
  public static void assertStylesContainSameAttributes(Context context) {
    MyView MyView = new MyView(context);
    StyleApplierUtils.Companion.assertSameAttributes(new MyViewStyleApplier(MyView), new StyleBuilder().addRed().build(), new StyleBuilder().addGreen().build(), new StyleBuilder().addBlue().build(), new StyleBuilder().addDefault().build());
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
     * @see MyView#RED_STYLE
     */
    public StyleBuilder addRed() {
      add(MyView.RED_STYLE);
      return this;
    }

    /**
     * @see MyView#greenStyle
     */
    public StyleBuilder addGreen() {
      add(MyView.greenStyle);
      return this;
    }

    /**
     * @see MyView#blue(StyleBuilder)
     */
    public StyleBuilder addBlue() {
      consumeProgrammaticStyleBuilder();
      debugName("Blue");
      MyView.blue(this);
      consumeProgrammaticStyleBuilder();
      return this;
    }

    /**
     * @see MyView#RED_STYLE
     */
    public StyleBuilder addDefault() {
      add(MyView.RED_STYLE);
      return this;
    }
  }
}
