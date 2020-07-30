package cn.rainbean.networklibrary.cache;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cache")
public class Cache implements Serializable {

    @PrimaryKey()
    @NotNull
    public String key;

    public byte[] data;

}
