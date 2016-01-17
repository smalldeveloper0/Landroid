package com.lxl.landroid.net.core;

import java.io.IOException;

import com.lxl.landroid.net.core.utils.Access;

public interface HttpAccess {

	public byte []  postAccess(Access request) throws IOException;
}
