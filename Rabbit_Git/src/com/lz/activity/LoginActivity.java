package com.lz.activity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.lfl.activity.Diyijiemian;
import com.lz.my.service.LoginService;
import com.lz.utils.Encrypt;
import com.lz.utils.StaticInfos;
import com.lz.utils.Util;
import com.hare.activity.R;

public class LoginActivity extends Activity {
	// 拿到各组组件的id
	private Button loginBT;
	private Button touristBT;
	private Button registerBT;
	private Dialog loadingDialog;
	private EditText usernameET;
	private EditText passwordET;
	private CheckBox isRemenberedCB;
	// 设置全局变量
	private Handler handler = new Handler();
	private SharedPreferences sp;
	private Context context;
	private String username = null;
	private String password = null;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏ActionBar
		setContentView(R.layout.activity_login);
		//判断是否登陆过
		if(StaticInfos.isLogin){
			startActivity(new Intent(LoginActivity.this,Diyijiemian.class));// 开启新的activity
			finish();
		}else{
			initView();
		}
	}

	// 初始化视图
	private void initView() {
		// 根据id拿到各种组件
		loginBT = (Button) findViewById(R.id.loginBT);
		registerBT = (Button) findViewById(R.id.login_registerBT);
		usernameET = (EditText) findViewById(R.id.usernameET);
		passwordET = (EditText) findViewById(R.id.passwordET);
		isRemenberedCB = (CheckBox) findViewById(R.id.isRemenberedCB);

		sp = getSharedPreferences("config", MODE_PRIVATE);
		context = getApplication();
		// 为usernameET和passwordET设置值
		usernameET.setText(sp.getString("username", ""));
		passwordET.setText(sp.getString("password", ""));
		isRemenberedCB.setChecked(Boolean.parseBoolean(sp.getString("checked",
				"0")));
		//为注册按钮设置监听器
		registerBT.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(context, RegisterActivity.class));
			}
		});
		// 为loginBT设置监听器
		loginBT.setOnClickListener(new OnClickListener() {
			String encryptedPassword;
			public void onClick(View v) {
				//弹出progressbar dialog
				loadingDialog = new Dialog(LoginActivity.this, R.style.loading_dialog_style);
				loadingDialog.setContentView(R.layout.loading_dialog);
				Window loadingDialogWindow = loadingDialog.getWindow();
				WindowManager.LayoutParams lParams = loadingDialogWindow.getAttributes();
				loadingDialogWindow.setGravity(Gravity.CENTER);
				lParams.alpha = 1f;
				loadingDialogWindow.setAttributes(lParams);
				loadingDialog.setCancelable(false); 
				
				// 获取username和password
				username = usernameET.getText().toString();
				password = passwordET.getText().toString();

				// 判断username和password是否为空
				if (username.equals("") || username == null
						|| password.equals("") || password == null) {
					Util.showToast(context, "用户名或密码不能为空");
					return;
				}
				// 判断是否记住密码
				if (isRemenberedCB.isChecked())
					save(username, password, "true");
				else
					save("", "", "false");

				// 将密码32位md5加密
				try {
					encryptedPassword = Encrypt.Bit32(password);
				} catch (NoSuchAlgorithmException e) {

					e.printStackTrace();
				}
				
				loadingDialog.show();
				
				// 开启新线程，进行联网验证操作
				new Thread() {
					public void run() {
						// 获取flag
						int flag = LoginService.login(username,encryptedPassword);
						// 如果flag=1
						if (flag == 1) {
							StaticInfos.isLogin=true;
							startActivity(new Intent(context,Diyijiemian.class));// 开启新的activity
							loadingDialog.cancel();
							sendBroadcast(new Intent().setAction("nut.broadcast.NotifyReceiver"));//发送开启notifyservice的广播
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
							
							finish();
							
						}
						// 如果flag=0或2
						else if (flag == 0 || flag == 2) {
							// 提示用户名或密码错误
							handler.post(new Runnable() {
								public void run() {
									loadingDialog.cancel();
									Util.showToast(context, "用户名或密码错误！");
								}
							});
						}
						// 如果flag=-1
						else if (flag == -1) {
							handler.post(new Runnable() {
								public void run() {
									loadingDialog.cancel();
									Util.showToast(context, "网络连接错误！");
								}
							});
						}
					}
				}.start();
			}
		});
	}
	/*
	 * 将usernmae和password保存到sharedpreferences中去
	 */
	private void save(String username, String password, String checked) {
		Editor editor = sp.edit();
		editor.putString("username", username);
		editor.putString("password", password);
		editor.putString("checked", checked);
		editor.commit();
	}



}