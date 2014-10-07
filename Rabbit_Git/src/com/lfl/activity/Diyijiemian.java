package com.lfl.activity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lfl.model.Mp3Info;
import com.lfl.service.PlayService;
import com.lfl.slidemenu.SlidingMenu;
import com.lfl.utils.AppConstant;
import com.lfl.utils.OfflineSaver;
import com.lfl.utils.RoundProgressBar;
import com.lfl.utils.Toolkits;
import com.lz.activity.FeedBackActivity;
import com.lz.activity.LoginActivity;
import com.lz.activity.PersonalInfoActivity;
import com.lz.activity.TreeActivity;
import com.lz.my.service.ImageService;
import com.lz.my.service.NetService;
import com.lz.service.NotifyService;
import com.lz.utils.HttpRequestUtil;
import com.lz.utils.StaticInfos;
import com.nut.activity.R;

/**
 * 在此界面退出程序，释放资源
 * 
 * @author FIRE_TRAY
 * 
 */
@SuppressLint("HandlerLeak")
public class Diyijiemian extends Activity
{

	private ImageService imageService;
	private NetService netService;

	private SlidingMenu slidingMenu;
	private Button menuSwitchButton;
	private View slideMenuView;
	private LinearLayout jingtingLinearLayout;
	private LinearLayout fantingLinearLayout;
	private ImageView headsetImageView;
	private RelativeLayout jukuLayout;
	private RelativeLayout dancibenLayout;
	private RelativeLayout tinglikuLayout;
	private RelativeLayout logoutLayout;
	private RoundProgressBar progressBar;
	private ImageView menuImageView;
	private RelativeLayout jianguoshuLayout;
	private RelativeLayout helpLayout;
	private Context mContext;

	private ImageView userPortraitImageView;
	private TextView userNicknameTextView;
	private TextView userMottoTextView;
	private RelativeLayout cehuaMenuFeedbackLL;
	private Bitmap portraitBitmap;

	private Handler handler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			if (msg.what == 1)
			{
				userPortraitImageView.setImageBitmap(portraitBitmap);
			}
		}
	};

	private int clickCount = 0;

	private Handler backKeyHandler = new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			clickCount = 0;
		}

	};

	public static OfflineSaver offlineSaver;

	private boolean isSwitchOn = false;

	private BroadcastReceiver receiver = new BroadcastReceiver()
	{

		@Override
		public void onReceive(Context context, Intent intent)
		{
			String action = intent.getAction();
			String resource = intent.getStringExtra("from");
			boolean isAuto = intent.getBooleanExtra("isAuto", false);
			if (resource.equals("Diyijiemian"))
			{
				if (action.equals(AppConstant.Actions.SERVICE_GIVE_INIT_STATUS_TO_HEADSET))
				{
					boolean isInit = intent.getBooleanExtra("isPlayServiceLoaded", false);
					if (isInit)
					{
						if (isAuto == false)
						{
							Intent startPlayerIntent = new Intent(mContext, NewLocalPlayer.class);
							startPlayerIntent.putExtra("mp3Info", (Mp3Info) intent.getSerializableExtra("mp3Info"));
							startActivity(startPlayerIntent);
						}
						headsetImageView.setImageResource(R.drawable.headset_loaded);

					}
					else
					{
						headsetImageView.setImageResource(R.drawable.headset);
						if (isAuto == false)
							Toast.makeText(getApplicationContext(), "尚未加载任何听力到播放器", Toast.LENGTH_SHORT).show();
					}
				}
			}
		}
	};

	private void initWidgets()
	{	
		
		isSwitchOn=Boolean.parseBoolean(StaticInfos.notifyToggle);//得到学习提醒按钮状态
		netService=new NetService();
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(AppConstant.Actions.SERVICE_GIVE_INIT_STATUS_TO_HEADSET);
		registerReceiver(receiver, filter);

		headsetImageView = (ImageView) findViewById(R.id.diyijiemian_headset_imageview);
		progressBar = (RoundProgressBar) findViewById(R.id.diyijiemian_progressBar);
		menuImageView = (ImageView) findViewById(R.id.diyijiemian_menu_imageview);

		progressBar.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
				LayoutInflater inflater = LayoutInflater.from(mContext);
				View editorView = inflater.inflate(R.layout.diyijiemian_goal_editor, null);
				builder.setView(editorView);
				builder.create();
				final AlertDialog dialog = builder.show();
				dialog.show();

				Button cancelButton = (Button) editorView.findViewById(R.id.diyijiemian_goal_editor_cancel_button);
				Button okButton = (Button) editorView.findViewById(R.id.diyijiemian_goal_editor_ok_button);
				final EditText editText = (EditText) editorView.findViewById(R.id.diyijiemian_goal_editor_edittext);

				cancelButton.setOnClickListener(new OnClickListener()
				{

					@Override
					public void onClick(View v)
					{
						dialog.dismiss();
					}
				});

				okButton.setOnClickListener(new OnClickListener()
				{
					private String goal;
					private InputStream inputStream = null;
					private int responseCode = 0;
					/**
					 * -1: Internet connection error
					 *  0: cannot submit
					 *  1: submit success
					 */
					private final Handler okButtonHandler = new Handler()
					{

						@Override
						public void handleMessage(Message msg)
						{
							super.handleMessage(msg);
							if(msg.what == AppConstant.INTERACTION_STATUS.INTERACTION_SUCCESSFUL)
							{
								progressBar.setMax(Integer.parseInt(goal));
								progressBar.invalidate();
								Toast.makeText(mContext, "设置成功", Toast.LENGTH_LONG).show();
							}
							else if(msg.what == AppConstant.INTERACTION_STATUS.SERVER_STATUS_EXCEPTION)
							{
								Toast.makeText(mContext, AppConstant.INTERACTION_STATUS.TOAST_SERVER_STATUS_EXCEPTION, Toast.LENGTH_LONG).show();
							}
							else if(msg.what == AppConstant.INTERACTION_STATUS.NETWORK_CONNECTION_EXCEPTION)
							{
								Toast.makeText(mContext, AppConstant.INTERACTION_STATUS.TOAST_NETWORK_CONNECTION_EXCEPTION, Toast.LENGTH_LONG).show();
							}
						}
						
					};

					@Override
					public void onClick(View v)
					{
						goal = editText.getText().toString().trim();
						if (goal.equals("") == false && goal.equals("0") == false)
						{
							new Thread(new Runnable()
							{

								@Override
								public void run()
								{
									HashMap<String, String> headers = new HashMap<String, String>();
									HashMap<String, String> params = new HashMap<String, String>();
									headers.put("Cookie", "PHPSESSID=" + StaticInfos.phpsessid);
									params.put("goal", goal);
									params.put("time", System.currentTimeMillis() + "");

									try
									{
										HttpURLConnection urlConnection = (HttpURLConnection) HttpRequestUtil
												.sendGetRequest(AppConstant.URL.AIMSET_PHP_URL, params, headers);
										responseCode = urlConnection.getResponseCode();
										inputStream = urlConnection.getInputStream();
										String result = Toolkits.convertStreamToString(inputStream).trim();
										
										if(responseCode == 200)
										{
											if(result.equals(AppConstant.INTERACTION_STATUS.SERVER_STATUS_EXCEPTION + ""))
											{
												okButtonHandler.sendEmptyMessage(AppConstant.INTERACTION_STATUS.SERVER_STATUS_EXCEPTION);
											}
											else if (result.equals(AppConstant.INTERACTION_STATUS.INTERACTION_SUCCESSFUL + ""))
											{
												okButtonHandler.sendEmptyMessage(AppConstant.INTERACTION_STATUS.INTERACTION_SUCCESSFUL);
											}
										}
										else
										{
											okButtonHandler.sendEmptyMessage(AppConstant.INTERACTION_STATUS.NETWORK_CONNECTION_EXCEPTION);
										}
										
									} catch (Exception e)
									{
										e.printStackTrace();
									}
								}
							}).start();
						}
						dialog.dismiss();
					}
				});
			}
		});

		menuImageView.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (!slidingMenu.isMenuShowing())
				{
					slidingMenu.showMenu();
				}
				else
				{
					slidingMenu.showContent();
				}
			}
		});

		headsetImageView.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(AppConstant.Actions.HEADSET_GET_INIT_STATUS_FROM_SERVICE);
				intent.putExtra("from", "Diyijiemian");
				sendBroadcast(intent);
			}
		});

		slidingMenu = new SlidingMenu(this);
		slidingMenu.setMode(SlidingMenu.LEFT);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		slidingMenu.setBehindOffset(80);
		slidingMenu.setFadeDegree(0.35f);
		slidingMenu.setShadowDrawable(R.drawable.slidemenu_shadow_left);
		slidingMenu.setShadowWidth(15);
		slidingMenu.setFadeEnabled(true);
		slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		slidingMenu.setMenu(R.layout.cehua_menu);
		slideMenuView = slidingMenu.getMenu();

		jingtingLinearLayout = (LinearLayout) findViewById(R.id.diyijiemian_jingting_linearLayout);
		fantingLinearLayout = (LinearLayout) findViewById(R.id.diyijiemian_fanting_linearLayout);
		menuSwitchButton = (Button) slideMenuView.findViewById(R.id.slideMenu_switch_button);
		jukuLayout = (RelativeLayout) slideMenuView.findViewById(R.id.cehua_shoucangdejuzi_relativeLayout);
		dancibenLayout = (RelativeLayout) slideMenuView.findViewById(R.id.cehua_danciben_relativeLayout);
		tinglikuLayout = (RelativeLayout) slideMenuView.findViewById(R.id.cehua_wodetingliku_relativeLayout);
		logoutLayout = (RelativeLayout) slideMenuView.findViewById(R.id.cehua_dengchu_relativeLayout);
		helpLayout = (RelativeLayout) slideMenuView.findViewById(R.id.cehua_bangzhu_relativeLayout);

		jianguoshuLayout = (RelativeLayout) slideMenuView.findViewById(R.id.cehua_jianguoshu_relativeLayout);
		userPortraitImageView = (ImageView) slideMenuView.findViewById(R.id.cehua_touxiang_imageView);
		userNicknameTextView = (TextView) slideMenuView.findViewById(R.id.cehua_user_name_textview);
		userMottoTextView = (TextView) slideMenuView.findViewById(R.id.cehua_user_motto_textview);
		cehuaMenuFeedbackLL=(RelativeLayout) slideMenuView.findViewById(R.id.cehua_fankui_relativeLayout);
		
		helpLayout.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(mContext, Help.class);
				startActivity(intent); 
			}
		});
		
		logoutLayout.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{	
				
				//登出
				new Thread(){
					public void run() {
						try {
							netService.logout();
							
							handler.post(new Runnable() {
								public void run() {
									StaticInfos.isLogin=false;
									stopService(new Intent(mContext, NotifyService.class));
									Intent resetServiceIntent = new Intent(AppConstant.Actions.PLAYER_ASK_SERVER_TO_RELEASE);
									sendBroadcast(resetServiceIntent);
									startActivity(new Intent(mContext,LoginActivity.class));
									finish();
								}
								
							});
						} catch (Exception e) {
						}
					}
				}.start();
			}
		});
		
		//根据是否打开学习提醒设置状态
		if(isSwitchOn)
			//开启了
			menuSwitchButton.setBackgroundResource(R.drawable.slidemenu_switch_on);
		else
			//没有开启
			menuSwitchButton.setBackgroundResource(R.drawable.slidemenu_switch_off);
		
		cehuaMenuFeedbackLL.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent=new Intent(mContext, FeedBackActivity.class);
				startActivity(intent);
			}
		});
		
		
		userPortraitImageView.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				Intent intent = new Intent(mContext, PersonalInfoActivity.class);
				startActivity(intent);
			}
		});

		userNicknameTextView.setText(StaticInfos.nickname);
		userMottoTextView.setText(StaticInfos.motto);

		// 拿到url
		new Thread()
		{
			public void run()
			{
				String porStr = com.lz.utils.AppConstant.PORTRAIT_URL + StaticInfos.portrait;
				try
				{
					portraitBitmap = imageService.getImage(porStr);
					StaticInfos.portraitBm=portraitBitmap;
				} catch (Exception e)
				{
					e.printStackTrace();
				}
				handler.sendEmptyMessage(1);
			}

		}.start();

		jianguoshuLayout.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				Intent intent = new Intent(mContext, TreeActivity.class);
				startActivity(intent);
			}
		});

		tinglikuLayout.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(mContext, MyMp3Store.class);
				startActivity(intent);
			}
		});

		dancibenLayout.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(mContext, Danciben.class);
				startActivity(intent);
			}
		});

		jukuLayout.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(mContext, Juku.class);
				startActivity(intent);
			}
		});

		fantingLinearLayout.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				State dataState = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
				State wifiState = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
				if (dataState == State.CONNECTED || wifiState == State.CONNECTED)
				{

					Intent intent = new Intent(mContext, Fanting.class);
					startActivity(intent);
				}
				else
				{
					Toast.makeText(mContext, "网络似乎开小差了 >_<", Toast.LENGTH_LONG).show();
				}
			}
		});

		jingtingLinearLayout.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(mContext, JingTingActivity.class);
				startActivity(intent);

			}
		});

		menuSwitchButton.setOnClickListener(new OnClickListener()
		{

			public void onClick(View v)
			{
				if (!isSwitchOn)
				{
					//开启
					menuSwitchButton.setBackgroundResource(R.drawable.slidemenu_switch_on);
					isSwitchOn = true;
					//将状态写回到本地文件
					FileWriter fw;
					try {
						File notifyToggleFile=new File(Environment.getExternalStorageDirectory()+"/nut/notifytoggle.nut");
						fw = new FileWriter(notifyToggleFile);
						fw.write("true");
						fw.flush();
					} catch (IOException e1) {
						e1.printStackTrace();
						System.out.println("打开");
					}
					//将状态保存到服务器
					new Thread(){
						public void run(){
							try {
								netService.getNotifyToggle(true);
								StaticInfos.notifyToggle="true";
							} catch (Exception e) {
								e.printStackTrace();
							
							}
						}
					}.start();
				}
				else
				{	//关闭
					menuSwitchButton.setBackgroundResource(R.drawable.slidemenu_switch_off);
					isSwitchOn = false;
					//将状态协会到本地文件
					FileWriter fw;
					try {
						File notifyToggleFile=new File(Environment.getExternalStorageDirectory()+"/nut/notifytoggle.nut");
						fw = new FileWriter(notifyToggleFile);
						fw.write("false");
						fw.flush();
					} catch (IOException e1) {
						System.out.println("取消");
						e1.printStackTrace();
					}
					//将状态保存到服务器
					new Thread(){
						public void run(){
							try {
								netService.getNotifyToggle(false);
								StaticInfos.notifyToggle="false";
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}.start();
				}
			}
		});
	}

	private void fetchDataOfProgressBar()
	{
		final Handler progressBarHandler = new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				super.handleMessage(msg);
				progressBar.setMax(msg.arg1);
				progressBar.setProgress(msg.arg2);
			}
		};

		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				HashMap<String, String> headers = new HashMap<String, String>();
				InputStream inputStream = null;
				int responseCode = 0;
				headers.put("Cookie", "PHPSESSID=" + StaticInfos.phpsessid);

				try
				{
					HttpURLConnection urlConnection = (HttpURLConnection) HttpRequestUtil.sendGetRequest(
							AppConstant.URL.START_PHP_URL, new HashMap<String, String>(), headers);
					responseCode = urlConnection.getResponseCode();
					inputStream = urlConnection.getInputStream();

					if (responseCode == 200)
					{
						String result = Toolkits.convertStreamToString(inputStream);

						System.out.println("第一界面的result:" + result);
						
						String pair[] = result.split("#");
						pair[1] = pair[1].trim();
						Message msg = new Message();
						msg.arg1 = Integer.parseInt(pair[0]);
						msg.arg2 = Integer.parseInt(pair[1]);
						progressBarHandler.sendMessage(msg);
					}
				} catch (Exception e)
				{
					e.printStackTrace();
				}

			}
		}).start();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.diyijiemian);
		
		mContext = this;

		offlineSaver = new OfflineSaver();
		imageService = new ImageService(this);
		initWidgets();

		fetchDataOfProgressBar();

	}

	protected void onRestart()
	{
		super.onRestart();
		userMottoTextView.setText(StaticInfos.motto);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		Intent updateHeadsetIntent = new Intent(AppConstant.Actions.HEADSET_GET_INIT_STATUS_FROM_SERVICE);
		updateHeadsetIntent.putExtra("from", "Diyijiemian");
		updateHeadsetIntent.putExtra("isAuto", true);
		sendBroadcast(updateHeadsetIntent);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		Intent intent = new Intent();
		intent.setAction("DIYIJIEMIAN_TO_PLAYER_RELEASE_MEDIAPLAYER");
		sendBroadcast(intent);

		unregisterReceiver(receiver);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			if (clickCount == 0)
			{
				backKeyHandler.sendEmptyMessageDelayed(0, 2000);
				Toast.makeText(mContext, "双击返回键退出", Toast.LENGTH_SHORT).show();
			}
			clickCount++;
			if (clickCount >= 2)
				finish();

		}
		return false;
	}

}
