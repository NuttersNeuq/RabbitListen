package com.lz.my.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.util.Log;
import android.util.Xml;

import com.lz.javabean.Answer;
import com.lz.javabean.Blog;
import com.lz.utils.AppConstant;
import com.lz.utils.HttpRequestUtil;
import com.lz.utils.StaticInfos;
import com.lz.utils.Util;

public class BlogService {
	public List<Blog> getBlogs(String limit) throws Exception {
	
			// 设置参数
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("type","show");
			params.put("limit", limit);
			
			// 设置头
			HashMap<String, String> headers = new HashMap<String, String>();
			headers.put("Cookie", "PHPSESSID=" + StaticInfos.phpsessid);
			
			// 发送请求,获取conn
			
			HttpURLConnection conn = (HttpURLConnection) HttpRequestUtil.sendGetRequest(AppConstant.TREE_BLOG_LIST_URL, params,headers);
			// 解析xml文件获得Question的list
			List<Blog> tempBlogs = parseBlogXML(conn.getInputStream());
			
			int code=conn.getResponseCode();
			
			Log.i("ResponseCode", "TreeBlogList----------"+code);
			
			return tempBlogs;


	}

	private List<Blog> parseBlogXML(InputStream inputStream) throws Exception {
		List<Blog>tempBlogs=new ArrayList<Blog>();
		Blog blog=null;
		XmlPullParser parser=Xml.newPullParser();
		parser.setInput(inputStream, "UTF-8");
		
		for(int i=parser.getEventType();i!=parser.END_DOCUMENT;i=parser.next()){
			if(i==parser.START_TAG){
				if("blog".equals(parser.getName())){
					blog=new Blog();
					tempBlogs.add(blog);
				}else if("bid".equals(parser.getName())){
					blog.setBid(parser.nextText());
				}else if("nickname".equals(parser.getName())){
					blog.setNickname(parser.nextText());
				}else if("portrait".equals(parser.getName())){
					blog.setPortrait(parser.nextText());
				}else if ("title".equals(parser.getName())) {
					blog.setTitle(parser.nextText());
				}else if ("content".equals(parser.getName())) {
					blog.setContent(parser.nextText());
				}else if("time".equals(parser.getName())){
					blog.setTime(Long.parseLong(parser.nextText()));
				}else if("zcount".equals(parser.getName())){
					blog.setzCount(parser.nextText());
				}else if("anscount".equals(parser.getName())){
					blog.setrCount(parser.nextText());
				}else if("ifz".equals(parser.getName())){
					blog.setIfz(Util.parseBoolean(Integer.parseInt(parser.nextText())));
				}
			}
		}
		return tempBlogs;
	}

	public List<Answer> getAnswers(String bid,String limit) throws Exception {
		// 设置参数
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("type","showb");
		params.put("bid", bid);
		params.put("limit", limit);
		// 设置头
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Cookie", "PHPSESSID=" + StaticInfos.phpsessid);
		// 发送请求,获取conn
		HttpURLConnection conn = (HttpURLConnection) HttpRequestUtil.sendGetRequest(AppConstant.BLOG_ANSWER_URL, params,headers);
		// 解析xml文件获得Question的list
		List<Answer> tempAnswers = parseAnswerXML(conn.getInputStream());
		
		int code=conn.getResponseCode();
		
		Log.i("ResponseCode", "BlogDetailList----------"+code);
		return tempAnswers;
	}

	private List<Answer> parseAnswerXML(InputStream inputStream) throws Exception {
		
		List<Answer>tempAnswer=new ArrayList<Answer>();
		Answer answer=null;
		XmlPullParser parser=Xml.newPullParser();
		parser.setInput(inputStream, "UTF-8");
		for(int i=parser.getEventType();i!=parser.END_DOCUMENT;i=parser.next()){
			if(i==parser.START_TAG){
				if("banswer".equals(parser.getName())){
					answer=new Answer();
					tempAnswer.add(answer);
				}else if("bansid".equals(parser.getName())){
					answer.setAnsid(parser.nextText());
				}else if("nickname".equals(parser.getName())){
					answer.setNickname(parser.nextText());
				}else if("portrait".equals(parser.getName())){
					answer.setPortrait(parser.nextText());
				}else if ("content".equals(parser.getName())) {
					answer.setContent(parser.nextText());
				}else if("time".equals(parser.getName())){
					answer.setTime(Long.parseLong(parser.nextText()));
				}else if("to".equals(parser.getName())){
					answer.setTo(parser.nextText());
				}else if("ifz".equals(parser.getName())){
					answer.setIfz(Util.parseBoolean(Integer.parseInt(parser.nextText())));
				}
			}
		}
		return tempAnswer;
	}

	public void sendZanAnsGetRequest(String ansid, boolean zanTag) {
		//设置参数
		HashMap<String,String>params=new HashMap<String, String>();
		params.put("type", "ifansz");
		params.put("bansid", ansid);
		if(zanTag)
			params.put("ifz", "1");
		else
			params.put("ifz", "0");
		//设置头
		HashMap<String, String>headers=new HashMap<String, String>();
		headers.put("Cookie", "PHPSESSID="+StaticInfos.phpsessid);
		//发送请求,获取conn
		try {
			HttpURLConnection conn=(HttpURLConnection) HttpRequestUtil.sendGetRequest(AppConstant.BLOG_ANSWER_ZAN_URL, params, headers);
			int code =conn.getResponseCode();

			ByteArrayOutputStream baos=new ByteArrayOutputStream();
			InputStream in=conn.getInputStream();
			int len;
			byte[] buffer=new byte[1024];
			while((len=in.read(buffer))!=-1){
				baos.write(buffer,0,len);
			}
			
			in.close();
			baos.close();
			String tag=baos.toString();
			Log.i("ReturnTag","帖子回答点赞 ："+ tag);
			
			Log.i("ResponseCode", "帖子回答点赞 ："+code);
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	public void sendZanGetRequest(String bid, boolean zanTag) {
		//设置参数
		HashMap<String,String>params=new HashMap<String, String>();
		params.put("type", "ifz");
		params.put("bid", bid);
		if(zanTag)
			params.put("ifz", "1");
		else
			params.put("ifz", "0");
		
		//设置头
		HashMap<String, String>headers=new HashMap<String, String>();
		headers.put("Cookie", "PHPSESSID="+StaticInfos.phpsessid);
		//发送请求,获取conn
		try {
			HttpURLConnection conn=(HttpURLConnection) HttpRequestUtil.sendGetRequest(AppConstant.BLOG_ZAN_URL, params, headers);
			int code =conn.getResponseCode();
			
			InputStream in=conn.getInputStream();
			ByteArrayOutputStream baos=new ByteArrayOutputStream();
			int len;
			byte[] buffer=new byte[1024];
			while((len=in.read(buffer))!=-1){
				baos.write(buffer,0,len);
			}
			
			in.close();
			baos.close();
			
			String tag=baos.toString();
			
			Log.i("ReturnTag", "帖子点赞--"+tag);
			
			Log.i("ResponseCode", "帖子点赞"+code);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public void newBlogPostRequest(String title, String content) {
		
		//设置参数
		HashMap<String,String>params=new HashMap<String, String>();
		params.put("type", "new");
		params.put("title", title);
		params.put("content",content);
		//设置头
		HashMap<String, String>headers=new HashMap<String, String>();
		headers.put("Cookie", "PHPSESSID="+StaticInfos.phpsessid);
		//发送请求
		HttpURLConnection conn;
		try {
			conn = (HttpURLConnection) HttpRequestUtil.sendPostRequest(AppConstant.BLOG_NEW_URL, params, headers);
			int code =conn.getResponseCode();
			Log.i("ResponseCode", "发帖 ："+code);
		} catch (Exception e) {
			Log.d("NetException", "发帖异常");
			e.printStackTrace();
		}
		
	}

	public void sendReplyAnswerPostRequest(String bid, String content,String ansidForReply, String nickNameForReply) {
		//设置参数
		HashMap<String,String>params=new HashMap<String, String>();
		params.put("type", "ansb");
		params.put("bid", bid);
		params.put("content",content);
		params.put("bansid", ansidForReply);
		params.put("to", nickNameForReply);
		//设置头
		HashMap<String, String>headers=new HashMap<String, String>();
		headers.put("Cookie", "PHPSESSID="+StaticInfos.phpsessid);
		//发送请求
		HttpURLConnection conn;
		try {
			conn = (HttpURLConnection) HttpRequestUtil.sendPostRequest(AppConstant.BLOG_REPLY_ANSWER_URL, params, headers);
			int code =conn.getResponseCode();
			Log.i("ResponseCode", "回复帖子回复 ："+code);
		} catch (Exception e) {
			Log.d("NetException", "回复帖子回复异常");
			e.printStackTrace();
		}
	}

	public void sendReplyQuestionPostRequest(String bid, String content){
		//设置参数
		HashMap<String,String>params=new HashMap<String, String>();
		params.put("type", "ansb");
		params.put("bid", bid);
		params.put("content",content);
		//设置头
		HashMap<String, String>headers=new HashMap<String, String>();
		headers.put("Cookie", "PHPSESSID="+StaticInfos.phpsessid);
		//发送请求
		HttpURLConnection conn;
		try {
			conn = (HttpURLConnection) HttpRequestUtil.sendPostRequest(AppConstant.BLOG_REPLY_BLOG_URL, params, headers);
			int code =conn.getResponseCode();
			Log.i("ResponseCode", "回复帖子 ："+code);
		} catch (Exception e) {
			Log.d("NetException", "回复帖子异常");
			e.printStackTrace();
		}
		
		
	}

	public List<Blog> getIndexBlogs(String uid,String limit) throws Exception {
		// 设置参数
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("type","uid");
		params.put("uid", uid);
		params.put("limit", limit);
		// 设置头
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Cookie", "PHPSESSID=" + StaticInfos.phpsessid);
		// 发送请求,获取conn
		HttpURLConnection conn = (HttpURLConnection) HttpRequestUtil.sendGetRequest(AppConstant.PERSONAL_INFO_BLOG_LIST_URL, params,headers);
		// 解析xml文件获得Question的list
		List<Blog> tempBlogs = parseBlogXML(conn.getInputStream());
		int code=conn.getResponseCode();
		Log.i("ResponseCode", "IndexBlogList----------"+code);
		return tempBlogs;
	}

	public List<Blog> getIndexReplyBlogs(String uid,String limit) throws Exception {
		// 设置参数
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("type","ansuid");
		params.put("uid", uid);
		params.put("limit", limit);
		// 设置头
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Cookie", "PHPSESSID=" + StaticInfos.phpsessid);
		// 发送请求,获取conn
		HttpURLConnection conn = (HttpURLConnection) HttpRequestUtil.sendGetRequest(AppConstant.PERSONAL_INFO_BLOG_REPLY_LIST_URL, params,headers);
		// 解析xml文件获得Question的list
		List<Blog> tempBlogs = parseBlogXML(conn.getInputStream());
		int code=conn.getResponseCode();
		Log.i("ResponseCode", "IndexReplyBlogList----------"+code);
		return tempBlogs;
	}
	

}
