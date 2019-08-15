package com.atlanticomnibus.controlsheet;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.appcompat.widget.AppCompatImageView;

public class ControlStripButton extends AppCompatImageView implements View.OnTouchListener {

    int animationStyle;

    public ControlStripButton(Context context, int padding, int animationStyle, final View.OnClickListener onClickListener){
        super(context);
        this.animationStyle=animationStyle;
        setOnTouchListener(this);
        setPadding(padding, padding, padding, padding);
        setOnClickListener(onClickListener);

    }

    public ControlStripButton setId(@IdRes int id, boolean makeChainable){

        super.setId(id);

        if (makeChainable) {
            return this;
        } else {
            return null;
        }
    }


    @Override
    public boolean performClick() {

        // This is not exactly used at the moment, but you can cusotmise the buttons to have a
        // uniform onClick behaviour that executes before the supplied onClick method.
        // But only if you want to.

        return super.performClick();
    }

    @Override
    public void setEnabled(boolean enabled) {
        if(!enabled){
            setAlpha(0.5f);
            setClickable(false);

        } else {
            this.setAlpha(1.0f);
            setClickable(true);
        }
        super.setEnabled(enabled);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_DOWN){
            ViewAnimations.animateButton(v, animationStyle);
        }
        return false;
    }
}