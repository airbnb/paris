package com.airbnb.paris.test;

import android.content.Context;
import android.content.res.Resources;
import android.view.ViewStyleApplier;
import android.widget.TextViewStyleApplier;
import androidx.annotation.StyleRes;
import androidx.annotation.UiThread;
import com.airbnb.paris.StyleApplier;
import com.airbnb.paris.styles.Style;
import com.airbnb.paris.typed_array_wrappers.TypedArrayWrapper;
import com.airbnb.paris.utils.StyleBuilderFunction;
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
    if (a.hasValue(R.styleable.MyView_titleStyle)) {
      title().apply(a.getStyle(R.styleable.MyView_titleStyle));
    }
    if (a.hasValue(R.styleable.MyView_subtitleStyle)) {
      subtitle().apply(a.getStyle(R.styleable.MyView_subtitleStyle));
    }
    if (a.hasValue(R.styleable.MyView_dividerStyle)) {
      divider().apply(a.getStyle(R.styleable.MyView_dividerStyle));
    }
  }

  @Override
  protected void processAttributes(Style style, TypedArrayWrapper a) {
    Context context = getView().getContext();
    Resources res = context.getResources();
  }

  public StyleBuilder builder() {
    return new StyleBuilder(this);
  }

  public TextViewStyleApplier title() {
    TextViewStyleApplier subApplier = new TextViewStyleApplier(getProxy().title);
    subApplier.setDebugListener(getDebugListener());
    return subApplier;
  }

  public TextViewStyleApplier subtitle() {
    TextViewStyleApplier subApplier = new TextViewStyleApplier(getProxy().subtitle);
    subApplier.setDebugListener(getDebugListener());
    return subApplier;
  }

  public ViewStyleApplier divider() {
    ViewStyleApplier subApplier = new ViewStyleApplier(getProxy().divider);
    subApplier.setDebugListener(getDebugListener());
    return subApplier;
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

    public B titleStyle(@StyleRes int resId) {
      getBuilder().putStyle(R.styleable.MyView[R.styleable.MyView_titleStyle], resId);
      return (B) this;
    }

    public B titleStyle(Style style) {
      getBuilder().putStyle(R.styleable.MyView[R.styleable.MyView_titleStyle], style);
      return (B) this;
    }

    public B titleStyle(StyleBuilderFunction<TextViewStyleApplier.StyleBuilder> function) {
      TextViewStyleApplier.StyleBuilder subBuilder = new TextViewStyleApplier.StyleBuilder();
      function.invoke(subBuilder);
      getBuilder().putStyle(R.styleable.MyView[R.styleable.MyView_titleStyle], subBuilder.build());
      return (B) this;
    }

    public B subtitleStyle(@StyleRes int resId) {
      getBuilder().putStyle(R.styleable.MyView[R.styleable.MyView_subtitleStyle], resId);
      return (B) this;
    }

    public B subtitleStyle(Style style) {
      getBuilder().putStyle(R.styleable.MyView[R.styleable.MyView_subtitleStyle], style);
      return (B) this;
    }

    public B subtitleStyle(StyleBuilderFunction<TextViewStyleApplier.StyleBuilder> function) {
      TextViewStyleApplier.StyleBuilder subBuilder = new TextViewStyleApplier.StyleBuilder();
      function.invoke(subBuilder);
      getBuilder().putStyle(R.styleable.MyView[R.styleable.MyView_subtitleStyle], subBuilder.build());
      return (B) this;
    }

    public B dividerStyle(@StyleRes int resId) {
      getBuilder().putStyle(R.styleable.MyView[R.styleable.MyView_dividerStyle], resId);
      return (B) this;
    }

    public B dividerStyle(Style style) {
      getBuilder().putStyle(R.styleable.MyView[R.styleable.MyView_dividerStyle], style);
      return (B) this;
    }

    public B dividerStyle(StyleBuilderFunction<ViewStyleApplier.StyleBuilder> function) {
      ViewStyleApplier.StyleBuilder subBuilder = new ViewStyleApplier.StyleBuilder();
      function.invoke(subBuilder);
      getBuilder().putStyle(R.styleable.MyView[R.styleable.MyView_dividerStyle], subBuilder.build());
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
