package com.atlanticomnibus.controlsheet;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import static com.atlanticomnibus.controlsheet.ControlSheet.BUTTON_ANIMATION_DURATION;
import static com.atlanticomnibus.controlsheet.ControlSheet.CHEVRON;
import static com.atlanticomnibus.controlsheet.ControlSheet.CUSTOM;
import static com.atlanticomnibus.controlsheet.ControlSheet.DIP_BUTTON;
import static com.atlanticomnibus.controlsheet.ControlSheet.NO_ANIMATION;
import static com.atlanticomnibus.controlsheet.ControlSheet.SPIN_BUTTON;



class ViewAnimations {


    public static void animateButton(View v, int animationStyle) {

        if (animationStyle == SPIN_BUTTON) {
            spinButton(v);
        } else if (animationStyle == DIP_BUTTON) {
            dipButton(v);
        }
    }

    public static void animateSheetControlButton(final ImageView view, final boolean sheetIsExpanded, Drawable expandedDrawable, Drawable collapsedDrawable, int animationStyle, int controlButtonStyle){

        if(controlButtonStyle==CHEVRON) {
            turnChevron(view, sheetIsExpanded,animationStyle!=NO_ANIMATION);
        } else if(animationStyle==SPIN_BUTTON){
            spinAndSwapButton(view, sheetIsExpanded,expandedDrawable,collapsedDrawable);
        } else if(animationStyle==DIP_BUTTON){
            if(controlButtonStyle==CUSTOM) {
                dipAndSwapButton(view, sheetIsExpanded, expandedDrawable, collapsedDrawable);
            } else {
                spinAndSwapButton(view, sheetIsExpanded,expandedDrawable,collapsedDrawable);
            }
        } else {
            if (sheetIsExpanded) {
                view.setImageDrawable(expandedDrawable);
            } else {
                view.setImageDrawable(collapsedDrawable);
            }
        }
    }



    private static void dipButton(View view){
        view.setPivotY((float)view.getMeasuredHeight()/2.0f);
        view.setPivotX((float)view.getMeasuredWidth()/2.0f);

        ObjectAnimator animatorX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 0.5f);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 0.5f);
        animatorX.setRepeatMode(ObjectAnimator.REVERSE);
        animatorY.setRepeatMode(ObjectAnimator.REVERSE);
        animatorX.setRepeatCount(1);
        animatorY.setRepeatCount(1);

        final AnimatorSet animSetXY = new AnimatorSet();
        animSetXY.playTogether(animatorX, animatorY);
        animSetXY.setDuration(BUTTON_ANIMATION_DURATION/3);
        animSetXY.start();
    }


    /**
     * Simple animation to spin a view 360degrees around its on midpoint. Used (here) to animate buttons
     * @param view
     */
    private static void spinButton(final View view) {
        view.setPivotY((float) view.getMeasuredHeight() / 2.0f);
        view.setPivotX((float) view.getMeasuredWidth() / 2.0f);
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", 0, 360);
        animator.setDuration(BUTTON_ANIMATION_DURATION);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
    }

    /**This one turns an ImageView, TWICE, swapping the Drawable after the first round. the second turn
     * follows seamlessly, so the effect is a "magic spin" that changes the button's appearance
     * @param view ImageView to be animated
     */
    private static void spinAndSwapButton(final ImageView view, final boolean sheetIsExpanded, final Drawable expandedDrawable, final Drawable collapsedDrawable){
        view.setPivotY((float) view.getMeasuredHeight() / 2.0f);
        view.setPivotX((float) view.getMeasuredWidth() / 2.0f);

        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", 0, 360);
        animator.setDuration(BUTTON_ANIMATION_DURATION);
        animator.setRepeatCount(1);
        animator.setInterpolator(new AccelerateInterpolator());

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationRepeat(Animator animation) {
                if (sheetIsExpanded) {
                    view.setImageDrawable(expandedDrawable);
                } else {
                    view.setImageDrawable(collapsedDrawable);
                }

                view.setPivotY((float) view.getMeasuredHeight() / 2.0f);
                view.setPivotX((float) view.getMeasuredWidth() / 2.0f);

                animation.setInterpolator(new DecelerateInterpolator());
                super.onAnimationRepeat(animation);
            }
        });
        animator.start();

    }

    private static void dipAndSwapButton(final ImageView view, final boolean sheetIsExpanded, final Drawable expandedDrawable, final Drawable collapsedDrawable){
        view.setPivotY((float)view.getMeasuredHeight()/2.0f);
        view.setPivotX((float)view.getMeasuredWidth()/2.0f);

        ObjectAnimator animatorX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 0f);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 0f);
        animatorY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationRepeat(Animator animation) {
                if (sheetIsExpanded) {
                    view.setImageDrawable(expandedDrawable);
                } else {
                    view.setImageDrawable(collapsedDrawable);
                }
                animation.setInterpolator(new DecelerateInterpolator());
                super.onAnimationRepeat(animation);
            }
        });

        animatorX.setRepeatMode(ObjectAnimator.REVERSE);
        animatorY.setRepeatMode(ObjectAnimator.REVERSE);
        animatorX.setRepeatCount(1);
        animatorY.setRepeatCount(1);
        final AnimatorSet animSetXY = new AnimatorSet();
        animSetXY.playTogether(animatorX, animatorY);
        animSetXY.setDuration(BUTTON_ANIMATION_DURATION/2);
        animSetXY.start();
    }

    private static void turnChevron(final View chevron, boolean sheetIsExpanded, boolean animate){

        int from, to;

        if(sheetIsExpanded){
            from=0;
            to=-180;
        } else{
            from=-180;
            to=0;
        }

        if(animate) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(chevron, "rotation", from, to);
            animator.setDuration(BUTTON_ANIMATION_DURATION);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.start();
        } else {
            chevron.setRotation(to);
        }
    }
}
