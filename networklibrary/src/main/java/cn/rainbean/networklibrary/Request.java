package cn.rainbean.networklibrary;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.arch.core.executor.ArchTaskExecutor;
import cn.rainbean.networklibrary.cache.CacheManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public abstract class Request<T,R extends Request> implements Cloneable {

    protected String mUrl;
    protected HashMap<String,String> headers = new HashMap<>();
    protected HashMap<String,Object> params = new HashMap<>();

    public static final int CACHE_ONLY = 1;
    public static final int CACHE_FIRST= 2;
    public static final int NET_ONLY = 3;
    public static final int NET_CACHE = 4;

    private String cacheKey;
    private Type mType;

    private int mCacheStrategy = NET_ONLY;

    @IntDef({CACHE_ONLY,CACHE_FIRST,NET_ONLY,NET_CACHE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CacheStrategy{

    }


    public Request(String url){
        mUrl = url;
    }

    public R addHeader(String key,String value){
        headers.put(key, value);
        return (R) this;
    }

    public R addParams(String key,Object value){
        if (value == null){
            return (R) this;
        }

        try{
            if (value.getClass() == String.class){
                params.put(key, value);
            }else {
                Field field = value.getClass().getField("TYPE");
                Class claz = (Class) field.get(null);
                if (claz.isPrimitive()){
                    params.put(key, value);
                }
            }
        }catch (NoSuchFieldException | IllegalAccessException e){
            e.printStackTrace();
        }
        return (R) this;
    }


    public R cacheStrategy(@CacheStrategy int cacheStrategy){
        mCacheStrategy = cacheStrategy;
        return (R) this;
    }


    public R cacheKey(String cacheKey){
        this.cacheKey = cacheKey;
        return (R) this;
    }

    public R responseType(Type type){
        mType = type;
        return (R) this;
    }

    public R responseType(Class claz){
        mType = claz;
        return (R) this;
    }


    private Call getCall(){
        okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
        addHeaders(builder);
        okhttp3.Request request = generateRequest(builder);
        return ApiService.okHttpClient.newCall(request);
    }

    protected abstract okhttp3.Request generateRequest(okhttp3.Request.Builder builder);

    private void addHeaders(okhttp3.Request.Builder builder){
        for (Map.Entry<String,String> entry:headers.entrySet()){
            builder.addHeader(entry.getKey(),entry.getValue());
        }
    }


    public ApiResponse<T> execute(){
        if (mType == null){
            throw new RuntimeException("同步方法，response返回值必须设置类型！");
        }
        if (mCacheStrategy == CACHE_ONLY){
            return readCache();
        }

        if (mCacheStrategy!=CACHE_ONLY){
            ApiResponse<T> result = null;
            try{
                Response response = getCall().execute();
                result = parseResponse(response,null);
            }catch (IOException e){
                e.printStackTrace();
                result = new ApiResponse<>();
                result.message = e.getMessage();
            }
            return result;
        }
        return null;
    }

    @SuppressLint("RestrictedApi")
    public void execute(final JsonCallback callback){

        if (mCacheStrategy !=NET_ONLY){
            ArchTaskExecutor.getIOThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    ApiResponse<T> response = readCache();
                    if (callback!=null && response.body !=null){
                        callback.onCacheSuccess(response);
                    }
                }
            });
        }

        if (mCacheStrategy !=CACHE_ONLY){
            getCall().enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    ApiResponse<T> result = new ApiResponse<>();
                    result.message = e.getMessage();
                    callback.onError(result);
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    ApiResponse<T> result = parseResponse(response, callback);
                    if (!result.success){
                        callback.onError(result);
                    }else {
                        callback.onSuccess(result);
                    }

                }
            });
        }

    }

    private ApiResponse<T> readCache() {
        String key = TextUtils.isEmpty(cacheKey) ? generateCacheKey() : cacheKey;
        Object cache = CacheManager.getCache(key);
        ApiResponse<T> result = new ApiResponse<>();
        result.status = 304;
        result.message = "缓存获取成功";
        result.body = (T) cache;
        result.success = true;
        return result;
    }


    private ApiResponse<T> parseResponse(Response response,JsonCallback<T> callback){

        String message = null;
        int status = response.code();

        boolean success = response.isSuccessful();

        ApiResponse<T> result = new ApiResponse<>();
        Convert convert = ApiService.sConvert;

        try{
            String content = response.body().string();
            if (success){
                if (callback !=null){
                    ParameterizedType type = (ParameterizedType) callback.getClass().getGenericSuperclass();
                    Type argument = type.getActualTypeArguments()[0];
                    result.body = (T) convert.convert(content,argument);
                }else if (mType !=null){
                    result.body = (T) convert.convert(content,mType);
                }else {
//                    无法解析
                }
            }
        }catch (Exception e){
            message = e.getMessage();
            success = false;
            status = 0;
        }

        result.success = success;
        result.status = status;
        result.message = message;

        if (mCacheStrategy!= NET_ONLY && result.success && result.body !=null && result.body instanceof Serializable){

        }

        return result;
    }

    private void saveCache(T body){
        String key = TextUtils.isEmpty(cacheKey)?generateCacheKey():cacheKey;
        CacheManager.save(key,body);
    }

    private String generateCacheKey(){
        cacheKey = UrlCreator.createUrlFromParams(mUrl,params);
        return cacheKey;
    }

    @NonNull
    @Override
    public Request clone() throws CloneNotSupportedException {
        return (Request<T, R>) super.clone();
    }

}
