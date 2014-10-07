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
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
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
import com.hare.activity.R;

@SuppressLint("HandlerLeak")
public class Fanting_dingyue_add extends ListActivity
{
	private List<CourseInfo> courseInfos;
	private List<Mp3Info> mp3Infos;
	private Context mContext;
	private int selectedCourseItem = -1;

	private boolean isOnTheMainScreen = true;

	private void fetchCourseXML()
	{
		new Thread(new Runnable()
		{
			private Handler fetchHandler = new Handler()
			{

				@Override
				public void handleMessage(Message msg)
				{
					super.handleMessage(msg);
					if (msg.what == AppConstant.INTERACTION_STATUS.INTERACTION_SUCCESSFUL)
					{
						setListAdapter(new MyListAdapter());
						coursePicDownloader();
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

			@Override
			public void run()
			{
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("type", "all");
				courseInfos = PullParseXML.parseOnlineCourseXML(AppConstant.URL.MY_SUBCRIBE_URL, params, true);
				String result = courseInfos.get(0).getName();
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
					for (int i = 0; i < courseInfos.size(); i++)
					{
						if (courseInfos.get(i).getIfd().equals("1"))
							courseInfos.remove(i);
					}
					fetchHandler.sendEmptyMessage(AppConstant.INTERACTION_STATUS.INTERACTION_SUCCESSFUL);
				}
			}
		}).start();
	}

	private void coursePicDownloader()
	{
		// final Handler picHandler = new Handler()
		// {
		//
		// @Override
		// public void handleMessage(Message msg)
		// {
		// super.handleMessage(msg);
		// setListAdapter(new MyListAdapter());
		// }
		//
		// };
		// new Thread(new Runnable()
		// {
		//
		// @Override
		// public void run()
		// {
		// for (int i = 0; i < courseInfos.size(); i++)
		// {
		// CourseInfo currentInfo = courseInfos.get(i);
		// File picFile = new File(AppConstant.FilePath.PIC_FILE_PATH +
		// currentInfo.getPic());
		// if (!picFile.exists())
		// {
		// int downloadResult = HttpDownloader.downloadFile(
		// AppConstant.URL.NCC_NEUQ_PIC_URL + currentInfo.getPic(),
		// AppConstant.FilePath.PIC_FILE_PATH, currentInfo.getPic());
		// System.out.println(currentInfo.getName() + "ͼƬ���ؽ����" +
		// downloadResult);
		// }
		// }
		// picHandler.sendEmptyMessage(0);
		//
		// }
		// }).start();

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);

		final int finalPos = position;

		if (isOnTheMainScreen == true)
		{

			new Thread(new Runnable()
			{
				private Handler getMp3ListHander = new Handler()
				{

					@Override
					public void handleMessage(Message msg)
					{
						super.handleMessage(msg);
						if (msg.what == 1)
						{
							isOnTheMainScreen = false;
							selectedCourseItem = finalPos;
							setListAdapter(new MyListAdapter());
						}
						else
						{
							Toast.makeText(mContext, "ץȡ�б�ʧ����", Toast.LENGTH_SHORT).show();
						}
					}
				};

				@Override
				public void run()
				{
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("type", "list");
					params.put("cid", courseInfos.get(finalPos).getId());
					mp3Infos = PullParseXML.parseOnlineMp3XML(AppConstant.URL.MY_SUBCRIBE_URL, params, true);

					String result = mp3Infos.get(0).getName();
					if (result.equals(AppConstant.INTERACTION_STATUS.NETWORK_CONNECTION_EXCEPTION + "")
							|| result.equals(AppConstant.INTERACTION_STATUS.SERVER_STATUS_EXCEPTION + ""))
					{
						getMp3ListHander.sendEmptyMessage(0);
					}
					else
					{
						getMp3ListHander.sendEmptyMessage(1);
					}
				}
			}).start();

		}
		else
		{
			if (position != mp3Infos.size())
			{
				Intent intent = new Intent(mContext, OnlinePlayer.class);
				intent.putExtra("mp3Info", mp3Infos.get(position));
				intent.putExtra("mode", "online");
				startActivity(intent);
			}
			else
			{
				for (int i = 0; i < courseInfos.size(); i++)
				{
					if (courseInfos.get(i).getIfd().equals("1"))
						courseInfos.remove(i);
				}
				isOnTheMainScreen = true;
				setListAdapter(new MyListAdapter());
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fanting_dingyue_add_dingyue);

		mContext = this;

		fetchCourseXML();
	}

	private class MyListAdapter extends BaseAdapter
	{

		@Override
		public int getCount()
		{
			if (isOnTheMainScreen == true)
			{
				return courseInfos.size();
			}
			else
			{

				return mp3Infos.size() + 1;

			}
		}

		@Override
		public Object getItem(int position)
		{
			if (isOnTheMainScreen == true)
				return (position == courseInfos.size()) ? null : courseInfos.get(position);
			else
				return (position == mp3Infos.size()) ? null : mp3Infos.get(position);
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
			final View convertFinalView = getLayoutInflater().inflate(R.layout.fanting_dingyue_listitem, null);
			convertView = convertFinalView;
			final LinearLayout outerLayout = (LinearLayout) convertView
					.findViewById(R.id.fanting_dingyue_listitem_outer_linearlayout);
			final LinearLayout innerLayout = (LinearLayout) convertView
					.findViewById(R.id.fanting_dingyue_listitem_inner_linearlayout);

			CourseInfo tmpInfo = null;

			if (position != courseInfos.size() && isOnTheMainScreen)
			{
				tmpInfo = courseInfos.get(position);
			}

			final CourseInfo cInfo = tmpInfo;

			if (isOnTheMainScreen == true)
			{
				innerLayout.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
				outerLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT));
				ImageView coursePicImageView = (ImageView) convertView
						.findViewById(R.id.fanting_dingyue_listitem_outer_imageview);

				// ImageView outerImageView = (ImageView) convertView
				// .findViewById(R.id.fanting_dingyue_listitem_outer_imageview);
				TextView courseTitleTextView = (TextView) convertView
						.findViewById(R.id.fanting_dingyue_listitem_outer_title_textview);
				TextView amountTextView = (TextView) convertView
						.findViewById(R.id.fanting_tuijian_listitem_outer_amount_textview);
				// Button triangleButton = (Button) convertView
				// .findViewById(R.id.fangting_dingyue_listitem_outer_triangle_button);

				if (position != courseInfos.size())
				{

					courseTitleTextView.setText(cInfo.getName());
					amountTextView.setText("�γ�  " + cInfo.getCount());
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
					// File picFile = new
					// File(AppConstant.FilePath.PIC_FILE_PATH +
					// cInfo.getPic());
					// if (picFile.exists())
					// {
					// try
					// {
					// coursePicImageView.setBackgroundDrawable(Drawable
					// .createFromPath(AppConstant.FilePath.PIC_FILE_PATH +
					// cInfo.getPic()));
					// } catch (Exception e)
					// {
					// coursePicImageView.setBackgroundResource(R.drawable.pic_loading);
					// }
					//
					// }

				}

			}
			else
			{

				outerLayout.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
				innerLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT));
				LinearLayout addSubscribeLayout = (LinearLayout) convertFinalView
						.findViewById(R.id.fanting_dingyue_listitem_cancel_subcribe_linearlayout);
				LinearLayout introductionLayout = (LinearLayout) convertFinalView
						.findViewById(R.id.fanting_dingyue_listitem_general_instruction_linearlayout);
				TextView mp3TitleTextView = (TextView) convertFinalView
						.findViewById(R.id.fanting_dingyue_listitem_title_textview);
				// ImageView innerImageView = (ImageView) convertFinalView
				// .findViewById(R.id.fanting_dingyue_listitem_imageview);
				TextView mp3DurationTextView = (TextView) convertFinalView
						.findViewById(R.id.fanting_dingyue_listitem_duration_textview);
				TextView mp3SizeTextView = (TextView) convertFinalView
						.findViewById(R.id.fanting_dingyue_listitem_size_textview);
				TextView mp3DifficultyTextView = (TextView) convertFinalView
						.findViewById(R.id.fanting_dingyue_listitem_difficulty_textview);
				TextView courseTitleTextView = (TextView) convertFinalView
						.findViewById(R.id.fanting_dingyue_listitem_subscribe_title_textview);
				TextView courseIntrodutionTextView = (TextView) convertFinalView
						.findViewById(R.id.fanting_dingyue_listitem_subscribe_introduction_textview);
				ImageView innerMenuImageView = (ImageView) convertFinalView
						.findViewById(R.id.fanting_dingyue_listitem_menu_imageview);
				// ImageView mp3PictureImageView = (ImageView) convertFinalView
				// .findViewById(R.id.fanting_dingyue_listitem_imageview);
				final TextView statusTextView = (TextView) convertFinalView
						.findViewById(R.id.fanting_dingyue_listitem_subscrbe_status_textview);

				statusTextView.setText("��Ҫ����");

				final CourseInfo currentCourseInfo = courseInfos.get(selectedCourseItem);
				courseTitleTextView.setText(currentCourseInfo.getName());
				courseIntrodutionTextView.setText(currentCourseInfo.getIntroduction());

				addSubscribeLayout.setOnClickListener(new OnClickListener()
				{

					@Override
					public void onClick(View v)
					{
						final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
						View alertDialogView = getLayoutInflater().inflate(
								R.layout.fanting_dingyue_subscribe_alert_dialog, null);
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
										statusTextView.setText("��Ҫ����");
										currentCourseInfo.setIfd("0");
										Toast.makeText(mContext, "ȡ�����ĳɹ�", Toast.LENGTH_SHORT).show();
									}
									else
									{
										Toast.makeText(mContext, "ȡ������ʧ�ܣ���������С����", Toast.LENGTH_SHORT).show();
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
										params.put("cid", currentCourseInfo.getId());
										try
										{
											HttpURLConnection urlConnection = (HttpURLConnection) HttpRequestUtil
													.sendGetRequest(AppConstant.URL.MY_SUBCRIBE_URL, params, headers);
											responseCode = urlConnection.getResponseCode();
											inputStream = urlConnection.getInputStream();
											result = Integer.parseInt(Toolkits.convertStreamToString(inputStream));

											System.out.println("���ȡ����ӵķ���ֵ��" + result);

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

						if (currentCourseInfo.getIfd().equals("1"))
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
										statusTextView.setText("ȡ������");
										currentCourseInfo.setIfd("1");
										Toast.makeText(mContext, "�Ѷ�����", Toast.LENGTH_SHORT).show();
									}
									else
									{
										Toast.makeText(mContext, "��Ӷ���ʧ�ܣ���������С����", Toast.LENGTH_SHORT).show();
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
									params.put("cid", currentCourseInfo.getId());
									try
									{
										HttpURLConnection urlConnection = (HttpURLConnection) HttpRequestUtil
												.sendGetRequest(AppConstant.URL.MY_SUBCRIBE_URL, params, headers);
										int responseCode = urlConnection.getResponseCode();
										InputStream inputStream = urlConnection.getInputStream();
										int result = Integer.parseInt(Toolkits.convertStreamToString(inputStream));

										System.out.println("�����ӵķ���ֵ��" + result);

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

				if (finalPos != 0)
				{
					introductionLayout.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
				}

				if (finalPos == mp3Infos.size())
				{
					// innerImageView.setLayoutParams(new
					// LinearLayout.LayoutParams(0, 0));
					RelativeLayout.LayoutParams zeroLayoutParams = new RelativeLayout.LayoutParams(0, 0);
					mp3DurationTextView.setLayoutParams(zeroLayoutParams);
					mp3SizeTextView.setLayoutParams(zeroLayoutParams);
					mp3DifficultyTextView.setLayoutParams(zeroLayoutParams);
					innerMenuImageView.setLayoutParams(zeroLayoutParams);

					mp3TitleTextView.setText("���ص��ܱ�");
					RelativeLayout.LayoutParams innerLayoutParams = new RelativeLayout.LayoutParams(
							RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
					innerLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
					mp3TitleTextView.setLayoutParams(innerLayoutParams);

				}
				else
				{
					final Mp3Info mp3Info = mp3Infos.get(finalPos);
					mp3TitleTextView.setText(mp3Info.getName());
					mp3DurationTextView.setText(MillisecondConvert.convert(Integer.parseInt(mp3Info.getDuration())));
					mp3SizeTextView.setText(mp3Info.getSize());
					mp3DifficultyTextView.setText(mp3Info.getDifficulty());
					// mp3PictureImageView.setBackgroundDrawable(Drawable.createFromPath(AppConstant.FilePath.PIC_FILE_PATH
					// + mp3Info.getPic()));
					innerMenuImageView.setOnClickListener(new OnClickListener()
					{

						@Override
						public void onClick(View v)
						{
							View menuView = getLayoutInflater().inflate(R.layout.fanting_listitem_menu_collected, null);

							TextView shoucangTextView = (TextView) menuView
									.findViewById(R.id.fanting_listitem_menu_shoucang_textview);
							shoucangTextView.setText("�ղش�����");

							final PopupWindow menuWindow = new PopupWindow(menuView,
									RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

							LinearLayout downloadLayout = (LinearLayout) menuView
									.findViewById(R.id.fanting_listitem_menu_download_linearlayout);
							LinearLayout shoucangLayout = (LinearLayout) menuView
									.findViewById(R.id.fanting_listitem_menu_shoucang_linearlayout);
							LinearLayout addLinearLayout = (LinearLayout) menuView
									.findViewById(R.id.fanting_listitem_menu_add_to_jingting_linearlayout);
							ImageView menuBackgroundImageView = (ImageView) menuView.findViewById(R.id.menu_background);

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
													Toast.makeText(mContext, "�ղسɹ�", Toast.LENGTH_SHORT).show();
												}
												else
												{
													Toast.makeText(mContext, "�ղ�ʧ�ܣ���������С��߷", Toast.LENGTH_SHORT).show();
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
														.sendGetRequest(AppConstant.URL.SHOUCANG_MP3_URL, params,
																headers);
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
											Toast.makeText(mContext, "���ع��̳������ˣ���������ӵ�����", Toast.LENGTH_LONG).show();
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
																AppConstant.URL.NCC_NEUQ_MP3_URL + mp3Info.getId()
																		+ ".mp3", AppConstant.FilePath.MP3_FILE_PATH,
																mp3Info.getId() + ".mp3");
														int lrcRet = HttpDownloader.downloadFile(
																AppConstant.URL.NCC_NEUQ_LRC_URL + mp3Info.getId()
																		+ ".lrc", AppConstant.FilePath.LRC_FILE_PATH,
																mp3Info.getId() + ".lrc");
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
											result = mp3Info.getName() + " ���سɹ���";
											Diyijiemian.offlineSaver.addMp3Info(mp3Info);
										}
											break;
										case -1:
										{
											result = mp3Info.getName() + " ����ʧ��߷";
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
										Toast.makeText(mContext, "������ʼ������", Toast.LENGTH_SHORT).show();
										new Thread(new Runnable()
										{

											@Override
											public void run()
											{
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
										Toast.makeText(mContext, "�ļ��Ѿ����ع��˹�", Toast.LENGTH_SHORT).show();
									}
								}
							});

							menuWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

						}
					});
				}

			}

			return convertView;
		}
	}
}
