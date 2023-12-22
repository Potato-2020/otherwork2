package me.goldze.mvvmhabit.http;

import java.io.IOException;

/**
 * Created by goldze on 2017/5/11.
 */

public class ResponseThrowable extends IOException {
    public int code;
    public String message;

    public ResponseThrowable(Throwable throwable, int code) {
        super(throwable);
        this.code = code;
    }
}
