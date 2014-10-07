package com.lfl.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.PopupWindow.OnDismissListener;

import com.lfl.model.Mp3Info;
import com.lfl.utils.AppConstant;
import com.lfl.utils.MillisecondConvert;
import com.lfl.utils.PullParseXML;
import com.lfl.utils.Toolkits;
import com.lz.utils.HttpRequestUtil;
import com.lz.utils.StaticInfos;
import com.lz.utils.Util;
import com.hare.activity.R;

@SuppressLint("HandlerLeak")
public class Tingliku_JingTing extends ListActivity
{

	private List<Mp3Info> mp3Infos;
	private Context mContext;
	private PopupWindow backgroundWindow;
	private PopupWindow menuWindow;

	private Comparator<Mp3Info> comparator = new Comparator<Mp3Info>()
	{

		@Override
		public int compare(Mp3Info lhs, Mp3Info rhs)
		{
			return lhs.getRound().compareTo(rhs.getRound());
		}

	};

	private void fetchMp3Infos()
	{
		final Handler fetchHandler = new Handler()
		{

			@Override
			public void handleMessage(Message msg)
			{
				super.handleMessage(msg);
				if (msg.what == AppConstant.INTERACTION_STATUS.INTERACTION_SUCCESSFUL)
				{
					Collections.sort(mp3Infos, comparator);
					TextView blankTextView = (TextView) findViewById(R.id.tingliku_jingting_blank_textview);
					if (mp3Infos.size() != 0)
					{
						blankTextView.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
						setListAdapter(new MyListAdapter());
					}

				}
				else if (msg.what == AppConstant.INTERACTION_STATUS.NETWORK_CONNECTION_EXCEPTION)
				{
					Toast.makeText(mContext, AppConstant.INTERACTION_STATUS.TOAST_NETWORK_CONNECTION_EXCEPTION,
							Toast.LENGTH_SHORT).show();
				}
				else if (msg.what == AppConstant.INTERACTION_STATUS.SERVER_STATUS_EXCEPTION)
				{
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
				mp3Infos = PullParseXML.parseOnlineMp3XML(AppConstant.URL.TINGLIKU_JINGTING_LIST_URL, params, true);

				System.out.println("获取的精听列表：" + mp3Infos);

				if (mp3Infos.size() == 0)
				{
					fetchHandler.sendEmptyMessage(AppConstant.INTERACTION_STATUS.INTERACTION_SUCCESSFUL);
				}
				else
				{
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
						fetchHandler.sendEmptyMessage(AppConstant.INTERACTION_STATUS.INTERACTION_SUCCESSFUL);
					}
				}

			}
		}).start();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tingliku_jingting);
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

		fetchMp3Infos();
	}

	private class MyListAdapter extends BaseAdapter
	{

		@Override
		public int getCount()
		{
			return mp3Infos.size();
		}

		@Override
		public Object getItem(int arg0)
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
			convertView = getLayoutInflater().inflate(R.layout.standard_mp3_listitem_pic, null);
			final int finalPosition = position;
			final Mp3Info mp3Info = mp3Infos.get(position);
			TextView titleTextView = (TextView) convertView.findViewById(R.id.standard_mp3_title_textview);
			TextView durationTextView = (TextView) convertView
					.findViewById(R.id.standard_mp3_listitem_duration_textview);
			TextView sizeTextView = (TextView) convertView.findViewById(R.id.standard_mp3_listitem_size_textview);
			TextView difficultyTextView = (TextView) convertView
					.findViewById(R.id.standard_mp3_listitem_difficulty_textview);
			ImageView imageView = (ImageView) convertView.findViewById(R.id.standard_mp3_listitem_imageview);
			ImageView menuImageView = (ImageView) convertView.findViewById(R.id.standard_mp3_listitem_menu_imageview);

			titleTextView.setText(mp3Info.getName());
			durationTextView.setText(MillisecondConvert.convert(Integer.parseInt(mp3Info.getDuration())));
			sizeTextView.setText(mp3Info.getSize());
			difficultyTextView.setText(mp3Info.getDifficulty());

			imageView.setPadding(10, 10, 10, 10);

			if (mp3Info.getRound().equals("11"))
			{
				imageView.setImageResource(R.drawable.jingting_jindu_0);
			}
			else if (mp3Info.getRound().equals("12"))
			{
				imageView.setImageResource(R.drawable.jingting_jindu_0);
			}
			else if (mp3Info.getRound().equals("13"))
			{
				imageView.setImageResource(R.drawable.jingting_jindu_0);
			}
			else if (mp3Info.getRound().equals("2"))
			{
				imageView.setImageResource(R.drawable.jingting_jindu_1);
			}
			else if (mp3Info.getRound().equals("3"))
			{
				imageView.setImageResource(R.drawable.jingting_jindu_2);
			}
			else if (mp3Info.getRound().equals("4"))
			{
				imageView.setImageResource(R.drawable.jingting_jindu_3);
			}
			else if (mp3Info.getRound().equals("5"))
			{
				imageView.setImageResource(R.drawable.jingting_jindu_4);
			}
			else if (mp3Info.getRound().equals("6"))
			{
				imageView.setImageResource(R.drawable.jingting_jindu_finish);
			}

			menuImageView.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					View menuView = menuWindow.getContentView();

					ImageView backgroundImageView = (ImageView) menuView.findViewById(R.id.menu_background);
					LinearLayout passLayout = (LinearLayout) menuView
							.findViewById(R.id.tingliku_jingting_menu_del_linearlayout);
					LinearLayout shoucangLayout = (LinearLayout) menuView
							.findViewById(R.id.tingliku_jingting_menu_shoucang_linearlayout);
					LinearLayout fenxiangLayout = (LinearLayout) menuView
							.findViewById(R.id.tingliku_jingting_menu_share_linearlayout);
					LinearLayout resetLayout = (LinearLayout) menuView
							.findViewById(R.id.tingliku_jingting_menu_reset_linearlayout);

					backgroundImageView.setOnClickListener(new OnClickListener()
					{

						@Override
						public void onClick(View v)
						{
							menuWindow.dismiss();
						}
					});

					passLayout.setOnClickListener(new OnClickListener()
					{

						@Override
						public void onClick(View v)
						{
							final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
							View dialogView = getLayoutInflater().inflate(R.layout.custom_alert_dialog, null);
							TextView descriptionTextView = (TextView) dialogView
									.findViewById(R.id.custom_alert_dialog_description_textview);
							descriptionTextView.setText("你确定要 PASS 掉这篇听力嘛");
							Button cancelButton = (Button) dialogView
									.findViewById(R.id.custom_alert_dialog_cancel_button);
							Button okButton = (Button) dialogView.findViewById(R.id.custom_alert_dialog_ok_button);
							builder.setView(dialogView);
							final AlertDialog dialog = builder.create();
							dialog.show();

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
								private int responseCode;
								private InputStream inputStream;
								private int result;
								private Handler delHandler = new Handler()
								{

									@Override
									public void handleMessage(Message msg)
									{
										super.handleMessage(msg);
										if (msg.what == 1)
										{
											Toast.makeText(mContext, "PASS成功", Toast.LENGTH_SHORT).show();
											mp3Infos.remove(finalPosition);
											Collections.sort(mp3Infos, comparator);
											setListAdapter(new MyListAdapter());
										}
										else
										{
											Toast.makeText(mContext, "PASS失败，服务器又开小差了", Toast.LENGTH_SHORT).show();
										}
									}

								};

								@Override
								public void onClick(View v)
								{
									menuWindow.dismiss();
									HashMap<String, String> headers = new HashMap<String, String>();
									HashMap<String, String> params = new HashMap<String, String>();
									headers.put("Cookie", "PHPSESSID=" + StaticInfos.phpsessid);
									params.put("lid", mp3Info.getId());

									try
									{
										HttpURLConnection urlConnection = (HttpURLConnection) HttpRequestUtil
												.sendGetRequest(AppConstant.URL.JINGTING_PASS_URL, params, headers);
										responseCode = urlConnection.getResponseCode();
										inputStream = urlConnection.getInputStream();
										result = Integer.parseInt(Toolkits.convertStreamToString(inputStream));
										if (responseCode != 200 || result == 0)
										{
											delHandler.sendEmptyMessage(0);
										}
										else
										{
											delHandler.sendEmptyMessage(1);
										}
									} catch (Exception e)
									{
										e.printStackTrace();
										delHandler.sendEmptyMessage(0);
									}
								}
							});
						}
					});

					resetLayout.setOnClickListener(new OnClickListener()
					{

						@Override
						public void onClick(View v)
						{
							final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
							View dialogView = getLayoutInflater().inflate(R.layout.custom_alert_dialog, null);
							TextView descriptionTextView = (TextView) dialogView
									.findViewById(R.id.custom_alert_dialog_description_textview);
							Button cancelButton = (Button) dialogView
									.findViewById(R.id.custom_alert_dialog_cancel_button);
							Button okButton = (Button) dialogView.findViewById(R.id.custom_alert_dialog_ok_button);
							descriptionTextView.setText("您确定要重置进度吗？");
							builder.setView(dialogView);
							final AlertDialog dialog = builder.create();
							dialog.show();

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
								private int responseCode;
								private InputStream inputStream;
								private int result;
								private Handler resetHandler = new Handler()
								{

									@Override
									public void handleMessage(Message msg)
									{
										super.handleMessage(msg);
										if (msg.what == 1)
										{
											mp3Info.setRound("11");
											Collections.sort(mp3Infos, comparator);
											setListAdapter(new MyListAdapter());
											Toast.makeText(mContext, "已重置", Toast.LENGTH_SHORT).show();
										}
										else
										{
											Toast.makeText(mContext, "重置失败，服务器开小差了", Toast.LENGTH_SHORT).show();
										}
									}
								};

								@Override
								public void onClick(View v)
								{
									menuWindow.dismiss();
									dialog.dismiss();
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
														.sendGetRequest(AppConstant.URL.RESET_JINGTING_RPOGRESS,
																params, headers);
												responseCode = urlConnection.getResponseCode();
												inputStream = urlConnection.getInputStream();
												result = Integer.parseInt(Toolkits.convertStreamToString(inputStream));
												if (responseCode != 200 || result == 0)
												{
													resetHandler.sendEmptyMessage(0);
												}
												else
												{
													resetHandler.sendEmptyMessage(1);
												}
											} catch (Exception e)
											{
												e.printStackTrace();
												resetHandler.sendEmptyMessage(0);
											}
										}
									}).start();
								}
							});
						}
					});

					fenxiangLayout.setOnClickListener(new OnClickListener()
					{

						@Override
						public void onClick(View v)
						{
							Util.showShare(Tingliku_JingTing.this, "今天我在坚果听力上听了这边文章，顿时感觉神清气爽！ ——" + mp3Info.getCourse());
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
					backgroundWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
					menuWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
				}
			});

			return convertView;
		}

	}
}
