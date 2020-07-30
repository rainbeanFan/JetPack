package cn.rainbean.basemodule.utils;

import android.util.DisplayMetrics;

import cn.rainbean.basemodule.GlobalApp;

public class PixUtils {

    public static int dp2px(int dpValue){
        DisplayMetrics metrics = GlobalApp.getApplication().getResources().getDisplayMetrics();
        return (int) (metrics.density*dpValue+0.5f);
    }

    public static int getScreenWidth(){
        DisplayMetrics metrics = GlobalApp.getApplication().getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }

    public static int getScreenHeight(){
        DisplayMetrics metrics = GlobalApp.getApplication().getResources().getDisplayMetrics();
        return metrics.heightPixels;
    }

}
