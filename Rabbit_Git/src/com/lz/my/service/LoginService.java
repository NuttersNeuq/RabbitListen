package com.lz.my.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

import com.lz.utils.AppConstant;
import com.lz.utils.HttpRequestUtil;
import com.lz.utils.StaticInfos;

public class LoginService {
	/*
	 * @return -1 网络链接错误
	 * @return  0 用户名错误
	 * @return  1 登陆成功
	 * @return  2 密码错误
	 * 
	 */
	
	
	public static int login(String username, String encryptedPassword){
		//将username和加密后的密码封装进params中
		HashMap<String, String> params=new HashMap<String, String>();
		params.put("username", username);
		params.put("password", encryptedPassword);
		
		//将请求头封装进headers中
		HashMap<String, String> headers=new HashMap<String, String>();
		headers.put("Host", AppConstant.HOST);
		
		//发送post请求
		try {
			//拿到链接
			HttpURLConnection conn=(HttpURLConnection) HttpRequestUtil.sendPostRequest(AppConstant.LOGIN_URL,params,headers);
			
			int code=conn.getResponseCode();
			
			Log.i("ResponseCode", "Login------"+code);
			
			if(code==200){
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				
				InputStream in=conn.getInputStream();
				
				byte [] buffer=new byte[1024];
				
				int len;
				
				while((len=in.read(buffer))!=-1){
					baos.write(buffer, 0, len);
				}
				
				in.close();
				baos.close();
				
				String info=baos.toString();
				
				String infos[]=info.split("\\-");
				
				int flag=Integer.valueOf(infos[0]);
				if(flag==1){
					StaticInfos.phpsessid=infos[1];	
					//获取用户信息
					getUserInfo(StaticInfos.phpsessid);
				}
				return flag;
			}else
				return -1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	/*
	 * @return 1获取成功
	 * @return -1网络错误
	 * 
	 */
	public static int getUserInfo(String phpsessid)throws Exception{
		HashMap<String, String>headers=new HashMap<String, String>();
		headers.put("Cookie", "PHPSESSID="+phpsessid);
		HttpURLConnection conn=(HttpURLConnection) HttpRequestUtil.sendGetRequest(AppConstant.LOGIN_USER_INFO_URL,null,headers);
		if(conn.getResponseCode()==200){
			parserUserInfo(conn);
		}else 
			return -1;
		return 1;
	}
	
	/*
	 *解析登陆验证成功后返回的xml文件，拿到响应用户信息并存储到静态变量中去。StaticInfo
	 * 
	 * 
	 */
	private static void parserUserInfo(HttpURLConnection conn)
			throws XmlPullParserException, IOException {
		//解析拿到的xml文件
		XmlPullParser parser=Xml.newPullParser();
		parser.setInput(conn.getInputStream(), "UTF-8");
		for(int type=parser.getEventType();type!=parser.END_DOCUMENT;type=parser.next()){
			if(type==parser.START_TAG){
				if("nickname".equals(parser.getName())){
					StaticInfos.nickname=parser.nextText();
				}else if("portrait".equals(parser.getName())){
					StaticInfos.portrait=parser.nextText();
				}else if("uid".equals(parser.getName())){
					StaticInfos.uid=parser.nextText();
				}else if("sqlhost".equals(parser.getName())){
					StaticInfos.sqlhost=parser.nextText();
				}else if("mp3host".equals(parser.getName())){
					StaticInfos.mp3host=parser.nextText();
				}else if("mp3url".equals(parser.getName())){
					StaticInfos.mp3url=parser.nextText();
				}else if("lrcurl".equals(parser.getName())){
					StaticInfos.lrcurl=parser.nextText();
				}else if("portraiturl".equals(parser.getName())){
					StaticInfos.portraiturl=parser.nextText();
				}else if("motto".equals(parser.getName())){
					StaticInfos.motto=parser.nextText();
				}else if("notifytoggle".equals(parser.getName())){
					StaticInfos.notifyToggle=parser.nextText();
				}
			}
		}
	}
}
