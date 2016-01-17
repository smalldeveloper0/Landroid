package com.lxl.landroid.net.core;

import android.os.Handler;
import android.os.Looper;

import com.lxl.landroid.net.core.exception.RespCodeErrException;
import com.lxl.landroid.net.core.utils.Access;

public class AccessLinker implements Runnable {

	private Access access;
	private HttpAccess httpAccess;
	

	
	public AccessLinker(Access access, HttpAccess httpAccess) {
		this.access = access;
		this.httpAccess = httpAccess;
	}



	@Override
	public void run() {
		
		try {
			final byte[]data=httpAccess.postAccess(access);
			Handler handler=new Handler(Looper.getMainLooper());
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					access.getResponseOrErrorCallback().onSuccess(data);
				}
			});
		}
		catch (RespCodeErrException respE){
			access.getResponseOrErrorCallback().onError(respE,respE.getCacheData());
		}catch (Exception e) {
			e.printStackTrace();
			access.getResponseOrErrorCallback().onError(e,null);
		}
		
	}

}
