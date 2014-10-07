package com.lfl.model;

import java.io.Serializable;

public class Mp3Info implements Serializable
{
	private static final long serialVersionUID = 1L;

	private String id;
	private String name;
	private String duration;
	private String size;
	private String difficulty;
	private String lrcLanguage;
	private String course;
	private String pic;
	private String startTime;
	private String round;

	public Mp3Info()
	{
		startTime = "0";
		round = "11";
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDuration()
	{
		return duration;
	}

	public void setDuration(String duration)
	{
		this.duration = duration;
	}

	public String getSize()
	{
		return size;
	}

	public void setSize(String size)
	{
		this.size = size;
	}

	public String getDifficulty()
	{
		return difficulty;
	}

	public void setDifficulty(String difficulty)
	{
		this.difficulty = difficulty;
	}

	public String getLrcLanguage()
	{
		return lrcLanguage;
	}

	public void setLrcLanguage(String lrcLanguage)
	{
		this.lrcLanguage = lrcLanguage;
	}

	public String getCourse()
	{
		return course;
	}

	public void setCourse(String course)
	{
		this.course = course;
	}

	public String getPic()
	{
		return pic;
	}

	public void setPic(String pic)
	{
		this.pic = pic;
	}

	public String getStartTime()
	{
		return startTime;
	}

	public void setStartTime(String startTime)
	{
		this.startTime = startTime;
	}

	/**
	 * 轮数设定 第一轮: 全文听力:11 每句重复三遍:12 正常播放，重点播放2遍:13 第二轮～第四轮：2-4
	 * 都是重点播放2句，其他播放一句。用户可取消重点句子
	 * 
	 */
	public String getRound()
	{
		return round;
	}

	public void setRound(String round)
	{
		this.round = round;
	}

	@Override
	public String toString()
	{
		return "Mp3Info [id=" + id + ", name=" + name + ", duration=" + duration + ", size=" + size + ", difficulty="
				+ difficulty + ", lrcLanguage=" + lrcLanguage + ", course=" + course + ", pic=" + pic + ", startTime="
				+ startTime + ", round=" + round + "]";
	}

	/**
	 * 在完成全程学习时返回true
	 */
	public boolean roundIncrease()
	{
		if (round.equals("11"))
		{
			round = "12";
		}
		else if (round.equals("12"))
		{
			round = "13";
		}
		else if (round.equals("13"))
		{
			round = "2";
		}
		else if (round.equals("2"))
		{
			round = "3";
		}
		else if (round.equals("3"))
		{
			round = "4";
		}
		else if (round.equals("4"))
		{
			round = "5";
		}
		else if (round.equals("5"))
		{
			return true;
		}
		return false;
	}

}
