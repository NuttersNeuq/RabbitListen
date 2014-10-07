package com.lz.my.service;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.util.Log;
import android.util.Xml;

import com.lz.javabean.Fan;
import com.lz.utils.AppConstant;
import com.lz.utils.HttpRequestUtil;
import com.lz.utils.StaticInfos;

public class FanService {

	public List<Fan> getFans(int fanTag, String uid) throws Exception {
		// 设置参数
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid",uid);
		
		// 设置头
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Cookie", "PHPSESSID=" + StaticInfos.phpsessid);
		
		List<Fan> tempFans=null;
	
		//判断是获取粉丝列表还是关注列表
		if(fanTag==0)	//为粉丝
		{
			// 发送请求,获取conn
			HttpURLConnection conn = (HttpURLConnection) HttpRequestUtil.sendGetRequest(AppConstant.FAN_LIST_URL, params,headers);
			// 解析xml文件获得Question的list
			tempFans = parseFanXML(conn.getInputStream());
			int code=conn.getResponseCode();
			
			Log.i("ResponseCode", "fanList----------"+code);
		}else	//为关注
		{
			// 发送请求,获取conn
			HttpURLConnection conn = (HttpURLConnection) HttpRequestUtil.sendGetRequest(AppConstant.FANED_LIST_URL, params,headers);
			// 解析xml文件获得Question的list
			tempFans = parseFanXML(conn.getInputStream());
			int code=conn.getResponseCode();
			
			Log.i("ResponseCode", "fanedList----------"+code);
		}
		return tempFans;
	}

	private List<Fan> parseFanXML(InputStream inputStream) throws Exception {
		List<Fan> tempFans = new ArrayList<Fan>();
		Fan fan=null;
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(inputStream, "UTF-8");
		for (int i = parser.getEventType(); i != parser.END_DOCUMENT; i = parser.next()) {
			if (i == parser.START_TAG) {
				if ("fan".equals(parser.getName())) {
					fan = new Fan();
					tempFans.add(fan);
				} else if ("uid".equals(parser.getName())) {
					fan.setUid(parser.nextText());
				} else if ("nickname".equals(parser.getName())) {
					fan.setNickname(parser.nextText());
				} else if ("portrait".equals(parser.getName())) {
					fan.setPortrait(parser.nextText());
				}else if ("motto".equals(parser.getName())) {
					fan.setMoto(parser.nextText());
				}
			}
		}
		
		return tempFans;
	}
	
	
}
