package com.lxl.landroid.net.core;

import android.support.annotation.NonNull;

import com.lxl.landroid.net.core.utils.Access;
import com.lxl.landroid.net.core.utils.Access.onResponseOrErrorCallback;
import com.lxl.landroid.net.core.utils.AccessParams;
import com.lxl.landroid.net.core.utils.Method;
public class HttpRequest {

	private Cache cache;
	private Processer processer;

	
	public HttpRequest(Cache cache, @NonNull Processer processer) {
		this.cache = cache;
		this.processer = processer;
	}
	public HttpRequest() {
	}

	private boolean isCache;
	
	public boolean isCache() {
		return isCache;
	}
	public void setCache(boolean isCache) {
		this.isCache = isCache;
	}
	public void sendReq(String url) {
		sendReq(url, null);
	}
	public void sendReq(String url, AccessParams params) {
		sendReq(url, params, null, null);
	}
	
	public void sendReq(String url,AccessParams params,onResponseCallback  responseCallback,onErrorCallback errorCallback){
		sendReq(Method.GET, url, params, responseCallback, errorCallback);
	}
	public void sendReq(Method method,String url,AccessParams params,final onResponseCallback  responseCallback,final onErrorCallback errorCallback){
		if(url==null)
			throw new RuntimeException("url 为空");
		Access access=new Access();
		access.setMethod(method);
		access.setCache(cache);
		access.setParams(params);
		access.setUrl(url);
		access.setUseCache(isCache);
		access.setResponseOrErrorCallback(new onResponseOrErrorCallback() {
			
			@Override
			public void onSuccess(byte[] data) {
				if(responseCallback!=null)
					responseCallback.onResponse(data);
			}
			
			@Override
			public void onError(Throwable errInfo,byte[]cache) {
				if(errorCallback!=null)
					errorCallback.onError(errInfo,cache);
			}
		});
		processer.addRequest(access);
	}

	public interface onResponseCallback{
		public void onResponse(byte[] data);
	}

	public interface onErrorCallback {
		public void onError( Throwable errInfo,byte[]cache);
	}

}
