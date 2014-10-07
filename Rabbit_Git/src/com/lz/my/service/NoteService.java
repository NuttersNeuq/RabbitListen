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

import com.lz.javabean.Note;
import com.lz.utils.AppConstant;
import com.lz.utils.HttpRequestUtil;
import com.lz.utils.StaticInfos;
import com.lz.utils.Util;

public class NoteService {
	
	public List<Note> getNotesTree(String limit)throws Exception{
			//设置参数
		HashMap<String,String>params=new HashMap<String, String>();
		params.put("type", "note");
		params.put("limit", limit);
			//设置头
		HashMap<String, String>headers=new HashMap<String, String>();
		headers.put("Cookie", "PHPSESSID="+StaticInfos.phpsessid);
			//发送请求,获取conn
		HttpURLConnection conn=(HttpURLConnection) HttpRequestUtil.sendGetRequest(AppConstant.TREE_NOTE_LIST_URL, params, headers);
		//解析xml文件获得Question的list
		int code=conn.getResponseCode();
		InputStream in=conn.getInputStream();
		List<Note> tempNotes=parseNoteXML(in);
		/*ByteArrayOutputStream baos=new ByteArrayOutputStream();
		int len;
		byte []buffer=new byte[1024];
		while ((len=in.read(buffer))!=-1) {
			baos.write(buffer, 0, len);
		}
		in.close();
		baos.close();*/
		//System.out.println(code+"----"+"\r\n"+baos.toString());
		Log.i("ResponseCode","TreeNoteList-----"+code);
		return tempNotes;
	}
	
	public List<Note> getNotesShare(String lid,String type,String limit) throws Exception{
		//设置参数
	HashMap<String,String>params=new HashMap<String, String>();
	params.put("type", type);
	params.put("lid", lid);
	params.put("limit", limit);
		//设置头
	HashMap<String, String>headers=new HashMap<String, String>();
	headers.put("Cookie", "PHPSESSID="+StaticInfos.phpsessid);
	
		//发送请求,获取conn
	
	HttpURLConnection conn=(HttpURLConnection) HttpRequestUtil.sendGetRequest(AppConstant.SHARE_NOTE_LIST_URL, params, headers);
	
	//解析xml文件获得Question的list
	int code=conn.getResponseCode();
	Log.i("ResponseCode","ShareNoteList-----"+code);
	List<Note> tempNotes=parseNoteXML(conn.getInputStream());
	return tempNotes;
	}
	
	public void sendFollowGetRequest(boolean followTag,String noteid){
		//设置参数
		HashMap<String,String>params=new HashMap<String, String>();
		params.put("type", "ss");
		params.put("noteid", noteid);
		if(followTag)
			params.put("ifss", "1");
		else
			params.put("ifss", "0");
		//设置头
		HashMap<String, String>headers=new HashMap<String, String>();
		headers.put("Cookie", "PHPSESSID="+StaticInfos.phpsessid);
		//发送请求,获取conn
		try {
			HttpURLConnection conn=(HttpURLConnection) HttpRequestUtil.sendGetRequest(AppConstant.NOTE_FOLLOW_URL, params, headers);
			int code =conn.getResponseCode();
			Log.i("ResponseCode", "笔记关注"+code);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	public void sendZanGetRequest(boolean zanTag,String noteid){
		//设置参数
		HashMap<String,String>params=new HashMap<String, String>();
		params.put("type", "ifz");
		params.put("noteid", noteid);
		if(zanTag)
			params.put("ifz", "1");
		else
			params.put("ifz", "0");
		//设置头
		HashMap<String, String>headers=new HashMap<String, String>();
		headers.put("Cookie", "PHPSESSID="+StaticInfos.phpsessid);
		//发送请求,获取conn
		try {
			HttpURLConnection conn=(HttpURLConnection) HttpRequestUtil.sendGetRequest(AppConstant.NOTE_ZAN_URL, params, headers);
			ByteArrayOutputStream baos=new ByteArrayOutputStream();
			int code =conn.getResponseCode();
			InputStream in=conn.getInputStream();
			int len;
			byte []buffer=new byte[1024];
			while ((len=in.read(buffer))!=-1) {
				baos.write(buffer, 0, len);
			}
			in.close();
			baos.close();
			Log.i("ResponseCode", "笔记点赞"+code);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	public void deleteMyNote(String noteid) {
		//设置参数
		HashMap<String,String>params=new HashMap<String, String>();
		params.put("type", "del");
		params.put("noteid", noteid);
		//设置头
		HashMap<String, String>headers=new HashMap<String, String>();
		headers.put("Cookie", "PHPSESSID="+StaticInfos.phpsessid);
		//发送请求
		HttpURLConnection conn;
		try {
			conn = (HttpURLConnection) HttpRequestUtil.sendGetRequest(AppConstant.NOTE_DELETE_URL, params, headers);
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
			String tag=new String(baos.toString());
			Log.i("ReturnTag", tag);
			Log.i("ResponseCode", "笔记删除"+code);
		} catch (Exception e) {
			Log.d("NetException", "笔记删除异常"+e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	private List<Note> parseNoteXML(InputStream inputStream) throws Exception {
		List<Note> tempNotes = new ArrayList<Note>();
		Note note = null;
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(inputStream, "UTF-8");
		for (int i = parser.getEventType(); i != parser.END_DOCUMENT; i = parser.next()) {
			if (i == parser.START_TAG) {
				if ("note".equals(parser.getName())) {
					note = new Note();
					tempNotes.add(note);
				} else if ("noteid".equals(parser.getName())) {
					note.setNoteid(parser.nextText());
				} else if ("nickname".equals(parser.getName())) {
					note.setNickname(parser.nextText());
				} else if ("portrait".equals(parser.getName())) {
					note.setPortrait(parser.nextText());
				} else if ("title".equals(parser.getName())) {
					note.setTitle(parser.nextText());
				} else if ("content".equals(parser.getName())) {
					note.setContent(parser.nextText());
				} else if ("time".equals(parser.getName())) {
					note.setTime(Long.parseLong(parser.nextText()));
				} else if ("sscount".equals(parser.getName())) {
					note.setfCount(parser.nextText());
				} else if ("zcount".equals(parser.getName())) {
					note.setzCount(parser.nextText());
				} else if ("ifss".equals(parser.getName())) {
					note.setIff(Util.parseBoolean(Integer.parseInt(parser.nextText())));
				} else if ("ifz".equals(parser.getName())) {
					note.setIfz(Util.parseBoolean(Integer.parseInt(parser.nextText())));
				}
			}
		}
		return tempNotes;
	}
	
	
	public void updateNotePostRequest(String title, String content, String noteid) {
		//设置参数
		HashMap<String,String>params=new HashMap<String, String>();
		params.put("type", "edit");
		params.put("title", title);
		params.put("content",content);
		params.put("noteid",noteid);
		//设置头
		HashMap<String, String>headers=new HashMap<String, String>();
		headers.put("Cookie", "PHPSESSID="+StaticInfos.phpsessid);
		//发送请求
		HttpURLConnection conn;
		try {
			conn = (HttpURLConnection) HttpRequestUtil.sendPostRequest(AppConstant.NOTE_UPDATE_URL, params, headers);
			int code =conn.getResponseCode();
			Log.i("ResponseCode", "笔记编辑"+code);
		} catch (Exception e) {
			Log.d("NetException", "笔记编辑异常");
			e.printStackTrace();
		
		}
		
	}

	public void newNotePostRequest(String title, String content, String lid) {
		//设置参数
		HashMap<String,String>params=new HashMap<String, String>();
		params.put("type", "new");
		params.put("title", title);
		params.put("content",content);
		params.put("lid", lid);
		//设置头
		HashMap<String, String>headers=new HashMap<String, String>();
		headers.put("Cookie", "PHPSESSID="+StaticInfos.phpsessid);
		//发送请求
		HttpURLConnection conn;
		try {
			conn = (HttpURLConnection) HttpRequestUtil.sendPostRequest(AppConstant.NOTE_NEW_URL, params, headers);
			int code =conn.getResponseCode();
			Log.i("ResponseCode", "笔记新建"+code);
		} catch (Exception e) {
			Log.d("NetException", "笔记新建异常");
			e.printStackTrace();
		}
		
		
		
	}

	public List<Note> getIndexNotes(String uid,String limit) throws Exception {
		// 设置参数
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("type", "uid");
		params.put("uid", uid);
		params.put("limit", limit);
		// 设置头
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Cookie", "PHPSESSID=" + StaticInfos.phpsessid);
		// 发送请求,获取conn
		HttpURLConnection conn = (HttpURLConnection) HttpRequestUtil.sendGetRequest(AppConstant.PERSONAL_INFO_NOTE_LIST_URL, params,headers);
		// 解析xml文件获得Note的list
		int code = conn.getResponseCode();
		Log.i("ResponseCode", "PersonalInfoMyNoteList-----" + code);
		List<Note> tempNotes = parseNoteXML(conn.getInputStream());
		return tempNotes;
	}

	public List<Note> getIndexFollowNotes(String uid,String limit) throws Exception {
		// 设置参数
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("type", "uidss");
		params.put("uid", uid);
		params.put("limit", limit);
		// 设置头
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Cookie", "PHPSESSID=" + StaticInfos.phpsessid);
		// 发送请求,获取conn
		HttpURLConnection conn = (HttpURLConnection) HttpRequestUtil.sendGetRequest(AppConstant.PERSONAL_INFO_COLLECT_NOTE_LIST_URL, params,headers);
		// 解析xml文件获得Note的list
		int code = conn.getResponseCode();
		Log.i("ResponseCode", "PersonalInfoCollectNoteList-----" + code);
		List<Note> tempNotes = parseNoteXML(conn.getInputStream());
		return tempNotes;
	}
}
