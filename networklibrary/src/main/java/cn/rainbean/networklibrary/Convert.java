package cn.rainbean.networklibrary;

import java.lang.reflect.Type;

public interface Convert<T> {

    T convert(String response, Type type);

}
