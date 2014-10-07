package com.lz.javabean;

import java.io.Serializable;


/*
 * @field	qid:问题id
 * @field	nickname：提问人昵称
 * @field	portrait：提问人头像
 * @field	title：提问标题
 * @field	content：提问内容
 * @field	time：提问时间
 * @field	fcount：关注该问题数量
 * @field	anscount：回答该问题人数量
 * @field	iff：用户是否关注过该问题
 */

public class Question implements Serializable{
	private String qid;
	private String nickname;
	private	String portrait;
	private String title;
	private String content;
	private String fcount;
	private String anscount;
	private String ofLid;
	private long time;
	private boolean iff;
	
	public String getOfLid() {
		return ofLid;
	}
	public void setOfLid(String ofLid) {
		this.ofLid = ofLid;
	}
	public String getQid() {
		return qid;
	}
	public void setQid(String qid) {
		this.qid = qid;
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
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getFcount() {
		return fcount;
	}
	public void setFcount(String fcount) {
		this.fcount = fcount;
	}
	public String getAnscount() {
		return anscount;
	}
	public void setAnscount(String anscount) {
		this.anscount = anscount;
	}
	public boolean getIff() {
		return iff;
	}
	public void setIff(boolean iff) {
		this.iff = iff;
	}



	

}
