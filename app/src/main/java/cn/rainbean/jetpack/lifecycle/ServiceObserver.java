package cn.rainbean.jetpack.lifecycle;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

public class ServiceObserver implements LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private void startLocation(){

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private void stopLocation(){

    }
}
