package com.lfl.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lfl.model.SentenceInfo;
import com.lfl.opensl.audio.OSLESMediaPlayer;
import com.lfl.utils.AppConstant;
import com.lfl.utils.PullParseXML;
import com.lfl.utils.SlideListView;
import com.lfl.utils.Toolkits;
import com.lz.utils.HttpRequestUtil;
import com.lz.utils.StaticInfos;
import com.lz.utils.Util;
import com.nut.activity.R;

@SuppressLint("HandlerLeak")
public class Juku extends Activity
{
	private List<SentenceInfo> sentenceInfos;
	private MyAdapter adapter;
	private Dialog loadingDialog;
	private OSLESMediaPlayer mediaPlayer;
	private SlideListView listView;
	private int currentPos = -1;
	private Context mContext;
	private LinearLayout blankLayout;

	private void initWidgets()
	{
		blankLayout = (LinearLayout) findViewById(R.id.juku_blank_linearlayout);
		listView = (SlideListView) findViewById(R.id.juku_listview);
		mediaPlayer = new OSLESMediaPlayer();
		adapter = new MyAdapter();

		getSentencesHashMaps();
	}

	private Comparator<SentenceInfo> comparator = new Comparator<SentenceInfo>()
	{

		@Override
		public int compare(SentenceInfo lhs, SentenceInfo rhs)
		{
			return lhs.getTime().compareTo(rhs.getTime());
		}
	};

	private void getSentencesHashMaps()
	{

		new Thread(new Runnable()
		{
			private Handler getSentenceHandler = new Handler()
			{

				@Override
				public void handleMessage(Message msg)
				{
					super.handleMessage(msg);
					if (msg.what == AppConstant.INTERACTION_STATUS.INTERACTION_SUCCESSFUL)
					{
						if (sentenceInfos.size() != 0)
						{
							blankLayout.setLayoutParams(new LinearLayout.LayoutParams(0, 0)); 
							Collections.sort(sentenceInfos, comparator);
							listView.setAdapter(adapter);
							listView.setOnItemClickListener(new OnItemClickListener()
							{

								private TextView sentenceView;

								private Handler handler;

								@SuppressLint("HandlerLeak")
								@Override
								public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3)
								{

									if (mediaPlayer.isPlaying() == false)
									{
										sentenceView = (TextView) v.findViewById(R.id.juku_listitem_sentence_textview);
										sentenceView.setTextColor(Color.parseColor("#2aa1f4"));
										final SentenceInfo currentInfo = sentenceInfos.get(position);
										handler = new Handler()
										{

											@Override
											public void handleMessage(Message msg)
											{
												super.handleMessage(msg);
												if (mediaPlayer.isPlaying())
												{
													if (mediaPlayer.getPosition() >= currentInfo.getEndPos())
													{
														mediaPlayer.stop();
														handler.removeMessages(0);
														mediaPlayer.releaseAudioPlayer();
														sentenceView.setTextColor(Color.parseColor("#000000"));
													}
													else
													{
														handler.sendEmptyMessageDelayed(0, 500);
													}
												}
											}

										};
										if (currentPos != position)
										{
											mediaPlayer.releaseAudioPlayer();
											mediaPlayer.createAudioPlayer(AppConstant.URL.NCC_NEUQ_MP3_URL
													+ currentInfo.getMp3Id() + ".mp3");
											position = currentPos;
										}
										mediaPlayer.setNoLoop();
										mediaPlayer.play();
										mediaPlayer.seekTo(currentInfo.getStartPos());
										handler.sendEmptyMessage(0);
									}
								}

							});
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

			@Override
			public void run()
			{
				HashMap<String, String> params = new HashMap<String, String>();
				sentenceInfos = PullParseXML.parseOnlineSentencesXML(AppConstant.URL.SENTENCE_LIST_URL, params, true);
				if (sentenceInfos.size() != 0)
				{
					String result = sentenceInfos.get(0).getSentence();
					if (result.equals(AppConstant.INTERACTION_STATUS.NETWORK_CONNECTION_EXCEPTION + ""))
					{
						getSentenceHandler
								.sendEmptyMessage(AppConstant.INTERACTION_STATUS.NETWORK_CONNECTION_EXCEPTION);
					}
					else if (result.equals(AppConstant.INTERACTION_STATUS.SERVER_STATUS_EXCEPTION + ""))
					{
						getSentenceHandler.sendEmptyMessage(AppConstant.INTERACTION_STATUS.SERVER_STATUS_EXCEPTION);
					}
					else
					{
						getSentenceHandler.sendEmptyMessage(AppConstant.INTERACTION_STATUS.INTERACTION_SUCCESSFUL);
					}
				}
				else
				{
					getSentenceHandler.sendEmptyMessage(AppConstant.INTERACTION_STATUS.INTERACTION_SUCCESSFUL);
				}
			}
		}).start();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.juku);

		mContext = this;

		loadingDialog = new Dialog(this, R.style.loading_dialog_style);
		loadingDialog.setContentView(R.layout.loading_dialog);
		Window loadingDialogWindow = loadingDialog.getWindow();
		WindowManager.LayoutParams lParams = loadingDialogWindow.getAttributes();
		loadingDialogWindow.setGravity(Gravity.CENTER);
		lParams.alpha = 1f;
		loadingDialogWindow.setAttributes(lParams);
		loadingDialog.setCancelable(false);
		loadingDialog.show();

		initWidgets();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		mediaPlayer.releaseAudioPlayer();
	}

	private class MyAdapter extends BaseAdapter
	{

		@Override
		public int getCount()
		{
			return sentenceInfos.size();
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
			final int finalPosition = position;
			convertView = getLayoutInflater().inflate(R.layout.juku_listitem, null);

			TextView timeTextView = (TextView) convertView.findViewById(R.id.juku_listitem_time_textview);
			final TextView sentenceTextView = (TextView) convertView.findViewById(R.id.juku_listitem_sentence_textview);
			TextView locationTextView = (TextView) convertView.findViewById(R.id.juku_listitem_position_textview);
			Button shareButton = (Button) convertView.findViewById(R.id.juku_listitem_share_button);
			Button translationButton = (Button) convertView.findViewById(R.id.juku_listitem_translation_button);
			Button deleteButton = (Button) convertView.findViewById(R.id.juku_listitem_delete_button);
			final SentenceInfo sInfo = sentenceInfos.get(position);

			sentenceTextView.setText(sInfo.getSentence());
			locationTextView.setText(sInfo.getMp3Name());

			if (position != 0)
			{
				if (sInfo.getTime().equals(sentenceInfos.get(position - 1).getTime()) == true)
				{
					timeTextView.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
				}
				else
				{
					timeTextView.setText(sInfo.getTime());
				}
			}
			else
			{
				timeTextView.setText(sInfo.getTime());
			}

			deleteButton.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					new Thread(new Runnable()
					{
						private int responseCode;
						private InputStream inputStream = null;
						private int result;
						private Handler delHandler = new Handler()
						{
							@Override
							public void handleMessage(Message msg)
							{
								super.handleMessage(msg);
								if (msg.what == 1)
								{
									sentenceInfos.remove(finalPosition);
									if (sentenceInfos.size() != 0)
									{
										listView.setAdapter(new MyAdapter());
									}
									else
									{
										blankLayout.setLayoutParams(new LinearLayout.LayoutParams(
												LinearLayout.LayoutParams.MATCH_PARENT,
												LinearLayout.LayoutParams.MATCH_PARENT));
									}
									Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
								}
								else
								{
									Toast.makeText(mContext, "删除失败，服务器开小差了", Toast.LENGTH_SHORT).show();
								}
							}
						};

						@Override
						public void run()
						{
							HashMap<String, String> headers = new HashMap<String, String>();
							HashMap<String, String> params = new HashMap<String, String>();
							headers.put("Cookie", "PHPSESSID=" + StaticInfos.phpsessid);
							params.put("type", "del");
							params.put("sid", sentenceInfos.get(finalPosition).getId());

							try
							{
								HttpURLConnection urlConnection = (HttpURLConnection) HttpRequestUtil.sendGetRequest(
										AppConstant.URL.SENTENCE_LIST_URL, params, headers);
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
								delHandler.sendEmptyMessage(0);
								e.printStackTrace();
							}
						}
					}).start();
				}
			});

			/**
			 * 属于刘哥更改的部分
			 */
			shareButton.setOnClickListener(new OnClickListener()
			{
				public void onClick(View v)
				{
					String shareSentence = sInfo.getSentence();
					String shareTrans = sInfo.getTranslation();
					String shareCourseName = sInfo.getMp3Name();
					Util.showShare(Juku.this, shareSentence + "." + shareTrans + "——" + shareCourseName + ".");
				}
			});

			translationButton.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					if (sentenceTextView.getText().equals(sInfo.getSentence()))
					{
						sentenceTextView.setText(sInfo.getTranslation());
					}
					else
					{
						sentenceTextView.setText(sInfo.getSentence());
					}
				}
			});
			return convertView;
		}

	}

}
