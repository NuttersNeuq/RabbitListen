package com.lz.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.ActionBar;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.hare.activity.R;

public class Util {
	/**
	 * 读取输入流中的数据, 返回
	 * @param in	要读取数据的输入流
	 * @return		输入流中的数据
	 */
	
	public static byte[] read(InputStream in) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len;
		while ((len = in.read(buffer)) != -1)
			baos.write(buffer, 0, len);
		in.close();
		baos.close();
		
		return baos.toByteArray();
	}
	
	public static void setTitle(Context context,ActionBar actionBar,String title) {
		actionBar.setDisplayHomeAsUpEnabled(true);//使返回按钮能够使用
		actionBar.setDisplayShowHomeEnabled( true);
		actionBar.setDisplayShowCustomEnabled(true);//使用自定义视图

		View v = View.inflate(context, R.layout.title, null);//获取布局
		
		TextView actionBarTitle=(TextView) v.findViewById(R.id.actionbar_titleTV);
		actionBarTitle.setText(title);
		
		actionBar.setCustomView(v,new ActionBar.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}
	

	public static boolean parseBoolean(int number){
		if(number==1){
			return true;
		}else
			return false;		
	}
	

	public static String getTime(long time) {
		Long currenrTime=System.currentTimeMillis();
		Long subtract=currenrTime-time;
		
		int min=(int) (subtract/1000/60)+1;
		int hour=min/60+1;
		int day=hour/60+1;
		
		if(min<60){
			return min+"分钟前";
		}else if(hour<24){
			return hour+"小时前";
		}else if(day<7)
			return day+"天前";
		else{
			Date date=new Date(time);
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
			String strDate=dateFormat.format(date);
			return strDate;					
		}
	}
	
	public static void showToast(Context context,String info){
		Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
	}

	public static void ToggleSoftInput(Context context) {
		InputMethodManager imm=(InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);		
	}
	
	// 设置要分享的文本和图片即可
	public static void showShare(Context context,String content)
	{
		ShareSDK.initSDK(context);
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(context.getString(R.string.share));
		// text是分享文本，所有平台都需要这个字段
		oks.setText("#坚果听力#" + content);
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		// 启动分享GUI
		oks.show(context);

	}
}
