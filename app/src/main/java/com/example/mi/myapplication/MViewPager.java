package com.example.mi.myapplication;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MViewPager extends ViewPager {
    public MViewPager(Context context) {
        super(context);
    }

    public MViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//        return true;
//    }
//
//    @Override
//    public boolean onInterceptHoverEvent(MotionEvent event) {
//        return false;
//    }
}
