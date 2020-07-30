package cn.rainbean.basemodule;

import android.annotation.SuppressLint;
import android.app.Application;

import java.lang.reflect.InvocationTargetException;


/**
 * 组件化获取Application
 */
public class GlobalApp {

    private static Application sApplication;

    @SuppressLint("PrivateApi")
    public static Application getApplication() {
        if (sApplication ==null){
            try{
                sApplication = (Application) Class.forName("android.app.ActivityThread")
                        .getMethod("currentApplication")
                        .invoke(null,(Object[]) null);
            }catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e){
                e.printStackTrace();
            }
        }
        return sApplication;
    }
}
