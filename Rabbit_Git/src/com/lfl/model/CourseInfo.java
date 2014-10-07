package com.lfl.model;

import java.io.Serializable;
/**
 * name,count,introduction,id,pic,ifd;
 * @author FIRE_TRAY
 *
 */

public class CourseInfo implements Serializable
{
	private static final long serialVersionUID = 1L;

	private String name;
	private String count;
	private String introduction;
	private String id;
	private String pic;
	private String ifd;	//�洢 Ϊ0,1
	
	public CourseInfo()
	{
		ifd = "0";
	}
	
	

	public String getIfd()
	{
		return ifd;
	}

	public void setIfd(String ifd)
	{
		this.ifd = ifd;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getCount()
	{
		return count;
	}

	public void setCount(String count)
	{
		this.count = count;
	}

	public String getIntroduction()
	{
		return introduction;
	}

	public void setIntroduction(String introduction)
	{
		this.introduction = introduction;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getPic()
	{
		return pic;
	}

	public void setPic(String pic)
	{
		this.pic = pic;
	}

	@Override
	public String toString()
	{
		return "CourseInfo [name=" + name + ", count=" + count + ", introduction=" + introduction + ", id=" + id
				+ ", pic=" + pic + ", ifd=" + ifd + "]";
	}

	

}
