package com.lfl.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
public class Fanting_Paihang_Course_Details extends ListActivity
{
	private List<Mp3Info> mp3Infos;
	private CourseInfo courseInfo;
	private Context mContext;
	private TextView introduceTextView;
	private TextView courseNameTextView;

	/**
	 * limit = num * size; size 为组的大小
	 */
	private int groupNum = 1;
	private final int groupSize = 10;
	private int currentLastItem;

	/**
	 * 自带刷Adapter功能
	 */
	private void fetchCourseDetails()
	{
		final Handler fetchHandler = new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				super.handleMessage(msg);
				if (msg.what == AppConstant.INTERACTION_STATUS.INTERACTION_SUCCESSFUL)
				{
					setDinyueButton();
					setListAdapter(new MyListAdapter());
					getListView().setSelection((groupNum - 1) * groupSize - 1);
				}
				else if (msg.what == AppConstant.INTERACTION_STATUS.SERVER_STATUS_EXCEPTION)
				{
					Toast.makeText(mContext, AppConstant.INTERACTION_STATUS.TOAST_SERVER_STATUS_EXCEPTION,
							Toast.LENGTH_SHORT).show();
				}
				else if (msg.what == AppConstant.INTERACTION_STATUS.NETWORK_CONNECTION_EXCEPTION)
				{
					Toast.makeText(mContext, AppConstant.INTERACTION_STATUS.TOAST_NETWORK_CONNECTION_EXCEPTION,
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
				params.put("type", "list");
				params.put("cid", courseInfo.getId());
				params.put("limit", groupNum * groupSize + "");
				mp3Infos = PullParseXML.parseOnlineMp3XML(AppConstant.URL.MY_SUBCRIBE_URL, params, true);

				String result = mp3Infos.get(0).getName();
				if (result.equals(AppConstant.INTERACTION_STATUS.NETWORK_CONNECTION_EXCEPTION + "")
						|| result.equals(AppConstant.INTERACTION_STATUS.SERVER_STATUS_EXCEPTION + ""))
				{
					fetchHandler.sendEmptyMessage(0);
				}
				else
				{
					/**
					 * 屏蔽了听力图片的下载
					 */
					// for (int i = 0; i < mp3Infos.size(); i++)
					// {
					// Mp3Info currentMp3Info = mp3Infos.get(i);
					// File picFile = new
					// File(AppConstant.FilePath.PIC_FILE_PATH +
					// currentMp3Info.getPic());
					// if (!picFile.exists())
					// {
					// int downloadResult =
					// HttpDownloader.downloadFile(AppConstant.URL.NCC_NEUQ_PIC_URL
					// + currentMp3Info.getPic(),
					// AppConstant.FilePath.PIC_FILE_PATH,
					// currentMp3Info.getPic());
					// System.out.println(currentMp3Info.getName() + "图片下载结果：" +
					// downloadResult);
					// }
					// }
					groupNum++;
					fetchHandler.sendEmptyMessage(1);
				}
			}
		}).start();
	}

	private void setDinyueButton()
	{
		LinearLayout dingyueLayout = (LinearLayout) findViewById(R.id.fanting_paihang_course_details_listitem_cancel_subcribe_linearlayout);
		final TextView statusTextView = (TextView) findViewById(R.id.fanting_paihang_course_details_dingyue_textview);
		if (courseInfo.getIfd().equals("1"))
		{
			statusTextView.setText("取消订阅");
		}
		else
		{
			statusTextView.setText("我要订阅");
		}

		dingyueLayout.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
				View alertDialogView = getLayoutInflater().inflate(R.layout.fanting_dingyue_subscribe_alert_dialog,
						null);
				builder.setView(alertDialogView);
				Button cancelButton = (Button) alertDialogView
						.findViewById(R.id.fanting_dingyue_subscribe_alert_dialog_cancel_button);
				Button okButton = (Button) alertDialogView
						.findViewById(R.id.fanting_dingyue_subscribe_alert_dialog_ok_button);
				final AlertDialog dialog = builder.create();

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
					private InputStream inputStream = null;
					private int responseCode = 0;
					private int result;
					private Handler cancelSubscribeHandler = new Handler()
					{

						@Override
						public void handleMessage(Message msg)
						{
							super.handleMessage(msg);
							if (msg.what == 1)
							{
								statusTextView.setText("我要订阅");
								courseInfo.setIfd("0");
								Toast.makeText(mContext, "取消订阅成功", Toast.LENGTH_SHORT).show();
							}
							else
							{
								Toast.makeText(mContext, "取消订阅失败，服务器开小差了", Toast.LENGTH_SHORT).show();
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
								params.put("type", "ifd");
								params.put("ifd", "0");
								params.put("cid", courseInfo.getId());
								try
								{
									HttpURLConnection urlConnection = (HttpURLConnection) HttpRequestUtil
											.sendGetRequest(AppConstant.URL.MY_SUBCRIBE_URL, params, headers);
									responseCode = urlConnection.getResponseCode();
									inputStream = urlConnection.getInputStream();
									result = Integer.parseInt(Toolkits.convertStreamToString(inputStream));
									if (responseCode != 200 || result == 0)
									{
										cancelSubscribeHandler.sendEmptyMessage(0);
									}
									else
									{
										cancelSubscribeHandler.sendEmptyMessage(1);
									}
								} catch (Exception e)
								{
									cancelSubscribeHandler.sendEmptyMessage(0);
									e.printStackTrace();
								}
							}
						}).start();
					}
				});

				if (courseInfo.getId().equals("1"))
				{
					dialog.show();
				}
				else
				{
					final Handler addSubscribeHandler = new Handler()
					{

						@Override
						public void handleMessage(Message msg)
						{
							super.handleMessage(msg);
							if (msg.what == 1)
							{
								Toast.makeText(mContext, "已订阅啦", Toast.LENGTH_SHORT).show();
								statusTextView.setText("取消订阅");
								courseInfo.setIfd("1");
							}
							else
							{
								Toast.makeText(mContext, "添加订阅失败，服务器开小差了", Toast.LENGTH_SHORT).show();
							}
						}
					};

					new Thread(new Runnable()
					{

						@Override
						public void run()
						{
							HashMap<String, String> headers = new HashMap<String, String>();
							HashMap<String, String> params = new HashMap<String, String>();
							headers.put("Cookie", "PHPSESSID=" + StaticInfos.phpsessid);
							params.put("type", "ifd");
							params.put("ifd", "1");
							params.put("cid", courseInfo.getId());
							try
							{
								HttpURLConnection urlConnection = (HttpURLConnection) HttpRequestUtil.sendGetRequest(
										AppConstant.URL.MY_SUBCRIBE_URL, params, headers);
								int responseCode = urlConnection.getResponseCode();
								InputStream inputStream = urlConnection.getInputStream();
								int result = Integer.parseInt(Toolkits.convertStreamToString(inputStream));
								if (responseCode != 200 || result == 0)
								{
									addSubscribeHandler.sendEmptyMessage(0);
								}
								else
								{
									addSubscribeHandler.sendEmptyMessage(1);
								}
							} catch (Exception e)
							{
								addSubscribeHandler.sendEmptyMessage(0);
								e.printStackTrace();
							}
						}
					}).start();
				}
			}
		});

	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fanting_paihang_course_details);

		courseInfo = (CourseInfo) getIntent().getSerializableExtra("courseInfo");
		mContext = this;
		courseNameTextView = (TextView) findViewById(R.id.fanting_paihang_course_details_listitem_subscribe_title_textview);
		introduceTextView = (TextView) findViewById(R.id.fanting_paihang_course_details_listitem_subscribe_introduction_textview);

		courseNameTextView.setText(courseInfo.getName());
		introduceTextView.setText(courseInfo.getIntroduction());

		getListView().setOnScrollListener(new OnScrollListener()
		{
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
			{
				currentLastItem = firstVisibleItem + visibleItemCount;
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState)
			{
				System.out.println("当前最后一条为：" + currentLastItem);
				if (currentLastItem == mp3Infos.size() && scrollState == SCROLL_STATE_IDLE)
				{
					groupNum++;
					fetchCourseDetails();
				}
			}
		});

		fetchCourseDetails();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		System.out.println("抓到的听力：" + mp3Infos.get(position));
		Intent intent = new Intent(mContext, OnlinePlayer.class);
		intent.putExtra("mp3Info", mp3Infos.get(position));
		intent.putExtra("mode", "online");
		startActivity(intent);
		super.onListItemClick(l, v, position, id);
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
			return null;
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		// @SuppressWarnings("deprecation")
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			convertView = getLayoutInflater().inflate(R.layout.fanting_paihang_course_details_listitem, null);
			// ImageView mp3PicImageView = (ImageView) convertView
			// .findViewById(R.id.fanting_paihang_course_details_listitem_imageview);
			TextView mp3NameTextView = (TextView) convertView
					.findViewById(R.id.fanting_paihang_course_details_listitem_title_textview);
			TextView durationTextView = (TextView) convertView
					.findViewById(R.id.fanting_paihang_course_details_listitem_duration_textview);
			TextView sizeTextView = (TextView) convertView
					.findViewById(R.id.fanting_paihang_course_details_listitem_size_textview);
			TextView difficultyTextView = (TextView) convertView
					.findViewById(R.id.fanting_paihang_course_details_listitem_difficulty_textview);
			LinearLayout menuLayout = (LinearLayout) convertView
					.findViewById(R.id.fanting_paihang_course_details_listitem_menu_button_linearlayout);
			final Mp3Info mp3Info = mp3Infos.get(position);

			// mp3PicImageView
			// .setBackgroundDrawable(Drawable.createFromPath(AppConstant.FilePath.PIC_FILE_PATH
			// + mp3Info.getPic()));
			mp3NameTextView.setText(mp3Info.getName());
			durationTextView.setText(MillisecondConvert.convert(Integer.parseInt(mp3Info.getDuration())));
			sizeTextView.setText(mp3Info.getSize());
			difficultyTextView.setText(mp3Info.getDifficulty());

			menuLayout.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					View menuView = getLayoutInflater().inflate(R.layout.fanting_listitem_menu_collected, null);
					final PopupWindow menuWindow = new PopupWindow(menuView, RelativeLayout.LayoutParams.MATCH_PARENT,
							RelativeLayout.LayoutParams.MATCH_PARENT);

					View menuBackgroundView = getLayoutInflater().inflate(R.layout.popupmenu_black_background, null);
					final PopupWindow backgroundWindow = new PopupWindow(menuBackgroundView,
							LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

					LinearLayout downloadLayout = (LinearLayout) menuView
							.findViewById(R.id.fanting_listitem_menu_download_linearlayout);
					LinearLayout shoucangLayout = (LinearLayout) menuView
							.findViewById(R.id.fanting_listitem_menu_shoucang_linearlayout);
					LinearLayout addLinearLayout = (LinearLayout) menuView
							.findViewById(R.id.fanting_listitem_menu_add_to_jingting_linearlayout);
					LinearLayout fenxiangLayout = (LinearLayout) menuView
							.findViewById(R.id.fanting_listitem_menu_share_linearlayout);

					backgroundWindow.setAnimationStyle(R.style.AnimPopupMenuBackground);
					menuWindow.setAnimationStyle(R.style.AnimBottom);
					menuWindow.setOnDismissListener(new OnDismissListener()
					{

						@Override
						public void onDismiss()
						{
							backgroundWindow.dismiss();
						}
					});

					ImageView menuBackgroundImageView = (ImageView) menuView.findViewById(R.id.menu_background);

					menuBackgroundImageView.setOnClickListener(new OnClickListener()
					{

						@Override
						public void onClick(View v)
						{
							menuWindow.dismiss();
						}
					});

					fenxiangLayout.setOnClickListener(new OnClickListener()
					{

						@Override
						public void onClick(View v)
						{
							Util.showShare(mContext, "今天我在坚果听力上听了这边文章，顿时感觉神清气爽！ ——" + mp3Info.getCourse());
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
												// int picRet =
												// HttpDownloader.downloadFile(
												// AppConstant.URL.NCC_NEUQ_PIC_URL
												// + mp3Info.getPic(),
												// AppConstant.FilePath.PIC_FILE_PATH,
												// mp3Info.getPic());
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
