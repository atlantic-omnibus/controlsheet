/*
 * ControlSheet
 *
 * Copyright (c) 2019 Attila Orosz
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package com.atlanticomnibus.controlsheet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.IntDef;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;


/**
 *  ToDo: main PoA
 *   - [ ] Push top GitLab and GitHub
 *      - [ ] Open an enhanceent ticket about samitising the loading of Drawables
 *   - [ ] Add Unit tests
 *   - [ ] Release to the wild
 */

/**
 * <p>An easy to use {@code BottomSheet} widget with a predefined {@link ViewPager}, a "control strip"
 * with dynamic buttons and many convenient methods</p>
 *
 * <p>To use  {@code ControlSheet} to your layout XML, you need to add it to a {@link CoordinatorLayout}.
 * You will need to set a {@link BottomSheetBehavior}, but that is all you need to specify, everything
 * else is optional, and can also be set from code, like below. (Or default values will be used, wherever applicable</p>
 *
 * <h3>Add it fFrom XML</h3>
 * <pre>
 *     &lt;com.atlanticomnibus.controlsheet.ControlSheet
 *         android:id="@+id/control_sheet"
 *         android:layout_width="match_parent"
 *         android:layout_height="wrap_content"
 *         app:layout_behavior="com.google.android.material.bottomsheet.BottomSheetBehavior" /&gt;
 * </pre>
 *
 * <p>You can also specify various properties from the XML. One convenient attribute is a commas separated list of
 * the layout IDs for all the layouts you want to include in the {@link ViewPager}. You only need to provide the IDs,
 * and everything will be set up automatically. the syntax is similar to what you can see in e.g. {@link ConstraintLayout}</p>
 * <br />
 * <pre>
 *         app:layout_ids="page_1,page_2, page_3"
 * </pre>
 *
 * <p>Note that yu can set up to 5 pages, above which, you have to manually raise th limit:</p>
 * <br />
 * <pre>
 *         app:viewpager_max_pages="6"
 * </pre>
 *
 * <p>Other XML attributes include:</p>
 *
 * <br />
 * <pre>
 *         app:button_color="@color/button_color"
 *         app:button_animation_style="none|dip|spin"
 *         app:sheet_control_button_style="none|cogwheel|chevron|custom"
 *         app:show_control_strip="true|false"
 * </pre>
 *
 * <p>Note that if you set the {@code sheet_control_button_style} to "custom", you can specify drawable IDs
 * for the collapsed and expanded states of the sheet. If you don't specify either of of these, it will be set to the
 * default cogwheel style. If you only specify one, the other will be set to the default cogwheel style</p>
 *
 * <br />
 * <pre>
 *         app:sheet_collapsed_button_drawable_id="@drawable/button_drawable"
 *         app:sheet_expanded_button_drawable_id="@drawable/button_drawable"
 * </pre>
 *
 * <p>If you turn off the control strip (by setting {@code show_control_strip} to "false), you might want to manually
 * set a peek-height, otherwise you will only be able to programmatically expand the Sheet, e.g.:</p>
 *
 * <br />
 * <pre>
 *         app:sheet_peek_height="@dimen/sheet_peek_height"
 * </pre>
 *
 * <p>All of the above is also available from java, with the addition of control strip buttons, which can only be set form code.
 * When you get a reference to your control sheet, you can keep chaining most methods right after the constructor:</p>
 *
 * <h3>Access in code</h3>
 *
 * <p>First get a reference</p>
 *
 * <pre>
 *     int[] layoutIds={R.layout.layout_1,R.layout.layout_2,R.layout.layout_3,R.layout.layout_4,R.layout.layout_5} // Set up an array of layout ids
 *
 *     ControlSheet controlSheet= findViewById(R.id.control_sheet); // Find our {@link ControlSheet}
 * </pre>
 *
 * <p>Manage the {@link ViewPager}'s layouts from code</p>
 *
 * <pre>
 *     controlSheet.setSheetPagerLayouts(layoutIds)                // Set the layoutIDs for the {@link ViewPager} to use
 *                 .setPagerMaxSize(6)                             // Raises the limit
 *                 .addSheetPagerLayout(R.layout_layout_6)         // Adds one more layout at the end
 *                 .removeSheetPagerLayout(2)                      // Removes the second page
 *                 .addSheetPagerLayout(R.layout_layout_extra, 3); // Adds one more layout in the specified position
 * </pre>
 *
 * <p>Manage the control strip</p>
 * <pre>
 *     controlSheet.setHasControlStrip(false)                       // Turns off the control strip
 *                 .setSheetPeekHeight(16)                          // Sets peek height in {@link TypedValue#COMPLEX_UNIT_DIP}
 *                 .setPeekHeight(TypedValue.COMPLEX_UNIT_PX, 26)   // Sets the peek height in any valid unit
 *                 .setPeekHeight(0)                                // Resets the peek height at 0
 *                 .setHasControlStrip(true);                       // Re-enables the control strip
 * </pre>
 *
 * <p>Manage the sheet's control button</p>
 * <pre>
 *     controlSheet.setSheetControlButton(ControlSheet.CHEVRON)                             // Changes the conrtl button style to a chevron
 *                 .setSheetControlButton(ControlSheet.NO_BUTTON)                           // Disables the control button
 *                 .setSheetControlButton(ControlSheet.CUSTOM)                              // Sets the control button style to custom
 *                 .setCustomCollapsedButtonDrawableId(R.drawable_control_button_collapsed) // Sets the button for the collapsed sheet
 *                 .setCustomExpandedButtonDrawableId(R.drawable_control_button_expanded)   // Sets the button for the expanded sheet
 *                 .setSheetControlButton(ControlSheet.COGWHEEL);                           // Re-sets the contrl button to the default cogwheel
 * </pre>
 *
 * <p>Manage the other controlstrip buttons</p>
 *     controlSheet.addControlStripButton(R.drawable.button_1, buttonOneOnclickListener) // Adds a button to the control strip with the specified {@link Drawable} {@View.OnClickListener}
 *                 .addControlStripButton(R.drawable.button_2, buttonTwoOnclickListener) // Adds another button to the control strip with the specified {@link Drawable} {@View.OnClickListener}
 *                 .setButtonAnimationStyle(ControlSheet.NO_ANIMATION)                   // Disables button pressed animation
 *                 .setButtonAnimationStyle(ControlSheet.DIP_BUTTON)                     // Sets button pressed animation to {@link ControlSheet#DIP_BUTTON}
 *                 .removeControlStripButton(2);                                         // Removes the butotn from the 2nd (relative) position
 *                 .setButtonAnimationStyle(ControlSheet.SPIN_BUTTON                     // Resets button pressed animation to the default {@link ControlSheet#SPIN_BUTTON}
 * </pre>
 *
 * <p>To know when the built-in {@link ViewPager} is ready, you need to set a {@link ControlSheetInflatedListener}
 * It's easiest to have the calling class or activity implement this interface, and directly override its single method
 * {@link ControlSheetInflatedListener#onControlSheetInflated}, which will then pass down the inflated and laid out {@link ViewPager}
 * whenever it's ready.</p>
 *
 * <pre>
 *     public class MainActivity extends AppCompatActivity implements ControlSheetInflatedListener {
 *
 *        &commat;Override
 *        protected void onCreate(Bundle savedInstanceState) {
 *            ControlSheet controlSheet = findViewById(R.id.control_sheet);
 *            controlSheet.addControlSheetInflatedListener(this);
 *         }
 *
 *        &commat;Override
 *        public void onControlSheetInflated(ViewPager viewPager) {
 *
 *            /&ast; Access the ready ViewPager to use its contents</p>
 *
 *        }
 *
 *        ...
 *
 *     }
 * </pre>
 *
 * <p>Other methods will allow you to directly collapse or expand the sheet, get it's behaviour's current state, or access some
 * of its widgets directly. For details, see the docs, or browse the code comments.
 *
 * Happy coding. :)</p>
 *
 */


public class ControlSheet extends LinearLayout {

    private final float DEFAULT_ELEVATION_VALUE = 24.0f, // Of the whole sheet
                        CONTROLSTRIP_ELEVATION;          // Calculated fromscreen density in constructors

    private final int CONTROLSTRIP_HEIGHT,                       // This is a constant for now
                      CONTROLSTRIP_DEFAULT_HEIGHT_VALUE    = 52, // This much
                      ZILCH_NADA_NIL_BUT_NOT_ZERO          = -1, // Non-zero zero. :)
                      DEFAULT_VIEWPAGER_SIZE_LIMIT         =  5, // Got to be enough. If not, you're doing design wrong. (You cvn raise it anyway)
                      DEFAULT_CONTROL_BUTTON_PADDING_VALUE = 16, // Non-negotiable. :)
                      CONTROL_BUTTON_PADDING,                    // This will eb calcualted int eh cinstructors
                      CONTROLSTRIP_DEFAULT_ELEVATION_VALUE =  2; // Not very high.

    public static final int NO_BUTTON                 =   0, // It means "no button"in an ancient, fogotten language
                            COGWHEEL                  =   1, // A wheel with cogs
                            CHEVRON                   =   2, // Like a lttle arrow
                            CUSTOM                    =   3, // You're in control
                            NO_ANIMATION              =   0, // It's like having animation, only not.
                            SPIN_BUTTON               =   1, // The button will spin, (and magically change shapes mid-spin, if it1s the control button)
                            DIP_BUTTON                =   2, // The button will have a "dip" effect
                            BUTTON_ANIMATION_DURATION = 300; // Not very fast, but not very slow either. just right

    private ViewPager viewPager;                        // A pager of views. (Or is it a view of pagers??)
    private ConstraintLayout controlStripLayout;        // This is the controlstrip itself
    private ImageView sheetControlButton;               // The "fixed" button at the end of the strip, which can open/close the sheet
    private List<Integer> layoutIds;                    // A list of layout ids hekld by the viewpager
    private ArrayList<ControlStripButton> stripButtons; // A list of buttons shown on the controlstrip

    private boolean controlStripVisible, // If true, it menas the control strip is visible
                    isDynamic = false;   // If true, the widget was added from code, and no from XML

    private float mElevation; // The melevation of the sheet

    private int buttonColor,               // It's the colour of the buttons
                sheetPeekHeight,           // link BottomSheet's peekHeight
                controlButtonStyle,        // Cogwhel, chevron, or custom (user defined)
                vpSizeLimit,               // The number of pages the virewpager can have. Since they need to be held in memory
                buttonAnimationStyle,      // Spin or dip or none
                customCollapsedDrawableId, // When controlSheetButton's stle is cutsom, you cna set a your own drawable.
                customExpandedDrawableId;  // When controlSheetButton's stle is cutsom, you cna set a your own drawable.

    private Drawable sheetCollapsedButtonDrawable, // The Drawable from the id above
                     sheetExpandedButtondrawable;  // The Drawable from the id above

    private BottomSheetBehavior sheetBehavior;                     // The layout_behaviour of the sheet
    private ControlSheetInflatedListener inflatedListener;         // Listener to listen to everythign beign laid out
    private ControlSheetStateChangedListener stateChangedListener; // listener to isten to state changes in the BottomSheetBehaviour



    /*************************************Some custom annotations**********************************/

    /** @hide **/
    @IntDef({NO_BUTTON, COGWHEEL, CHEVRON, CUSTOM})
    @Retention(RetentionPolicy.SOURCE)
    private @interface ControlButtonStyle {
    }

    /** @hide **/
    @IntDef({BottomSheetBehavior.STATE_COLLAPSED, BottomSheetBehavior.STATE_EXPANDED, BottomSheetBehavior.STATE_HALF_EXPANDED, BottomSheetBehavior.STATE_HIDDEN})
    @Retention(RetentionPolicy.SOURCE)
    private @interface ValidControlSheetState {
    }

    /** @hide **/
    @IntDef({NO_ANIMATION, SPIN_BUTTON, DIP_BUTTON})
    @Retention(RetentionPolicy.SOURCE)
    private @interface ControlButtonAnimationStyle {
    }

    /** @hide **/
    @IntDef({TypedValue.COMPLEX_UNIT_PX, TypedValue.COMPLEX_UNIT_DIP, TypedValue.COMPLEX_UNIT_SP,
            TypedValue.COMPLEX_UNIT_PT, TypedValue.COMPLEX_UNIT_IN, TypedValue.COMPLEX_UNIT_MM})
    @Retention(RetentionPolicy.SOURCE)
    private @interface ValidSizeUnit {
    }


    /*********************************Constructors*************************************************/


    /**
     * <p>Will initialise an empty sheet, with nothing on its {@link ViewPager}. This one needs a
     * {@link Context} object only. Note: BottomSheetBehaviour needs to be added manually</p>
     *
     * @param context  The Context
     */
    public ControlSheet(Context context) {
        super(context);
        CONTROLSTRIP_HEIGHT = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CONTROLSTRIP_DEFAULT_HEIGHT_VALUE, getResources().getDisplayMetrics()));
        CONTROLSTRIP_ELEVATION = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CONTROLSTRIP_DEFAULT_ELEVATION_VALUE, getResources().getDisplayMetrics());
        CONTROL_BUTTON_PADDING=Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_CONTROL_BUTTON_PADDING_VALUE, getResources().getDisplayMetrics()));
        mElevation = DEFAULT_ELEVATION_VALUE;
        isDynamic = true;
        controlStripVisible = true;
        sheetPeekHeight = 0;
        controlButtonStyle = COGWHEEL;
        vpSizeLimit = DEFAULT_VIEWPAGER_SIZE_LIMIT;
        buttonAnimationStyle = SPIN_BUTTON;
        initSheet(context);
    }

    /**
     * <p>Will initialise a sheet in the given context, and set up the {@link ViewPager} with the supplied layout ids.
     * Note: BottomSheetBehaviour needs to be added manually</p>
     * @param context the context
     * @param layoutIds A {@link List<Integer>} (or even an {@code int[]} of layout ids to add to the viewpager, in order or appearance
     */
    public ControlSheet(Context context, List<Integer> layoutIds) {
        super(context);
        CONTROLSTRIP_HEIGHT = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CONTROLSTRIP_DEFAULT_HEIGHT_VALUE, getResources().getDisplayMetrics()));
        CONTROLSTRIP_ELEVATION = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CONTROLSTRIP_DEFAULT_ELEVATION_VALUE, getResources().getDisplayMetrics());
        CONTROL_BUTTON_PADDING=Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_CONTROL_BUTTON_PADDING_VALUE, getResources().getDisplayMetrics()));
        mElevation = DEFAULT_ELEVATION_VALUE;
        isDynamic = true;
        controlStripVisible = true;
        sheetPeekHeight = 0;
        this.layoutIds = layoutIds;
        controlButtonStyle = COGWHEEL;
        vpSizeLimit = DEFAULT_VIEWPAGER_SIZE_LIMIT;
        buttonAnimationStyle = SPIN_BUTTON;
        initSheet(context);
    }

    /************************ Extended XML constructors from {@link LinearLayout} ******************/

    public ControlSheet(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        CONTROLSTRIP_HEIGHT = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CONTROLSTRIP_DEFAULT_HEIGHT_VALUE, getResources().getDisplayMetrics()));
        CONTROLSTRIP_ELEVATION = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CONTROLSTRIP_DEFAULT_ELEVATION_VALUE, getResources().getDisplayMetrics());
        CONTROL_BUTTON_PADDING=Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_CONTROL_BUTTON_PADDING_VALUE, getResources().getDisplayMetrics()));
        getAttributes(context, attrs);
        initSheet(context);
    }

    public ControlSheet(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        CONTROLSTRIP_HEIGHT = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CONTROLSTRIP_DEFAULT_HEIGHT_VALUE, getResources().getDisplayMetrics()));
        CONTROLSTRIP_ELEVATION = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CONTROLSTRIP_DEFAULT_ELEVATION_VALUE, getResources().getDisplayMetrics());
        CONTROL_BUTTON_PADDING=Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_CONTROL_BUTTON_PADDING_VALUE, getResources().getDisplayMetrics()));
        getAttributes(context, attrs);
        initSheet(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ControlSheet(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        CONTROLSTRIP_HEIGHT = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CONTROLSTRIP_DEFAULT_HEIGHT_VALUE, getResources().getDisplayMetrics()));
        CONTROLSTRIP_ELEVATION = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CONTROLSTRIP_DEFAULT_ELEVATION_VALUE, getResources().getDisplayMetrics());
        CONTROL_BUTTON_PADDING=Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_CONTROL_BUTTON_PADDING_VALUE, getResources().getDisplayMetrics()));
        getAttributes(context, attrs);
        initSheet(context);
    }

    /**
     * Reads the attribztes from the XML
     * @param context the context
     * @param attrs he Attributes to read
     */
    private void getAttributes(Context context, AttributeSet attrs) {

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ControlSheet,
                0, 0);
        try {

            controlStripVisible = a.getBoolean(R.styleable.ControlSheet_show_control_strip, true);
            sheetPeekHeight = Math.round(a.getDimension(R.styleable.ControlSheet_sheet_peek_height, 0.0f));
            mElevation = a.getDimension(R.styleable.ControlSheet_android_elevation, DEFAULT_ELEVATION_VALUE);

            String idsString = a.getString(R.styleable.ControlSheet_layout_ids);
            if (!TextUtils.isEmpty(idsString)) {
                layoutIds = resolveIds(idsString);
            }
            buttonColor=a.getColor(R.styleable.ControlSheet_button_color, resolveButtonColor());
            controlButtonStyle = a.getInt(R.styleable.ControlSheet_sheet_control_button_style, COGWHEEL);
            vpSizeLimit = a.getInt(R.styleable.ControlSheet_viewpager_max_pages, DEFAULT_VIEWPAGER_SIZE_LIMIT);
            buttonAnimationStyle = a.getInt(R.styleable.ControlSheet_button_animation_style, SPIN_BUTTON);
            customCollapsedDrawableId = a.getResourceId(R.styleable.ControlSheet_sheet_collapsed_button_drawable_id, 0);
            customExpandedDrawableId = a.getResourceId(R.styleable.ControlSheet_sheet_expanded_button_drawable_id, 0);

        } finally {
            a.recycle();
        }

    }

    /**
     * Inflates the sheet when all is done
     *
     * @param context The Context
     */
    private void initSheet(Context context) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.control_sheet, this, true);

        if (this.isDynamic) {
            onFinishInflate();
        }
    }

    /**
     * <p>When inflating ont he component views finishes, this function will finish setting them up and applying any attributes to them
     * Called automatically when layout is added form XLM. Also called from dynamic constructor manually.</p>
     *
     * <p>The {@link SuppressLint} annotation is required to address Android bug <a href="https://issuetracker.google.com/37065042">#37065042</a> "setForeground()
     * incorrectly flagged as requiring API 23 (NewApi) for ViewGroups extending FrameLayout"</p>
     */
    @SuppressLint("NewApi")
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        setOrientation(LinearLayout.VERTICAL);
        setBackgroundColor(Color.WHITE);
        setElevationInternal(mElevation);

        if(isDynamic) {
            buttonColor = resolveButtonColor();
        }

        viewPager = findViewById(R.id.viewpager_config_selector);
        viewPager.setOffscreenPageLimit(vpSizeLimit-1);
        controlStripLayout = findViewById(R.id.control_strip_layout);

        setUpSheetControlButton();
        setUpViewPager();

        /**
         * Really can't do much before layout is lay'd out and everything is attached properly
         */
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                getViewTreeObserver().removeOnGlobalLayoutListener(this);

                if (getParent() instanceof CoordinatorLayout) {

                    sheetBehavior = BottomSheetBehavior.from(ControlSheet.this);

                    setUpControlStrip();
                    setControlStripConstraints();

                    sheetBehavior.setHideable(false);
                    sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                        @Override
                        public void onStateChanged(@NonNull View bottomSheet, int newState) {

                            if(stateChangedListener!=null) {
                                stateChangedListener.controlSheetStateChanged(newState);
                            }

                            switch (newState) {
                                case BottomSheetBehavior.STATE_SETTLING: {
                                    if (sheetControlButton.getTag() != "open") {
                                        updateSheetControlButton(true);
                                        sheetControlButton.setTag("open");
                                    } else {
                                        updateSheetControlButton(false);
                                        sheetControlButton.setTag("closed");
                                    }
                                    break;
                                }
                                case BottomSheetBehavior.STATE_EXPANDED: {
                                    //in case it ws dragged open all the way and never ebtered "settling state
                                    if (sheetControlButton.getTag() != "open") {
                                        updateSheetControlButton(true);
                                        sheetControlButton.setTag("open");
                                    }
                                    break;
                                }
                                case BottomSheetBehavior.STATE_COLLAPSED: {
                                    //in case it ws dragged closed all the way and never ebtered "settling state
                                    if (sheetControlButton.getTag() != "closed") {
                                        updateSheetControlButton(false);
                                        sheetControlButton.setTag("closed");
                                    }
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onSlide(@NonNull View bottomSheet, float slideOffset) { /*This does noting*/ }
                    });

                    sheetControlButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Will open/close the sheet as needed
                            if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            } else {
                                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                            }
                        }
                    });

                } else {
                    Log.e("ControlStrip", "Sheet is not attached to a CoordinatorLayout!");
                }
            }
        });
    }


    /*************************Control Sheet controlling stuff (public)******************************/


    /**
     * Does what it says.
     *
     * @param elevation Elevation to set
     */
    @Override
    public void setElevation(float elevation) {
        setElevationInternal(elevation);
    }

    /**
     * Convenience method to collapse the sheet without having to access its {@link BottomSheetBehavior}
     */
    public void collapseControlSheet(){
        setControlSheetState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    /**
     * Convenience method to expand the sheet without having to access its {@link BottomSheetBehavior}
     */
    public void expandsControlSheet(){
        setControlSheetState(BottomSheetBehavior.STATE_EXPANDED);
    }

    /**
     * Set the sheet's state to any valid state from {@link BottomSheetBehavior}
     *
     * @param state Any valid state for the sheet
     * @see ValidControlSheetState
     */
    public void setControlSheetState(@ValidControlSheetState int state){
        sheetBehavior.setState(state);
    }

    /**
     * Returns the current state from the sheet's {@link BottomSheetBehavior}
     * @return The current state from the sheet's {@link BottomSheetBehavior}
     */
    public int getControlSheetState(){
        return sheetBehavior.getState();
    }

    /**
     * Returns the sheet's {@link BottomSheetBehavior}
     * @return The sheet's {@link BottomSheetBehavior}
     */
    public BottomSheetBehavior getBottomSheetBehavior(){
        return sheetBehavior;
    }

    /**
     * <p>Sets the sheet's peek height in {@link TypedValue#COMPLEX_UNIT_DIP}</p>
     *
     * <p>Note: Whne the control strip is visible, the default peek height is the controlstrip's own height.
     * Any value defined here will be added to that so that <em>more</em> of the sheet would be visible.
     * When the control strip is turned off with {@link ControlSheet#setHasControlStrip(boolean)} set to {@code false},
     * a peek height set here will control the sheet1s totqal peek height</p>
     *
     * @param peekHeight peek height in {@link TypedValue#COMPLEX_UNIT_DIP}
     */
    public void setSheetPeekHeight(@IntRange(from=0) int peekHeight){
        sheetPeekHeight=Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, peekHeight, getResources().getDisplayMetrics()));
        setUpControlStrip();
    }

    /**
     * <p>Sets the sheet's peek height in any valid unit from {@link TypedValue}</p>
     *
     * <p>Note: Whne the control strip is visible, the default peek height is the controlstrip's own height.
     * Any value defined here will be added to that so that <em>more</em> of the sheet would be visible.
     * When the control strip is turned off with {@link ControlSheet#setHasControlStrip(boolean)} set to {@code false},
     * a peek height set here will control the sheet1s totqal peek height</p>
     *
     * @param peekHeight peek height in any valid unit from {@link TypedValue}
     */
    public void setSheetPeekHeight(@ValidSizeUnit int unit, @IntRange(from=0) int peekHeight){
        sheetPeekHeight=Math.round(TypedValue.applyDimension(unit, peekHeight, getResources().getDisplayMetrics()));
        setUpControlStrip();
    }

    /**
     * Gets the sheet's raw peek height in {@link TypedValue#COMPLEX_UNIT_PX}
     * @return peek height in {@link TypedValue#COMPLEX_UNIT_PX}
     */
    public int getSheetPeekHeightPX(){
        return sheetPeekHeight;
    }


    /*************************Control Sheet controlling stuff (private)******************************/


    /**
     * Internal method to set the sheet1s elevation in a version agnostic manner
     *
     * @param elevation elevation to set
     */
    private void setElevationInternal(@FloatRange(from = 0) float elevation) {

        mElevation = elevation;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.setElevation(mElevation);
        } else {
            ViewCompat.setElevation(this, mElevation);
        }
        requestLayout();
    }


    /***********************************ViewPager Stuff (public)***********************************/


    //

    /**
     * <p>Sets the {@link ViewPager's} size limit, that is the maximum pages it can hold. </p>
     * <p>Size limit is necessary to avoid using too much memeory. Since the {@link WrappingViewPager} that is used will
     * set its size according to its children, we need to kep the children in memory to avoid changing sizes on paging
     * (so the {@link ViewPager} will conform to the tallest child and keep tis height. If the children were to be dynamically loaded,
     * the pager's size would change, and with the the sheet's height too</p>
     *
     * ToDo: This needs some attentipon. Pager should measure once, and keep the tallest measured size forever, without
     *       having to keep its children in memory. That way this whole concept can be deporeceted, and probably even the
     *       {@link WrappingViewPager} itself as well
     * '
     * @param maxSize Maximum size of the pager
     * @return A {@link ControlSheet} object for method chaining
     */
    public ControlSheet setPagerMaxSize(int maxSize){
        vpSizeLimit=maxSize;
        viewPager.setOffscreenPageLimit(vpSizeLimit-1);
        return setUpViewPager();
    }

    /**
     * Set the {@link ViewPager}'s layouts all at once}
     *
     * @param layoutIds Array fo layout ids to set (in order)
     * @return A {@link ControlSheet} object for method chaining
     */
    public ControlSheet setSheetPagerLayouts(int[] layoutIds){
        if(this.layoutIds==null){
            this.layoutIds=new ArrayList<>();
        }

        for(int i:layoutIds) {
            this.layoutIds.add(i);
        }

        return setUpViewPager();
    }

    /**
     * Add a single layout to the @link Viewpager} (To the last position)
     *
     * @param layoutId The id of the layout to add
     * @return A {@link ControlSheet} object for method chaining
     */
    public ControlSheet addSheetPagerLayout(int layoutId){
        return handlePagerLayouts(layoutId, ZILCH_NADA_NIL_BUT_NOT_ZERO, false);
    }

    /**
     * <p>Add a single layout to the {@link ViewPager} to the specified position (starting form 1)</p>
     *
     * <p>Note: The numbering starts from <strong>1, <em>not</em> from 0</strong>, corresponding to the layout's visual position </p>
     *
     * @param layoutId The id of the layout to add
     * @param position The position t add the layout to (starting form 1)
     */
    public ControlSheet addSheetPagerLayout(int layoutId, @IntRange(from=1)  int position){
        return handlePagerLayouts(layoutId, position, false);
    }

    /**
     * <p>Remove layout in the specified position 8starting form 1) from the {@link ViewPager}.</p>
     *
     * <p>Note: The numbering starts from <strong>1, <em>not</em> from 0</strong>, corresponding to the layout's visual position </p>
     *
     * @param position The position of the layout to remove (starting from 1)
     * @return A {@link ControlSheet} object for method chaining
     */
    public ControlSheet removeSheetPagerLayout(@IntRange(from=1) int position){
        return handlePagerLayouts(ZILCH_NADA_NIL_BUT_NOT_ZERO, position, true);
    }

    /**
     * Return a {@link List<Integer>} of all the layout ids currently set to the {@link ViewPager>}
     * @return a {@link List<Integer>} of all the layout ids currently set to the {@link ViewPager>}
     */
    public List<Integer> getSheetPageLayoutIds(){
        return layoutIds;
    }

    /**
     * <p>eturns a single layout id set to the {@link ViewPager} in the specified positon</p>
     *
     * <p>Note: The numbering starts from <strong>1, <em>not</em> from 0</strong>, corresponding to the layout's visual position </p>
     *
     * @param position The position of the layout to get the id of (starting from 1)
     * @return The id of the layout in the specified position, or 0 if the position is not valid
     */
    public int getSheetPagerLayoutIdAtPosition(@IntRange(from=1) int position){
        if(position<=vpSizeLimit) {
            return layoutIds.get(position - 1);
        } else {
            Log.e("ControlSheet", "getSheetPagerLayoutIdAtPosition(): 'position' exceeds maximum number of pages (Currently: " + vpSizeLimit + ")");
            return 0;
        }
    }

    /**
     * Returns the {@link ViewPager} itself for direct manipulation
     *
     * @return the {@link ViewPager} itself for direct manipulation
     */
    public ViewPager getViewPager(){
        return viewPager;
    }


    /**********************************ViewPager stuff (private)***********************************/


    /**
     * Sets up the ViewPager, checks size limits, and sets up the adapter and the tabs
     *
     * @return A {@link ControlSheet} object for method chaining
     */
    private ControlSheet setUpViewPager() {
        if (layoutIds != null && layoutIds.size() > 0) {

            if (layoutIds.size() > vpSizeLimit) {
                layoutIds = layoutIds.subList(0, vpSizeLimit - 1);
            }

            if (viewPager.getAdapter() == null) {
                viewPager.setAdapter(new SimplePagerAdapter(getContext(), layoutIds));
                viewPager.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        if (inflatedListener != null) {
                            inflatedListener.onControlSheetInflated(viewPager);
                        }
                    }
                });
            } else {
                viewPager.getAdapter().notifyDataSetChanged();
            }

            TabLayout tablayout = findViewById(R.id.config_selector_tablayout);

            if (viewPager.getAdapter().getCount() > 1) {
                tablayout.setVisibility(View.VISIBLE);
                tablayout.setupWithViewPager(viewPager, true);
            } else {
                tablayout.setVisibility(View.GONE);
            }
        }

        return this;
    }

    /**
     * <p>Internal method to handle dynamically adding and removing pages from the {@link ViewPager} to/from the
     * specified position (starting from 1). It will call {@link ControlSheet#setUpViewPager()} internaly, so you
     * don't need to worry abut that</p>
     *
     * <p>Note: The numbering starts from <strong>1, <em>not</em> from 0</strong>, corresponding to the layout's visual position </p>
     *
     * @param id The id of the layout ot add or remove
     * @param position position to add or remove to/from (starting form 1)
     * @param removing Whether we1re removing the layout (true of removing)
     * @return A {@link ControlSheet} object for method chaining
     */
    private ControlSheet handlePagerLayouts(int id, int position, boolean removing){

        if(this.layoutIds==null){
            this.layoutIds=new ArrayList<>();
        }

        if(removing){
            if(position>0 && position<=layoutIds.size()) {
                layoutIds.remove(position-1);
                if(viewPager.getCurrentItem()==position-1 && position>1){
                    viewPager.setCurrentItem(position-2, true);
                }
                viewPager.setAdapter(null);
            }
        } else {
            if(layoutIds.size()>=vpSizeLimit){
                Log.e("ControlSheet", "Maximum number of pages reached (Currently: " + vpSizeLimit + ")");
            } else {
                if (position > 0) {
                    layoutIds.add(position - 1, id);
                } else {
                    layoutIds.add(id);
                }
            }
        }

        return setUpViewPager();
    }


    /**
     * Internal method to resolve layout ids from the {@ilnk String} provided in the layout XML
     * @param idString String of ids from XML (comma separated)
     * @return An ArrayList of ids
     */
    @Nullable
    private ArrayList<Integer> resolveIds(String idString) {
        ArrayList<Integer> ids = new ArrayList<>();

        if (idString.contains(",")) {

            String[] idStrings = idString.split(",");

            for (String s : idStrings) {
                int id = findLayoutId(s);

                if (id != 0) {
                    ids.add(id);
                }
            }
        } else {

            int id = findLayoutId(idString);

            if (id != 0) {
                ids.add(id);
            }
        }

        return ids;
    }

    /**
     * Actually tries ot find the id of the layout from
     * @param id the id name String
     * @return the layout id
     */
    private int findLayoutId(String id) {
        return getResources().getIdentifier(id.trim(), "layout",
                getContext().getPackageName());
    }


    /**********************ControlStrip stuff (public)**********************************************/


    ////SheetControl button////

    /**
     * <p>Dynamically set the Sheet's control button style.</p>
     * <p>Accepted values are
     *     <ul>
     *          <li>Do not show button: {@link ControlSheet#NO_BUTTON} ({@value ControlSheet#NO_BUTTON})</li>
     *          <li>Cogwheel style button {@link ControlSheet#COGWHEEL} ({@value ControlSheet#COGWHEEL})</li>
     *          <li>Chevron style button: {@link ControlSheet#CHEVRON} ({@value ControlSheet#CHEVRON})</li>
     *          <li>User defined style: {@link ControlSheet#CUSTOM} ({@value ControlSheet#CUSTOM})</li>
     *     </ul>
     * </p>
     * <p>When {@link ControlSheet#CUSTOM} is used the button's drawables for the collapsed and expanded states of the
     * sheet can be set with the {@link ControlSheet#setCustomCollapsedButtonDrawableId(int)} and
     * {@link ControlSheet#setCustomExpandedButtonDrawableId(int)} respectively. If either of these is not set,
     * the cogwheel style equivalent will be automatically used instead</p>
     *
     * @param controlButtonStyle Control button style. Either {@link ControlSheet#NO_BUTTON},
*    *        {@link ControlSheet#COGWHEEL}, {@link ControlSheet#CHEVRON} or {@link ControlSheet#CUSTOM}
     * @return A {@link ControlSheet} object for method chaining
     */
    public ControlSheet setSheetControlButtonStyle(@ControlButtonStyle int controlButtonStyle){
        this.controlButtonStyle=controlButtonStyle;
        setUpSheetControlButton();

        if(stripButtons!=null && stripButtons.size()>0) {
            return setControlStripConstraints();
        }

        return this;
    }

    /**
     * <p>When the sheet control button's style is set to {@link ControlSheet#CUSTOM} ({@value ControlSheet#CUSTOM})
     * this method sets the custom drawable for the {@link ControlSheet}'s collapsed state</p>
     *
     * @param drawableId Id of the Drawable
     * @return A {@link ControlSheet} object for method chaining
     */
    public ControlSheet setCustomCollapsedButtonDrawableId(int drawableId){
        customCollapsedDrawableId=drawableId;
        return this;
    }

    /**
     * <p>When the sheet control button's style is set to {@link ControlSheet#CUSTOM} ({@value ControlSheet#CUSTOM})
     * this method sets the custom drawable for the {@link ControlSheet}'s expanded state</p>
     *
     * @param drawableId Id of the Drawable
     * @return A {@link ControlSheet} object for method chaining
     */
    public ControlSheet setCustomExpandedButtonDrawableId(int drawableId){
        customExpandedDrawableId=drawableId;
        return this;
    }

    ////User Buttons////

    /**
     * <p>Set the contro strip buttons' animation style.</p>
     *  <p>Accepted values are
     *     <ul>
     *          <li>Do not animate buttons {@link ControlSheet#NO_ANIMATION} ({@value ControlSheet#NO_ANIMATION})</li>
     *          <li>Spin buttons when pressed {@link ControlSheet#SPIN_BUTTON} ({@value ControlSheet#SPIN_BUTTON})</li>
     *          <li>Dip buttons when pressed {@link ControlSheet#DIP_BUTTON} ({@value ControlSheet#DIP_BUTTON})</li>
     *     </ul>
     * </p>
     * <p>When the sheet control button's style is set to {@link ControlSheet#CUSTOM}, it will use this animation. If set to
     * {@link ControlSheet#SPIN_BUTTON}, a spin and swap style animation will be used, just like with the {@link ControlSheet#CUSTOM}
     * style button. When {@link ControlSheet#DIP_BUTTON} animation is set, it will dip and swap the button drawables when state changes</p>
     *
     * @param style Animation style. Either {@link ControlSheet#NO_ANIMATION}, {@link ControlSheet#SPIN_BUTTON}, or {@link ControlSheet#DIP_BUTTON}
     * @return A {@link ControlSheet} object for method chaining
     */
    public ControlSheet setButtonAnimationStyle(@ControlButtonAnimationStyle int style) {
        buttonAnimationStyle = style;
        return this;
    }

    /**
     * <p>Turn the control strip on and off. If {@code true}, there is a strip, if {@code false}, there is not.</p>
     *
     * <p>Note: When the strip is turned off,a custom peek height should be set with {@link ControlSheet#setSheetPeekHeight(int)} or
     * {@link ControlSheet#setSheetPeekHeight(int, int)}, otherwise the sheet will only be expandable code.</p>
     *
     * @param visible Boolean, whether the stripis visible
     * @return A {@link ControlSheet} object for method chaining
     */
    public ControlSheet setHasControlStrip(boolean visible){
        controlStripVisible=visible;
        return setUpControlStrip();
    }

    /**
     * <p>Dynamically add a button to the control strip. You must specify a {@link res.drawable} id and optionally an
     * {@link View.OnClickListener} which will be automatically applied to the button.</p>
     *
     * <p>Buttons will appear in the rder in which they were attachd, stretching out between the control strip's start side and the sheet
     * control button if visible, or the controls trip's end side, if not</p>
     *
     * @param drawableId the id of the button1s drawable
     * @param onClickListener {@link View.OnClickListener} to set on the butotn 8can be null)
     * @return A {@link ControlSheet} object for method chaining
     */
    public ControlSheet addControlStripButton(final int drawableId, @Nullable final View.OnClickListener onClickListener){
        attachControlStripButton(drawableId, onClickListener);
        return setControlStripConstraints();
    }

    /**
     * <p>Enable or disable a control strip button in the specified position (starting from 1). Disabled buttons are not clickable and also dimmed</p>
     *
     * <p>Note: The numbering starts from <strong>1, <em>not</em> from 0</strong>, corresponding to the layout's visual position </p>
     *
     * @param position position of the button to be enabled or disabled (starting form one)
     * @param isEnabled boolean value {@code true} for enabled and {@code false} for disabled
     * @return A {@link ControlSheet} object for method chaining
     */
    public ControlSheet setControlStripButtonEnabled(@IntRange(from=1) int position, boolean isEnabled){
        if (position < controlStripLayout.getChildCount()) {
            controlStripLayout.findViewById(stripButtons.get(position - 1).getId()).setEnabled(isEnabled);
        } else {
            Log.e("ControlStrip", "You ain't got that many buttons either!");
        }
        return this;
    }

    /**
     * <p>Get a {@link ControlStripButton} object for direct manipualation from the specified position (starting form 1). {(@link ControlStripButton}s
     * are basically glirified ImageViews with a few extras.) You can not get the sheet control button this way, sorry.</p>
     *
     * <p>Note: The numbering starts from <strong>1, <em>not</em> from 0</strong>, corresponding to the layout's visual position </p>
     *
     * @param position of the button to get (startng from 1)
     * @return A {@link ControlStripButton} object
     */
    public ControlStripButton getControlStripButton(@IntRange(from=1) int position){
        if (position < controlStripLayout.getChildCount()) {
            return controlStripLayout.findViewById(stripButtons.get(position - 1).getId());
        } else {
            Log.e("ControlStrip", "You ain't got that many buttons either!");
            return null;
        }
    }

    /**
     * <p>Check whether the button in the specified position (starting form 1) is currently enabled</p>
     *
     * <p>Note: The numbering starts from <strong>1, <em>not</em> from 0</strong>, corresponding to the layout's visual position </p>
     *
     * @param position Position of the butotn to check 8starting form 1)
     * @return boolean of the button's "enabledness"
     */
    public boolean controlStripButtonIsEnabled(@IntRange(from=1) int position){
        if (position < controlStripLayout.getChildCount()) {
            return controlStripLayout.findViewById(stripButtons.get(position - 1).getId()).isEnabled();
        } else {
            Log.e("ControlStrip", "You ain't got that many buttons either!");
            return false;
        }
    }

    /**
     * <p>Get the current {@link Drawable} set on the control strip button in the specified position (starting from 1).</p>
     *
     * <p>Note: The numbering starts from <strong>1, <em>not</em> from 0</strong>, corresponding to the layout's visual position </p>
     *
     * @param position of the button to get {@link Drawable} of (starting from 1)
     * @return {@link Drawable} that is set on  the button
     */
    public Drawable getControlStripButtonDrawable(@IntRange(from=1) int position){
        if (position < controlStripLayout.getChildCount()) {
            return stripButtons.get(position - 1).getDrawable();
        } else {
            Log.e("ControlStrip", "You ain't got that many buttons either!");
            return null;
        }
    }

    /**
     * <p>Remove a control strip button form the specified position (starting from 1).</p>
     *
     * <p>Note: The numbering starts from <strong>1, <em>not</em> from 0</strong>, corresponding to the layout's visual position </p>
     *
     * @param position of the butotn to remove (starting from 1)
     */
    public ControlSheet removeControlStripButton(@IntRange(from=1) int position){
        if (position < controlStripLayout.getChildCount()) {
            controlStripLayout.removeView(controlStripLayout.findViewById(stripButtons.get(position - 1).getId()));
            stripButtons.remove(position - 1);
            return setControlStripConstraints();
        } else {
            Log.e("ControlStrip", "You ain't got that many buttons either!");
            return this;
        }

    }


    /**
     * Set a custom color to the controlstrip's buttons
     *
     * @param color a {@link ColorInt} of the color
     * @return A {@link ControlSheet} object for method chaining
     */
    public ControlSheet setControlStripButtonsColor(@ColorInt int color){
        buttonColor=color;
        return updateButtonColous();
    }


    /**********************ControlStrip stuff (private)*********************************************/


    /**
     * Internal methd to update every button's colour
     *
     * @return A {@link ControlSheet} object for method chaining
     */
    private ControlSheet updateButtonColous(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (sheetControlButton.getDrawable() != null) {
                sheetControlButton.getDrawable().setTint(buttonColor);
            }
            for (ControlStripButton button : stripButtons) {
                button.getDrawable().setTint(buttonColor);
            }
        } else {
            if (sheetControlButton.getDrawable() != null) {
                sheetControlButton.getDrawable().setColorFilter(buttonColor, PorterDuff.Mode.MULTIPLY);
            }
            for (ControlStripButton button : stripButtons) {
                button.getDrawable().setColorFilter(buttonColor, PorterDuff.Mode.MULTIPLY);
            }
        }

        return this;
    }

    /**
     * An attempot to resolve he theme's primary colour to set to the buttons, if not other colour is
     * specified. Defaults to black if primary colour cannot be resolved for any reason.
     *
     * @return The resolved {@link androidx.annotation.ColorInt}
     */
    private int resolveButtonColor() {

        if (buttonColor == Color.TRANSPARENT) {
            TypedValue outValue = new TypedValue();
            Resources.Theme theme = getContext().getTheme();
            boolean wasResolved =
                    theme.resolveAttribute(
                            android.R.attr.colorPrimary, outValue, true);
            if (wasResolved) {
                return outValue.resourceId == 0
                        ? outValue.data
                        : ContextCompat.getColor(
                        getContext(), outValue.resourceId);
            } else {
                // fallback colour handling
                return getResources().getColor(android.R.color.black);
            }
        } else {
            return buttonColor;
        }
    }

    /**
     * Sets up the visibility of the control strip
     *
     * @return  A {@link ControlSheet} object for method chaining
     */
    private ControlSheet setUpControlStrip(){

        if(getParent() instanceof CoordinatorLayout) {

            if (controlStripVisible) {
                controlStripLayout.setVisibility(View.VISIBLE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    controlStripLayout.setElevation(CONTROLSTRIP_ELEVATION);
                } else {
                    ViewCompat.setElevation(controlStripLayout, CONTROLSTRIP_ELEVATION);
                }
                sheetBehavior.setPeekHeight(CONTROLSTRIP_HEIGHT + sheetPeekHeight);
            } else {
                controlStripLayout.setVisibility(View.GONE);
                sheetBehavior.setPeekHeight(sheetPeekHeight);
            }
        } else {
            Log.e("ControlStrip", "Sheet is not attached to a CoordinatorLayout!");
        }
        return this;
    }

    /**
     * When sheet is expanded or collapsed, the control button should get animated
     *
     * @param sheetIsExpanded to know which way the animation should go
     */
    private void updateSheetControlButton(boolean sheetIsExpanded){
        ViewAnimations.animateSheetControlButton(sheetControlButton, sheetIsExpanded, sheetExpandedButtondrawable, sheetCollapsedButtonDrawable, buttonAnimationStyle, controlButtonStyle);
    }

    /**
     * <p>This is a <strong>poor</strong> way to resolve and set the {@link Drawable}(s) of the sheet control button</p>
     *
     * ToDO: there surely is a better (cleaner) way to do this, only I had very little time, and this seemdthe most straightforward...
     *
     * @return A {@link ControlSheet} object for method chaining
     */
    private ControlSheet setUpSheetControlButton(){

        if(sheetControlButton==null){
            sheetControlButton=new ImageView(getContext());
            sheetControlButton.setPadding(CONTROL_BUTTON_PADDING,CONTROL_BUTTON_PADDING,CONTROL_BUTTON_PADDING, CONTROL_BUTTON_PADDING);
            sheetControlButton.setId(generateViewId());

            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, CONTROLSTRIP_HEIGHT);
            sheetControlButton.setLayoutParams(params);
        }

        if(controlButtonStyle== NO_BUTTON){
            sheetControlButton.setVisibility(View.GONE);
        } else {
            sheetControlButton.setVisibility(View.VISIBLE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Resources.Theme theme = getContext().getTheme();
                if(controlButtonStyle==CHEVRON) {
                    sheetCollapsedButtonDrawable=getResources().getDrawable(R.drawable.ic_expand_less_18dp, theme);
                } else if (controlButtonStyle==COGWHEEL) {
                    sheetCollapsedButtonDrawable=getResources().getDrawable(R.drawable.ic_settings_18dp, theme);
                    sheetExpandedButtondrawable=getResources().getDrawable(R.drawable.ic_close_18dp, theme);
                } else {
                    if(customCollapsedDrawableId==0){
                        sheetCollapsedButtonDrawable=getResources().getDrawable(R.drawable.ic_settings_18dp, theme);
                    } else {
                        try {
                            sheetCollapsedButtonDrawable = getResources().getDrawable(customCollapsedDrawableId, theme);
                        } catch (Resources.NotFoundException e){
                            e.printStackTrace();
                            sheetCollapsedButtonDrawable=getResources().getDrawable(R.drawable.ic_settings_18dp, theme);
                        }
                    }
                    if(customExpandedDrawableId==0){
                        sheetExpandedButtondrawable=getResources().getDrawable(R.drawable.ic_close_18dp, theme);
                    } else {
                        try {
                            sheetExpandedButtondrawable=getResources().getDrawable(customExpandedDrawableId, theme);
                        } catch (Resources.NotFoundException e){
                            e.printStackTrace();
                            sheetExpandedButtondrawable=getResources().getDrawable(R.drawable.ic_close_18dp, theme);
                        }
                    }
                }

                sheetCollapsedButtonDrawable.setTint(buttonColor);
                if(sheetExpandedButtondrawable!=null){
                    sheetExpandedButtondrawable.setTint(buttonColor);
                }
            } else {
                if(controlButtonStyle==CHEVRON) {
                    sheetCollapsedButtonDrawable=getResources().getDrawable(R.drawable.ic_expand_less_18dp);
                } else if (controlButtonStyle==COGWHEEL) {
                    sheetCollapsedButtonDrawable=getResources().getDrawable(R.drawable.ic_settings_18dp);
                    sheetExpandedButtondrawable=getResources().getDrawable(R.drawable.ic_close_18dp);
                } else {
                    if(customCollapsedDrawableId==0){
                        sheetCollapsedButtonDrawable=getResources().getDrawable(R.drawable.ic_settings_18dp);
                    } else {
                        try {
                            sheetCollapsedButtonDrawable = getResources().getDrawable(customCollapsedDrawableId);
                        } catch (Resources.NotFoundException e){
                            e.printStackTrace();
                            sheetCollapsedButtonDrawable=getResources().getDrawable(R.drawable.ic_settings_18dp);
                        }
                    }
                    if(customExpandedDrawableId==0){
                        sheetExpandedButtondrawable=getResources().getDrawable(R.drawable.ic_close_18dp);
                    } else {
                        try {
                            sheetExpandedButtondrawable = getResources().getDrawable(customExpandedDrawableId);
                        } catch (Resources.NotFoundException e){
                            e.printStackTrace();
                            sheetExpandedButtondrawable=getResources().getDrawable(R.drawable.ic_close_18dp);
                        }
                    }
                }

                sheetCollapsedButtonDrawable.setColorFilter(buttonColor, PorterDuff.Mode.MULTIPLY);
                if(sheetExpandedButtondrawable!=null){
                    sheetExpandedButtondrawable.setColorFilter(buttonColor, PorterDuff.Mode.MULTIPLY);
                }
            }

            if(sheetBehavior==null){
                //We've got no behaviour yet
                sheetControlButton.setImageDrawable(sheetCollapsedButtonDrawable);
            } else {
                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED && sheetExpandedButtondrawable != null) {
                    sheetControlButton.setImageDrawable(sheetExpandedButtondrawable);
                } else {
                    sheetControlButton.setImageDrawable(sheetCollapsedButtonDrawable);
                }
            }
        }

        return this;
    }

    /**
     * <p>Create a {@link ControlStripButton} and attach it to the controlStrip</p>
     * <p>Note: this method returns a {@link ControlSheet} object for method chaining, and <strong>not</strong> {@link ControlStripButton} object
     * since we would not know where to put those or what to do with them. </p>
     *
     * @param drawableId the id of the drawable
     * @param onClickListener optional onclicklistener
     * @return A {@link ControlSheet} object for method chaining
     */
    private ControlSheet attachControlStripButton(final int drawableId, @Nullable final View.OnClickListener onClickListener){

        ControlStripButton button = new ControlStripButton(getContext(),
                CONTROL_BUTTON_PADDING,
                buttonAnimationStyle,
                onClickListener)
                .setId(generateViewId(), true);

        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, CONTROLSTRIP_HEIGHT);
        button.setLayoutParams(params);

        Drawable buttonDrawable;

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            buttonDrawable=getResources().getDrawable(drawableId, getContext().getTheme());
        } else {
            buttonDrawable=getResources().getDrawable(drawableId);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            buttonDrawable.setTint(buttonColor);
        }else {
            buttonDrawable.setColorFilter(buttonColor, PorterDuff.Mode.MULTIPLY);
        }

        button.setImageDrawable(buttonDrawable);

        if(stripButtons==null){
            stripButtons=new ArrayList<>();
        }

        stripButtons.add(button);
        controlStripLayout.addView(button);

        return this;
    }

    /**
     * (Re-)sets the constraints on the control strip, essentially creating a chain, if there is more then one button, or aligning the
     * control strip button ot the right if there are no user defined ones
     *
     * @return A {@link ControlSheet} object for method chaining
     */
    private ControlSheet setControlStripConstraints(){

        if(controlStripLayout.findViewById(sheetControlButton.getId())==null) {
            controlStripLayout.addView(sheetControlButton);
        }

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(controlStripLayout);

        constraintSet.connect(sheetControlButton.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        constraintSet.connect(sheetControlButton.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);

        if(stripButtons!=null && stripButtons.size()>1) {

            constraintSet.clear(sheetControlButton.getId(), ConstraintSet.END);

            for (ControlStripButton stripButton : stripButtons) {
                constraintSet.connect(stripButton.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
                constraintSet.connect(stripButton.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
            }

            int[] viewIds = new int[stripButtons.size() + 1];

            for (int i = 0; i < viewIds.length - 1; i++) {

                viewIds[i] = stripButtons.get(i).getId();
            }

            viewIds[viewIds.length - 1] = sheetControlButton.getId();

            constraintSet.createHorizontalChain(ConstraintSet.PARENT_ID, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, viewIds, null, ConstraintSet.CHAIN_SPREAD_INSIDE);
        } else {
            constraintSet.connect(sheetControlButton.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        }

        constraintSet.applyTo(controlStripLayout);


        controlStripLayout.invalidate();
        return this;
    }



    /***********************************Handle Listeners*******************************************/

    /**
     * Set the {@link ControlSheetInflatedListener}
     * @param listener Listener to liste with
     * @return A {@link ControlSheet} object for method chaining
     */
    public ControlSheet addControlSheetInflatedListener(ControlSheetInflatedListener listener) {
        this.inflatedListener = listener;
        return this;
    }

    /**
     * Returns the current {@link ControlSheetInflatedListener} or {@code null}
     * @return the current {@link ControlSheetInflatedListener} or {@code null}
     */
    public ControlSheetInflatedListener getControlSheetInflatedListener(){
        return inflatedListener;
    }

    /**
     * Set the {@link ControlSheetStateChangedListener}
     * @param listener Listener to listen with
     * @return A {@link ControlSheet} object for method chaining
     */
    public ControlSheet addControlSheetStateChangedListener(ControlSheetStateChangedListener listener) {
        this.stateChangedListener=listener;
        return this;
    }

    /**
     * Returns the current {@link ControlSheetStateChangedListener} or {@code null}
     * @return the current {@link ControlSheetStateChangedListener} or {@code null}
     */
    public ControlSheetStateChangedListener getControlSheetStateChangedListener() {
        return stateChangedListener;
    }


}

