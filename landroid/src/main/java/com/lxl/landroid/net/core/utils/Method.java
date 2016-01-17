package com.lxl.landroid.net.core.utils;

public enum Method {

	GET("GET"),POST("POST");
	
	
	private Method(String name){
		this.name=name;
	}
	
	private String name;
	
	@Override
	public String toString() {
		return name;
	}
}
