package com.ipusoft.siplibrary;

/**
 * Created by GWFan on 2019/9/28.
 */


import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class KeyEventLinearLayout extends LinearLayout {

    private DispatchKeyEventListener mDispatchKeyEventListener;

    public KeyEventLinearLayout(Context context) {
        super(context);
    }

    public KeyEventLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public KeyEventLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (mDispatchKeyEventListener != null) {
            return mDispatchKeyEventListener.dispatchKeyEvent(event);
        }
        return super.dispatchKeyEvent(event);
    }

    public DispatchKeyEventListener getDispatchKeyEventListener() {
        return mDispatchKeyEventListener;
    }

    public void setDispatchKeyEventListener(DispatchKeyEventListener mDispatchKeyEventListener) {
        this.mDispatchKeyEventListener = mDispatchKeyEventListener;
    }

    public interface DispatchKeyEventListener {
        boolean dispatchKeyEvent(KeyEvent event);
    }

}
