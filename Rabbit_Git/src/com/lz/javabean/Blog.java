package com.lz.javabean;

import java.io.Serializable;



public class Blog implements Serializable{
	private String bid;
	private String nickname;
	private String portrait;
	private String title;
	private String content;
	private String zCount;
	private String rCount;
	
	private long time;
	private boolean ifz;
	public String getBid() {
		return bid;
	}
	public void setBid(String bid) {
		this.bid = bid;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getPortrait() {
		return portrait;
	}
	public void setPortrait(String portrait) {
		this.portrait = portrait;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getzCount() {
		return zCount;
	}
	public void setzCount(String zCount) {
		this.zCount = zCount;
	}
	public String getrCount() {
		return rCount;
	}
	public void setrCount(String rCount) {
		this.rCount = rCount;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public boolean isIfz() {
		return ifz;
	}
	public void setIfz(boolean ifz) {
		this.ifz = ifz;
	}
	
	

}
