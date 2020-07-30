package cn.rainbean.networklibrary.cache;

import androidx.room.Room;
import androidx.room.RoomDatabase;
import cn.rainbean.basemodule.GlobalApp;

public abstract class CacheDatabase extends RoomDatabase {

    private static final CacheDatabase database;

    static {
        database = Room.databaseBuilder(GlobalApp.getApplication(),CacheDatabase.class,"jetpack_cache")
                .allowMainThreadQueries()
                .build();
    }


    public abstract CacheDao getCache();

    public static CacheDatabase get(){return database;}

}
