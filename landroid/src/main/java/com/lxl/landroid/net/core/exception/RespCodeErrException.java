package com.lxl.landroid.net.core.exception;

/**
 * Created by lxl on 16-1-17.
 */
public class RespCodeErrException extends RuntimeException {


    public RespCodeErrException() {
    }

    public RespCodeErrException(String detailMessage) {
        super(detailMessage);
    }

    public RespCodeErrException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public RespCodeErrException(Throwable throwable) {
        super(throwable);
    }

    /**
     * 相应码错误时从缓存取出的数据 由开发者决定是否使用该缓存
     */
    private byte[] cacheData;

    public byte[] getCacheData() {
        return cacheData;
    }

    public void setCacheData(byte[] cacheData) {
        this.cacheData = cacheData;
    }
}
