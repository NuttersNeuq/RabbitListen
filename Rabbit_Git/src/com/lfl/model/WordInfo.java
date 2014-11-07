package com.lfl.model;

import java.io.Serializable;

public class WordInfo implements Serializable
{
	private static final long serialVersionUID = 1L;

	private String id;
	private String word;
	private String meaning;
	private String pronunciation;
	private SentenceInfo sentenceInfo;

	public WordInfo()
	{
		pronunciation = " ";
		meaning = "......";
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getWord()
	{
		return word;
	}

	public void setWord(String word)
	{
		this.word = word;
	}

	public String getMeaning()
	{
		return meaning;
	}

	public void setMeaning(String meaning)
	{
		this.meaning = meaning;
	}

	public String getPronunciation()
	{
		return pronunciation;
	}

	public void setPronunciation(String pronunciation)
	{
		this.pronunciation = pronunciation;
	}

	public SentenceInfo getSentenceInfo()
	{
		return sentenceInfo;
	}

	public void setSentenceInfo(SentenceInfo sentenceInfo)
	{
		this.sentenceInfo = sentenceInfo;
	}

	@Override
	public String toString()
	{
		return "WordInfo [id=" + id + ", word=" + word + ", meaning=" + meaning + ", pronunciation=" + pronunciation
				+ ", sentenceInfo=" + sentenceInfo + "]";
	}

}
