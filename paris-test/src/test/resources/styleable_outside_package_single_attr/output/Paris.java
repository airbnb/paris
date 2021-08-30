package com.airbnb.paris.test;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupStyleApplier;
import android.view.ViewStyleApplier;
import android.widget.ImageView;
import android.widget.ImageViewStyleApplier;
import android.widget.TextView;
import android.widget.TextViewStyleApplier;
import com.airbnb.other.MyView;
import com.airbnb.other.MyViewStyleApplier;
import com.airbnb.paris.spannables.SpannableBuilder;

public final class Paris {
  public static ImageViewStyleApplier style(ImageView view) {
    return new ImageViewStyleApplier(view);
  }

  public static ImageViewStyleApplier.StyleBuilder styleBuilder(ImageView view) {
    return new ImageViewStyleApplier.StyleBuilder(new ImageViewStyleApplier(view));
  }

  public static MyOtherViewStyleApplier style(MyOtherView view) {
    return new MyOtherViewStyleApplier(view);
  }

  public static MyOtherViewStyleApplier.StyleBuilder styleBuilder(MyOtherView view) {
    return new MyOtherViewStyleApplier.StyleBuilder(new MyOtherViewStyleApplier(view));
  }

  public static MyViewStyleApplier style(MyView view) {
    return new MyViewStyleApplier(view);
  }

  public static MyViewStyleApplier.StyleBuilder styleBuilder(MyView view) {
    return new MyViewStyleApplier.StyleBuilder(new MyViewStyleApplier(view));
  }

  public static TextViewStyleApplier style(TextView view) {
    return new TextViewStyleApplier(view);
  }

  public static TextViewStyleApplier.StyleBuilder styleBuilder(TextView view) {
    return new TextViewStyleApplier.StyleBuilder(new TextViewStyleApplier(view));
  }

  public static ViewGroupStyleApplier style(ViewGroup view) {
    return new ViewGroupStyleApplier(view);
  }

  public static ViewGroupStyleApplier.StyleBuilder styleBuilder(ViewGroup view) {
    return new ViewGroupStyleApplier.StyleBuilder(new ViewGroupStyleApplier(view));
  }

  public static ViewStyleApplier style(View view) {
    return new ViewStyleApplier(view);
  }

  public static ViewStyleApplier.StyleBuilder styleBuilder(View view) {
    return new ViewStyleApplier.StyleBuilder(new ViewStyleApplier(view));
  }

  public static SpannableBuilder spannableBuilder() {
    return new SpannableBuilder();
  }

  /**
   * For debugging
   */
  public static void assertStylesContainSameAttributes(Context context) {
    ImageViewStyleApplier.assertStylesContainSameAttributes(context);
    MyOtherViewStyleApplier.assertStylesContainSameAttributes(context);
    MyViewStyleApplier.assertStylesContainSameAttributes(context);
    TextViewStyleApplier.assertStylesContainSameAttributes(context);
    ViewGroupStyleApplier.assertStylesContainSameAttributes(context);
    ViewStyleApplier.assertStylesContainSameAttributes(context);
  }
}