An easy to use BottomSheet widget with a predefined `ViewPager`, a "control strip"
with dynamic buttons and many convenient methods
 
*ToDo: Add screenshots*

### How to get it

*ToDo: Add Jcenter info*

### Usage

To use  `ControlSheet` to your layout XML, you need to add it to a `CoordinatorLayout`.
You will need to set a `BottomSheetBehavior`, but that is all you need to specify, everything
else is optional, and can alo be set from code, like below. (Or default values will be used, wherever applicable


#### From XML

```XML
<com.atlanticomnibus.controlsheet.ControlSheet
    android:id="@+id/control_sheet"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:layout_behavior="com.google.android.material.bottomsheet.BottomSheetBehavior" />
```
 
You can also specify various properties from the XML. One convenient attribute is a commas separated list of
the layout IDs for all the layouts you want to include in the viewpager. You only need to provide the IDs,
and everything will be set up automatically. the syntax is similar to what you can see in e.g. `ConstraintLayout`
 
```XML
    app:layout_ids="page_1,page_2, page_3"
```
 
Note that yu can set up to 5 pages, above which, you have to manually raise th limit:
   
```XML
    app:viewpager_max_pages="6"
```
 
Other XML attributes include:
 
```XML
    app:button_color="@color/button_color"
    app:button_animation_style="none|dip|spin"
    app:sheet_control_button_style="none|cogwheel|chevron|custom"
    app:show_control_strip="true|false"
```
 
Note that if you set the `sheet_control_button_style` to "custom", you can specify drawable IDs
for the collapsed and expanded states of the sheet. If you don1t specify either of of these, it will be set to the
default cogwheel style. If you only specify one, the other will be set to the default cogwheel style
 
```XML
   app:sheet_collapsed_button_drawable_id="@drawable/button_drawable"
   app:sheet_expanded_button_drawable_id="@drawable/button_drawable"
```
 
If you turn off the control strip (by setting `show_control_strip` to "false), you might want to manually
set a peek-height, otherwise you will only be able to programmatically expand the Sheet, e.g.:
   
```XML
    app:sheet_peek_height="@dimen/sheet_peek_height"
```
 
All of the above is also available from java, with the addition of ControlStrip buttons, which cna only be set form code.
When you get a reference to your control sheet, you can keep chaining most methods right after the constructor:
 
#### From code

   
Get a reference to the `ControlSheet`

```Java
int[] layoutIds={R.layout.layout_1,R.layout.layout_2,R.layout.layout_3,R.layout.layout_4,R.layout.layout_5} // Set up an array of layout ids
 
ControlSheet controlSheet= findViewById(R.id.control_sheet); // Find our ControlSheet
``` 

Manage the viewpager's layouts from code 

```Java
controlSheet.setSheetPagerLayouts(layoutIds)                // Set the layoutIDs for the `ViewPager` to use
            .setPagerMaxSize(6)                             // Raises the limit
            .addSheetPagerLayout(R.layout_layout_6)         // Adds one more layout at the end
            .removeSheetPagerLayout(2)                      // Removes the second page
            .addSheetPagerLayout(R.layout_layout_extra, 3); // Adds one more layout in the specified position
``` 

Manage the controlstrip

```Java
controlSheet.setHasControlStrip(false)                       // Turns off the control strip
            .setSheetPeekHeight(16)                          // Sets peek height in `TypedValue#COMPLEX_UNIT_DIP`
            .setPeekHeight(TypedValue.COMPLEX_UNIT_PX, 26)   // Sets the peek height in any valid unit
            .setPeekHeight(0)                                // Resets the peek height at 0
            .setHasControlStrip(true);                       // Re-enables the control strip
```
 
Manage the sheet's control button

```Java
controlSheet.setSheetControlButton(ControlSheet.CHEVRON)                             // Changes the conrtol button style to a chevron
            .setSheetControlButton(ControlSheet.NO_BUTTON)                           // Disables the control button
            .setSheetControlButton(ControlSheet.CUSTOM)                              // Sets the control button style to custom
            .setCustomCollapsedButtonDrawableId(R.drawable_control_button_collapsed) // Sets the button for the collapsed sheet
            .setCustomExpandedButtonDrawableId(R.drawable_control_button_expanded)   // Sets the button for the expanded sheet
            .setSheetControlButton(ControlSheet.COGWHEEL);                           // Re-sets the control button to the default cogwheel
```

 
Manage the other controlstrip buttons

```Java
controlSheet.addControlStripButton(R.drawable.button_1, buttonOneOnclickListener) // Adds a button to the control strip with the specified Drawable and View.OnClickListener
            .addControlStripButton(R.drawable.button_2, buttonTwoOnclickListener) // Adds another button to the control strip with the specified Drawable and View.OnClickListener
            .setButtonAnimationStyle(ControlSheet.NO_ANIMATION)                   // Disables button pressed animation
            .setButtonAnimationStyle(ControlSheet.DIP_BUTTON)                     // Sets button pressed animation to ControlSheet#DIP_BUTTON
            .removeControlStripButton(2);                                         // Removes the butotn from the 2nd (relative) position
            .setButtonAnimationStyle(ControlSheet.SPIN_BUTTON                     // Resets button pressed animation to the default ControlSheet#SPIN_BUTTON
```
 
To know when the built-in `ViewPager` is ready, you need to set a `ControlSheetInflatedListener`
It's easiest to have the calling class or activity implement this interface, and directly override its single method
`ControlSheetInflatedListener#onControlSheetInflated`, which will then pass down the inflated and laid out `ViewPager`
whenever it's ready.
 
```Java
public class MainActivity extends AppCompatActivity implements ControlSheetInflatedListener {
 
   @Override
   protected void onCreate(Bundle savedInstanceState) {
       ControlSheet controlSheet = findViewById(R.id.control_sheet);
       controlSheet.addControlSheetInflatedListener(this);
    }
 
   @Override
   public void onControlSheetInflated(ViewPager viewPager) {

         /* Access the ready ViewPager to use its contents */
 
   }
 
   ...
 
}
```
 
Other methods will allow you to directly collapse or expand the sheet, get it's behaviour's current state, or access some
of its widgets directly. For details, see the docs, or browse the code comments.
 
Happy coding. :) 

### Copyright, Licensing and Contributions

Copyright (c) 2019 Attila Orosz

SwitchButton is licensed under the MIT license (See license file).

The development of this library happens on [GitLab](https://gitlab.com/atlantic_omnibus/open-source/switch-button), which is an **open source** DevOps platform. The repository automatically gets pushed to GitHub as well for better discoverability. You are welcome to open any issues on GH, but if you want to contribute, please visit the [GitLab repo](https://gitlab.com/atlantic_omnibus/open-source/switch-button).
