package com.lz.service;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.lfl.activity.Diyijiemian;
import com.lz.activity.TreeActivity;
import com.lz.my.service.LoginService;
import com.lz.my.service.NetService;
import com.lz.utils.Encrypt;
import com.lz.utils.StaticInfos;
import com.nut.activity.R;

public class NotifyService extends Service {
	private String studyNum="0";
	private String username;
	private String password;
	private String encryptedPassword;
	private SharedPreferences preferences;
	private StudyNotifyTimerTask studyNotifyTimerTask;
	private NotificationTimerTask notificationTimerTask;
	private NetService netService;
	private NotificationCompat.Builder builder;
	private int studyNotificationNum=0;				//有几条notification
	private int notificationNum=0;					//有几条notification
	private int notificationNumCount=0;            //notification上显示的数字
	private String allNotification="";
	
	private SharedPreferences notifyPrefrences;

	public void onCreate() {
		preferences=getSharedPreferences("confige", MODE_PRIVATE);
		netService=new NetService();
		studyNotifyTimerTask=new StudyNotifyTimerTask();
		notificationTimerTask=new NotificationTimerTask();
		//判断是否登陆过
		if (!StaticInfos.isLogin){
			//没有     则登陆
			// 获取username和password
			username=preferences.getString("username", "default");
			password=preferences.getString("password", "default");
			// 将密码32位md5加密
			try {
				encryptedPassword = Encrypt.Bit32(password);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			// 开启新线程，进行联网验证操作
			new Thread() {
				public void run() {
					// 获取flag
					int flag = LoginService.login(username,encryptedPassword);
					// 如果flag=1
					if (flag == 1) {
						//将标志置为真
						StaticInfos.isLogin=true;
						//写提醒文件
						try { // 创建notifytoggle文件
							File notifyToggleFile = new File(Environment.getExternalStorageDirectory() + "/nut");
							// 判断目录是否存在，不存在则创建
							if (!notifyToggleFile.exists())
								notifyToggleFile.mkdirs();
							// 指针指到notifytoggle.nut文件
							notifyToggleFile = new File(Environment.getExternalStorageDirectory()+ "/nut/notifytoggle.nut");
							// 判断文件是否存在,不存在则创建
							if (!notifyToggleFile.exists()) {
								notifyToggleFile.createNewFile();
							}
							FileWriter fw = new FileWriter(notifyToggleFile);
							// 判断学习提醒状态
							if (Boolean.parseBoolean(StaticInfos.notifyToggle)) {
								// 写值
								fw.write("true");
								fw.flush();
							} else {
								// 写值
								fw.write("false");
								fw.flush();
							}
							fw.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					// 如果flag=0或2
					else if (flag == 0 || flag == 2||flag==-1) {
						stopSelf();
					}
				}
			}.start();
		}
	}
	
	public void onStart(Intent intent, int startId) {
		Timer timer=new Timer(true);
		// 定时向服务器发请求获取notification
		timer.scheduleAtFixedRate(notificationTimerTask,0,20* 1000);
		// 定时进行学习提醒服务
		if (StaticInfos.notifyToggle.equals("true")) {
			timer.scheduleAtFixedRate(studyNotifyTimerTask, 4 * 60 * 60 * 1000,4 * 60 * 60 * 1000);
		}
	}
	
	private class StudyNotifyTimerTask extends TimerTask{
		public void run() {
			new Thread(){
				public void run() {
					//向服务器发送请求获取数据
					try {
						studyNum = netService.getStudyNotify();
					} catch (Exception e) {
						e.printStackTrace();
					}
					//发送通知，提示用户学习
					if(!studyNum.equals("0")){
						 Intent resultIntent = new Intent(NotifyService.this, Diyijiemian.class);
						 ShowNotification("坚果听力","还有"+studyNum+"篇精听听力没有完成，赶快去学习吧！","你还有没有完成的学习任务",resultIntent,studyNotificationNum++,0);
					}
				}
			}.start();
		}
		
	}
	
	private class NotificationTimerTask extends TimerTask{
		public void run() {
			new Thread(){
				public void run(){
					// 向服务器发送请求获取数据
					try {
						notifyPrefrences=getSharedPreferences("notify"+StaticInfos.uid, MODE_WORLD_WRITEABLE);
						allNotification = netService.getNotifications();
						if (allNotification.equals("")||allNotification==null||allNotification.equals("0")||!allNotification.contains("#")){
							return;
						}else{
							String[] notifications = allNotification.split("@]");
							Set<String> notificationSet=new TreeSet<String>();
							for(String notification:notifications){
								notificationSet.add(notification);
							}
							notificationNumCount = notificationSet.size();
							//写入sharedPrefrence
							Editor editor=notifyPrefrences.edit();
							for(String notification:notifications){
								System.out.println("notification:"+notification);
								editor.putString(notification,"true");
							}
							editor.commit();
							// 发送通知，提示用户查看
							Intent resultIntent = new Intent(NotifyService.this, TreeActivity.class);
							ShowNotification("坚果听力", "你有新的动态","你有新的动态", resultIntent,notificationNum, notificationNumCount);
						}
					} catch (IOException e) {
						System.out.println("异常"+e.toString());
						e.printStackTrace();
					}
				}
			}.start();
		}
	}
	
	private void ShowNotification(String title,String content,String tickerText,Intent resultIntent,int mID,int num) {
		//设置跳转activity
    	PendingIntent pendingIntent = PendingIntent.getActivity(this, 100, resultIntent, PendingIntent.FLAG_ONE_SHOT);
		//获取Notification.Builder
		Notification.Builder builder=new Notification.Builder(NotifyService.this);
		builder.setSmallIcon(R.drawable.notify_icon).setTicker(tickerText).setContentTitle(title).setContentText(content).setNumber(num).setContentIntent(pendingIntent);
//		Bitmap bmp=BitmapFactory.decodeResource(getResources(), R.drawable.notify_icon);
//		builder.setLargeIcon(bmp);
		Notification notification=builder.build();
//		notification.icon = R.drawable.notify_icon;
		notification.flags = Notification.FLAG_AUTO_CANCEL;		// 设置自动清除
		//获取通知管理器
		NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    	// 4.发送通知
    	manager.notify(mID, notification);
	}
	
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	public void onDestroy() {
		StaticInfos.isLogin=false;
	}
}
