package com.lfl.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

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
public class Fanting_search extends ListActivity implements OnScrollListener
{
	private SearchView searchView;
	private List<Mp3Info> mp3Infos = new ArrayList<Mp3Info>();
	private Context mContext;
	private Dialog loadingDialog;
	private String queryString;
	private PopupWindow backgroundWindow;
	private PopupWindow menuWindow;

	/**
	 * limit = num * size; size 为组的大小
	 */
	private int groupNum = 1;
	private final int groupSize = 10;
	private boolean isSubmitMode = false;
	private int currentLastItem;

	/**
	 * 包括刷新Adapter
	 */
	private void fetchMp3List()
	{
		final Handler submitHandler = new Handler()
		{

			@Override
			public void handleMessage(Message msg)
			{
				super.handleMessage(msg);
				if (msg.what == AppConstant.INTERACTION_STATUS.INTERACTION_SUCCESSFUL)
				{
					if (mp3Infos.size() == 0)
					{
						Toast.makeText(mContext, "木找到相关听力哎", Toast.LENGTH_SHORT).show();
					}
					else
					{
						setListAdapter(new MyListAdapter());
						if (isSubmitMode == false)
						{
							System.out.println("获取的selelction：" + ((groupNum - 1) * groupSize - 1));
							getListView().setSelection((groupNum - 1) * groupSize - 1);
						}

					}
					loadingDialog.dismiss();
				}
				else if (msg.what == AppConstant.INTERACTION_STATUS.NETWORK_CONNECTION_EXCEPTION)
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
			}

		};

		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("kw", queryString);
				params.put("limit", groupNum * groupSize + "");
				mp3Infos = PullParseXML.parseOnlineMp3XML(AppConstant.URL.SEARCH_RESULT_LIST_URL, params, true);

				System.out.println("获取的MP3LIST大小为：" + mp3Infos.size());

				if (mp3Infos.size() != 0)
				{
					String result = mp3Infos.get(0).getName();
					if (result.equals(AppConstant.INTERACTION_STATUS.NETWORK_CONNECTION_EXCEPTION + ""))
					{
						submitHandler.sendEmptyMessage(AppConstant.INTERACTION_STATUS.NETWORK_CONNECTION_EXCEPTION);
					}
					else if (result.equals(AppConstant.INTERACTION_STATUS.SERVER_STATUS_EXCEPTION + ""))
					{
						submitHandler.sendEmptyMessage(AppConstant.INTERACTION_STATUS.SERVER_STATUS_EXCEPTION);
					}
					else
					{
						submitHandler.sendEmptyMessage(AppConstant.INTERACTION_STATUS.INTERACTION_SUCCESSFUL);
					}
				}
				else
				{
					submitHandler.sendEmptyMessage(AppConstant.INTERACTION_STATUS.INTERACTION_SUCCESSFUL);
				}

			}
		}).start();
	}

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
			isSubmitMode = false;
			groupNum++;
			fetchMp3List();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fanting_search);

		loadingDialog = new Dialog(this, R.style.loading_dialog_style);
		loadingDialog.setContentView(R.layout.loading_dialog);
		Window loadingDialogWindow = loadingDialog.getWindow();
		WindowManager.LayoutParams lParams = loadingDialogWindow.getAttributes();
		loadingDialogWindow.setGravity(Gravity.CENTER);
		lParams.alpha = 1f;
		loadingDialogWindow.setAttributes(lParams);

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

		searchView = (SearchView) findViewById(R.id.fanting_search_searchview);
		searchView.setOnQueryTextListener(new OnQueryTextListener()
		{

			@Override
			public boolean onQueryTextSubmit(String query)
			{
				groupNum = 1;
				loadingDialog.show();
				queryString = query;
				fetchMp3List();
				isSubmitMode = true;
				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText)
			{
				return false;
			}
		});

		getListView().setOnScrollListener(this);

	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		menuWindow.dismiss();
		backgroundWindow.dismiss();
		loadingDialog.dismiss();
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

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			convertView = getLayoutInflater().inflate(R.layout.fanting_search_listitem, null);
			TextView titleTextView = (TextView) convertView.findViewById(R.id.fanting_search_title_textview);
			TextView durationTextView = (TextView) convertView
					.findViewById(R.id.fanting_search_listitem_duration_textview);
			TextView sizeTextView = (TextView) convertView.findViewById(R.id.fanting_search_listitem_size_textview);
			TextView difficultyTextView = (TextView) convertView
					.findViewById(R.id.fanting_search_listitem_difficulty_textview);
			ImageView menuImageView = (ImageView) convertView.findViewById(R.id.fanting_search_listitem_menu_imageview);
			final Mp3Info mp3Info = mp3Infos.get(position);

			titleTextView.setText(mp3Info.getName());
			sizeTextView.setText(mp3Info.getSize());
			difficultyTextView.setText(mp3Info.getDifficulty());
			durationTextView.setText(MillisecondConvert.convert(Integer.parseInt(mp3Info.getDuration())));

			menuImageView.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					View menuView = menuWindow.getContentView();

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
							Util.showShare(Fanting_search.this, "今天我在坚果听力上听了这边文章，顿时感觉神清气爽！ ——" + mp3Info.getCourse());
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
