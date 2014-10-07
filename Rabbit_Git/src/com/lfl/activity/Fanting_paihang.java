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
import android.graphics.Color;
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

import com.lfl.model.CourseInfo;
import com.lfl.model.Mp3Info;
import com.lfl.utils.AppConstant;
import com.lfl.utils.HttpDownloader;
import com.lfl.utils.MillisecondConvert;
import com.lfl.utils.PullParseXML;
import com.lfl.utils.Toolkits;
import com.lz.utils.HttpRequestUtil;
import com.lz.utils.StaticInfos;
import com.lz.utils.Util;
import com.hare.activity.R;

@SuppressLint("HandlerLeak")
public class Fanting_paihang extends ListActivity
{
	private RelativeLayout courseOrderLayout;
	private RelativeLayout mp3OrderLayout;
	private Dialog loadingDialog;
	private List<CourseInfo> courseInfos;
	private List<Mp3Info> mp3Infos;
	private Context mContext;
	private boolean isOrderedByCourses = true;
	private PopupWindow backgroundWindow;
	private PopupWindow menuWindow;

	private void fetchXMLsFromServer()
	{
		final Handler fetchDataHandler = new Handler()
		{

			@Override
			public void handleMessage(Message msg)
			{
				super.handleMessage(msg);

				if (msg.what == AppConstant.INTERACTION_STATUS.INTERACTION_SUCCESSFUL)
				{
					setListAdapter(new MyListAdapter());
					loadingDialog.dismiss();
				}
				else if (msg.what == AppConstant.INTERACTION_STATUS.SERVER_STATUS_EXCEPTION)
				{
					loadingDialog.dismiss();
					Toast.makeText(mContext, AppConstant.INTERACTION_STATUS.TOAST_SERVER_STATUS_EXCEPTION,
							Toast.LENGTH_LONG).show();
				}
				else if (msg.what == AppConstant.INTERACTION_STATUS.NETWORK_CONNECTION_EXCEPTION)
				{
					loadingDialog.dismiss();
					Toast.makeText(mContext, AppConstant.INTERACTION_STATUS.TOAST_NETWORK_CONNECTION_EXCEPTION,
							Toast.LENGTH_LONG).show();
				}
			}

		};

		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				HashMap<String, String> params_mp3 = new HashMap<String, String>();
				HashMap<String, String> params_course = new HashMap<String, String>();
				params_mp3.put("type", "listen");
				params_mp3.put("limit", "10");
				params_course.put("type", "zhuanji");
				params_course.put("limit", "10");
				courseInfos = PullParseXML.parseOnlineCourseXML(AppConstant.URL.PAIHANG_LIST_URL, params_course, true);
				mp3Infos = PullParseXML.parseOnlineMp3XML(AppConstant.URL.PAIHANG_LIST_URL, params_mp3, true);

				if (courseInfos.size() != 0 && mp3Infos.size() != 0)
				{
					String courseResult = courseInfos.get(0).getName();
					String mp3Result = mp3Infos.get(0).getName();

					if (courseResult.equals(AppConstant.INTERACTION_STATUS.SERVER_STATUS_EXCEPTION + "")
							|| mp3Result.equals(AppConstant.INTERACTION_STATUS.SERVER_STATUS_EXCEPTION + ""))
					{
						fetchDataHandler.sendEmptyMessage(AppConstant.INTERACTION_STATUS.SERVER_STATUS_EXCEPTION);
					}
					else if (courseResult.equals(AppConstant.INTERACTION_STATUS.NETWORK_CONNECTION_EXCEPTION + "")
							|| mp3Result.equals(AppConstant.INTERACTION_STATUS.NETWORK_CONNECTION_EXCEPTION))
					{
						fetchDataHandler.sendEmptyMessage(AppConstant.INTERACTION_STATUS.NETWORK_CONNECTION_EXCEPTION);
					}
					else
					{
						// for (int i = 0; i < courseInfos.size(); i++)
						// {
						// HttpDownloader.downloadFile(AppConstant.URL.NCC_NEUQ_PIC_URL
						// + courseInfos.get(i).getPic(),
						// AppConstant.FilePath.PIC_FILE_PATH,
						// courseInfos.get(i).getPic());
						// }
						/**
						 * 屏蔽单篇排行中图片
						 */
						// for (int i = 0; i < mp3Infos.size(); i++)
						// {
						// HttpDownloader.downloadFile(AppConstant.URL.NCC_NEUQ_PIC_URL
						// + mp3Infos.get(i).getPic(),
						// AppConstant.FilePath.PIC_FILE_PATH,
						// mp3Infos.get(i).getPic());
						// }
						fetchDataHandler.sendEmptyMessage(AppConstant.INTERACTION_STATUS.INTERACTION_SUCCESSFUL);
					}
				}
				else
				{
					fetchDataHandler.sendEmptyMessage(AppConstant.INTERACTION_STATUS.SERVER_STATUS_EXCEPTION);
				}
			}
		}).start();
	}

	private void setTabsLayouts()
	{
		TextView courseTextView = (TextView) findViewById(R.id.fangting_paihang_orderby_course_textview);
		ImageView courseImageView = (ImageView) findViewById(R.id.fangting_paihang_orderby_course_imageview);
		TextView mp3TextView = (TextView) findViewById(R.id.fangting_paihang_orderby_mp3_textview);
		ImageView mp3ImageView = (ImageView) findViewById(R.id.fangting_paihang_orderby_mp3_imageview);

		RelativeLayout.LayoutParams selectedParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT, 5);
		selectedParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

		RelativeLayout.LayoutParams unselectedParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT, 1);
		unselectedParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

		if (isOrderedByCourses == true)
		{
			courseTextView.setTextColor(Color.parseColor("#A1C6D0"));
			courseImageView.setBackgroundColor(Color.parseColor("#A1C6D0"));
			courseImageView.setLayoutParams(selectedParams);
			mp3TextView.setTextColor(Color.parseColor("#7c7d7d"));
			mp3ImageView.setBackgroundColor(Color.parseColor("#7c7d7d"));
			mp3ImageView.setLayoutParams(unselectedParams);
		}
		else
		{
			courseTextView.setTextColor(Color.parseColor("#7c7d7d"));
			courseImageView.setBackgroundColor(Color.parseColor("#7c7d7d"));
			courseImageView.setLayoutParams(unselectedParams);
			mp3TextView.setTextColor(Color.parseColor("#A1C6D0"));
			mp3ImageView.setBackgroundColor(Color.parseColor("#A1C6D0"));
			mp3ImageView.setLayoutParams(selectedParams);
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);
		if (isOrderedByCourses)
		{
			Intent intent = new Intent(mContext, Fanting_Paihang_Course_Details.class);
			intent.putExtra("courseInfo", courseInfos.get(position));
			startActivity(intent);
		}
		else
		{
			Intent intent = new Intent(mContext, OnlinePlayer.class);
			intent.putExtra("mp3Info", mp3Infos.get(position));
			intent.putExtra("mode", "online");
			startActivity(intent);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fanting_paihang);

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

		courseOrderLayout = (RelativeLayout) findViewById(R.id.fanting_paihang_orderby_courses_relativelayout);
		mp3OrderLayout = (RelativeLayout) findViewById(R.id.fanting_paihang_orderby_mp3_relativelayout);

		courseOrderLayout.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (courseInfos.size() != 0)
				{
					isOrderedByCourses = true;
					setTabsLayouts();
					setListAdapter(new MyListAdapter());
				}

			}
		});
		mp3OrderLayout.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (mp3Infos.size() != 0)
				{
					isOrderedByCourses = false;
					setTabsLayouts();
					setListAdapter(new MyListAdapter());
				}

			}
		});

		setTabsLayouts();
		// fetchXMLsFromServer();

	}

	@Override
	protected void onResume()
	{
		super.onResume();
		fetchXMLsFromServer();
	}

	private class MyListAdapter extends BaseAdapter
	{

		@Override
		public int getCount()
		{
			if (isOrderedByCourses)
			{
				String courseResult = courseInfos.get(0).getName();
				if (courseResult.equals(AppConstant.INTERACTION_STATUS.NETWORK_CONNECTION_EXCEPTION + "")
						|| courseResult.equals(AppConstant.INTERACTION_STATUS.SERVER_STATUS_EXCEPTION + ""))
				{
					return 0;
				}
				else
				{
					return courseInfos.size();
				}
			}
			else
			{
				String mp3Result = mp3Infos.get(0).getName();
				if (mp3Result.equals(AppConstant.INTERACTION_STATUS.NETWORK_CONNECTION_EXCEPTION + "")
						|| mp3Result.equals(AppConstant.INTERACTION_STATUS.SERVER_STATUS_EXCEPTION + ""))
				{
					return 0;
				}
				else
				{
					return mp3Infos.size();
				}
			}

		}

		@Override
		public Object getItem(int position)
		{
			return null;
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			final int randId = position + 1;

			if (isOrderedByCourses)
			{
				CourseInfo cInfo = courseInfos.get(position);
				convertView = getLayoutInflater().inflate(R.layout.fanting_paihang_listitem_course_order, null);
				TextView rankTextView = (TextView) convertView
						.findViewById(R.id.fanting_paihang_listitem_rank_textview);
				ImageView coursePicImageView = (ImageView) convertView
						.findViewById(R.id.fanting_paihang_listitem_imageview);
				TextView courseTitleTextView = (TextView) convertView.findViewById(R.id.fanting_paihang_title_textview);
				TextView courseAmountTextView = (TextView) convertView
						.findViewById(R.id.fanting_paihang_listitem_course_amount_textview);
				rankTextView.setText(randId + "");
				// coursePicImageView.setBackgroundDrawable(Drawable.createFromPath(AppConstant.FilePath.PIC_FILE_PATH
				// + cInfo.getPic()));
				courseTitleTextView.setText(cInfo.getName());
				courseAmountTextView.setText("听力篇数：" + cInfo.getCount());

				if (cInfo.getPic().equals("apnews.jpg"))
				{
					coursePicImageView.setBackgroundResource(R.drawable.apnews);
				}
				else if (cInfo.getPic().equals("BBC.jpg"))
				{
					coursePicImageView.setBackgroundResource(R.drawable.bbc);
				}
				else if (cInfo.getPic().equals("CNN.jpg"))
				{
					coursePicImageView.setBackgroundResource(R.drawable.cnn);
				}
				else if (cInfo.getPic().equals("CRI.jpg"))
				{
					coursePicImageView.setBackgroundResource(R.drawable.cri);
				}
				else if (cInfo.getPic().equals("economy.jpg"))
				{
					coursePicImageView.setBackgroundResource(R.drawable.economy);
				}
				else if (cInfo.getPic().equals("gossip.jpg"))
				{
					coursePicImageView.setBackgroundResource(R.drawable.gossip);
				}
				else if (cInfo.getPic().equals("media.jpg"))
				{
					coursePicImageView.setBackgroundResource(R.drawable.media);
				}
				else if (cInfo.getPic().equals("movie.jpg"))
				{
					coursePicImageView.setBackgroundResource(R.drawable.movie);
				}
				else if (cInfo.getPic().equals("NPR.jpg"))
				{
					coursePicImageView.setBackgroundResource(R.drawable.npr);
				}
				else if (cInfo.getPic().equals("sa.jpg"))
				{
					coursePicImageView.setBackgroundResource(R.drawable.sa);
				}
				else if (cInfo.getPic().equals("speech.jpg"))
				{
					coursePicImageView.setBackgroundResource(R.drawable.speech);
				}
				else if (cInfo.getPic().equals("VOA.jpg"))
				{
					coursePicImageView.setBackgroundResource(R.drawable.voa);
				}
			}

			else
			{
				Mp3Info currentMp3Info = mp3Infos.get(position);
				convertView = getLayoutInflater().inflate(R.layout.fanting_paihang_listitem_2, null);
				// ImageView mp3PicImageView = (ImageView) convertView
				// .findViewById(R.id.fanting_paihang_listitem_imageview);
				// mp3PicImageView.setBackgroundDrawable(Drawable.createFromPath(AppConstant.FilePath.PIC_FILE_PATH
				// + currentMp3Info.getPic()));

				TextView mp3NameTextView = (TextView) convertView.findViewById(R.id.fanting_paihang_title_textview);
				TextView durationTextView = (TextView) convertView
						.findViewById(R.id.fanting_paihang_listitem_duration_textview);
				TextView sizeTextView = (TextView) convertView
						.findViewById(R.id.fanting_paihang_listitem_size_textview);
				TextView difficultyTextView = (TextView) convertView
						.findViewById(R.id.fanting_paihang_listitem_difficulty_textview);
				TextView rankTextView = (TextView) convertView
						.findViewById(R.id.fanting_paihang_listitem_rank_textview);

				mp3NameTextView.setText(currentMp3Info.getName());
				durationTextView.setText(MillisecondConvert.convert(Integer.parseInt(currentMp3Info.getDuration())));
				sizeTextView.setText(currentMp3Info.getSize());
				difficultyTextView.setText(currentMp3Info.getDifficulty());
				rankTextView.setText(randId + "");
				final Mp3Info mp3Info = mp3Infos.get(position);

				ImageView menuImageView = (ImageView) convertView
						.findViewById(R.id.fanting_paihang_listitem_menu_imageview);

				menuImageView.setOnClickListener(new OnClickListener()
				{

					@Override
					public void onClick(View v)
					{
						View menuView = menuWindow.getContentView();
						final PopupWindow menuWindow = new PopupWindow(menuView,
								RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

						LinearLayout downloadLayout = (LinearLayout) menuView
								.findViewById(R.id.fanting_listitem_menu_download_linearlayout);
						LinearLayout shoucangLayout = (LinearLayout) menuView
								.findViewById(R.id.fanting_listitem_menu_shoucang_linearlayout);
						LinearLayout addLinearLayout = (LinearLayout) menuView
								.findViewById(R.id.fanting_listitem_menu_add_to_jingting_linearlayout);
						LinearLayout fenxiangLayout = (LinearLayout) menuView
								.findViewById(R.id.fanting_listitem_menu_share_linearlayout);
						ImageView menuBackgroundImageView = (ImageView) menuView.findViewById(R.id.menu_background);

						fenxiangLayout.setOnClickListener(new OnClickListener()
						{

							@Override
							public void onClick(View v)
							{
								Util.showShare(Fanting_paihang.this,
										"今天我在坚果听力上听了这边文章，顿时感觉神清气爽！ ——" + mp3Info.getCourse());
							}
						});

						shoucangLayout.setOnClickListener(new OnClickListener()
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
									private Handler shoucangHandler = new Handler()
									{

										@Override
										public void handleMessage(Message msg)
										{
											super.handleMessage(msg);
											if (msg.what == 1)
											{
												Toast.makeText(mContext, "收藏成功", Toast.LENGTH_SHORT).show();
											}
											else
											{
												Toast.makeText(mContext, "收藏失败，服务器开小差叻", Toast.LENGTH_SHORT).show();
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
											shoucangHandler.sendEmptyMessage(0);
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
													.sendGetRequest(AppConstant.URL.ADD_TO_JINGTING_URL, params,
															headers);
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
															AppConstant.FilePath.MP3_FILE_PATH, mp3Info.getId()
																	+ ".mp3");
													int lrcRet = HttpDownloader.downloadFile(
															AppConstant.URL.NCC_NEUQ_LRC_URL + mp3Info.getId() + ".lrc",
															AppConstant.FilePath.LRC_FILE_PATH, mp3Info.getId()
																	+ ".lrc");
//													int picRet = HttpDownloader.downloadFile(
//															AppConstant.URL.NCC_NEUQ_PIC_URL + mp3Info.getPic(),
//															AppConstant.FilePath.PIC_FILE_PATH, mp3Info.getPic());
													if (mp3Ret != -1 && lrcRet != -1)
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
//											int picRet = HttpDownloader.downloadFile(AppConstant.URL.NCC_NEUQ_PIC_URL
//													+ mp3Info.getPic(), AppConstant.FilePath.PIC_FILE_PATH,
//													mp3Info.getPic());

											if (mp3Ret != -1 && lrcRet != -1)
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
			}
			return convertView;
		}
	}
}
