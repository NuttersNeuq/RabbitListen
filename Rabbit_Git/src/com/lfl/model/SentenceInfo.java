package com.lfl.model;

import java.io.Serializable;

public class SentenceInfo implements Serializable
{
	private static final long serialVersionUID = 1L;

	private String id;
	private String sentence;
	private String translation;
	private String position;
	private String time;
	private String mp3Name;
	private String mp3Id;
	private int startPos;
	private int endPos;

	public SentenceInfo()
	{
		startPos = endPos = -1;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getMp3Id()
	{
		return mp3Id;
	}

	public void setMp3Id(String mp3Id)
	{
		this.mp3Id = mp3Id;
	}

	public String getSentence()
	{
		return sentence;
	}

	public void setSentence(String sentence)
	{
		this.sentence = sentence;
	}

	public String getPosition()
	{
		return position;
	}

	public void setPosition(String position)
	{
		this.position = position;
	}

	public String getTime()
	{
		return time;
	}

	public void setTime(String time)
	{
		this.time = time;
	}

	public String getMp3Name()
	{
		return mp3Name;
	}

	public void setMp3Name(String mp3Name)
	{
		this.mp3Name = mp3Name;
	}

	public int getStartPos()
	{
		return startPos;
	}

	public void setStartPos(int startPos)
	{
		this.startPos = startPos;
	}

	public int getEndPos()
	{
		return endPos;
	}

	public void setEndPos(int endPos)
	{
		this.endPos = endPos;
	}

	public String getTranslation()
	{
		return translation;
	}

	public void setTranslation(String translation)
	{
		this.translation = translation;
	}

	@Override
	public String toString()
	{
		return "SentenceInfo [sentence=" + sentence + ", translation=" + translation + ", position=" + position
				+ ", time=" + time + ", mp3Name=" + mp3Name + ", mp3Id=" + mp3Id + ", startPos=" + startPos
				+ ", endPos=" + endPos + "]";
	}

}
