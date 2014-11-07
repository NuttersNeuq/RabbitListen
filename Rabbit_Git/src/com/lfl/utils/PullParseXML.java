package com.lfl.utils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.lfl.model.CourseInfo;
import com.lfl.model.Mp3Info;
import com.lfl.model.SentenceInfo;
import com.lfl.model.WordInfo;
import com.lz.utils.HttpRequestUtil;
import com.lz.utils.StaticInfos;

/**
 * 一旦解析发生异常，返回一个仅带有一个标记了异常的对象的链表，错误在Name中保存。此外，!!!接受size为0的状态!!!
 * 
 * @author FIRE_TRAY
 * 
 */
public class PullParseXML
{
	/**
	 * 错误在Name中保存
	 * 
	 * @param urlStr
	 * @param params
	 * @param isGet
	 * @return mp3Infos
	 */
	public static List<Mp3Info> parseOnlineMp3XML(String urlStr, HashMap<String, String> params, boolean isGet)
	{
		List<Mp3Info> mp3Infos = new ArrayList<Mp3Info>();
		Mp3Info mp3Info = null;
		InputStream inputStream = null;

		int responseCode = 0;

		try
		{
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();

			HashMap<String, String> headers = new HashMap<String, String>();
			headers.put("Cookie", "PHPSESSID=" + StaticInfos.phpsessid);

			if (isGet)
			{
				HttpURLConnection urlConnection = (HttpURLConnection) HttpRequestUtil.sendGetRequest(urlStr, params,
						headers);
				responseCode = urlConnection.getResponseCode();
				inputStream = urlConnection.getInputStream();

			}
			else
			{
				HttpURLConnection urlConnection = (HttpURLConnection) HttpRequestUtil.sendPostRequest(urlStr, params,
						headers);
				responseCode = urlConnection.getResponseCode();
				inputStream = urlConnection.getInputStream();
			}

			if (responseCode == 200)
			{
				parser.setInput(inputStream, "utf-8");
				int eventType = parser.getEventType();
				while (eventType != XmlPullParser.END_DOCUMENT)
				{
					switch (eventType)
					{

					case XmlPullParser.START_TAG:
						if ("id".equals(parser.getName()))
						{
							mp3Info = new Mp3Info();
							mp3Info.setId(parser.nextText());
						}
						else if ("name".equals(parser.getName()))
						{
							mp3Info.setName(parser.nextText());
						}
						else if ("duration".equals(parser.getName()))
						{
							mp3Info.setDuration(parser.nextText());
						}
						else if ("size".equals(parser.getName()))
						{
							mp3Info.setSize(parser.nextText());
						}
						else if ("difficulty".equals(parser.getName()))
						{
							mp3Info.setDifficulty(parser.nextText());
						}
						else if ("lrclanguage".equals(parser.getName()))
						{
							mp3Info.setLrcLanguage(parser.nextText());
						}
						else if ("course".equals(parser.getName()))
						{
							mp3Info.setCourse(parser.nextText());
						}
						else if ("pic".equals(parser.getName()))
						{
							mp3Info.setPic(parser.nextText());
						}
						else if ("round".equals(parser.getName()))
						{
							mp3Info.setRound(parser.nextText());
						}
						else if ("starttime".equals(parser.getName()))
						{
							mp3Info.setStartTime(parser.nextText());
						}
						break;
					case XmlPullParser.END_TAG:
						if ("resource".equals(parser.getName()))
						{
							mp3Infos.add(mp3Info);
							mp3Info = null;
						}
						break;
					}
					eventType = parser.next();
				}
			}
			else
			{
				mp3Infos.clear();
				Mp3Info info = new Mp3Info();
				info.setName(AppConstant.INTERACTION_STATUS.NETWORK_CONNECTION_EXCEPTION + "");
				mp3Infos.add(info);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			mp3Infos.clear();
			Mp3Info info = new Mp3Info();
			info.setName(AppConstant.INTERACTION_STATUS.SERVER_STATUS_EXCEPTION + "");
			mp3Infos.add(info);
		}
		
		return mp3Infos;
	}
	
	/**
	 * 错误在Name中保存
	 * @param urlStr
	 * @param params
	 * @param isGet
	 * @return
	 */
	public static List<CourseInfo> parseOnlineCourseXML(String urlStr, HashMap<String, String> params, boolean isGet)
	{
		List<CourseInfo> courseInfos = new ArrayList<CourseInfo>();
		CourseInfo courseInfo = null;
		InputStream inputStream = null;
		int responseCode = 0;
		try
		{
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();

			HashMap<String, String> headers = new HashMap<String, String>();
			headers.put("Cookie", "PHPSESSID=" + StaticInfos.phpsessid);

			if (isGet)
			{
				HttpURLConnection urlConnection = (HttpURLConnection) HttpRequestUtil.sendGetRequest(urlStr, params,
						headers);
				responseCode = urlConnection.getResponseCode();
				inputStream = urlConnection.getInputStream();

			}
			else
			{
				HttpURLConnection urlConnection = (HttpURLConnection) HttpRequestUtil.sendPostRequest(urlStr, params,
						headers);
				responseCode = urlConnection.getResponseCode();
				inputStream = urlConnection.getInputStream();
			}

			if (responseCode == 200)
			{
				parser.setInput(inputStream, "utf-8");
				int eventType = parser.getEventType();
				while (eventType != XmlPullParser.END_DOCUMENT)
				{
					while (eventType != XmlPullParser.END_DOCUMENT)
					{
						switch (eventType)
						{
						case XmlPullParser.START_TAG:
							if ("cid".equals(parser.getName()))
							{
								courseInfo = new CourseInfo();
								courseInfo.setId(parser.nextText());
							}
							else if ("name".equals(parser.getName()))
							{
								courseInfo.setName(parser.nextText());
							}
							else if ("pic".equals(parser.getName()))
							{
								courseInfo.setPic(parser.nextText());
							}
							else if ("count".equals(parser.getName()))
							{
								courseInfo.setCount(parser.nextText());
							}
							else if ("content".equals(parser.getName()))
							{
								courseInfo.setIntroduction(parser.nextText());
							}
							else if ("ifd".equals(parser.getName()))
							{
								courseInfo.setIfd(parser.nextText());
							}
							break;
						case XmlPullParser.END_TAG:
							if ("course".equals(parser.getName()))
							{

								courseInfos.add(courseInfo);
								courseInfo = null;
							}
							break;
						}
						eventType = parser.next();
					}
				}
			}
			else
			{
				courseInfos.clear();
				CourseInfo info = new CourseInfo();
				info.setName(AppConstant.INTERACTION_STATUS.NETWORK_CONNECTION_EXCEPTION + "");
				courseInfos.add(info);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			courseInfos.clear();
			CourseInfo info = new CourseInfo();
			info.setName(AppConstant.INTERACTION_STATUS.SERVER_STATUS_EXCEPTION + "");
			courseInfos.add(info);
		}

		return courseInfos;
	}

	/**
	 * 在 Word中获取异常信息
	 * 
	 * @param urlStr
	 * @param params
	 * @param isGet
	 * @return
	 */
	public static List<WordInfo> parseOnlineWordsXML(String urlStr, HashMap<String, String> params, boolean isGet)
	{
		List<WordInfo> wordInfos = new ArrayList<WordInfo>();
		WordInfo wordInfo = null;
		SentenceInfo sentenceInfo = null;
		InputStream inputStream = null;
		int responseCode = 0;
		try
		{
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();

			HashMap<String, String> headers = new HashMap<String, String>();
			headers.put("Cookie", "PHPSESSID=" + StaticInfos.phpsessid);

			if (isGet)
			{
				HttpURLConnection urlConnection = (HttpURLConnection) HttpRequestUtil.sendGetRequest(urlStr, params,
						headers);
				responseCode = urlConnection.getResponseCode();
				inputStream = urlConnection.getInputStream();

			}
			else
			{
				HttpURLConnection urlConnection = (HttpURLConnection) HttpRequestUtil.sendPostRequest(urlStr, params,
						headers);
				responseCode = urlConnection.getResponseCode();
				inputStream = urlConnection.getInputStream();
			}

			if (responseCode == 200)
			{
				parser.setInput(inputStream, "utf-8");
				int eventType = parser.getEventType();
				while (eventType != XmlPullParser.END_DOCUMENT)
				{
					while (eventType != XmlPullParser.END_DOCUMENT)
					{
						switch (eventType)
						{
						case XmlPullParser.START_TAG:
							if ("wid".equals(parser.getName()))
							{
								wordInfo = new WordInfo();
								sentenceInfo = new SentenceInfo();
								wordInfo.setId(parser.nextText());
							}
							else if ("word".equals(parser.getName()))
							{
								wordInfo.setWord(parser.nextText());
							}
//							else if ("wtranslation".equals(parser.getName()))
//							{
//								wordInfo.setMeaning(parser.nextText());
//							}
							else if ("sentence".equals(parser.getName()))
							{
								sentenceInfo.setSentence(parser.nextText());
							}
							else if ("stranslation".equals(parser.getName()))
							{
								sentenceInfo.setTranslation(parser.nextText());
							}
							else if ("position".equals(parser.getName()))
							{
								sentenceInfo.setPosition(parser.nextText());
							}
							else if ("time".equals(parser.getName()))
							{
								sentenceInfo.setTime(parser.nextText());
							}
							else if ("mp3Name".equals(parser.getName()))
							{
								sentenceInfo.setMp3Name(parser.nextText());
							}
							else if ("mp3Id".equals(parser.getName()))
							{
								sentenceInfo.setMp3Id(parser.nextText());
							}
							else if ("startPos".equals(parser.getName()))
							{
								sentenceInfo.setStartPos(Integer.parseInt(parser.nextText()));
							}
							else if ("endPos".equals(parser.getName()))
							{
								sentenceInfo.setEndPos(Integer.parseInt(parser.nextText()));
							}
							break;
						case XmlPullParser.END_TAG:
							if ("wordInfo".equals(parser.getName()))
							{
								wordInfo.setSentenceInfo(sentenceInfo);
								wordInfos.add(wordInfo);
							}
							break;
						}
						eventType = parser.next();
					}
				}
			}
			else
			{
				wordInfos.clear();
				WordInfo info = new WordInfo();
				info.setWord(AppConstant.INTERACTION_STATUS.NETWORK_CONNECTION_EXCEPTION + "");
				wordInfos.add(info);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			wordInfos.clear();
			WordInfo info = new WordInfo();
			info.setWord(AppConstant.INTERACTION_STATUS.SERVER_STATUS_EXCEPTION + "");
			wordInfos.add(info);
		}

		return wordInfos;
	}

	/**
	 * 在 Sentence中获取异常信息
	 * 
	 * @param urlStr
	 * @param params
	 * @param isGet
	 * @return
	 */
	public static List<SentenceInfo> parseOnlineSentencesXML(String urlStr, HashMap<String, String> params,
			boolean isGet)
	{
		List<SentenceInfo> sentenceInfos = new ArrayList<SentenceInfo>();
		SentenceInfo sentenceInfo = null;
		InputStream inputStream = null;
		int responseCode = 0;
		try
		{
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();

			HashMap<String, String> headers = new HashMap<String, String>();
			headers.put("Cookie", "PHPSESSID=" + StaticInfos.phpsessid);

			if (isGet)
			{
				HttpURLConnection urlConnection = (HttpURLConnection) HttpRequestUtil.sendGetRequest(urlStr, params,
						headers);
				responseCode = urlConnection.getResponseCode();
				inputStream = urlConnection.getInputStream();

			}
			else
			{
				HttpURLConnection urlConnection = (HttpURLConnection) HttpRequestUtil.sendPostRequest(urlStr, params,
						headers);
				responseCode = urlConnection.getResponseCode();
				inputStream = urlConnection.getInputStream();
			}

			if (responseCode == 200)
			{
				parser.setInput(inputStream, "utf-8");
				int eventType = parser.getEventType();
				while (eventType != XmlPullParser.END_DOCUMENT)
				{
					while (eventType != XmlPullParser.END_DOCUMENT)
					{
						switch (eventType)
						{
						case XmlPullParser.START_TAG:
							if ("sid".equals(parser.getName()))
							{
								sentenceInfo = new SentenceInfo();
								sentenceInfo.setId(parser.nextText());
							}
							else if ("sentence".equals(parser.getName()))
							{
								sentenceInfo.setSentence(parser.nextText());
							}
							else if ("stranslation".equals(parser.getName()))
							{
								sentenceInfo.setTranslation(parser.nextText());
							}
							else if ("position".equals(parser.getName()))
							{
								sentenceInfo.setPosition(parser.nextText());
							}
							else if ("time".equals(parser.getName()))
							{
								sentenceInfo.setTime(parser.nextText());
							}
							else if ("mp3Name".equals(parser.getName()))
							{
								sentenceInfo.setMp3Name(parser.nextText());
							}
							else if ("mp3Id".equals(parser.getName()))
							{
								sentenceInfo.setMp3Id(parser.nextText());
							}
							else if ("startPos".equals(parser.getName()))
							{
								sentenceInfo.setStartPos(Integer.parseInt(parser.nextText()));
							}
							else if ("endPos".equals(parser.getName()))
							{
								sentenceInfo.setEndPos(Integer.parseInt(parser.nextText()));
							}
							break;
						case XmlPullParser.END_TAG:
							if ("sentenceInfo".equals(parser.getName()))
							{
								sentenceInfos.add(sentenceInfo);
							}
							break;
						}
						eventType = parser.next();
					}
				}
			}
			else
			{
				sentenceInfos.clear();
				SentenceInfo info = new SentenceInfo();
				info.setSentence(AppConstant.INTERACTION_STATUS.NETWORK_CONNECTION_EXCEPTION + "");
				sentenceInfos.add(info);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			sentenceInfos.clear();
			SentenceInfo info = new SentenceInfo();
			info.setSentence(AppConstant.INTERACTION_STATUS.SERVER_STATUS_EXCEPTION + "");
			sentenceInfos.add(info);
		}

		return sentenceInfos;
	}
}
