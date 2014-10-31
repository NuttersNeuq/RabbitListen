package com.lz.my.service;

import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import com.lz.utils.Util;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

public class ImageService {
	private Context context;

	public ImageService(Context context) {
		this.context = context;
	}

	public Bitmap getImage(String address) throws Exception {
		if (address == null)
			return null;
		
		URL url = new URL(address);		
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();	
		conn.setConnectTimeout(3000);
		
		File cacheFile = new File(context.getCacheDir(), URLEncoder.encode(address));		//第一个参数是获取缓存地址/data/data/nut.activity/cache    第二个参数为文件名：将地址编码后作为文件名
		
		if (cacheFile.exists()) {
			conn.setIfModifiedSince(cacheFile.lastModified());	// 获取文件的最后修改时间, 作为请求头
		}
		
		int code = conn.getResponseCode();
		if (code == 200) {
			byte[] data = Util.read(conn.getInputStream());							// 读取服务端写回的数据
			Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);		// 把字节数据解码为图片
			bm.compress(CompressFormat.PNG, 100, new FileOutputStream(cacheFile));	// 存储图片到本地, 用作缓存. 建议新线程中处理
			return bm;	
		}
		else if (code == 304) {
			Bitmap bm = BitmapFactory.decodeFile(cacheFile.getAbsolutePath());		// 读取cacheFile, 生成Bitmap
			return bm;
		}
		
		throw new NetworkErrorException("连接出错: " + code);
	}
}
