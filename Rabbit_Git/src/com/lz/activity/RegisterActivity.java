package com.lz.activity;

import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.lz.my.service.LoginService;
import com.lz.my.service.NetService;
import com.lz.utils.Encrypt;
import com.lz.utils.Util;
import com.hare.activity.R;

public class RegisterActivity extends Activity {
	private Context context;
	private Handler handler;
	
	private Button registerRegisterBT;
	private EditText registerUsernameET;
	private EditText registerNicknameET;
	private EditText registerEmailET;
	private EditText registerPasswordET;
	private EditText registerConfirmPasswordET;
	
	private String nickname;
	private String username;
	private String password;
	private String confirmPassword;
	private String email;
	private String responseCode;
	private Dialog loadingDialog;
	
	private NetService netService;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏  
		setContentView(R.layout.activity_register);
		initView();
		initData();
	}


	private void initView() {
		context=getApplication();
		handler=new Handler();
		
		registerRegisterBT=(Button) findViewById(R.id.register_registerBT);
		registerUsernameET=(EditText) findViewById(R.id.register_usernameET);
		registerNicknameET=(EditText) findViewById(R.id.register_nicknameET);
		registerEmailET=(EditText) findViewById(R.id.register_emailET);
		registerPasswordET=(EditText) findViewById(R.id.register_passwordET);
		registerConfirmPasswordET=(EditText) findViewById(R.id.register_confirm_passwordET);
		
		netService=new NetService();
		
		loadingDialog = new Dialog(RegisterActivity.this, R.style.loading_dialog_style);
		loadingDialog.setContentView(R.layout.loading_dialog);
		Window loadingDialogWindow = loadingDialog.getWindow();
		WindowManager.LayoutParams lParams = loadingDialogWindow.getAttributes();
		loadingDialogWindow.setGravity(Gravity.CENTER);
		lParams.alpha = 1f;
		loadingDialogWindow.setAttributes(lParams);
		loadingDialog.setCancelable(false); 
	}
	
	


	private void initData() {
		
		//设置监听器
		
		registerRegisterBT.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				loadingDialog.show();
				//拿到数据
				username=registerUsernameET.getText().toString();
				nickname=registerNicknameET.getText().toString();
				password=registerPasswordET.getText().toString();
				confirmPassword=registerConfirmPasswordET.getText().toString();
				email=registerEmailET.getText().toString();
				//对数据进行判断
					//判断是否为空，用户名和密码只能用字母数字下划线，

				//判断用户名是否符合要求
					if(!username.matches("^[a-zA-Z]\\w{5,15}$")){
						Util.showToast(context, "用户名不符合规范，以字母开头，包含字母数字和下划线，长度在6-15之间");
						loadingDialog.cancel();
						return;
					}
					
				//判断邮箱
					if(!email.matches("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$")){
						Util.showToast(context, "邮箱不符合规范");
						loadingDialog.cancel();
						return;
					}
					
				//判断密码
					if(!password.matches("^[a-zA-Z]\\w{5,17}$")){
						Util.showToast(context, "密码不符合规范，以字母开头，包含字母数字和下划线，长度在6-18之间");
						loadingDialog.cancel();
						return;
					}
				//判断两次密码是否相同
					if(!password.equals(confirmPassword)){
						Util.showToast(context, "两次密码不一致");
						loadingDialog.cancel();
						return;
					}
				//联网获取数据
					new Thread(){
						public void run() {
							try {
								responseCode=netService.register(username,nickname,password,email);
								handler.post(new Runnable() {
									public void run() {
										//对返回的数据进行判断
										if(responseCode.equals("0")){
											loadingDialog.cancel();
											Util.showToast(context, "连接错误");
										}else if(responseCode.equals("2")){
											loadingDialog.cancel();
											Util.showToast(context, "用户名已存在");
										}else if(responseCode.equals("3")){
											loadingDialog.cancel();
											Util.showToast(context, "昵称已存在");
										}else if(responseCode.equals("1")){
											//注册成功
											Util.showToast(context, "注册成功");
											//登陆
											new Thread(){
												public void run() {
													try {
														int flag=LoginService.login(username,Encrypt.Bit32(password));
														if(flag==1){
															startActivity(new Intent(context, GuideOneActvity.class));
															loadingDialog.cancel();
															finish();
															//失败，提示，return	
														}else if(flag==-1){
															handler.post(new Runnable() {
																public void run() {
																		Util.showToast(context, "网络似乎开小差了");
																		return;
																}
															});
														}
													} catch (NoSuchAlgorithmException e) {
														e.printStackTrace();
													}
												}
											}.start();
										}
									}
								});
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}.start();
			}
		});
	}
}
