package com.lz.javabean;

import java.io.Serializable;

public class Note implements Serializable{
	private String noteid;
	private String nickname;
	private String portrait;

	private String title;
	private String content;
	private String zCount;
	private String fCount;
	private String lid;
	
	private long time;
	private boolean ifz;
	private boolean iff;
	
	public String getPortrait() {
		return portrait;
	}
	public void setPortrait(String portrait) {
		this.portrait = portrait;
	}
	public String getNoteid() {
		return noteid;
	}
	public void setNoteid(String noteid) {
		this.noteid = noteid;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
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
	public String getfCount() {
		return fCount;
	}
	public void setfCount(String fCount) {
		this.fCount = fCount;
	}
	public String getLid() {
		return lid;
	}
	public void setLid(String lid) {
		this.lid = lid;
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
	public boolean isIff() {
		return iff;
	}
	public void setIff(boolean iff) {
		this.iff = iff;
	}
	
	

}
