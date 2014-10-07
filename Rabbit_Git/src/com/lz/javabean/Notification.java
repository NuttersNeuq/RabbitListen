package com.lz.javabean;

public class Notification {
	private String nickName;
	private String title;
	private String portrait;
	private String content;
	private String time;
	private String iftype;			//iff为问题，ifz为帖子
	private String type;
	private String typeid;         //qid为问题，bid为帖子
	private String uid;
	private String from;
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	private boolean notRead;	//是否读过，没读过为true
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public boolean isNotRead() {
		return notRead;
	}
	public void setNotRead(boolean notRead) {
		this.notRead = notRead;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPortrait() {
		return portrait;
	}
	public void setPortrait(String portrait) {
		this.portrait = portrait;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getIftype() {
		return iftype;
	}
	public void setIftype(String iftype) {
		this.iftype = iftype;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTypeid() {
		return typeid;
	}
	public void setTypeid(String typeid) {
		this.typeid = typeid;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}

	
	
}
