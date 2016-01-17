package com.lxl.landroid.net.core.utils;

import com.lxl.landroid.net.core.Cache;


public class Access {

	private Method method=Method.GET;//请求方法 默认get
	private int timeout=5000;//超时时间 默认5秒
	private String url;//请求url
	private RequestStatus status;
	private AccessParams params;//请求参数
	private onResponseOrErrorCallback responseOrErrorCallback;
	
	public onResponseOrErrorCallback getResponseOrErrorCallback() {
		return responseOrErrorCallback;
	}
	public void setResponseOrErrorCallback(
			onResponseOrErrorCallback responseOrErrorCallback) {
		this.responseOrErrorCallback = responseOrErrorCallback;
	}



	private boolean useCache;
	
	private Cache cache;
	
	public Cache getCache() {
		return cache;
	}
	public void setCache(Cache cache) {
		this.cache = cache;
	}
	public boolean isUseCache() {
		return useCache;
	}
	public void setUseCache(boolean useCache) {
		this.useCache = useCache;
	}
	public Method getMethod() {
		return method;
	}
	public void setMethod(Method method) {
		this.method = method;
	}
	public int getTimeout() {
		return timeout;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public AccessParams getParams() {
		return params;
	}
	public void setParams(AccessParams params) {
		this.params = params;
	}
	
	
	public RequestStatus getStatus() {
		return status;
	}
	public void setStatus(RequestStatus status) {
		this.status = status;
	}

	
	public interface onResponseOrErrorCallback{
		public void onSuccess(byte[] data);
		public void onError(Throwable errInfo,byte[]cahe);
	}


	static enum RequestStatus{
		WAITING,ACCESSING,FINISHED
	}
	
	
	
}
