package com.lfl.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;

import com.lfl.model.OnlineWordInfo;
import com.lz.utils.HttpRequestUtil;

public class OnlineDictionaryXMLParser
{
	public static OnlineWordInfo parser(String word)
	{
		int responseCode;
		InputStream inputStream;
		OnlineWordInfo wordInfo = new OnlineWordInfo();
		wordInfo.setWord(word);
		try
		{

			HashMap<String, String> headers = new HashMap<String, String>();
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("wordKey", word);

			HttpURLConnection urlConnection = (HttpURLConnection) HttpRequestUtil.sendGetRequest(
					AppConstant.URL.ONLINE_DICTIONARY_URL, params, headers);
			responseCode = urlConnection.getResponseCode();
			inputStream = urlConnection.getInputStream();

			if (responseCode != 200)
			{
				wordInfo.setTranslation("网络异常，再试试吧");
			}
			else
			{
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int len;
				byte[] buffer = new byte[1024];
				while ((len = inputStream.read(buffer)) != -1)
				{
					baos.write(buffer, 0, len);
				}
				// inputStream.close();
				baos.close();
				String returnCode = baos.toString();

				String[] str = returnCode.split("<Translation>");
				String[] trans = str[1].split("</Translation>");
				String wordMeaning = trans[0];
				String pron = "";
				if (wordMeaning.equals("欢迎访问海词网(http://dict.cn/)"))
				{
					wordMeaning = "没有找到该词释义";
				}
				else
				{
					str = returnCode.split("<Pron>");
					trans = str[1].split("</Pron>");
					pron = "[" + trans[0] + "]";
					wordInfo.setPronunciation(pron);
				}
				wordInfo.setTranslation(wordMeaning);

				returnCode = null;
				str = null;
				trans = null;

				// inputStream.close();
			}
		} catch (Exception e)
		{

			e.printStackTrace();
			wordInfo.setTranslation("没找到解释");
		}
		System.out.println("拿到的Word解释：" + wordInfo);
		return wordInfo;
	}
}
