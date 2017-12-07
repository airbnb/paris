package com.airbnb.paris;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.airbnb.paris.proxies.ImageViewProxyStyleApplier;
import com.airbnb.paris.proxies.TextViewProxyStyleApplier;
import com.airbnb.paris.proxies.ViewGroupProxyStyleApplier;
import com.airbnb.paris.proxies.ViewProxyStyleApplier;
import com.airbnb.paris.test.MyView;
import com.airbnb.paris.test.MyViewStyleApplier;

public final class Paris {
  public static ViewProxyStyleApplier style(View view) {
    return new ViewProxyStyleApplier(view);
  }

  public static ViewProxyStyleApplier.StyleBuilder styleBuilder(View view) {
    return new ViewProxyStyleApplier.StyleBuilder(new ViewProxyStyleApplier(view));
  }

  public static TextViewProxyStyleApplier style(TextView view) {
    return new TextViewProxyStyleApplier(view);
  }

  public static TextViewProxyStyleApplier.StyleBuilder styleBuilder(TextView view) {
    return new TextViewProxyStyleApplier.StyleBuilder(new TextViewProxyStyleApplier(view));
  }

  public static ImageViewProxyStyleApplier style(ImageView view) {
    return new ImageViewProxyStyleApplier(view);
  }

  public static ImageViewProxyStyleApplier.StyleBuilder styleBuilder(ImageView view) {
    return new ImageViewProxyStyleApplier.StyleBuilder(new ImageViewProxyStyleApplier(view));
  }

  public static ViewGroupProxyStyleApplier style(ViewGroup view) {
    return new ViewGroupProxyStyleApplier(view);
  }

  public static ViewGroupProxyStyleApplier.StyleBuilder styleBuilder(ViewGroup view) {
    return new ViewGroupProxyStyleApplier.StyleBuilder(new ViewGroupProxyStyleApplier(view));
  }

  public static MyViewStyleApplier style(MyView view) {
    return new MyViewStyleApplier(view);
  }

  public static MyViewStyleApplier.StyleBuilder styleBuilder(MyView view) {
    return new MyViewStyleApplier.StyleBuilder(new MyViewStyleApplier(view));
  }

  /**
   * For debugging */
  public static void assertStylesContainSameAttributes(Context context) {
    MyView MyView = new MyView(context);
    StyleApplierUtils.Companion.assertSameAttributes(style(MyView), new MyViewStyleApplier.StyleBuilder().addRed().build(), new MyViewStyleApplier.StyleBuilder().addGreen().build(), new MyViewStyleApplier.StyleBuilder().addBlue().build(), new MyViewStyleApplier.StyleBuilder().addDefault().build());
  }
}