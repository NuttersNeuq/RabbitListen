package com.lfl.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;

public class Toolkits
{
	/**
	 * 格式：**** 年 ** 月 ** 日
	 */
	public static String getCurrentDate()
	{
		String retString = null;
		int year, month, day;
		Calendar calendar = Calendar.getInstance();
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH) + 1;
		day = calendar.get(Calendar.DATE);
		retString = year + " 年 " + month + " 月 " + day + " 日 ";
		return retString;
	}

	/**
	 * 格式：y + "_" + m + "_" + d + "_" + h + "_" + min + "_" + sec + "_" +
	 * milliSec
	 */
	public static String getCurrrentMoment()
	{
		String retString = "";
		Calendar calendar = Calendar.getInstance();
		int y, m, d, h, min, sec, milliSec;
		y = calendar.get(Calendar.YEAR);
		m = calendar.get(Calendar.MONTH) + 1;
		d = calendar.get(Calendar.DATE);
		h = calendar.get(Calendar.HOUR_OF_DAY);
		min = calendar.get(Calendar.MINUTE);
		sec = calendar.get(Calendar.SECOND);
		milliSec = calendar.get(Calendar.MILLISECOND);
		retString += y + "_" + m + "_" + d + "_" + h + "_" + min + "_" + sec + "_" + milliSec;
		return retString;
	}
	/**
	 * 返回时去掉最后的空格 trim()
	 * @param is
	 * @return
	 */
	public static String convertStreamToString(InputStream is)
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;

		try
		{
			while ((line = reader.readLine()) != null)
			{
				sb.append(line + " ");
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				is.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return sb.toString().trim();
	}
}
