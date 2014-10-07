package com.lz.my.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map.Entry;

import org.xmlpull.v1.XmlPullParser;

import android.util.Log;
import android.util.Xml;

import com.lz.utils.AppConstant;
import com.lz.utils.Encrypt;
import com.lz.utils.HttpRequestUtil;
import com.lz.utils.StaticInfos;

public class NetService {
	public  HashMap<String, String> getPersonalInfos() throws Exception {
		HashMap<String,String> infoMap=new HashMap<String, String>();
		//联网，解析xml文件
		
		// 设置参数
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("type","me");

		// 设置头
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Cookie", "PHPSESSID=" + StaticInfos.phpsessid);
		
		// 发送请求,获取conn		
		HttpURLConnection conn = (HttpURLConnection)HttpRequestUtil.sendGetRequest(AppConstant.PERSONALINFO_URL, params,headers);
		// 解析xml文件获得Question的list
		
		infoMap = parseInfosXML(conn.getInputStream());
		
		Log.i("ResponseCode", "PersonalInfo:"+conn.getResponseCode());
		
		return infoMap;
	}

	private  HashMap<String,String> parseInfosXML(InputStream inputStream) throws Exception {
		HashMap<String, String> infoMap=new HashMap<String, String>();
		
		XmlPullParser parser=Xml.newPullParser();
		
		parser.setInput(inputStream,"UTF-8");
		
		for(int i=parser.getEventType();i!=parser.END_DOCUMENT;i=parser.next()){
			if(i==parser.START_TAG){
				if("uid".equals(parser.getName())){
					infoMap.put("uid", parser.nextText());
				}else if("nickname".equals(parser.getName())){
					infoMap.put("nickname", parser.nextText());
				}else if("portrait".equals(parser.getName())){
					infoMap.put("portrait", parser.nextText());
				}else if ("faned".equals(parser.getName())) {
					infoMap.put("faned", parser.nextText());					
				}else if ("fan".equals(parser.getName())) {
					infoMap.put("fan", parser.nextText());
				}else if("label".equals(parser.getName())){
					infoMap.put("label",parser.nextText());
				}else if("motto".equals(parser.getName())){
					infoMap.put("motto", parser.nextText());
				}else if("timefrom".equals(parser.getName())){
					infoMap.put("timefrom", parser.nextText());
				}else if("timeall".equals(parser.getName())){
					infoMap.put("timeall", parser.nextText());
				}else if("timetoday".equals(parser.getName())){
					infoMap.put("timetoday", parser.nextText());
				}else if("jingtingcount".equals(parser.getName())){
					infoMap.put("jingtingcount", parser.nextText());
				}else if("fantingcount".equals(parser.getName())){
					infoMap.put("fantingcount", parser.nextText());
				}else if("resistdays".equals(parser.getName())){
					infoMap.put("resistdays", parser.nextText());
				}else if("chat".equals(parser.getName())){
					infoMap.put("chat",parser.nextText());
				}else if("itemjtcount".equals(parser.getName())){
					infoMap.put("itemjtcount", parser.nextText());
				}else if("itemmyblogcount".equals(parser.getName())){
					infoMap.put("itemmyblogcount", parser.nextText());
				}else if("itemmyquescount".equals(parser.getName())){
					infoMap.put("itemmyquescount", parser.nextText());
				}else if("itemmynotecount".equals(parser.getName())){
					infoMap.put("itemmynotecount", parser.nextText());
				}else if("iff".equals(parser.getName())){
					infoMap.put("iff", parser.nextText());
				}
			}
		}
		return infoMap;
	}

	public HashMap<String, String> getOtherInfos(String uid) throws Exception {
		HashMap<String,String> infoMap=new HashMap<String, String>();
		//联网，解析xml文件
		// 设置参数
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("type","uid");
		params.put("uid", uid);
		// 设置头
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Cookie", "PHPSESSID=" + StaticInfos.phpsessid);
		// 发送请求,获取conn		
		HttpURLConnection conn = (HttpURLConnection)HttpRequestUtil.sendGetRequest(AppConstant.PERSONALINFO_URL, params,headers);
		// 解析xml文件获得Question的list
		infoMap = parseInfosXML(conn.getInputStream());
		int code=conn.getResponseCode();
		Log.i("ResponseCode", "OtherInfo:"+code);
		return infoMap;
	}

	public  String register(String username, String nickname,String password, String email) throws Exception {
		HashMap<String,String> infoMap=new HashMap<String, String>();
		//联网，解析xml文件
		// 设置参数
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("loginname",username);
		params.put("nickname", nickname);
		params.put("email", email);
		params.put("password",Encrypt.Bit32(password));
		// 设置头
		HashMap<String, String> headers = new HashMap<String, String>();
		// 发送请求,获取conn		
		HttpURLConnection conn = (HttpURLConnection)HttpRequestUtil.sendPostRequest(AppConstant.REGISTER_URL, params,headers);
		// 获取返回值
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		InputStream in=conn.getInputStream();
		int len;
		byte []buffer=new byte[1024];
		while ((len=in.read(buffer))!=-1) {
			baos.write(buffer, 0, len);
		}
		in.close();
		baos.close();
		
		String returnCode=baos.toString();
		int code=conn.getResponseCode();
		Log.i("ResponseCode", "RegisterInfo:"+code);
		return returnCode;
	}

	//提交评论
	public void getComment(String lid, String difficulty, String like) throws Exception {
		HashMap<String,String> infoMap=new HashMap<String, String>();
		//联网，解析xml文件
		// 设置参数
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("lid",lid);
		params.put("difficulty", difficulty);
		params.put("like", like);
		// 设置头
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Cookie", "PHPSESSID=" + StaticInfos.phpsessid);
		// 发送请求,获取conn		
		HttpURLConnection conn = (HttpURLConnection)HttpRequestUtil.sendGetRequest(AppConstant.SHARE_COMMENT_URL, params,headers);
		// 获取返回值
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		
		InputStream in=conn.getInputStream();
		
		int len;
		byte []buffer=new byte[1024];
		while ((len=in.read(buffer))!=-1) {
			baos.write(buffer, 0, len);
		}
		in.close();
		baos.close();
		
		System.out.println("comment------------------------>"+baos.toString());
		int code=conn.getResponseCode();
		Log.i("ResponseCode", "CommentInfo:"+code);
	}

	public String postMotto(String motto) throws Exception {
		
		HashMap<String,String> infoMap=new HashMap<String, String>();
		//联网，解析xml文件
		// 设置参数
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("motto", motto);
		// 设置头
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Cookie", "PHPSESSID=" + StaticInfos.phpsessid);
		// 发送请求,获取conn		
		HttpURLConnection conn = (HttpURLConnection)HttpRequestUtil.sendPostRequest(AppConstant.PERSONAL_INFO_EDIT_MOTTO, params,headers);
		// 获取返回值
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		InputStream in=conn.getInputStream();
		int len;
		byte []buffer=new byte[1024];
		while ((len=in.read(buffer))!=-1) {
			baos.write(buffer, 0, len);
		}
		in.close();
		baos.close();
		
		int code=conn.getResponseCode();
		Log.i("ResponseCode", "EitMottoInfo:"+code);
		
		return baos.toString();
		
	}

	public String postLabel(String label) throws Exception {
		
		HashMap<String,String> infoMap=new HashMap<String, String>();
		//联网，解析xml文件
		// 设置参数
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("label", label);
		// 设置头
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Cookie", "PHPSESSID=" + StaticInfos.phpsessid);
		// 发送请求,获取conn		
		HttpURLConnection conn = (HttpURLConnection)HttpRequestUtil.sendPostRequest(AppConstant.GUIDE_THREE_POST_LABEL_URL, params,headers);
		// 获取返回值
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		InputStream in=conn.getInputStream();
		int len;
		byte []buffer=new byte[1024];
		while ((len=in.read(buffer))!=-1) {
			baos.write(buffer, 0, len);
		}
		in.close();
		baos.close();
		
		int code=conn.getResponseCode();
		Log.i("ResponseCode", "PostLabelInfo:"+code);
		
		return baos.toString();
	}

	public void personalInfoFollow(boolean iff,String uid) throws Exception {
		HashMap<String,String> infoMap=new HashMap<String, String>();
		//联网，解析xml文件
		// 设置参数
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("fuid", uid);
		if(iff){
			params.put("iff","1");  //关注
		}else{
			params.put("iff", "0");  //取消关注
		}
		// 设置头
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Cookie", "PHPSESSID=" + StaticInfos.phpsessid);
		// 发送请求,获取conn		
		HttpURLConnection conn = (HttpURLConnection)HttpRequestUtil.sendGetRequest(AppConstant.PERSONAL_INFO_FOLLOW, params,headers);
		// 获取返回值
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		InputStream in=conn.getInputStream();
		int len;
		byte []buffer=new byte[1024];
		while ((len=in.read(buffer))!=-1) {
			baos.write(buffer, 0, len);
		}
		in.close();
		baos.close();
		System.out.println("关注-------------->"+baos.toString());
		int code=conn.getResponseCode();
		Log.i("ResponseCode", "PersonalInfoFollow:"+code);
	}

	public String postFeedBack(String title, String content) throws Exception {
		// 设置参数
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("title", title);
		params.put("content", content);
		// 设置头
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Cookie", "PHPSESSID=" + StaticInfos.phpsessid);
		// 发送请求,获取conn		
		HttpURLConnection conn = (HttpURLConnection)HttpRequestUtil.sendPostRequest(AppConstant.FEED_BACK_URL, params,headers);
		// 获取返回值
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		InputStream in=conn.getInputStream();
		int len;
		byte []buffer=new byte[1024];
		while ((len=in.read(buffer))!=-1) {
			baos.write(buffer, 0, len);
		}
		in.close();
		baos.close();
		int code=conn.getResponseCode();
		Log.i("ResponseCode", "FeedBack---------->:"+code);
		return baos.toString();
	}

	public String[] getPersonalInfoLabel() throws Exception {
		String label="";
		// 设置参数
		HashMap<String, String> params = new HashMap<String, String>();
		// 设置头
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Cookie", "PHPSESSID=" + StaticInfos.phpsessid);
		// 发送请求,获取conn		
		HttpURLConnection conn = (HttpURLConnection)HttpRequestUtil.sendGetRequest(AppConstant.PERSONAL_INFO_LABEL_URL, params,headers);
		// 获取返回值
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		InputStream in=conn.getInputStream();
		int len;
		byte []buffer=new byte[1024];
		while ((len=in.read(buffer))!=-1) {
			baos.write(buffer, 0, len);
		}
		in.close();
		baos.close();
		label=baos.toString();
		String[] labeled=label.split(",");
		int code=conn.getResponseCode();
		Log.i("ResponseCode", "PersonalInfoLabel---------->:"+code);
		return labeled;
	}

	public void getNotifyToggle(boolean isOn) throws Exception {
		// 设置参数
		HashMap<String, String> params = new HashMap<String, String>();
		if(isOn)
			params.put("n", "true");
		else
			params.put("n", "false");
		// 设置头
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Cookie", "PHPSESSID=" + StaticInfos.phpsessid);
		// 发送请求,获取conn		
		HttpURLConnection conn = (HttpURLConnection)HttpRequestUtil.sendGetRequest(AppConstant.NOTIFY_TOGGLE_URL, params,headers);
		
		// 获取返回值
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		InputStream in=conn.getInputStream();
		int len;
		byte []buffer=new byte[1024];
		while ((len=in.read(buffer))!=-1) {
			baos.write(buffer, 0, len);
		}
		in.close();
		baos.close();
		int code=conn.getResponseCode();
		Log.i("ResponseCode", "NotifyToggle---------->:"+code);
	}

	public String getStudyNotify() throws Exception {
		// 设置参数
		HashMap<String, String> params = new HashMap<String, String>();
		// 设置头
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Cookie", "PHPSESSID=" + StaticInfos.phpsessid);
		// 发送请求,获取conn		
		HttpURLConnection conn = (HttpURLConnection)HttpRequestUtil.sendGetRequest(AppConstant.STUDY_NOTIFY_URL, params,headers);
		
		// 获取返回值
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		InputStream in=conn.getInputStream();
		int len;
		byte []buffer=new byte[1024];
		while ((len=in.read(buffer))!=-1) {
			baos.write(buffer, 0, len);
		}
		in.close();
		baos.close();
		int code=conn.getResponseCode();
		Log.i("ResponseCode", "StudyNotify---------->:"+code);
		return baos.toString();
	}

	public String getNotifications() throws IOException {
		// 设置参数
		HashMap<String, String> params = new HashMap<String, String>();
		// 设置头
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Cookie", "PHPSESSID=" + StaticInfos.phpsessid);
		// 发送请求,获取conn		
		HttpURLConnection conn = (HttpURLConnection)HttpRequestUtil.sendGetRequest(AppConstant.NOTIFICATION_URL, params,headers);
		// 获取返回值
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		InputStream in=conn.getInputStream();
		int len;
		byte []buffer=new byte[1024];
		while ((len=in.read(buffer))!=-1) {
			baos.write(buffer, 0, len);
		}
		in.close();
		baos.close();
		int code=conn.getResponseCode();
		Log.i("ResponseCode", "Notification---------->:"+code);
		return baos.toString();
	}

	public void logout() throws Exception {
		// 设置参数
		HashMap<String, String> params = new HashMap<String, String>();
		// 设置头
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Cookie", "PHPSESSID=" + StaticInfos.phpsessid);
		// 发送请求,获取conn		
		HttpURLConnection conn = (HttpURLConnection)HttpRequestUtil.sendGetRequest(AppConstant.LOGOUT_URL, params,headers);
		// 获取返回值
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		InputStream in=conn.getInputStream();
		int len;
		byte []buffer=new byte[1024];
		while ((len=in.read(buffer))!=-1) {
			baos.write(buffer, 0, len);
		}
		in.close();
		baos.close();
		int code=conn.getResponseCode();
		Log.i("ResponseCode", "logout---------->:"+code);
	}

	public HashMap<String, String> getOtherInfosByNickname(String nickname) throws Exception {
		HashMap<String,String> infoMap=new HashMap<String, String>();
		//联网，解析xml文件
		// 设置参数
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("type","nickname");
		params.put("nickname",nickname);
		// 设置头
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Cookie", "PHPSESSID=" + StaticInfos.phpsessid);
		// 发送请求,获取conn		
		HttpURLConnection conn = (HttpURLConnection)HttpRequestUtil.sendGetRequest(AppConstant.PERSONALINFO_URL, params,headers);
		// 解析xml文件获得Question的list
		infoMap = parseInfosXML(conn.getInputStream());
		int code=conn.getResponseCode();
		Log.i("ResponseCode", "OtherInfo:"+code);
		return infoMap;
	}
}
