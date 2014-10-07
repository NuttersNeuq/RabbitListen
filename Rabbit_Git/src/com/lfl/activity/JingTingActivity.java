package com.lfl.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lfl.model.Mp3Info;
import com.lfl.utils.AppConstant;
import com.lfl.utils.MillisecondConvert;
import com.lfl.utils.PullParseXML;
import com.lfl.utils.SlideListView;
import com.lfl.utils.Toolkits;
import com.lz.utils.HttpRequestUtil;
import com.lz.utils.StaticInfos;
import com.hare.activity.R;

@SuppressLint("HandlerLeak")
public class JingTingActivity extends Activity
{
	private TextView reviewAmountTextView;
//	private TextView objectiveTextView;
	private SlideListView listView;
	private ImageView headsetImageView;
	private Context mContext;
	private Dialog loadingDialog;

	private List<Mp3Info> mp3Infos = new ArrayList<Mp3Info>();
//	private void debugDialog()
//	{
//		AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		View finishDialogView = getLayoutInflater()
//				.inflate(R.layout.jingting_finish_alert_dialog, null);
//		builder.setView(finishDialogView);
//		Button okButton = (Button) finishDialogView.findViewById(R.id.jingting_finish_ok_button);
//		
//		final AlertDialog finishDialog = builder.show();
//		
//		okButton.setOnClickListener(new OnClickListener()
//		{
//			
//			@Override
//			public void onClick(View v)
//			{
//				finishDialog.dismiss();
//			}
//		});
//	}

	private BroadcastReceiver receiver = new BroadcastReceiver()
	{

		@Override
		public void onReceive(Context context, Intent intent)
		{
			String action = intent.getAction();
			String resource = intent.getStringExtra("from");
			boolean isAuto = intent.getBooleanExtra("isAuto", false);
			if (resource.equals("JingTingActivity"))
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
							Toast.makeText(mContext, "尚未加载任何听力到播放器", Toast.LENGTH_SHORT).show();
					}
				}
			}
		}
	};

	private void initWidegts()
	{
		IntentFilter filter = new IntentFilter();
		filter.addAction(AppConstant.Actions.SERVICE_GIVE_INIT_STATUS_TO_HEADSET);
		registerReceiver(receiver, filter);

		reviewAmountTextView = (TextView) findViewById(R.id.jingting_reviewAmount_textView);
//		objectiveTextView = (TextView) findViewById(R.id.jingting_objective_textview);
		headsetImageView = (ImageView) findViewById(R.id.jingting_headset_imageview);
		
		ImageView touxiangImageView = (ImageView) findViewById(R.id.jingting_touxiang_imageview);
		
		touxiangImageView.setImageBitmap(StaticInfos.portraitBm);
//		objectiveTextView.setText(StaticInfos.nickname + "，快开启今日精听之旅吧~");

//		objectiveTextView.setOnClickListener(new OnClickListener()
//		{
//
//			@Override
//			public void onClick(View v)
//			{
//				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//				LayoutInflater inflater = LayoutInflater.from(mContext);
//				View editorView = inflater.inflate(R.layout.custom_alert_dialog_goal_editor, null);
//				builder.setView(editorView);
//				builder.create();
//				final AlertDialog dialog = builder.show();
//				dialog.show();
//
//				Button cancelButton = (Button) editorView.findViewById(R.id.custom_alert_dialog_cancel_button);
//				Button okButton = (Button) editorView.findViewById(R.id.custom_alert_dialog_ok_button);
//				final EditText editText = (EditText) editorView
//						.findViewById(R.id.custom_alert_dialog_goal_editor_edittext);
//
//				cancelButton.setOnClickListener(new OnClickListener()
//				{
//
//					@Override
//					public void onClick(View v)
//					{
//						dialog.dismiss();
//					}
//				});
//
//				okButton.setOnClickListener(new OnClickListener()
//				{
//
//					@Override
//					public void onClick(View v)
//					{
//						String goal = editText.getText().toString().trim();
//						objectiveTextView.setText(goal);
//						dialog.dismiss();
//					}
//				});
//			}
//		});

		headsetImageView.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(AppConstant.Actions.HEADSET_GET_INIT_STATUS_FROM_SERVICE);
				intent.putExtra("from", "JingTingActivity");
				sendBroadcast(intent);
			}
		});

	}

	private void setListView()
	{
		final Handler setListViewHandler = new Handler()
		{

			@Override
			public void handleMessage(Message msg)
			{
				super.handleMessage(msg);

				loadingDialog.dismiss();

				switch (msg.what)
				{
				case AppConstant.INTERACTION_STATUS.INTERACTION_SUCCESSFUL:
					listView = (SlideListView) findViewById(R.id.jingting_slideListView);
					listView.setOnItemClickListener(new OnItemClickListener()
					{

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
						{
							if (position == mp3Infos.size())
							{
								Intent intent = new Intent(mContext, Jinrituijian.class);
								startActivity(intent);
							}
							else
							{
								Intent startPlayerIntent = new Intent(mContext, NewLocalPlayer.class);
								startPlayerIntent.putExtra("mp3Info", mp3Infos.get(position));
								startActivity(startPlayerIntent);

								Intent intent = new Intent(AppConstant.Actions.SEND_MP3INFO_TO_SERVICE);
								intent.putExtra("mp3Info", mp3Infos.get(position));
								sendBroadcast(intent);
							}
						}

					});
					listView.setAdapter(new SlideAdapter());
					reviewAmountTextView.setText("" + mp3Infos.size());
					break;

				case AppConstant.INTERACTION_STATUS.NETWORK_CONNECTION_EXCEPTION:
					Toast.makeText(mContext, AppConstant.INTERACTION_STATUS.TOAST_NETWORK_CONNECTION_EXCEPTION,
							Toast.LENGTH_SHORT).show();
					break;
				case AppConstant.INTERACTION_STATUS.SERVER_STATUS_EXCEPTION:
					Toast.makeText(mContext, AppConstant.INTERACTION_STATUS.TOAST_SERVER_STATUS_EXCEPTION,
							Toast.LENGTH_SHORT).show();
				}
			}

		};

		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				HashMap<String, String> params = new HashMap<String, String>();
				mp3Infos = PullParseXML.parseOnlineMp3XML(AppConstant.URL.JINGTING_LIST_URL, params, true);

				if (mp3Infos.size() != 0)
				{

					String result = mp3Infos.get(0).getName();
					if (result.equals(AppConstant.INTERACTION_STATUS.NETWORK_CONNECTION_EXCEPTION + ""))
					{
						setListViewHandler
								.sendEmptyMessage(AppConstant.INTERACTION_STATUS.NETWORK_CONNECTION_EXCEPTION);
					}
					else if (result.equals(AppConstant.INTERACTION_STATUS.SERVER_STATUS_EXCEPTION + ""))
					{
						setListViewHandler
								.sendEmptyMessage(AppConstant.INTERACTION_STATUS.NETWORK_CONNECTION_EXCEPTION);
					}
					else
					{
						setListViewHandler.sendEmptyMessage(AppConstant.INTERACTION_STATUS.INTERACTION_SUCCESSFUL);
					}
				}
				else
				{
					setListViewHandler.sendEmptyMessage(AppConstant.INTERACTION_STATUS.INTERACTION_SUCCESSFUL);
				}

			}
		}).start();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jingting);

		loadingDialog = new Dialog(this, R.style.loading_dialog_style);
		loadingDialog.setContentView(R.layout.loading_dialog);
		Window loadingDialogWindow = loadingDialog.getWindow();
		WindowManager.LayoutParams lParams = loadingDialogWindow.getAttributes();
		loadingDialogWindow.setGravity(Gravity.CENTER);
		lParams.alpha = 1f;
		loadingDialogWindow.setAttributes(lParams);
		loadingDialog.show();

		mContext = this;

		initWidegts();
		
//		debugDialog();

	}

	@Override
	protected void onResume()
	{
		super.onResume();
		
		setListView();
		
		Intent updateHeadsetIntent = new Intent(AppConstant.Actions.HEADSET_GET_INIT_STATUS_FROM_SERVICE);
		updateHeadsetIntent.putExtra("from", "JingTingActivity");
		updateHeadsetIntent.putExtra("isAuto", true);
		sendBroadcast(updateHeadsetIntent);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	private class SlideAdapter extends BaseAdapter
	{

		@Override
		public int getCount()
		{
			return mp3Infos.size() + 1;
		}

		@Override
		public Object getItem(int position)
		{
			return mp3Infos.get(position);
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			final int finalPos = position;

			ViewHolder holder = new ViewHolder();
			convertView = getLayoutInflater().inflate(R.layout.jingting_listitem, null);
			holder.duihaoLayout = (LinearLayout) convertView.findViewById(R.id.jingting_listItem_duihao_linearlayout);
			holder.shoucangLayout = (LinearLayout) convertView.findViewById(R.id.jingting_listItem_shoucang_linearlayout);
			holder.shanchuLayout = (LinearLayout) convertView.findViewById(R.id.jingting_listItem_shanchu_linearlayout);
			holder.title = (TextView) convertView.findViewById(R.id.jingting_listItem_title_textView);
			holder.lrcLang = (TextView) convertView.findViewById(R.id.jingting_listItem_lrcLanguage_textView);
			holder.size = (TextView) convertView.findViewById(R.id.jingting_listItem_size_textView);
			holder.difficulty = (TextView) convertView.findViewById(R.id.jingting_listItem_difficulty_textView);
			holder.duration = (TextView) convertView.findViewById(R.id.jingting_listItem_duration_textView);
			holder.progress = (ImageView) convertView.findViewById(R.id.jingting_listItem_jindu_imageView);
			if (position == mp3Infos.size())
			{
				LinearLayout menuLayout = (LinearLayout) convertView.findViewById(R.id.llayout_right);
				menuLayout.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
				holder.progress.setImageResource(R.drawable.ic_launcher);
				holder.title.setText("精听推荐：一筐新鲜的坚果");
				holder.title.setTextColor(Color.parseColor("#42c0fb"));
				holder.lrcLang.setText("From 你的喜欢和你的朋友");
				holder.difficulty.setText("");
				holder.size.setText("");
				holder.duration.setText("");
				holder.duration.setBackground(null);

			}
			else
			{
				holder.title.setText(mp3Infos.get(position).getName());
				holder.duration.setText(MillisecondConvert.convert(Integer.parseInt(mp3Infos.get(position)
						.getDuration())));
				holder.lrcLang.setText("双语同步课文");
				holder.size.setText(mp3Infos.get(position).getSize());
				holder.difficulty.setText(mp3Infos.get(position).getDifficulty());

				String round = mp3Infos.get(finalPos).getRound();
				if (round.equals("11") || round.equals("12") || round.equals("13"))
				{
					holder.progress.setImageResource(R.drawable.jingting_jindu_0);
				}
				else if (round.equals("2"))
				{
					holder.progress.setImageResource(R.drawable.jingting_jindu_1);
				}
				else if (round.equals("3"))
				{
					holder.progress.setImageResource(R.drawable.jingting_jindu_2);
				}
				else if (round.equals("4"))
				{
					holder.progress.setImageResource(R.drawable.jingting_jindu_3);
				}
				else if (round.equals("5"))
				{
					holder.progress.setImageResource(R.drawable.jingting_jindu_4);
				}

				holder.duihaoLayout.setOnClickListener(new OnClickListener()
				{
					private InputStream inputStream = null;
					private int responseCode = 0;
					private int result;
					private Handler passHandler = new Handler()
					{

						@Override
						public void handleMessage(Message msg)
						{
							super.handleMessage(msg);
							if (msg.what == 1)
							{
								mp3Infos.remove(finalPos);
								listView.setAdapter(new SlideAdapter());
								Toast.makeText(mContext, "直接完成成功", Toast.LENGTH_SHORT).show();
							}
							else if (msg.what == 0) 
							{
								Toast.makeText(mContext, "操作失败，服务器开小差了", Toast.LENGTH_SHORT).show();
							}
						}

					};
					@Override
					public void onClick(View v)
					{
						new Thread(new Runnable()
						{
							public void run()
							{
								HashMap<String, String> headers = new HashMap<String, String>();
								HashMap<String, String> params = new HashMap<String, String>();
								headers.put("Cookie", "PHPSESSID=" + StaticInfos.phpsessid);
								params.put("lid", mp3Infos.get(finalPos).getId());
								
								try
								{
									HttpURLConnection urlConnection = (HttpURLConnection) HttpRequestUtil.sendGetRequest(
											AppConstant.URL.JINGTING_PASS_URL, params, headers);
									responseCode = urlConnection.getResponseCode();
									inputStream = urlConnection.getInputStream();
									result = Integer.parseInt(Toolkits.convertStreamToString(inputStream));
									if (responseCode != 200 || result == 0)
									{
										passHandler.sendEmptyMessage(0);
									}
									else
									{
										passHandler.sendEmptyMessage(1);
									}
								} catch (Exception e)
								{
									passHandler.sendEmptyMessage(0);
									e.printStackTrace();
								}
							}
						}).start();
					}
				});
				holder.shoucangLayout.setOnClickListener(new OnClickListener()
				{

					@Override
					public void onClick(View v)
					{
						new Thread(new Runnable()
						{
							private int responseCode;
							private InputStream inputStream;
							private int result;
							private Handler shoucangHandler = new Handler()
							{

								@Override
								public void handleMessage(Message msg)
								{
									super.handleMessage(msg);
									if (msg.what == 0)
									{
										Toast.makeText(mContext, "收藏失败，服务器好像开小差了", Toast.LENGTH_SHORT).show();
									}
									else if (msg.what == 1)
									{
										Toast.makeText(mContext, "收藏成功", Toast.LENGTH_SHORT).show();
									}
								}
							};

							@Override
							public void run()
							{

								HashMap<String, String> headers = new HashMap<String, String>();
								HashMap<String, String> params = new HashMap<String, String>();
								headers.put("Cookie", "PHPSESSID=" + StaticInfos.phpsessid);
								params.put("lid", mp3Infos.get(finalPos).getId());
								params.put("ifss", "1");

								try
								{
									HttpURLConnection urlConnection = (HttpURLConnection) HttpRequestUtil
											.sendGetRequest(AppConstant.URL.SHOUCANG_MP3_URL, params, headers);
									responseCode = urlConnection.getResponseCode();
									inputStream = urlConnection.getInputStream();
									result = Integer.parseInt(Toolkits.convertStreamToString(inputStream));
									if (responseCode != 200 || result == 0)
									{
										shoucangHandler.sendEmptyMessage(0);
									}
									else
									{
										shoucangHandler.sendEmptyMessage(1);
									}
								} catch (Exception e)
								{
									e.printStackTrace();
									shoucangHandler.sendEmptyMessage(0);
								}
							}
						}).start();
					}
				});
				holder.shanchuLayout.setOnClickListener(new OnClickListener()
				{

					@Override
					public void onClick(View v)
					{
						final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
						LayoutInflater inflater = LayoutInflater.from(mContext);
						View alertView = inflater.inflate(R.layout.custom_alert_dialog, null);
						builder.setView(alertView);
						final AlertDialog dialog = builder.create();
						dialog.show();
						Button cancelButton = (Button) alertView.findViewById(R.id.custom_alert_dialog_cancel_button);
						cancelButton.setOnClickListener(new OnClickListener()
						{

							@Override
							public void onClick(View v)
							{
								dialog.dismiss();
							}
						});

						Button okButton = (Button) alertView.findViewById(R.id.custom_alert_dialog_ok_button);
						okButton.setOnClickListener(new OnClickListener()
						{
							private int responseCode;
							private InputStream inputStream;
							private int result;
							private Handler okHandler = new Handler()
							{

								@Override
								public void handleMessage(Message msg)
								{
									super.handleMessage(msg);
									if (msg.what == 1)
									{
										mp3Infos.remove(finalPos);
										listView.setAdapter(new SlideAdapter());
										Toast.makeText(mContext, "成功删除 ", Toast.LENGTH_SHORT).show();
									}
									else if (msg.what == 0)
									{
										Toast.makeText(mContext, "删除失败，服务器开小差叻", Toast.LENGTH_SHORT).show();
									}
								}
							};

							@Override
							public void onClick(View v)
							{
								dialog.dismiss();
								new Thread(new Runnable()
								{
									
									@Override
									public void run()
									{
										HashMap<String, String> headers = new HashMap<String, String>();
										HashMap<String, String> params = new HashMap<String, String>();
										headers.put("Cookie", "PHPSESSID=" + StaticInfos.phpsessid);
										params.put("lid", mp3Infos.get(finalPos).getId());
										
										try
										{
											HttpURLConnection urlConnection = (HttpURLConnection) HttpRequestUtil.sendGetRequest(
													AppConstant.URL.DEL_JINGTING_MP3_URL, params, headers);
											responseCode = urlConnection.getResponseCode();
											inputStream = urlConnection.getInputStream();
											result = Integer.parseInt(Toolkits.convertStreamToString(inputStream));
											if (responseCode != 200 || result == 0)
											{
												okHandler.sendEmptyMessage(0);
											}
											else
											{
												okHandler.sendEmptyMessage(1);
											}
										} catch (Exception e)
										{
											okHandler.sendEmptyMessage(0);
											e.printStackTrace();
										}
									}
								}).start();
							}
						});
					}
				});
			}
			return convertView;
		}

	}

	private class ViewHolder
	{
		public LinearLayout duihaoLayout;
		public LinearLayout shoucangLayout;
		public LinearLayout shanchuLayout;
		public TextView title;
		public TextView lrcLang;
		public TextView size;
		public TextView difficulty;
		public TextView duration;
		public ImageView progress;
	}

}
