package com.lxl.landroid.net.core.utils;

import java.io.File;
import java.util.Map;

public class AccessParams {
	
	
	private Map<String, String> header;
	private Map<String, String> params;
	private Map<String,File> files;

	
	public Map<String, File> getFiles() {
		return files;
	}
	public void setFiles(Map<String, File> files) {
		this.files = files;
	}
	public Map<String, String> getParams() {
		return params;
	}
	public void setParams(Map<String, String> params) {
		this.params = params;
	}
	
	public Map<String, String> getHeader() {
		return header;
	}
	public void setHeader(Map<String, String> header) {
		this.header = header;
	}
	

	
}
