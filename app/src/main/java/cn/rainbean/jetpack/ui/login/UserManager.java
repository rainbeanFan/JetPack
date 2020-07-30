package cn.rainbean.jetpack.ui.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import cn.rainbean.basemodule.GlobalApp;
import cn.rainbean.jetpack.module.User;
import cn.rainbean.networklibrary.ApiResponse;
import cn.rainbean.networklibrary.ApiService;
import cn.rainbean.networklibrary.JsonCallback;
import cn.rainbean.networklibrary.cache.CacheManager;

public class UserManager {

    private static final String KEY_CACHE_USER = "cache_user";
    private static UserManager mUserManager = new UserManager();
    private MutableLiveData<User> userLiveData;
    private User mUser;

    public static UserManager get(){
        return mUserManager;
    }

    private UserManager(){
        User cache = (User) CacheManager.getCache(KEY_CACHE_USER);
        if (cache!=null && cache.expires_time>System.currentTimeMillis()){
            mUser = cache;
        }
    }

    public void save(User user){
        mUser = user;
        CacheManager.save(KEY_CACHE_USER,user);
        if (getUserLiveData().hasObservers()){
            getUserLiveData().postValue(user);
        }
    }

    public LiveData<User> login(Context context){
        Intent intent = new Intent(context, LoginActivity.class);
        if (!(context instanceof Activity)){
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
        return getUserLiveData();
    }

    public boolean isLogin(){
        if (mUser == null) return false;
        return mUser.expires_time > System.currentTimeMillis();
    }

    public User getUser(){
        return isLogin()?mUser:null;
    }

    public long getUserId(){
        return isLogin()?mUser.userId:0;
    }

    public LiveData<User> refresh(){
        if (!isLogin()){
            return login(GlobalApp.getApplication());
        }
        MutableLiveData<User> liveData = new MutableLiveData<>();

        ApiService.get("/user/query")
                .addParams("userId",getUserId())
                .execute(new JsonCallback<User>() {
                    @Override
                    public void onSuccess(ApiResponse<User> response) {
                        save(response.body);
                        liveData.postValue(getUser());
                    }

                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onError(ApiResponse<User> response) {
                        ArchTaskExecutor.getMainThreadExecutor().execute(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GlobalApp.getApplication(),response.message,Toast.LENGTH_SHORT).show();
                            }
                        });
                        liveData.postValue(null);
                    }
                });
        return liveData;
    }
    /**
     * bugfix:  liveData默认情况下是支持黏性事件的，即之前已经发送了一条消息，当有新的observer注册进来的时候，也会把先前的消息发送给他，
     * <p>
     * 就造成了{@linkplain MainActivity#onNavigationItemSelected(MenuItem) }死循环
     * <p>
     * 那有两种解决方法
     * 1.我们在退出登录的时候，把livedata置为空，或者将其内的数据置为null
     * 2.利用我们改造的stickyLiveData来发送这个登录成功的事件
     * <p>
     * 我们选择第一种,把livedata置为空
     */
    public void logout() {
        CacheManager.delete(KEY_CACHE_USER, mUser);
        mUser = null;
        userLiveData = null;
    }

    private MutableLiveData<User> getUserLiveData() {
        if (userLiveData == null) {
            userLiveData = new MutableLiveData<>();
        }
        return userLiveData;
    }

}
