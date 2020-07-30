package cn.rainbean.jetpack;

import android.app.Application;

import androidx.lifecycle.ProcessLifecycleOwner;
import cn.rainbean.jetpack.lifecycle.ApplicationObserver;
import cn.rainbean.networklibrary.ApiService;

public class JPApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ApiService.init("http://123.56.232.18:8080/serverdemo",null);

        ProcessLifecycleOwner.get().getLifecycle().addObserver(new ApplicationObserver());
    }

}
