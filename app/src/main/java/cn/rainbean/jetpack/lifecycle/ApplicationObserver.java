package cn.rainbean.jetpack.lifecycle;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

public class ApplicationObserver implements LifecycleObserver {

    /**
     * 整个应用生命周期仅调用一次
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreated(){

    }

    /**
     * 应用在前台时调用
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart(){

    }

    /**
     * 应用在前台时调用
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume(){

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause(){

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop(){

    }

}
