package com.lfl.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class FileUtils
{
	public static void createFile(String filePath)
	{
		File file = new File(filePath);
		if (!file.exists())
			try
			{
				file.createNewFile();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
	}

	public static void createFileFromStream(String filePath, InputStream iStream)
	{
		File file = new File(filePath);
		if (file.exists())
			file.delete();
		OutputStream outStream = null;
		try
		{
			outStream = new FileOutputStream(file);
			byte buffer[] = new byte[1024];
			int temp;
			while ((temp = iStream.read(buffer)) != -1)
			{
				outStream.write(buffer, 0, temp);
			}
			outStream.flush();
			outStream.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void createFileDirectory(String directoryPath)
	{
		File file = new File(directoryPath);
		if (!file.exists())
			file.mkdirs();
	}

	public static void deleteFile(String filePath)
	{
		File file = new File(filePath);
		if (file.isFile())
			file.delete();
		else if (file.isDirectory())
		{
			File[] childFiles = file.listFiles();
			if (childFiles == null || childFiles.length == 0)
			{
				file.delete();
				return;
			}

			for (int i = 0; i < childFiles.length; i++)
			{
				deleteFile(childFiles[i].getAbsolutePath());
			}
			file.delete();
		}
	}

	public static void insertContentToFile(String filePath, String content)
	{
		try
		{
			FileOutputStream fOutputStream = new FileOutputStream(filePath);
			byte[] bytes = content.getBytes(AppConstant.URL.ENCODING_DEFAULT);
			fOutputStream.write(bytes);
			fOutputStream.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void overrideFile(String filePath, String content)
	{
		try
		{
			File outputFile = new File(filePath);
			if (outputFile.exists())
				outputFile.delete();
			outputFile.createNewFile();
			FileOutputStream outputStream = new FileOutputStream(outputFile);
			outputStream.write(content.getBytes(AppConstant.URL.ENCODING_DEFAULT));
			outputStream.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static String readTxtFile(String filePath)
	{
		String retString = "";
		try
		{
			File file = new File(filePath);
			if (file.isFile() && file.exists())
			{
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), AppConstant.URL.ENCODING_DEFAULT);

				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null)
				{
					retString += lineTxt;
				}
				read.close();
			}
			else
			{
				System.out.println("找不到指定的文件");
			}
		} catch (Exception e)
		{
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}
		return retString;
	}

	public static boolean isFileExist(String filePath)
	{
		File file = new File(filePath);
		return file.exists();
	}

}
