package com.ibd.dipper.net.GsonConverterFactory;

import com.google.gson.Gson;
import com.ibd.dipper.utils.JsonUtils;

import java.io.IOException;
import java.lang.reflect.Type;

import me.goldze.mvvmhabit.http.BaseResponse;
import okhttp3.ResponseBody;
import retrofit2.Converter;

public class MyGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    private final Gson gson;
    private final Type type;

    MyGsonResponseBodyConverter(Gson gson, Type type) {
        this.gson = gson;
        this.type = type;
    }


    @Override
    public T convert(ResponseBody value) throws IOException {
        String response = value.string();
        try {
            BaseResponse baseBean = JsonUtils.fromJson(response, BaseResponse.class);//拦截code非0情况

            response = JsonUtils.toJson(baseBean);
            return gson.fromJson(response, type);
        } finally {
            value.close();
        }
    }
}