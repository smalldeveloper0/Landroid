package com.lxl.landroid.net.core;

import com.lxl.landroid.net.core.utils.Access;

public interface Processer {

	public void startProcess();
	public void stopProcess();
	public boolean addRequest(Access request);
	public boolean cancelRequest(Access request);
}
