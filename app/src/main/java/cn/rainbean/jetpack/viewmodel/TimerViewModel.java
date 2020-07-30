package cn.rainbean.jetpack.viewmodel;

import java.util.Timer;
import java.util.TimerTask;

import androidx.lifecycle.ViewModel;

public class TimerViewModel extends ViewModel {

    private Timer timer;
    private int currentSecond;

    private OnTimeChangeListener onTimeChangeListener;

    public void setOnTimeChangeListener(OnTimeChangeListener onTimeChangeListener) {
        this.onTimeChangeListener = onTimeChangeListener;
    }

    public void startTiming(){
        if (timer == null){
            currentSecond = 0;
            timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    currentSecond++;
                    if (onTimeChangeListener!=null){
                        onTimeChangeListener.onTimeChanged(currentSecond);
                    }
                }
            };
            timer.schedule(timerTask,1000,1000);
        }
    }

    public interface OnTimeChangeListener{
        void onTimeChanged(int second);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        timer.cancel();
    }

}
