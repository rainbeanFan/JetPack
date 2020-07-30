package cn.rainbean.networklibrary;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Type;

public class JsonConvert implements Convert {

    @Override
    public Object convert(String response, Type type) {
        JSONObject jsonObject = JSON.parseObject(response);
        JSONObject outData = jsonObject.getJSONObject("data");
        if (outData!=null){
            Object data = outData.get("data");
            assert data != null;
            return JSON.parseObject(data.toString(),type);
        }
        return null;
    }

}
