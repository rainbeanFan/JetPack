package cn.rainbean.jetpack.ui.home;

import android.annotation.SuppressLint;

import com.alibaba.fastjson.TypeReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.PagedList;
import cn.rainbean.jetpack.MutablePageKeyedDataSource;
import cn.rainbean.jetpack.module.Feed;
import cn.rainbean.jetpack.ui.AbsViewModel;
import cn.rainbean.jetpack.ui.login.UserManager;
import cn.rainbean.networklibrary.ApiResponse;
import cn.rainbean.networklibrary.ApiService;
import cn.rainbean.networklibrary.JsonCallback;
import cn.rainbean.networklibrary.Request;

public class HomeViewModel extends AbsViewModel<Feed> {

    private volatile boolean witchCache = true;
    private MutableLiveData<PagedList<Feed>> cacheLiveData = new MutableLiveData<>();
    private AtomicBoolean loadAfter = new AtomicBoolean(false);
    private String mFeedType;

    @Override
    public DataSource createDataSource() {
        return new FeedDataSource();
    }

    public MutableLiveData<PagedList<Feed>> getCacheLiveData() {
        return cacheLiveData;
    }

    public void setFeedType(String feedType){
        mFeedType = feedType;
    }

    class FeedDataSource extends ItemKeyedDataSource<Integer,Feed>{

        @Override
        public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Feed> callback) {
            loadData(0,params.requestedLoadSize,callback);
            witchCache = false;
        }

        @Override
        public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Feed> callback) {
            loadData(params.key,params.requestedLoadSize,callback);
        }

        @Override
        public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Feed> callback) {
            callback.onResult(Collections.emptyList());
        }

        @NonNull
        @Override
        public Integer getKey(@NonNull Feed item) {
            return item.id;
        }
    }



    private void loadData(int key,int count,ItemKeyedDataSource.LoadCallback<Feed> callback){
        if (key>0){
            loadAfter.set(true);
        }

        Request request = ApiService.get("/feeds/queryHotFeedsList")
                .addParams("feedType", mFeedType)
                .addParams("userId", UserManager.get().getUserId())
                .addParams("feedId", key)
                .addParams("pageCount", count)
                .responseType(new TypeReference<ArrayList<Feed>>() {
                }.getType());

        if (witchCache){
            request.cacheStrategy(Request.CACHE_ONLY);
            request.execute(new JsonCallback<List<Feed>>() {
                @Override
                public void onCacheSuccess(ApiResponse<List<Feed>> response) {

                    MutablePageKeyedDataSource<Feed> dataSource = new MutablePageKeyedDataSource<>();
                    dataSource.data.addAll(response.body);

                    PagedList<Feed> pagedList = dataSource.buildNewPagedList(config);
                    cacheLiveData.postValue(pagedList);

                }
            });
        }

        try{
            Request netRequest = witchCache?request.clone():request;
            netRequest.cacheStrategy(key == 0? Request.NET_CACHE:Request.NET_ONLY);
            ApiResponse<List<Feed>> response = netRequest.execute();
            List<Feed> data = response.body == null? Collections.emptyList():response.body;

            callback.onResult(data);

            if (key>0){
                ((MutableLiveData)getBoundaryPageData()).postValue(data.size()>0);
                loadAfter.set(false);
            }

        }catch (CloneNotSupportedException e){
            e.printStackTrace();
        }

    }


    @SuppressLint("RestrictedApi")
    public void loadAfter(int id, ItemKeyedDataSource.LoadCallback<Feed> callback){
        if (loadAfter.get()){
            callback.onResult(Collections.emptyList());
            return;
        }
        ArchTaskExecutor.getIOThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                loadData(id,config.pageSize,callback);
            }
        });
    }


}
