package com.lfl.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.lfl.model.Mp3Info;

/**
 * 离线保存器，将在构造函数时初始化 ，仅对所下载听力的列表负责
 * 
 * @author FIRE_TRAY
 * 
 */
public class OfflineSaver
{
	private String logDate;

	private List<Mp3Info> downloadMp3Infos = new ArrayList<Mp3Info>();

	private String sdcardLogFileDate;

	public OfflineSaver()
	{
		String sdcardLogFileContent = FileUtils.readTxtFile(AppConstant.FilePath.LOG_FILE_PATH);
		String attrs[] = sdcardLogFileContent.split("@");
		for (int i = 0; i < attrs.length; i++)
		{
			String attr = attrs[i];
			String vals[] = attr.split("#");
			if (vals[0].equals("logDate"))
			{
				sdcardLogFileDate = vals[1];
				logDate = sdcardLogFileDate;
			}
			else if (vals[0].equals("mp3Info"))
			{
				Mp3Info mp3Info = new Mp3Info();
				for (int j = 1; j < vals.length; j++)
				{
					String map[] = vals[j].split(":");
					String key = map[0];
					String value = map[1];
					if (key.equals("id"))
					{
						mp3Info.setId(value);
					}
					else if (key.equals("name"))
					{
						mp3Info.setName(value);
					}
					else if (key.equals("duration"))
					{
						mp3Info.setDuration(value);
					}
					else if (key.equals("size"))
					{
						mp3Info.setSize(value);
					}
					else if (key.equals("difficulty"))
					{
						mp3Info.setDifficulty(value);
					}
					else if (key.equals("lrcLanguage"))
					{
						mp3Info.setLrcLanguage(value);
					}
					else if (key.equals("course"))
					{
						mp3Info.setCourse(value);
					}
					else if (key.equals("pic"))
					{
						mp3Info.setPic(value);
					}

				}
				downloadMp3Infos.add(mp3Info);
			}
		}
	}

	public String getSdcardLogFileDate()
	{
		return sdcardLogFileDate;
	}

	public String getLogDate()
	{
		return logDate;
	}

	public void setLogDate(String logDate)
	{
		this.logDate = logDate;
	}

	public List<Mp3Info> getDownloadMp3Infos()
	{
		return downloadMp3Infos;
	}

	public boolean isMp3InfoLoaded(Mp3Info mp3Info)
	{
		for (int i = 0; i < downloadMp3Infos.size(); i++)
		{
			if (downloadMp3Infos.get(i).getId().equals(mp3Info.getId()))
			{
				return true;
			}
		}
		return false;
	}

	public int getDownloadListSize()
	{
		return downloadMp3Infos.size();
	}

	/**
	 * 自动更新了log日期并保存到SDcard中
	 * 
	 * @param mp3Info
	 */
	public void addMp3Info(Mp3Info mp3Info)
	{
		
		if (!isMp3InfoLoaded(mp3Info))
		{
			setLogDate(Toolkits.getCurrrentMoment());
			downloadMp3Infos.add(mp3Info);
			saveToSdcard();
		}
	}

	/**
	 * 自动更新了log日期并保存到SDcard中
	 * 
	 * @param mp3Info
	 */
	public void removeMp3Info(Mp3Info mp3Info)
	{
		File picfFile = new File(AppConstant.FilePath.PIC_FILE_PATH + mp3Info.getPic());
		File mp3File = new File(AppConstant.FilePath.MP3_FILE_PATH + mp3Info.getId() + ".mp3");
		File lrcFile = new File(AppConstant.FilePath.LRC_FILE_PATH + mp3Info.getId() + ".lrc");
		for (int i = 0; i < downloadMp3Infos.size(); i++)
		{
			if (downloadMp3Infos.get(i).getId().equals(mp3Info.getId()))
			{
				downloadMp3Infos.remove(i);
				if (picfFile.exists())
					picfFile.delete();
				if (mp3File.exists())
					mp3File.delete();
				if (lrcFile.exists())
					lrcFile.delete();
				setLogDate(Toolkits.getCurrrentMoment());
				saveToSdcard();
				break;
			}
		}
	}

	private void saveToSdcard()
	{
		if (sdcardLogFileDate.equals(logDate) == false)
		{
			String content = "";
			content += "@logDate#" + logDate + "\r\n";
			for (int i = 0; i < downloadMp3Infos.size(); i++)
			{
				Mp3Info mp3Info = downloadMp3Infos.get(i);
				String mp3InfoContent = "@mp3Info" + "#" + "id:" + mp3Info.getId() + "#" + "name:" + mp3Info.getName()
						+ "#" + "duration:" + mp3Info.getDuration() + "#" + "size:" + mp3Info.getSize() + "#"
						+ "difficulty:" + mp3Info.getDifficulty() + "#" + "lrcLanguage:" + mp3Info.getLrcLanguage()
						+ "#" + "course:" + mp3Info.getCourse() + "#" + "pic:" + mp3Info.getPic() + "\r\n";
				content += mp3InfoContent;
			}
			FileUtils.overrideFile(AppConstant.FilePath.LOG_FILE_PATH, content);
		}
	}

	public void printOfflineSaver()
	{
		System.out.println("=================================");
		System.out.println("OfflineSaver");
		System.out.println("logDate : " + logDate);
		System.out.println("sdcardLogFileDate : " + sdcardLogFileDate);
		System.out.println("id name duration size difficulty lrcLang course pic");
		for (int i = 0; i < downloadMp3Infos.size(); i++)
		{
			Mp3Info mp3Info = downloadMp3Infos.get(i);
			String logCatOutput = mp3Info.getId() + " " + mp3Info.getName() + " " + mp3Info.getDuration() + " "
					+ mp3Info.getSize() + " " + mp3Info.getDifficulty() + " " + mp3Info.getLrcLanguage() + " "
					+ mp3Info.getCourse() + " " + mp3Info.getPic();
			System.out.println(logCatOutput);
		}
		System.out.println("=================================");
	}
}
