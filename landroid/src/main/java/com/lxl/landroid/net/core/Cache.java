package com.lxl.landroid.net.core;

public interface Cache {

	public void put(String key,byte[]data);
	public byte[] get(String key);
}
