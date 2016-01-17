package com.lxl.landroid.net.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.lxl.landroid.net.core.Cache;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DiskLruCache implements Cache{

	private long maxSize;
	private long curSize;
	private File cacheDir;
	private Map<String, CacheFile> curFiles;
	private SQLiteDatabase db;
	private static final String CACHE_TABLE_NAME = "disk_cache";
	private static final String FIELD_ID = "dc_id";
	private static final String FIELD_MURL = "murl";
	private static final String FIELD_USENUM = "usenum";

	/**
	 * 
	 * @param maxSize 最大缓存大小  单位MB
	 * @param cacheDir 缓存目录
	 */
	public DiskLruCache(int maxSize, File cacheDir) {
		this.maxSize = maxSize;
		this.cacheDir = cacheDir;

		if (!cacheDir.exists()) {
			if (!cacheDir.mkdirs()) {
				throw new RuntimeException("创建缓存目录失败 请检查应用是否具备相应权限");
			}
		}
		if (cacheDir.isFile()) {
			throw new RuntimeException("指定缓存目录是一个文件");
		}
		maxSize=maxSize*1024*1024;
		loadFiles();
	}

	/**
	 * 初始化本地缓存文件
	 * 
	 */
	private void loadFiles() {
		curFiles = new HashMap<String, DiskLruCache.CacheFile>();
		File fcfg = new File(cacheDir, "landroid_disk_cache.db3");
		db = SQLiteDatabase.openOrCreateDatabase(fcfg, null);
		if (!db.isOpen() || db.isReadOnly()) {
			throw new RuntimeException("缓存数据库打开失败");
		}
		String sql = new StringBuilder("create table if not exists ")
				.append(CACHE_TABLE_NAME).append("(").append(FIELD_ID)
				.append(" integer primary key autoincrement,")
				.append(FIELD_MURL).append(" varchar(50),")
				.append(FIELD_USENUM).append(" integer, unique(")
				.append(FIELD_ID).append("))").toString();
		db.execSQL(sql);
		sql = new StringBuilder("select * from ").append(CACHE_TABLE_NAME)
				.toString();
		Cursor cursor = db.rawQuery(sql, null);
		while (cursor.moveToNext()) {
			String filename = cursor.getString(cursor
					.getColumnIndex(FIELD_MURL));
			File file = new File(cacheDir, filename);
			long id = cursor.getLong(cursor.getColumnIndex(FIELD_ID));
			if (!file.exists()) {
				// 如果文件已经被手动移除 则数据库中移除该记录
				sql = new StringBuilder("delete from disk_cache where ").append(FIELD_ID).append("=").append(id).toString();
				db.execSQL(sql);
			} else {
				CacheFile cacheFile = new CacheFile();
				curSize+=file.length();
				cacheFile.file = file;
				cacheFile.id = id;
				cacheFile.usenum = cursor.getInt(cursor
						.getColumnIndex(FIELD_USENUM));
				curFiles.put(filename, cacheFile);
			}
		}

	}

	/**
	 * 移除最少使用缓存
	 */
	private void verifyCacheSize() {
		
		if(curSize>maxSize){
		
			CacheFile cacheFile = null;
			String filename=null;
			long size;
			String sql=new StringBuilder("select ").append(FIELD_MURL).append(" , min(").append(FIELD_USENUM).append(") from ").append(CACHE_TABLE_NAME).toString();
			Cursor cursor=db.rawQuery(sql, null);
			while(cursor.moveToNext()){
				filename=cursor.getString(cursor.getColumnIndex(FIELD_MURL));
				cacheFile=curFiles.get(filename);
			}
			
			if(cacheFile!=null){
				size=cacheFile.file.length();
				if(cacheFile.file.delete()){
					sql = new StringBuilder("delete from disk_cache where ").append(FIELD_ID).append("=").append(cacheFile.id).toString();
					db.execSQL(sql);
					curFiles.remove(filename);
					curSize-=size;
					verifyCacheSize();
				}
			}
		}
	}

	public void put(String key, byte[] data) {

		CacheFile cacheFile = curFiles.get(key);
		FileOutputStream fileOutputStream = null;
		File pfile = new File(cacheDir, key);

		try {
			if (cacheFile == null || !pfile.exists()) {
				pfile.createNewFile();
				// 数据库中key存在unique约束 如果数据库中已经记录该缓存 则数据不能成功插入
				ContentValues values = new ContentValues();
				values.put(FIELD_MURL, key);
				values.put(FIELD_USENUM, 0);
				long id = db.insert(CACHE_TABLE_NAME, null, values);
				cacheFile = new CacheFile();
				cacheFile.id = id;
				cacheFile.file = new File(cacheDir, key);

			}else{
				curSize-=pfile.length();
				cacheFile.usenum=0;
				String sql=new StringBuilder("update ").append(CACHE_TABLE_NAME).append(" set ").append(FIELD_USENUM).append("=0").append(" where ").append(FIELD_ID).append("=").append(cacheFile.id).toString();
				db.execSQL(sql);
			}
			fileOutputStream = new FileOutputStream(cacheFile.file);
			fileOutputStream.write(data);
			fileOutputStream.close();
			curSize+=pfile.length();
			verifyCacheSize();
		} catch (IOException e) {
			e.printStackTrace();
//			throw new RuntimeException(e);
		}

	}

	public byte[] get(String key) {

		byte[] data = null;
		CacheFile cacheFile = curFiles.get(key);
		try {
			if (cacheFile != null) {
				
				InputStream inputStream = new FileInputStream(cacheFile.file);
				ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
				int len = 0;
				byte[] buffer = new byte[4096];
				while (-1 != (len = inputStream.read(buffer))) {
					arrayOutputStream.write(buffer, 0, len);
				}
				inputStream.close();
				data = arrayOutputStream.toByteArray();
				cacheFile.usenum++;
				String sql=new StringBuilder("update ").append(CACHE_TABLE_NAME).append(" set ").append(FIELD_USENUM).append("=").append(FIELD_USENUM).append("+1 ").append(" where ").append(FIELD_ID).append("=").append(cacheFile.id).toString();
				db.execSQL(sql);
			}
		} catch (IOException e) {
			e.printStackTrace();
//			throw new RuntimeException(e);
		}

		return data;
	}

	class CacheFile {
		long id;
		File file;
		int usenum;
	}

}
