package com.speedy.radar;

import android.app.Activity;
import android.view.WindowManager;

/**
 * Created by Speedy on 2017/5/4.
 */
public class WindowUtils {


    /**
     * 隐藏状态栏（全屏显示）
     * @param activity
     */
    public static void hideStatusBar(Activity activity){
        WindowManager.LayoutParams params = activity.getWindow().getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        activity.getWindow().setAttributes(params);
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    /**
     * 显示状态栏（退出全屏显示）
     * @param activity
     */
    public static void showStatusBar(Activity activity){
        WindowManager.LayoutParams params = activity.getWindow().getAttributes();
        params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity.getWindow().setAttributes(params);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

}
