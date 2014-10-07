package com.lfl.model;

import java.io.Serializable;

public class OnlineWordInfo implements Serializable
{
	private static final long serialVersionUID = 1L;

	private String word;
	private String translation;
	private String pronunciation;
	
	public OnlineWordInfo()
	{
		pronunciation = "";
	}

	public String getWord()
	{
		return word;
	}

	public void setWord(String word)
	{
		this.word = word;
	}

	public String getTranslation()
	{
		return translation;
	}

	public void setTranslation(String translation)
	{
		this.translation = translation;
	}

	public String getPronunciation()
	{
		return pronunciation;
	}

	public void setPronunciation(String pronunciation)
	{
		this.pronunciation = pronunciation;
	}

	@Override
	public String toString()
	{
		return "OnlineWordInfo [word=" + word + ", translation=" + translation + ", pronunciation=" + pronunciation
				+ "]";
	}

}
