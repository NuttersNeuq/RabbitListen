package com.lfl.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.lfl.model.Mp3Info;
import com.lfl.utils.AppConstant;
import com.lfl.utils.HttpDownloader;
import com.lfl.utils.MillisecondConvert;
import com.lfl.utils.MyListView;
import com.lfl.utils.PullParseXML;
import com.lfl.utils.Toolkits;
import com.lz.utils.HttpRequestUtil;
import com.lz.utils.StaticInfos;
import com.lz.utils.Util;
import com.hare.activity.R;

@SuppressLint("HandlerLeak")
public class Fanting_tuijian extends Activity
{
	private ViewPager viewPager;
	private ImageView[] tips;
	private ImageView[] mImageViews;
	private TextView mp3TitleTextView;
	private List<Mp3Info> latestMp3Infos;
	private List<Mp3Info> tuijianMp3Infos;
	private MyListView listView;
	private ScrollView mScrollView;
	private ViewGroup group;
	private Dialog loadingDialog;
	private Context mContext;
	private PopupWindow backgroundWindow;
	private PopupWindow menuWindow;
	private RelativeLayout viewPagerBarLayout;


	private final int VIEWPAGER_REFRESS_DELAYED = 10 * 1000;

	private Handler autoScrollHandler = new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			viewPager.arrowScroll(2);
			autoScrollHandler.sendEmptyMessageDelayed(0, VIEWPAGER_REFRESS_DELAYED);
		}

	};

	private void fetchDataFromServer()
	{
		new Thread(new Runnable()
		{
			private Handler refreshUIHandler = new Handler()
			{

				@SuppressWarnings("deprecation")
				@Override
				public void handleMessage(Message msg)
				{
					super.handleMessage(msg);

					if (msg.what == AppConstant.INTERACTION_STATUS.NETWORK_CONNECTION_EXCEPTION)
					{
						loadingDialog.dismiss();
						Toast.makeText(mContext, AppConstant.INTERACTION_STATUS.TOAST_NETWORK_CONNECTION_EXCEPTION,
								Toast.LENGTH_SHORT).show();
					}
					else if (msg.what == AppConstant.INTERACTION_STATUS.SERVER_STATUS_EXCEPTION)
					{
						loadingDialog.dismiss();
						Toast.makeText(mContext, AppConstant.INTERACTION_STATUS.TOAST_SERVER_STATUS_EXCEPTION,
								Toast.LENGTH_SHORT).show();
					}
					else
					{
						// 将图片装载到数组中
						mImageViews = new ImageView[latestMp3Infos.size()];
						for (int i = 0; i < latestMp3Infos.size(); i++)
						{
							final Mp3Info mp3Info = latestMp3Infos.get(i);
							mImageViews[i] = new ImageView(mContext);
							String picPath = AppConstant.FilePath.PIC_FILE_PATH + mp3Info.getPic();
							// BitmapFactory.Options options = new
							// BitmapFactory.Options();
							// options.inSampleSize = 1;
							// Bitmap bm = BitmapFactory.decodeFile(picPath,
							// options);
							mImageViews[i].setBackgroundDrawable(Drawable.createFromPath(picPath));

							mImageViews[i].setOnClickListener(new OnClickListener()
							{

								@Override
								public void onClick(View v)
								{
									Intent intent = new Intent(mContext, OnlinePlayer.class);
									intent.putExtra("mp3Info", mp3Info);
									intent.putExtra("mode", "online");
									startActivity(intent);
								}
							});

						}

						// 将点点加入到ViewGroup中
						tips = new ImageView[latestMp3Infos.size()];
						for (int i = 0; i < tips.length; i++)
						{
							ImageView imageView = new ImageView(mContext);
							imageView.setLayoutParams(new LayoutParams(5, 5));
							tips[i] = imageView;
							tips[i].setPadding(5, 5, 5, 5);
							if (i == 0)
							{
								tips[i].setImageResource(R.drawable.help_spot_seleted);
							}
							else
							{
								tips[i].setImageResource(R.drawable.help_spot);
							}

							LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
									new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
							layoutParams.gravity = Gravity.CENTER_VERTICAL;
							// layoutParams.leftMargin = 5;
							// layoutParams.rightMargin = 5;
							group.addView(imageView, layoutParams);

						}
						viewPagerBarLayout.setBackgroundColor(Color.parseColor("#60000000"));
						viewPager.setAdapter(new MyViewPagerAdapter());
						viewPager.setCurrentItem((mImageViews.length) * 100);
						mp3TitleTextView.setText(latestMp3Infos.get(0).getName());
						viewPager.setOnPageChangeListener(new OnPageChangeListener()
						{

							@Override
							public void onPageSelected(int arg0)
							{
								mp3TitleTextView.setText(latestMp3Infos.get(arg0 % mImageViews.length).getName());
								setSpotsBackground(arg0 % mImageViews.length);
							}

							@Override
							public void onPageScrolled(int arg0, float arg1, int arg2)
							{

							}

							@Override
							public void onPageScrollStateChanged(int arg0)
							{

							}
						});
						viewPager.setAdapter(new MyViewPagerAdapter());

						mScrollView.smoothScrollTo(0, 0);

						listView.setAdapter(new MyListAdapter());
						listView.setOnItemClickListener(new OnItemClickListener()
						{

							@Override
							public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
							{
								Intent intent = new Intent(mContext, OnlinePlayer.class);
								Mp3Info mp3Info = tuijianMp3Infos.get(position);
								intent.putExtra("mp3Info", mp3Info);
								intent.putExtra("mode", "online");
								startActivity(intent);
							}

						});
						autoScrollHandler.sendEmptyMessageDelayed(0, VIEWPAGER_REFRESS_DELAYED);
						loadingDialog.dismiss();
					}
				}
			};

			@Override
			public void run()
			{

				HashMap<String, String> params = new HashMap<String, String>();

				latestMp3Infos = PullParseXML.parseOnlineMp3XML(AppConstant.URL.LATESTLISTEN_PHP_URL, params, true);
				tuijianMp3Infos = PullParseXML.parseOnlineMp3XML(AppConstant.URL.TUIJIANBYBIAOQIAN_PHP_URL, params,
						true);

				if (latestMp3Infos.size() != 0 && tuijianMp3Infos.size() != 0)
				{
					String statusOfLatestMp3Infos = latestMp3Infos.get(0).getName();
					String statusOfTuijianMp3Infos = tuijianMp3Infos.get(0).getName();

					if (statusOfLatestMp3Infos.equals(AppConstant.INTERACTION_STATUS.NETWORK_CONNECTION_EXCEPTION + "")
							|| statusOfTuijianMp3Infos
									.equals(AppConstant.INTERACTION_STATUS.NETWORK_CONNECTION_EXCEPTION + ""))
					{
						refreshUIHandler.sendEmptyMessage(AppConstant.INTERACTION_STATUS.NETWORK_CONNECTION_EXCEPTION);
					}
					else if (statusOfLatestMp3Infos.equals(AppConstant.INTERACTION_STATUS.SERVER_STATUS_EXCEPTION + "")
							|| statusOfTuijianMp3Infos.equals(AppConstant.INTERACTION_STATUS.SERVER_STATUS_EXCEPTION
									+ ""))
					{
						refreshUIHandler.sendEmptyMessage(AppConstant.INTERACTION_STATUS.SERVER_STATUS_EXCEPTION);
					}
					else
					{
						for (int i = 0; i < latestMp3Infos.size(); i++)
						{
							Mp3Info mp3Info = latestMp3Infos.get(i);
							String address = AppConstant.URL.NCC_NEUQ_PIC_URL + mp3Info.getPic();
							HttpDownloader.downloadFile(address, AppConstant.FilePath.PIC_FILE_PATH, mp3Info.getPic());
						}
						/**
						 * 屏蔽了听力图片的下载，推荐大图不在处理之列
						 */
						// for (int i = 0; i < tuijianMp3Infos.size(); i++)
						// {
						// Mp3Info mp3Info = tuijianMp3Infos.get(i);
						// String address = AppConstant.URL.NCC_NEUQ_PIC_URL +
						// mp3Info.getPic();
						// HttpDownloader.downloadFile(address,
						// AppConstant.FilePath.PIC_FILE_PATH,
						// mp3Info.getPic());
						// }

						refreshUIHandler.sendEmptyMessage(AppConstant.INTERACTION_STATUS.INTERACTION_SUCCESSFUL);
					}
				}
				else
				{
					refreshUIHandler.sendEmptyMessage(AppConstant.INTERACTION_STATUS.SERVER_STATUS_EXCEPTION);
				}

			}
		}).start();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fanting_tuijian);

		mContext = this;

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

		group = (ViewGroup) findViewById(R.id.fanting_tuijian_viewgroup);
		viewPager = (ViewPager) findViewById(R.id.fanting_tuijian_viewpager);
		mp3TitleTextView = (TextView) findViewById(R.id.fanting_tuijian_viewpager_mp3name_textview);
		viewPagerBarLayout = (RelativeLayout) findViewById(R.id.fanting_viewpager_title_bar);

		loadingDialog = new Dialog(this, R.style.loading_dialog_style);
		loadingDialog.setContentView(R.layout.loading_dialog);
		Window loadingDialogWindow = loadingDialog.getWindow();
		WindowManager.LayoutParams lParams = loadingDialogWindow.getAttributes();
		loadingDialogWindow.setGravity(Gravity.CENTER);
		lParams.alpha = 1f;
		loadingDialogWindow.setAttributes(lParams);
		loadingDialog.show();

		fetchDataFromServer();

		listView = (MyListView) findViewById(R.id.fanting_tuijian_listview);
		mScrollView = (ScrollView) findViewById(R.id.fanting_tuijian_scrollview);

	}

	@Override
	protected void onResume()
	{
		super.onResume();
		mScrollView.smoothScrollTo(0, 0);
	}

	@Override
	protected void onDestroy()
	{
		menuWindow.dismiss();
		backgroundWindow.dismiss();
		loadingDialog.dismiss();
		super.onDestroy();
	}

	/**
	 * 设置选中的tip的背景
	 * 
	 * @param selectItems
	 */
	private void setSpotsBackground(int selectItems)
	{
		for (int i = 0; i < tips.length; i++)
		{
			if (i == selectItems)
			{
				tips[i].setImageResource(R.drawable.help_spot_seleted);
			}
			else
			{
				tips[i].setImageResource(R.drawable.help_spot);
			}
		}
	}

	private class MyViewPagerAdapter extends PagerAdapter
	{

		@Override
		public int getCount()
		{
			return Integer.MAX_VALUE;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1)
		{
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(View container, int position, Object object)
		{
			((ViewPager) container).removeView(mImageViews[position % mImageViews.length]);

		}

		/**
		 * 载入图片进去，用当前的position 除以 图片数组长度取余数是关键
		 */
		@Override
		public Object instantiateItem(View container, int position)
		{
			((ViewPager) container).addView(mImageViews[position % mImageViews.length], 0);
			return mImageViews[position % mImageViews.length];
		}

	}

	private class MyListAdapter extends BaseAdapter
	{

		@Override
		public int getCount()
		{
			return tuijianMp3Infos.size();
		}

		@Override
		public Object getItem(int position)
		{
			return tuijianMp3Infos.get(position);
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
			final Mp3Info mp3Info = tuijianMp3Infos.get(position);

			TextView titleTextView = (TextView) convertView.findViewById(R.id.fanting_tuijian_title_textview);
			TextView durationTextView = (TextView) convertView
					.findViewById(R.id.fanting_tuijian_listitem_duration_textview);
			TextView sizeTextView = (TextView) convertView.findViewById(R.id.fanting_tuijian_listitem_size_textview);
			TextView difficultyTextView = (TextView) convertView
					.findViewById(R.id.fanting_tuijian_listitem_difficulty_textview);
			// ImageView imageView = (ImageView)
			// convertView.findViewById(R.id.fanting_tuijian_listitem_imageview);
			ImageView menuImageView = (ImageView) convertView
					.findViewById(R.id.fanting_tuijian_listitem_menu_imageview);

			titleTextView.setText(mp3Info.getName());
			durationTextView.setText(MillisecondConvert.convert(Integer.parseInt(mp3Info.getDuration())));
			sizeTextView.setText(mp3Info.getSize());
			difficultyTextView.setText(mp3Info.getDifficulty());

			// String picPath = AppConstant.FilePath.PIC_FILE_PATH +
			// mp3Info.getPic();
			// BitmapFactory.Options options = new BitmapFactory.Options();
			// options.inSampleSize = 1;
			// Bitmap bm = BitmapFactory.decodeFile(picPath, options);
			// imageView.setBackgroundDrawable(Drawable.createFromPath(picPath));

			menuImageView.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					View menuView = menuWindow.getContentView();

					TextView shoucangTextView = (TextView) menuView
							.findViewById(R.id.fanting_listitem_menu_shoucang_textview);
					shoucangTextView.setText("收藏此听力");

					// final PopupWindow backgroundWindow = new
					// PopupWindow(menuBackgroundView,
					// LinearLayout.LayoutParams.MATCH_PARENT,
					// LinearLayout.LayoutParams.MATCH_PARENT);
					// final PopupWindow menuWindow = new PopupWindow(menuView,
					// RelativeLayout.LayoutParams.MATCH_PARENT,
					// RelativeLayout.LayoutParams.MATCH_PARENT);
					// menuWindow.setAnimationStyle(R.style.AnimBottom);
					// backgroundWindow.setAnimationStyle(R.style.AnimPopupMenuBackground);

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
						public void onClick(View v)
						{
							Util.showShare(Fanting_tuijian.this, "今天我在坚果听力上听了这边文章，顿时感觉神清气爽！ ——" + mp3Info.getName());
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
//												int picRet = HttpDownloader.downloadFile(
//														AppConstant.URL.NCC_NEUQ_PIC_URL + mp3Info.getPic(),
//														AppConstant.FilePath.PIC_FILE_PATH, mp3Info.getPic());
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
//										int picRet = HttpDownloader.downloadFile(AppConstant.URL.NCC_NEUQ_PIC_URL
//												+ mp3Info.getPic(), AppConstant.FilePath.PIC_FILE_PATH,
//												mp3Info.getPic());

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
			return convertView;
		}
	}

}
