package com.lxl.landroid.net.impl;

import android.content.Context;

import com.lxl.landroid.net.core.Cache;
import com.lxl.landroid.net.core.HttpRequest;
import com.lxl.landroid.net.core.Processer;
import com.lxl.landroid.net.core.utils.AccessParams;

public class HttpStringRequest extends HttpRequest{

	private static  HttpStringRequest httpStringRequest;
	private static int DEFAULT_DISK_CACHE_SIZE=10;//MB
	private HttpStringRequest(Context context,Cache cache,Processer processer) {
		super(cache,processer);
	}
	
	public static HttpStringRequest getInstance(Context context){
		if(httpStringRequest==null){
		Cache cache=new DiskLruCache(DEFAULT_DISK_CACHE_SIZE, context.getExternalCacheDir());
		HttpUrlAccess access=new HttpUrlAccess();
		Processer processer=new ExecutorProcesser(3,access);
		processer.startProcess();
		httpStringRequest=new HttpStringRequest(context, cache, processer);
		}
		
		return  httpStringRequest; 
		
	}
	
	@Override
	public void sendReq(String url, AccessParams params,
			onResponseCallback responseCallback, onErrorCallback errorCallback) {
			super.sendReq(url, params, responseCallback, errorCallback);
	}
	
	
	
	
}
