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
import com.lz.javabean.Question;
import com.lz.utils.AppConstant;
import com.lz.utils.HttpRequestUtil;
import com.lz.utils.StaticInfos;
import com.lz.utils.Util;

public class QuestionService {

	public List<Question> getQuestions(String lid,String limit) throws Exception {
		if (lid == null) {
			// 设置参数
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("type","question");
			params.put("limit", limit);
			// 设置头
			HashMap<String, String> headers = new HashMap<String, String>();
			headers.put("Cookie", "PHPSESSID=" + StaticInfos.phpsessid);
			// 发送请求,获取conn
			HttpURLConnection conn = (HttpURLConnection) HttpRequestUtil.sendGetRequest(AppConstant.TREE_QUESTION_LIST_URL, params,headers);
			// 解析xml文件获得Question的list
			List<Question> tempQuestions = parseQuestionXML(conn.getInputStream());
			
			int code=conn.getResponseCode();
			
			Log.i("ResponseCode", "TreeQuestionList----------"+code);
			return tempQuestions;

		} else {

			// 设置参数
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("lid", lid);
			params.put("limit", limit);
			// 设置头
			HashMap<String, String> headers = new HashMap<String, String>();
			headers.put("Cookie", "PHPSESSID=" + StaticInfos.phpsessid);
			// 发送请求,获取conn
			HttpURLConnection conn = (HttpURLConnection) HttpRequestUtil.sendGetRequest(AppConstant.QUESTION_LIST_URL, params,headers);
			// 解析xml文件获得Question的list
			List<Question> tempQuestions = parseQuestionXML(conn.getInputStream());
			
			return tempQuestions;
		}

	}

	private List<Question> parseQuestionXML(InputStream inputStream) throws Exception {
		List<Question>tempQuestions=new ArrayList<Question>();
		Question question=null;
		XmlPullParser parser=Xml.newPullParser();
		parser.setInput(inputStream, "UTF-8");
		
		for(int i=parser.getEventType();i!=parser.END_DOCUMENT;i=parser.next()){
			if(i==parser.START_TAG){
				if("question".equals(parser.getName())){
					question=new Question();
					tempQuestions.add(question);
				}else if("qid".equals(parser.getName())){
					question.setQid(parser.nextText());
				}else if("nickname".equals(parser.getName())){
					question.setNickname(parser.nextText());
				}else if("portrait".equals(parser.getName())){
					question.setPortrait(parser.nextText());
				}else if ("title".equals(parser.getName())) {
					question.setTitle(parser.nextText());
				}else if ("content".equals(parser.getName())) {
					question.setContent(parser.nextText());
				}else if("time".equals(parser.getName())){
					question.setTime(Long.parseLong(parser.nextText()));
				}else if("fcount".equals(parser.getName())){
					question.setFcount(parser.nextText());
				}else if("anscount".equals(parser.getName())){
					question.setAnscount(parser.nextText());
				}else if("iff".equals(parser.getName())){
					question.setIff(Util.parseBoolean(Integer.parseInt(parser.nextText())));
				}else if("lid".equals(parser.getName())){
					question.setOfLid(parser.nextText());
				}
			}
		}
		return tempQuestions;
	}

	public List<Answer> getAnswers(String qid,String limit) throws Exception {
		// 设置参数
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("qid",qid);
		params.put("limit",limit);
		// 设置头
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Cookie", "PHPSESSID=" + StaticInfos.phpsessid);
		// 发送请求,获取conn
		HttpURLConnection conn = (HttpURLConnection) HttpRequestUtil.sendGetRequest(AppConstant.QUESTION_ANSWER_URL, params,headers);
		// 解析xml文件获得Question的list
		List<Answer> tempAnswers = parseAnswerXML(conn.getInputStream());
		
		int code=conn.getResponseCode();
		
		Log.i("ResponseCode", "QuestionDetailList----------"+code);
		return tempAnswers;
	}

	private List<Answer> parseAnswerXML(InputStream inputStream) throws Exception{
		List<Answer>tempAnswer=new ArrayList<Answer>();
		Answer answer=null;
		XmlPullParser parser=Xml.newPullParser();
		parser.setInput(inputStream, "UTF-8");
		
		for(int i=parser.getEventType();i!=parser.END_DOCUMENT;i=parser.next()){
			if(i==parser.START_TAG){
				if("answer".equals(parser.getName())){
					answer=new Answer();
					tempAnswer.add(answer);
				}else if("ansid".equals(parser.getName())){
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
			params.put("ansid", ansid);
			if(zanTag)
				params.put("ifz", "1");
			else
				params.put("ifz", "0");
			//设置头
			HashMap<String, String>headers=new HashMap<String, String>();
			headers.put("Cookie", "PHPSESSID="+StaticInfos.phpsessid);
			//发送请求,获取conn
			try {
				HttpURLConnection conn=(HttpURLConnection) HttpRequestUtil.sendGetRequest(AppConstant.QUESTION_ANSWER_ZAN_URL, params, headers);
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
				Log.i("ReturnTag","问题回答点赞 ："+ tag);
				
				Log.i("ResponseCode", "问题回答点赞 ："+code);
				
				
			} catch (Exception e) {
				
				e.printStackTrace();
			}
			
			
		
		
	}

	public void sendFollowGetRequest(String qid, boolean followTag) {
		//设置参数
		HashMap<String,String>params=new HashMap<String, String>();
		params.put("qid", qid);
		
		if(followTag)
			params.put("iff", "1");
		else
			params.put("iff", "0");
		
		//设置头
		HashMap<String, String>headers=new HashMap<String, String>();
		headers.put("Cookie", "PHPSESSID="+StaticInfos.phpsessid);
		//发送请求,获取conn
		try {
			HttpURLConnection conn=(HttpURLConnection) HttpRequestUtil.sendGetRequest(AppConstant.QUESTION_FOLLOW_URL, params, headers);
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
			
			Log.i("ReturnTag", "问题关注--"+tag);
			
			Log.i("ResponseCode", "问题关注"+code);
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	public void newQuestionPostRequest(String lid, String title, String content) {
		//设置参数
		HashMap<String,String>params=new HashMap<String, String>();
		params.put("lid", lid);
		params.put("title", title);
		params.put("content",content);
		//设置头
		HashMap<String, String>headers=new HashMap<String, String>();
		headers.put("Cookie", "PHPSESSID="+StaticInfos.phpsessid);
		//发送请求
		HttpURLConnection conn;
		try {
			conn = (HttpURLConnection) HttpRequestUtil.sendPostRequest(AppConstant.QUESTION_NEW_URL, params, headers);
			int code =conn.getResponseCode();
			Log.i("ResponseCode", "提问 ："+code);
		} catch (Exception e) {
			Log.d("NetException", "提问异常");
			e.printStackTrace();
		}
	}

	public void sendReplyQuestionPostRequest(String qid,String content) {
		//设置参数
		HashMap<String,String>params=new HashMap<String, String>();
		params.put("qid", qid);
		params.put("content",content);
		//设置头
		HashMap<String, String>headers=new HashMap<String, String>();
		headers.put("Cookie", "PHPSESSID="+StaticInfos.phpsessid);
		//发送请求
		HttpURLConnection conn;
		try {
			conn = (HttpURLConnection) HttpRequestUtil.sendPostRequest(AppConstant.QUESTION_REPLY_QUESTION_URL, params, headers);
			int code =conn.getResponseCode();
			Log.i("ResponseCode", "回复问题 ："+code);
		} catch (Exception e) {
			Log.d("NetException", "回复问题异常");
			e.printStackTrace();
		}
	
	}

	public void sendReplyAnswerPostRequest(String qid, String content,String ansid, String to) {
		//设置参数
		HashMap<String,String>params=new HashMap<String, String>();
		params.put("qid", qid);
		params.put("content",content);
		params.put("ansid", ansid);
		params.put("to", to);
		//设置头
		HashMap<String, String>headers=new HashMap<String, String>();
		headers.put("Cookie", "PHPSESSID="+StaticInfos.phpsessid);
		//发送请求
		HttpURLConnection conn;
		try {
			conn = (HttpURLConnection) HttpRequestUtil.sendPostRequest(AppConstant.QUESTION_REPLY_ANSWER_URL, params, headers);
			int code =conn.getResponseCode();
			Log.i("ResponseCode", "回复回答 ："+code);
		} catch (Exception e) {
			Log.d("NetException", "回复回答异常");
			e.printStackTrace();
		}
	}

	public List<Question> getIndexQuestions(String uid,String limit) throws Exception {
		// 设置参数
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("type","question");
		params.put("uid",uid);
		params.put("limit", limit);
		// 设置头
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Cookie", "PHPSESSID=" + StaticInfos.phpsessid);
		// 发送请求,获取conn
		HttpURLConnection conn = (HttpURLConnection) HttpRequestUtil.sendGetRequest(AppConstant.PERSONAL_INFO_QUESTION_LIST_URL, params,headers);
		// 解析xml文件获得Question的list
		List<Question> tempQuestions = parseQuestionXML(conn.getInputStream());
		int code=conn.getResponseCode();
		Log.i("ResponseCode", "IndexQuestionList----------"+code);
		return tempQuestions;
		
	}

	public List<Question> getIndexReplyQuestions(String uid,String limit) throws Exception {
		// 设置参数
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("type","answer");
		params.put("uid",uid);
		params.put("limit", limit);
		// 设置头
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Cookie", "PHPSESSID=" + StaticInfos.phpsessid);
		// 发送请求,获取conn
		HttpURLConnection conn = (HttpURLConnection) HttpRequestUtil.sendGetRequest(AppConstant.PERSONAL_INFO_QUESTION_REPLY_NOTE_LIST_URL, params,headers);
		// 解析xml文件获得Question的list
		List<Question> tempQuestions = parseQuestionXML(conn.getInputStream());
		int code=conn.getResponseCode();
		Log.i("ResponseCode", "IndexReplyQuestionList----------"+code);
		return tempQuestions;
	}

	public List<Question> getIndexFollowQuestions(String uid,String limit) throws Exception {
		// 设置参数
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("type","follow");
		params.put("uid",uid);
		params.put("limit", limit);
		// 设置头
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Cookie", "PHPSESSID=" + StaticInfos.phpsessid);
		// 发送请求,获取conn
		HttpURLConnection conn = (HttpURLConnection) HttpRequestUtil.sendGetRequest(AppConstant.PERSONAL_INFO_COLLECT_QUESTION_LIST_URL, params,headers);
		// 解析xml文件获得Question的list
		List<Question> tempQuestions = parseQuestionXML(conn.getInputStream());
		int code=conn.getResponseCode();
		Log.i("ResponseCode", "IndexFollowQuestionList----------"+code);
		return tempQuestions;
	}
	
}
