package com.lz.activity;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.lfl.activity.Help;
import com.lfl.service.PlayService;
import com.lfl.utils.AppConstant;
import com.lfl.utils.FileUtils;
import com.lfl.utils.Toolkits;
import com.nut.activity.R;

/**
 * 此activity实际上仅仅负责初始化，对用户不可见，正式版中是否采用这种模式待定
 * @author FIRE_TRAY
 */
public class WelcomeActivity extends Activity
{
	private Context context;
	private SharedPreferences isFirst;
	
	private Handler handler=new Handler(){
		public void handleMessage(Message msg) {
			if(msg.what==1){
				if(isFirst.getString("isFirst", "false").equals("false")){
					Intent intent=new Intent(context,Help.class);
					startActivity(intent);
					finish();
				}else{
					Intent intent=new Intent(context, LoginActivity.class);
					startActivity(intent);
					Intent startPlayServiceIntent = new Intent(context, PlayService.class);
					startService(startPlayServiceIntent);
					
					finish();
				}
			}
		}
	};
	
	/**
	 * 测试下，用户为default
	 */
	private void buildNUTDirs()
	{
		File nutFile = new File(AppConstant.FilePath.NUT_ROOT);
		File mp3File = new File(AppConstant.FilePath.MP3_FILE_PATH);
		File lrcFile = new File(AppConstant.FilePath.LRC_FILE_PATH);
		File picFile = new File(AppConstant.FilePath.PIC_FILE_PATH);
		File logFile = new File(AppConstant.FilePath.LOG_FILE_PATH);
		if (!nutFile.exists())
		{
			nutFile.mkdirs();
		}
		if (!mp3File.exists())
		{
			mp3File.mkdirs();
		}
		if (!lrcFile.exists())
		{
			lrcFile.mkdirs();
		}
		if (!picFile.mkdirs())
		{
			picFile.mkdirs();
		}
		if (!logFile.exists())
		{
			try
			{
				logFile.createNewFile();
			} catch (IOException e)
			{
				e.printStackTrace();
			}

			String initLogContent = "@logDate#" + Toolkits.getCurrrentMoment();
			FileUtils.overrideFile(AppConstant.FilePath.LOG_FILE_PATH, initLogContent);
		}
	}

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		context=getApplication();
		isFirst=getSharedPreferences("isFirst", MODE_PRIVATE);
		buildNUTDirs();
		handler.sendEmptyMessageDelayed(1, 2000);
	}

	protected void onDestroy()
	{
		super.onDestroy();
		Editor editor=isFirst.edit();
		editor.putString("isFirst", "true");
		editor.commit();
		finish();
	}

}

