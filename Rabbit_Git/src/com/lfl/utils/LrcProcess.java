package com.lfl.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.lfl.model.LrcContent;

public class LrcProcess
{
	private ArrayList<LrcContent> lrcList;
	private LrcContent mLrcContent;

	/**
	 * ���һ��lrcContentû��endPos������Player�м��ص�Mp3��duration����
	 * 
	 * @param path
	 */
	public LrcProcess(String path,boolean isOnline)
	{
		mLrcContent = new LrcContent();
		lrcList = new ArrayList<LrcContent>();
		if(isOnline == false)
			readLrc(path);
		else
			readOnlineLrc(path); 
	}
	

	private void readOnlineLrc(String urlStr)
	{
		String line = null;
		BufferedReader buffer = null;
		URL url = null;
		boolean isFirstTime = true;
		try
		{
			// ����һ��URL����
			url = new URL(urlStr);
			// ����һ��Http����
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			// ʹ��IO����ȡ����
			buffer = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"GBK")); 
			while ((line = buffer.readLine()) != null)
			{
				if (line.charAt(0) == '[')
				{
					if (isFirstTime == false)
					{
						lrcList.add(mLrcContent);
						mLrcContent = new LrcContent();
					}
					isFirstTime = false;
					line = line.replace("[", "");
					line = line.replace("]", "@");
					// ���롰@���ַ�
					String splitLrcData[] = line.split("@");

					if (splitLrcData.length > 1)
					{
						int pos = time2Str(splitLrcData[0]);
						mLrcContent.setStartPos(pos);
						if (lrcList.size() > 0)
							lrcList.get(lrcList.size() - 1).setEndPos(pos);
						mLrcContent.setEngLrc(splitLrcData[1]);

					}
				}
				else
				{
					mLrcContent.setChsLrc(line);
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				buffer.close();
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private void readLrc(String path)
	{
		File file = new File(path);
		try
		{
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis, "GBK");
			BufferedReader br = new BufferedReader(isr);
			String s = "";
			boolean isFirstTime = true;

			while ((s = br.readLine()) != null)
			{
				if (s.charAt(0) == '[')
				{
					if (isFirstTime == false)
					{
						lrcList.add(mLrcContent);
						mLrcContent = new LrcContent();
					}
					isFirstTime = false;
					s = s.replace("[", "");
					s = s.replace("]", "@");
					// ���롰@���ַ�
					String splitLrcData[] = s.split("@");

					if (splitLrcData.length > 1)
					{
						int pos = time2Str(splitLrcData[0]);
						mLrcContent.setStartPos(pos);
						if (lrcList.size() > 0)
							lrcList.get(lrcList.size() - 1).setEndPos(pos);
						mLrcContent.setEngLrc(splitLrcData[1]);

					}
				}
				else
				{
					mLrcContent.setChsLrc(s);
				}
			}
			lrcList.add(mLrcContent);
			br.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private int time2Str(String timeStr)
	{
		timeStr = timeStr.replace(":", ".");
		timeStr = timeStr.replace(".", "@");

		String timeData[] = timeStr.split("@"); // ��ʱ��ָ����ַ�������

		// ������֡��벢ת��Ϊ����
		int minute = Integer.parseInt(timeData[0]);
		int second = Integer.parseInt(timeData[1]);
		int millisecond = Integer.parseInt(timeData[2]);

		// ������һ������һ�е�ʱ��ת��Ϊ������
		int currentTime = (minute * 60 + second) * 1000 + millisecond * 10;
		return currentTime;
	}

	public ArrayList<LrcContent> getLrcList()
	{
		return lrcList;
	}

}
