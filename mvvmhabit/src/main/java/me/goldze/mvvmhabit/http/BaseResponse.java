package me.goldze.mvvmhabit.http;

/**
 * Created by goldze on 2017/5/10.
 * 该类仅供参考，实际业务返回的固定字段, 根据需求来定义，
 */
public class BaseResponse<T> {
    public int code;
    public String message;
    public T data;
    public boolean isOk() {
        return code == 1;
    }
}
