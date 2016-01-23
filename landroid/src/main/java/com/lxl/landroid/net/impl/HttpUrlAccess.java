package com.lxl.landroid.net.impl;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.lxl.landroid.net.core.Cache;
import com.lxl.landroid.net.core.HttpAccess;
import com.lxl.landroid.net.core.exception.RespCodeErrException;
import com.lxl.landroid.net.core.utils.MD5Util;
import com.lxl.landroid.net.core.utils.Access;
import com.lxl.landroid.net.core.utils.AccessParams;

public class HttpUrlAccess implements HttpAccess {

	public byte[] postAccess(Access request) throws IOException {
		// 缓存的MD5加密key
		String curl_md5 = null;
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(
					request.getUrl()).openConnection();
			// 设置超时时间
			connection.setReadTimeout(request.getTimeout());
			connection.setUseCaches(request.isUseCache());
			connection.setRequestMethod(request.getMethod().toString());
			
			
			AccessParams requestParams = request.getParams();

			// 如果有请求参数
			if (requestParams != null) {
				Map<String, String> header = requestParams.getHeader();
				// 如果有请求头
				if (header != null) {
					// 设置请求头
					Iterator<Entry<String, String>> ie = header.entrySet()
							.iterator();
					while (ie.hasNext()) {
						Entry<String, String> item = ie.next();
						connection.addRequestProperty(item.getKey(),
								item.getValue());
					}
				}

				Map<String, String> params = requestParams.getParams();
				String BOUNDARY = "------------ksardtyyuioip";
				// 如果有参数内容
				if (params != null && !params.isEmpty()) {
					connection.setRequestProperty("Charsert", "UTF-8");
					connection.setRequestProperty("Content-Type",
							"multipart/form-data; boundary=" + BOUNDARY);
					Iterator<Entry<String, String>> ie = params.entrySet()
							.iterator();
					StringBuilder pstr = new StringBuilder();

					while (ie.hasNext()) {
						Entry<String, String> item = ie.next();
						pstr.append("--")
								.append(BOUNDARY)
								.append("\r\n")
								.append("Content-Disposition: form-data; name=\"")
								.append(item.getKey()).append("\"")
								.append("\r\n\r\n").append(item.getValue())
								.append("\r\n");
					}
					connection.setDoInput(true);
					connection.setDoOutput(true);
					connection.getOutputStream().write(
							pstr.toString().getBytes("utf-8"));
					// 如果不是上传文件请求 就缓存该请求结果
					if (requestParams.getFiles() == null
							|| requestParams.getFiles().isEmpty())
						curl_md5 = MD5Util.MD5(request.getUrl()
								+ pstr.toString());
				}else{
					curl_md5=MD5Util.MD5(request.getUrl());
				}

				// 如果有文件要上传
				Map<String, File> files = requestParams.getFiles();
				if (files != null && !files.isEmpty()) {

//					connection.setRequestProperty("connection", "Keep-Alive");
					OutputStream out = new DataOutputStream(
							connection.getOutputStream());
					byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n")
							.getBytes();// 定义最后数据分隔线

					Iterator<Entry<String, File>> ie = files.entrySet()
							.iterator();

					while (ie.hasNext()) {
						Entry<String, File> item = ie.next();
						StringBuilder sb = new StringBuilder();
						sb.append("--");
						sb.append(BOUNDARY);
						sb.append("\r\n");
						sb.append("Content-Disposition: form-data;name=\""
								+ item.getKey() + "\";filename=\""
								+ item.getKey() + "\"\r\n");
						sb.append("Content-Type:application/octet-stream\r\n\r\n");

						byte[] data = sb.toString().getBytes();
						out.write(data);
						DataInputStream in = new DataInputStream(
								new FileInputStream(item.getValue()));
						int bytes = 0;
						byte[] bufferOut = new byte[8196];
						while ((bytes = in.read(bufferOut)) != -1) {
							out.write(bufferOut, 0, bytes);
						}
						out.write("\r\n".getBytes()); // 多个文件时，二个文件之间加入这个
						in.close();
					}
					out.write(end_data);

				}
				
			}else {
				curl_md5=MD5Util.MD5(request.getUrl());
			}

			connection.getOutputStream().flush();
			connection.getOutputStream().close();
			// 获取响应码
			int status_code = connection.getResponseCode();

			if (status_code < 200 || status_code > 299) {

				byte[]cacheData=null;
				RespCodeErrException respCodeErrException=new RespCodeErrException();

				// 请求失败 去缓存找

				if (curl_md5 != null) {
					Cache cache=request.getCache();
						if(cache!=null&&request.isUseCache()){
							cacheData=cache.get(curl_md5);
						}
				}

				respCodeErrException.setCacheData(cacheData);
				throw respCodeErrException;
			}else{
				//响应码ok
				ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
				InputStream inputStream=connection.getInputStream();
				byte[]buffer=new byte[4096];
				int len=-1;
				while(-1!=(len=inputStream.read(buffer))){
					outputStream.write(buffer,0,len);
				}
				inputStream.close();
				//写回缓存
				byte[]data= outputStream.toByteArray();
				if(request.getCache()!=null&&(requestParams.getFiles()==null||requestParams.getFiles().isEmpty()))
					request.getCache().put(curl_md5, data);
				return data;
			}

		} catch (IOException e) {

			//可能断网了 可能文件操作失败

			byte[] data = null;
			if (request.getCache() != null&&request.isUseCache()) {
				data = request.getCache().get(curl_md5);
			}
			RespCodeErrException respCodeErrException = new RespCodeErrException(e.getCause());
			respCodeErrException.setCacheData(data);
			throw respCodeErrException;
		}

	}

}
