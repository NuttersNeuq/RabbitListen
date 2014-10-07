package com.lfl.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.PopupWindow.OnDismissListener;

import com.lfl.model.Mp3Info;
import com.lfl.utils.AppConstant;
import com.lfl.utils.HttpDownloader;
import com.lfl.utils.MillisecondConvert;
import com.lfl.utils.PullParseXML;
import com.lfl.utils.Toolkits;
import com.lz.utils.HttpRequestUtil;
import com.lz.utils.StaticInfos;
import com.lz.utils.Util;
import com.nut.activity.R;

@SuppressLint("HandlerLeak")
public class Tingliku_Shoucang extends ListActivity
{
	private List<Mp3Info> mp3Infos;
	private Context mContext;
	private Dialog loadingDialog;
	private PopupWindow backgroundWindow;
	private PopupWindow menuWindow;

	private void fetchMp3List()
	{
		new Thread(new Runnable()
		{
			private Handler fetchHandler = new Handler()
			{

				@Override
				public void handleMessage(Message msg)
				{
					super.handleMessage(msg);
					loadingDialog.dismiss();
					if (msg.what == AppConstant.INTERACTION_STATUS.INTERACTION_SUCCESSFUL)
					{
						setListAdapter(new MyListAdapter());
					}
					else if (msg.what == AppConstant.INTERACTION_STATUS.NETWORK_CONNECTION_EXCEPTION)
					{
						Toast.makeText(mContext, AppConstant.INTERACTION_STATUS.TOAST_INTERACTION_SUCCESSFUL,
								Toast.LENGTH_SHORT).show();
					}
					else if (msg.what == AppConstant.INTERACTION_STATUS.SERVER_STATUS_EXCEPTION)
					{
						Toast.makeText(mContext, AppConstant.INTERACTION_STATUS.TOAST_SERVER_STATUS_EXCEPTION,
								Toast.LENGTH_SHORT).show();
					}
				}

			};

			@Override
			public void run()
			{
				HashMap<String, String> headers = new HashMap<String, String>();
				HashMap<String, String> params = new HashMap<String, String>();
				headers.put("Cookie", "PHPSESSID=" + StaticInfos.phpsessid);

				mp3Infos = PullParseXML.parseOnlineMp3XML(AppConstant.URL.MY_SHOUCANG_LIST_URL, params, true);
				String result = mp3Infos.get(0).getName();

				if (result.equals(AppConstant.INTERACTION_STATUS.NETWORK_CONNECTION_EXCEPTION + ""))
				{
					fetchHandler.sendEmptyMessage(AppConstant.INTERACTION_STATUS.NETWORK_CONNECTION_EXCEPTION);
				}
				else if (result.equals(AppConstant.INTERACTION_STATUS.SERVER_STATUS_EXCEPTION + ""))
				{
					fetchHandler.sendEmptyMessage(AppConstant.INTERACTION_STATUS.SERVER_STATUS_EXCEPTION);
				}
				else
				{
					/**
					 * 屏蔽了听力图片的下载
					 */
//					for (int i = 0; i < mp3Infos.size(); i++)
//					{
//						Mp3Info currentMp3Info = mp3Infos.get(i);
//						File picFile = new File(AppConstant.FilePath.PIC_FILE_PATH + currentMp3Info.getPic());
//						if (picFile.exists() == false)
//						{
//							HttpDownloader.downloadFile(AppConstant.URL.NCC_NEUQ_PIC_URL + currentMp3Info.getPic(),
//									AppConstant.FilePath.MP3_FILE_PATH, currentMp3Info.getPic());
//						}
//					}
					fetchHandler.sendEmptyMessage(AppConstant.INTERACTION_STATUS.INTERACTION_SUCCESSFUL);
				}
			}
		}).start();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tingliku_shoucang);

		mContext = this;
		
		loadingDialog = new Dialog(this, R.style.loading_dialog_style);
		loadingDialog.setContentView(R.layout.loading_dialog);
		Window loadingDialogWindow = loadingDialog.getWindow();
		WindowManager.LayoutParams lParams = loadingDialogWindow.getAttributes();
		loadingDialogWindow.setGravity(Gravity.CENTER);
		lParams.alpha = 1f;
		loadingDialogWindow.setAttributes(lParams);
		loadingDialog.show();
		
		View menuBackgroundView = getLayoutInflater().inflate(R.layout.popupmenu_black_background, null);
		View menuView = getLayoutInflater().inflate(R.layout.fanting_listitem_menu_collected, null);
		
		backgroundWindow = new PopupWindow(menuBackgroundView, LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		menuWindow = new PopupWindow(menuView, RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		menuWindow.setAnimationStyle(R.style.AnimBottom);
		backgroundWindow.setAnimationStyle(R.style.AnimPopupMenuBackground);
		
		menuWindow.setOnDismissListener(new OnDismissListener()
		{

			@Override
			public void onDismiss()
			{
				backgroundWindow.dismiss();
			}
		});

		fetchMp3List();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);
		Intent intent = new Intent(mContext, OnlinePlayer.class);
		intent.putExtra("mp3Info", mp3Infos.get(position));
		startActivity(intent);
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		menuWindow.dismiss();
	}

	private class MyListAdapter extends BaseAdapter
	{

		@Override
		public int getCount()
		{
			return mp3Infos.size();
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
			convertView = getLayoutInflater().inflate(R.layout.fanting_tuijian_listitem, null);
			final Mp3Info mp3Info = mp3Infos.get(position);
			final int finalPos = position;

			TextView titleTextView = (TextView) convertView.findViewById(R.id.fanting_tuijian_title_textview);
			TextView durationTextView = (TextView) convertView
					.findViewById(R.id.fanting_tuijian_listitem_duration_textview);
			TextView sizeTextView = (TextView) convertView.findViewById(R.id.fanting_tuijian_listitem_size_textview);
			TextView difficultyTextView = (TextView) convertView
					.findViewById(R.id.fanting_tuijian_listitem_difficulty_textview);
//			ImageView imageView = (ImageView) convertView.findViewById(R.id.fanting_tuijian_listitem_imageview);
			ImageView menuImageView = (ImageView) convertView
					.findViewById(R.id.fanting_tuijian_listitem_menu_imageview);

			titleTextView.setText(mp3Info.getName());
			durationTextView.setText(MillisecondConvert.convert(Integer.parseInt(mp3Info.getDuration())));
			sizeTextView.setText(mp3Info.getSize());
			difficultyTextView.setText(mp3Info.getDifficulty());

//			String picPath = AppConstant.FilePath.PIC_FILE_PATH + mp3Info.getPic();
//			BitmapFactory.Options options = new BitmapFactory.Options();
//			options.inSampleSize = 1;
//			Bitmap bm = BitmapFactory.decodeFile(picPath, options);
//			imageView.setBackgroundDrawable(Drawable.createFromPath(picPath)); 

			menuImageView.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					View menuView = menuWindow.getContentView();

					LinearLayout downloadLayout = (LinearLayout) menuView
							.findViewById(R.id.fanting_listitem_menu_download_linearlayout);
					LinearLayout delShouchangLayout = (LinearLayout) menuView
							.findViewById(R.id.fanting_listitem_menu_shoucang_linearlayout);
					LinearLayout fenxiangLayout = (LinearLayout) menuView
							.findViewById(R.id.fanting_listitem_menu_share_linearlayout);
					LinearLayout addLinearLayout = (LinearLayout) menuView
							.findViewById(R.id.fanting_listitem_menu_add_to_jingting_linearlayout);
					ImageView menuBackgroundImageView = (ImageView) menuView.findViewById(R.id.menu_background);
					TextView shoucangTextView = (TextView) menuView.findViewById(R.id.fanting_listitem_menu_shoucang_textview);
					shoucangTextView.setText("取消收藏"); 

					fenxiangLayout.setOnClickListener(new OnClickListener()
					{

						@Override
						public void onClick(View v)
						{
							menuWindow.dismiss();
							Util.showShare(Tingliku_Shoucang.this,"今天我在坚果听力上听了这边文章，顿时感觉神清气爽！ ——"+mp3Info.getCourse());
						}
					});
					delShouchangLayout.setOnClickListener(new OnClickListener()
					{

						@Override
						public void onClick(View v)
						{
							menuWindow.dismiss();
							new Thread(new Runnable()
							{
								private InputStream inputStream = null;
								private int responseCode = 0;
								private int result;
								private Handler delMp3Handler = new Handler()
								{

									@Override
									public void handleMessage(Message msg)
									{
										super.handleMessage(msg);
										if (msg.what == 1)
										{
											mp3Infos.remove(finalPos);
											setListAdapter(new MyListAdapter());
											Toast.makeText(mContext, "取消成功", Toast.LENGTH_SHORT).show();
										}
										else
										{
											Toast.makeText(mContext, "取消失败，服务器开小差叻", Toast.LENGTH_SHORT).show();
										}
									}

								};

								@Override
								public void run()
								{
									HashMap<String, String> headers = new HashMap<String, String>();
									HashMap<String, String> params = new HashMap<String, String>();
									headers.put("Cookie", "PHPSESSID=" + StaticInfos.phpsessid);
									params.put("lid", mp3Info.getId());
									params.put("ifss", "0");
									try
									{
										HttpURLConnection urlConnection = (HttpURLConnection) HttpRequestUtil
												.sendGetRequest(AppConstant.URL.SHOUCANG_MP3_URL, params, headers);
										responseCode = urlConnection.getResponseCode();
										inputStream = urlConnection.getInputStream();
										result = Integer.parseInt(Toolkits.convertStreamToString(inputStream));

										if (responseCode != 200 || result == 0)
										{
											delMp3Handler.sendEmptyMessage(0);
										}
										else
										{
											delMp3Handler.sendEmptyMessage(1);
										}
									} catch (Exception e)
									{
										delMp3Handler.sendEmptyMessage(0);
										e.printStackTrace();
									}
								}
							}).start();
						}
					});
					addLinearLayout.setOnClickListener(new OnClickListener()
					{

						private int responseCode;
						private InputStream inputStream;
						private Handler addJingtingHandler = new Handler()
						{

							@Override
							public void handleMessage(Message msg)
							{
								super.handleMessage(msg);
								switch (msg.what)
								{
								case AppConstant.INTERACTION_STATUS.INTERACTION_SUCCESSFUL:
									Toast.makeText(mContext,
											AppConstant.INTERACTION_STATUS.TOAST_INTERACTION_SUCCESSFUL,
											Toast.LENGTH_SHORT).show();
									break;

								case AppConstant.INTERACTION_STATUS.NETWORK_CONNECTION_EXCEPTION:
									Toast.makeText(mContext,
											AppConstant.INTERACTION_STATUS.TOAST_NETWORK_CONNECTION_EXCEPTION,
											Toast.LENGTH_SHORT).show();
									break;
								case AppConstant.INTERACTION_STATUS.SERVER_STATUS_EXCEPTION:
									Toast.makeText(mContext,
											AppConstant.INTERACTION_STATUS.TOAST_SERVER_STATUS_EXCEPTION,
											Toast.LENGTH_SHORT).show();
								}
							}

						};
						private Handler downloadHandler = new Handler()
						{

							@Override
							public void handleMessage(Message msg)
							{
								super.handleMessage(msg);
								if (msg.what == -1)
								{
									Toast.makeText(mContext, "下载过程出问题了，请重新添加到精听", Toast.LENGTH_LONG).show();
								}
								else if (msg.what == 0)
								{
									Diyijiemian.offlineSaver.addMp3Info(mp3Info);
								}
							}

						};

						@Override
						public void onClick(View v)
						{
							menuWindow.dismiss();

							new Thread(new Runnable()
							{

								@Override
								public void run()
								{
									HashMap<String, String> headers = new HashMap<String, String>();
									HashMap<String, String> params = new HashMap<String, String>();
									headers.put("Cookie", "PHPSESSID=" + StaticInfos.phpsessid);
									params.put("lid", mp3Info.getId());

									try
									{
										HttpURLConnection urlConnection = (HttpURLConnection) HttpRequestUtil
												.sendGetRequest(AppConstant.URL.ADD_TO_JINGTING_URL, params, headers);
										responseCode = urlConnection.getResponseCode();
										inputStream = urlConnection.getInputStream();
										String result = Toolkits.convertStreamToString(inputStream).trim();

										if (responseCode != 200)
										{
											addJingtingHandler
													.sendEmptyMessage(AppConstant.INTERACTION_STATUS.NETWORK_CONNECTION_EXCEPTION);
										}
										else
										{
											if (result.equals("0"))
											{
												addJingtingHandler
														.sendEmptyMessage(AppConstant.INTERACTION_STATUS.SERVER_STATUS_EXCEPTION);
											}
											else
											{
												addJingtingHandler
														.sendEmptyMessage(AppConstant.INTERACTION_STATUS.INTERACTION_SUCCESSFUL);

												int mp3Ret = HttpDownloader.downloadFile(
														AppConstant.URL.NCC_NEUQ_MP3_URL + mp3Info.getId() + ".mp3",
														AppConstant.FilePath.MP3_FILE_PATH, mp3Info.getId() + ".mp3");
												int lrcRet = HttpDownloader.downloadFile(
														AppConstant.URL.NCC_NEUQ_LRC_URL + mp3Info.getId() + ".lrc",
														AppConstant.FilePath.LRC_FILE_PATH, mp3Info.getId() + ".lrc");
												int picRet = HttpDownloader.downloadFile(
														AppConstant.URL.NCC_NEUQ_PIC_URL + mp3Info.getPic(),
														AppConstant.FilePath.PIC_FILE_PATH, mp3Info.getPic());
												if (mp3Ret != -1 && lrcRet != -1 && picRet != -1)
												{
													downloadHandler.sendEmptyMessage(0);
												}
												else
												{
													downloadHandler.sendEmptyMessage(-1);
												}
											}
										}
									} catch (Exception e)
									{
										e.printStackTrace();
										addJingtingHandler
												.sendEmptyMessage(AppConstant.INTERACTION_STATUS.SERVER_STATUS_EXCEPTION);
									}
								}
							}).start();
						}
					});

					menuBackgroundImageView.setOnClickListener(new OnClickListener()
					{

						@Override
						public void onClick(View v)
						{
							menuWindow.dismiss();
						}
					});

					downloadLayout.setOnClickListener(new OnClickListener()
					{
						
						private Handler toastHandler = new Handler()
						{

							@Override
							public void handleMessage(Message msg)
							{
								super.handleMessage(msg);
								String result = null;
								switch (msg.what)
								{
								case 0:
								{
									result = mp3Info.getName() + " 下载成功啦";
									Diyijiemian.offlineSaver.addMp3Info(mp3Info);
								}
									break;
								case -1:
								{
									result = mp3Info.getName() + " 下载失败叻";
								}
									break;
								}
								Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
							}

						};

						@Override
						public void onClick(View v)
						{
							menuWindow.dismiss();
							if (Diyijiemian.offlineSaver.isMp3InfoLoaded(mp3Info) == false)
							{
								Toast.makeText(mContext, "听力开始下载啦", Toast.LENGTH_SHORT).show();
								new Thread(new Runnable()
								{

									@Override
									public void run()
									{
										int mp3Ret = HttpDownloader.downloadFile(AppConstant.URL.NCC_NEUQ_MP3_URL
												+ mp3Info.getId() + ".mp3", AppConstant.FilePath.MP3_FILE_PATH,
												mp3Info.getId() + ".mp3");
										int lrcRet = HttpDownloader.downloadFile(AppConstant.URL.NCC_NEUQ_LRC_URL
												+ mp3Info.getId() + ".lrc", AppConstant.FilePath.LRC_FILE_PATH,
												mp3Info.getId() + ".lrc");
										int picRet = HttpDownloader.downloadFile(AppConstant.URL.NCC_NEUQ_PIC_URL
												+ mp3Info.getPic(), AppConstant.FilePath.PIC_FILE_PATH,
												mp3Info.getPic());

										if (mp3Ret != -1 && lrcRet != -1 && picRet != -1)
										{
											toastHandler.sendEmptyMessage(0);
										}
										else
										{
											toastHandler.sendEmptyMessage(-1);
										}
									}
								}).start();
							}
							else
							{
								Toast.makeText(mContext, "文件已经下载过了哈", Toast.LENGTH_SHORT).show();
							}
						}
					});

					backgroundWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
					menuWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);

				}
			});
			return convertView;
		}
	}

}
