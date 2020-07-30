package cn.rainbean.jetpack.lifecycle;

import android.app.Activity;
import android.content.Context;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

public class LocationListener implements LifecycleObserver {

    public LocationListener(Activity activity, OnLocationChangeListener onLocationChangelistener) {
        initLocationManager(activity);
    }

    private void initLocationManager(Context context) {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private void startGetLocation(){

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private void stopGetLocation(){

    }

    public interface OnLocationChangeListener{
        void onChanged(double latitude,double longitude);
    }

}
