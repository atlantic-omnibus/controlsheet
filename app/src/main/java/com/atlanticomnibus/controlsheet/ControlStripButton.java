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
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.appcompat.widget.AppCompatImageView;

@SuppressWarnings("unused")
@SuppressLint("ViewConstructor")
public class ControlStripButton extends AppCompatImageView implements View.OnTouchListener {

    private final int animationStyle;

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


    @SuppressWarnings("EmptyMethod")
    @Override
    public boolean performClick() {

        // This is not exactly used at the moment, but you can customise the buttons to have a
        // uniform onClick behaviour that executes before the supplied onClick method.
        // But only if you want to.
        // Lint warning for empty method is suppressed, just remove the hint of you use this

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