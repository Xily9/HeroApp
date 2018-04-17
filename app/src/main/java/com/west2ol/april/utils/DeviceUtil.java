package com.west2ol.april.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.west2ol.april.MyApplication;

public class DeviceUtil {

    private static final DisplayMetrics outMetrics= MyApplication.getInstance().getResources().getDisplayMetrics();

    /**
     * 获取设备宽度
     * @return
     */
    public static int getWidth() {
        return outMetrics.widthPixels;
    }

    /**
     * 获取设备高度
     * @return
     */
    public static int getHeight() {
        return outMetrics.heightPixels;
    }

    /**
     * dp转px
     * @param dpValue dp值
     * @return
     */
    public static int dip2px(float dpValue) {
        return ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, outMetrics));
    }

    /**
     * 显示或隐藏StatusBar
     *
     * @param enable false 显示，true 隐藏
     */
    public static void hideStatusBar(Window window, boolean enable) {
        WindowManager.LayoutParams p = window.getAttributes();
        if (enable) {
            p.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        } else {
            p.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        window.setAttributes(p);
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    public static void hideSoftInput(Activity activity) {
        InputMethodManager imm = (InputMethodManager) MyApplication.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
