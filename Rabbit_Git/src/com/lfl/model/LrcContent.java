package com.lfl.model;

import java.io.Serializable;



public class LrcContent implements Serializable
{
	/**
	 * default val
	 */
	private static final long serialVersionUID = 1L; 
	private String engLrc;
	private String chsLrc;
	private int startPos;
	private int endPos;
	
	public LrcContent()
	{
		startPos = endPos = -1;
		engLrc = chsLrc = "NUL";
	}
	
	public String getEngLrc()
	{
		return engLrc;
	}
	public void setEngLrc(String engLrc)
	{
		this.engLrc = engLrc;
	}
	public String getChsLrc()
	{
		return chsLrc;
	}
	public void setChsLrc(String chsLrc)
	{
		this.chsLrc = chsLrc;
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
	
	@Override
	public String toString()
	{
		return "LrcContent [engLrc=" + engLrc + ", chsLrc=" + chsLrc
				+ ", startPos=" + startPos + ", endPos=" + endPos + "]";
	}
	
	
	
}
