# Paris
Programmatic style application for Android views, including custom attributes.

* Apply styles programmatically at any time, in addition to XML
* Use annotations to easily support custom attributes (inspired by [Barber](https://github.com/hzsweers/barber))
* Declare explicitly supported styles for your custom views

## Basic Usage

Styles are defined in XML as your normally would:
```xml
<style name="Title1">
    <item name="android:textColor">#000000</item>
    <item name="android:textSize">30sp</item>
    <item name="android:paddingTop">20dp</item>
</style>
```

And can now be applied programmatically at any time, like so:
```java
Paris.style(textView).apply(R.style.Title1);
```

You can find the list of currently supported attributes [here](paris/src/main/res/values/attrs.xml).

## Style Links

XML resource files can get big and chaotic. For styles it can become hard to find which are available for any given view type. To remedy this, Paris lets your custom views explicitly declare their supported styles:
```java
@Styleable(styles = {
        @Style(name = "Red", id = R.style.MyView_Red),
        @Style(name = "Green", id = R.style.MyView_Green),
        @Style(name = "Blue", id = R.style.MyView_Blue)
})
public class MyView extends View {
    ...
}
```

Now when styling a view of type `MyView` you'll have access to helper methods for each of those styles:
```java
Paris.style(myView).applyRed();
Paris.style(myView).applyGreen();
Paris.style(myView).applyBlue(); // Same as calling ...apply(R.style.MyView_Blue)
```

**Note:** This doesn't prevent the application of other styles.

## Custom View Attributes

In addition to supporting the application of custom attributes, Paris helps get rid of the boilerplate associated with adding custom attributes to your views in the first place. Here's how.

Declare your custom attributes as you normally would:
```xml
<declare-styleable name="MyView">
    <attr name="image" format="reference" />
    <attr name="title" format="string" />
</declare-styleable>
```

Next we'll use two annotations to give your custom view information about its attributes.

* `@Styleable` is the class level annotation used to reference the name of your styleable declaration
* `@Attr` will bind an attribute value when used on a field, or pass it as a parameter when used on a method

Here's an example:
```java
// The value here corresponds to the name chosen in declare-styleable
@Styleable("MyView")
public class MyView extends ViewGroup {

    @Attr(R2.styleable.MyView_image)
    Drawable image;

    public MyView(Context context) {
        super(context);
    }

    public MyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyView(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle);
        // This call enables the custom attributes when used in XML layouts. It extracts
        // styling information from AttributeSet just like it would a StyleRes
        Paris.style(this).apply(attrs);
    }

    @Attr(R2.styleable.MyView_title)
    public void setTitle(String title) {
        // Presumably something is done with the title
    }
}
```
**Note:** Paris converts the attribute value based on the type of the field or method parameter, as well as the use of support annotations like `@DimenRes`, `@DrawableRes`, all the other `@...Res`, and `@Px`.

That's it!

You can now use these custom attributes in XML:
```xml
<MyView
    ...
    app:image="@drawable/beach"
    app:title="@string/hello" />
```

Or in a style:
```xml
<style name="Beach">
    <item name="image">@drawable/beach</item>
    <item name="title">@string/hello</item>
    <item name="android:textColor">#FFFF00</item>
</style>
```

Which can then be applied in XML:
```xml
<MyView
    style="@style/Beach"
    ... />
```

Or programmatically:
```java
Paris.style(myView).apply(R.style.Beach);
```

Baller!

## Styleable Subviews

Sometimes your custom views may have subviews which you'd like to make individually styleable. For example, a custom view may contain both a title and subtitle, and each can be restyled separately. Paris has you covered.

First declare custom attributes for the substyles you'd like to support:
```xml
<declare-styleable name="MyHeader">
    <attr name="titleStyle" format="reference" />
    <attr name="subtitleStyle" format="reference" />
    ...
</declare-styleable>
```

Then annotate your custom view's subview fields with the corresponding attribute ids:
```java
@Styleable("MyHeader")
public class MyHeader extends ViewGroup {

    @Attr(R.styleable.MyHeader_titleStyle)
    TextView title;
    
    @Attr(R.styleable.MyHeader_subtitleStyle)
    TextView subtitle;
    
    ...
    // Make sure to call Paris.style(this).apply(attrs) during initialization
}
```

That's it!

You can now use these attributes in XML and Paris will automatically apply the specified styles to the subviews:
```xml
<MyHeader
    ...
    app:titleStyle="@style/Title2"
    app:subtitleStyle="@style/Regular" />
```

Or programmatically. Paris generates helper methods based on the name of the fields:
```java
Paris.style(myHeader).title().apply(R.style.Title2);
Paris.style(myHeader).subtitle().apply(R.style.Regular);
```

## How do I...

* ... apply **multiple styles** to a view?
* ... **share** custom attribute logic across multiple views?
* ... apply a style to a **subview's subview**?
* ... **extend** a @Styleable class?
