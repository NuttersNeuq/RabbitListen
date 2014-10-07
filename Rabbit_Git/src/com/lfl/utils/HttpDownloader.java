package com.lfl.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpDownloader
{
	/**
	 * -1：代表下载文件出错 0：代表下载文件成功 1：代表文件已经存在
	 * 
	 * @param urlString
	 * @param storePath
	 * @param fileName
	 * @return downloadStatus
	 */
	public static int downloadFile(String urlString, String storePath, String fileName)
	{
		InputStream inputStream = null;
		String filePath = storePath + fileName;
		try
		{
			if (FileUtils.isFileExist(filePath))
				return 1;
			else
			{
				inputStream = getInputStreamFromUrl(urlString);
				FileUtils.createFileFromStream(filePath, inputStream);
				if (FileUtils.isFileExist(filePath))
				{
					return 0;
				}
				else
				{
					return -1;
				}

			}
		} catch (Exception e)
		{
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * 根据URL得到输入流
	 * 
	 * @param urlStr
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private static InputStream getInputStreamFromUrl(String urlStr) throws MalformedURLException, IOException
	{
		URL url = new URL(urlStr);
		HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
		InputStream inputStream = urlConn.getInputStream();
		return inputStream;
	}
}
