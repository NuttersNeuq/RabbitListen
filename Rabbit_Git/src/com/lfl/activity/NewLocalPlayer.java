package com.lfl.activity;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.lfl.model.LrcContent;
import com.lfl.model.Mp3Info;
import com.lfl.model.OnlineWordInfo;
import com.lfl.model.SentenceInfo;
import com.lfl.model.WordInfo;
import com.lfl.slidemenu.SlidingMenu;
import com.lfl.utils.AppConstant;
import com.lfl.utils.HttpDownloader;
import com.lfl.utils.MillisecondConvert;
import com.lfl.utils.OnlineDictionaryXMLParser;
import com.lfl.utils.PlayerAdapter;
import com.lfl.utils.PullParseXML;
import com.lfl.utils.Toolkits;
import com.lz.activity.CommentActivity;
import com.lz.activity.NoteListActivity;
import com.lz.activity.QuestionListActivity;
import com.lz.utils.HttpRequestUtil;
import com.lz.utils.StaticInfos;
import com.lz.utils.Util;
import com.hare.activity.R;

/**
 * 精听播放器
 * 
 * @author FIRE_TRAY
 */
@SuppressLint("HandlerLeak")
public class NewLocalPlayer extends ListActivity
{
	private final String ROUND_11 = "第一轮 第一阶段";
	private final String ROUND_12 = "第一轮 第二阶段";
	private final String ROUND_13 = "第一轮 第三阶段";
	private final String ROUND_2 = "第二轮";
	private final String ROUND_3 = "第三轮";
	private final String ROUND_4 = "第四轮";
	private final String ROUND_5 = "第五轮";
	// private final String ROUND_FINAL

	private String currentRound;

	private Mp3Info mp3Info;
	private ArrayList<LrcContent> lrcContents;
	private int lrcLangMode;
	private int currentLrcIndex;
	private int currentPos;
	private boolean isSingleRepeat;
	private List<SentenceInfo> shouchangSentenceInfos = new ArrayList<SentenceInfo>();
	private List<SentenceInfo> nanjuSentenceInfos = new ArrayList<SentenceInfo>();
	private List<WordInfo> wordInfos = new ArrayList<WordInfo>();
	private List<String> chsLrcList = new ArrayList<String>();
	private List<String> engLrcList = new ArrayList<String>();
	private List<String> bothLrcList = new ArrayList<String>();
	private List<String> nullLrcList = new ArrayList<String>();
	private int currentPlaySpeed;
	private int duration;
	private String startTime;
	private String time;
	private String endTime;
	private int studyDuration;

	private String isFinishedString = "0";

	private TextView titleTextView, currentPosTextView, durationTextView;
	private Button changLrcLangButton, playButton;
	private TextView playSpeedTextView;
	private SeekBar seekBar;
	private LinearLayout exitLayout;
	private Button singleRepeatButton;
	private Button fenxiangButton;
	private Button backwardButton;
	private Button forwardButton;
	private SlidingMenu dancibenMenu;
	private ListView dancibenListView;
	private Dialog loadingDialog;
	private TextView shengciTextView, nanjuTextView;
	private TextView dancibenTitleTextView;
	private Button dancibenButton;
	private Button addSentenceButton;
	private AlertDialog.Builder alertDialogBuilder;
	private AlertDialog fenxiangAlertDialog;
	private static Context mContext;

	private Handler sleepHandler = null;

	private boolean isShengciSelected = true;
	private boolean isPlay = false;
	private static boolean isShareSDKMode = false;
	private static boolean isNewLocalPlayerActive = false;

	public static void resumePlaying()
	{
		if (isNewLocalPlayerActive)
		{
			Intent intent = new Intent(AppConstant.Actions.PLAYER_ASK_SERVICE_TO_START);
			mContext.sendBroadcast(intent);
			isShareSDKMode = false;
		}
	}

	private BroadcastReceiver newLocalPlayerReceiver = new BroadcastReceiver()
	{

		@SuppressWarnings("unchecked")
		@Override
		public void onReceive(Context context, Intent intent)
		{
			String action = intent.getAction();
			if (action.equals(AppConstant.Actions.SERVICE_SEND_CURRENTPOS_TO_PLAYER) == false)
			{
				System.out.println("NewLocalPlayer Act: " + action);
			}
			if (action.equals(AppConstant.Actions.SERVICE_SEND_INIT_TO_PLAYER))
			{
				currentLrcIndex = intent.getIntExtra("currentLrcIndex", -1);
				isSingleRepeat = intent.getBooleanExtra("isSingleRepeat", false);
				currentPos = intent.getIntExtra("currentPos", 0);
				lrcLangMode = intent.getIntExtra("lrcLangMode", AppConstant.PlayParms.LRC_LANG_MODE_BOTH);
				currentPlaySpeed = intent.getIntExtra("currentPlaySpeed", AppConstant.PlayParms.PLAY_SPEED_1X);
				duration = intent.getIntExtra("duration", 0);
				lrcContents = (ArrayList<LrcContent>) intent.getSerializableExtra("lrcContents");

				for (int i = 0; i < lrcContents.size(); i++)
				{
					LrcContent tmp = lrcContents.get(i);
					String chsLrc = tmp.getChsLrc();
					String engLrc = tmp.getEngLrc();
					int id = i + 1;
					bothLrcList.add(engLrc + "\n" + chsLrc);
					chsLrcList.add(chsLrc);
					engLrcList.add(engLrc);
					nullLrcList.add("Sentence No. " + id);
				}

				initWidgets();

				startTime = currentPos + "";
				time = System.currentTimeMillis() + "";
				new Thread(new Runnable()
				{
					private String[] importantSens;
					private InputStream inputStream = null;
					private int responseCode = 0;
					private Handler fetchWordsAndSensHandler = new Handler()
					{
						@Override
						public void handleMessage(Message msg)
						{
							super.handleMessage(msg);
							if (msg.what == 1)
							{
								Intent startPlayIntent = new Intent(AppConstant.Actions.PLAYER_ASK_SERVICE_TO_START);
								isPlay = true;
								playButton.setBackgroundResource(R.drawable.player_play);
								sendBroadcast(startPlayIntent);

								for (int i = 0; i < importantSens.length; i++)
								{
									int nanjuPos = Integer.parseInt(importantSens[i]);
									LrcContent nanjuLrcContent = lrcContents.get(nanjuPos);
									SentenceInfo info = new SentenceInfo();
									info.setEndPos(nanjuLrcContent.getEndPos());
									info.setMp3Id(mp3Info.getId());
									info.setMp3Name(mp3Info.getName());
									info.setPosition(nanjuPos + "");
									info.setSentence(nanjuLrcContent.getEngLrc());
									info.setStartPos(nanjuLrcContent.getStartPos());
									info.setTranslation(nanjuLrcContent.getChsLrc());
									nanjuSentenceInfos.add(info);
								}
								dancibenListView.setAdapter(new MyListAdapter());

								String toastRoundString = "";

								if (currentRound.equals("11"))
								{
									toastRoundString = ROUND_11;
								}
								else if (currentRound.equals("12"))
								{
									toastRoundString = ROUND_12;
								}
								else if (currentRound.equals("13"))
								{
									toastRoundString = ROUND_13;
								}
								else if (currentRound.equals("2"))
								{
									toastRoundString = ROUND_2;
								}
								else if (currentRound.equals("3"))
								{
									toastRoundString = ROUND_3;
								}
								else if (currentRound.equals("4"))
								{
									toastRoundString = ROUND_4;
								}
								else if (currentRound.equals("5"))
								{
									toastRoundString = ROUND_5;
								}
								Toast.makeText(mContext, "开始进行 " + toastRoundString + " 的学习", Toast.LENGTH_LONG).show();

								loadingDialog.dismiss();

								fetchWordsPron();
							}
							else if (msg.what == 0)
							{
								loadingDialog.dismiss();
								Toast.makeText(mContext, "初始化失败，服务器开小差叻", Toast.LENGTH_SHORT).show();
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
						try
						{
							wordInfos = PullParseXML.parseOnlineWordsXML(AppConstant.URL.CURRENT_WORD_LIST_URL, params,
									true);
							if (wordInfos.size() != 0)
							{
								if (wordInfos.get(0).getWord()
										.equals(AppConstant.INTERACTION_STATUS.SERVER_STATUS_EXCEPTION + "")
										|| wordInfos
												.get(0)
												.getWord()
												.equals(AppConstant.INTERACTION_STATUS.NETWORK_CONNECTION_EXCEPTION
														+ ""))
								{
									fetchWordsAndSensHandler.sendEmptyMessage(0);
								}
								else
								{
									for (int i = 0; i < wordInfos.size(); i++)
									{
										WordInfo currentInfo = wordInfos.get(i);
										currentInfo.setPronunciation(OnlineDictionaryXMLParser.parser(
												currentInfo.getWord()).getPronunciation());
									}

									HttpURLConnection urlConnection = (HttpURLConnection) HttpRequestUtil
											.sendGetRequest(AppConstant.URL.DIFFICULT_SENTENCE_LIST_URL, params,
													headers);
									responseCode = urlConnection.getResponseCode();
									inputStream = urlConnection.getInputStream();
									String retString = Toolkits.convertStreamToString(inputStream);

									System.out.println("服务器返回代码：" + responseCode);

									if (responseCode != 200)
									{
										System.out.println("服务器返回代码：" + responseCode);
										fetchWordsAndSensHandler.sendEmptyMessage(0);
									}
									else
									{
										if (retString.equals(""))
										{
											importantSens = new String[0];
										}
										else
										{
											importantSens = retString.split(",");
										}

										fetchWordsAndSensHandler.sendEmptyMessage(1);
									}
								}

							}
							else
							{
								HttpURLConnection urlConnection = (HttpURLConnection) HttpRequestUtil.sendGetRequest(
										AppConstant.URL.DIFFICULT_SENTENCE_LIST_URL, params, headers);
								responseCode = urlConnection.getResponseCode();
								inputStream = urlConnection.getInputStream();
								String retString = Toolkits.convertStreamToString(inputStream);

								System.out.println("服务器返回代码：" + responseCode);

								if (responseCode != 200)
								{
									System.out.println("服务器返回代码：" + responseCode);
									fetchWordsAndSensHandler.sendEmptyMessage(0);
								}
								else
								{
									if (retString.equals(""))
									{
										importantSens = new String[0];
									}
									else
									{
										importantSens = retString.split(",");
									}

									fetchWordsAndSensHandler.sendEmptyMessage(1);
								}
							}
						} catch (Exception e)
						{
							e.printStackTrace();
							fetchWordsAndSensHandler.sendEmptyMessage(0);
						}
					}
				}).start();

			}
			else if (action.equals(AppConstant.Actions.SERVICE_SEND_CURRENTPOS_TO_PLAYER))
			{
				studyDuration += AppConstant.PlayParms.SYNC_HANDLER_DELAY_MILLS;
				currentPos = intent.getIntExtra("currentPos", 0);

				int newLrcIndex = intent.getIntExtra("currentLrcIndex", 0);

				if (currentPos >= Integer.parseInt(mp3Info.getDuration())
						- AppConstant.PlayParms.SYNC_HANDLER_DELAY_MILLS * 10)
				{
					System.out.println("更改前的轮数：" + currentRound);

					boolean isFinishAllRound = mp3Info.roundIncrease();

					System.out.println("更改后的轮数：" + mp3Info.getRound());

					/**
					 * 完成全部的学习
					 */
					if (isFinishAllRound)
					{
						isFinishedString = "1";
						Intent stopPlayIntent = new Intent(AppConstant.Actions.PLAYER_ASK_SERVER_TO_RELEASE);
						sendBroadcast(stopPlayIntent);

						AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
						View dialogView = getLayoutInflater().inflate(R.layout.jingting_remark_notification_dialog,
								null);
						builder.setView(dialogView);
						final AlertDialog alertDialog = builder.create();
						TextView titleTextView = (TextView) dialogView
								.findViewById(R.id.jingting_remark_dialog_mp3title_textview);
						Button okButton = (Button) dialogView.findViewById(R.id.jingting_remark_dialog_ok_button);
						okButton.setOnClickListener(new OnClickListener()
						{

							@Override
							public void onClick(View v)
							{
								alertDialog.dismiss();
								finish();
								Intent intent = new Intent(mContext, CommentActivity.class);
								intent.putExtra("lid", mp3Info.getId());
								startActivity(intent);
							}
						});
						titleTextView.setText(mp3Info.getName());
						alertDialog.show();
					}
					/**
					 * 今日任务完成时的情况
					 */
					else if (currentRound.charAt(0) != mp3Info.getRound().charAt(0))
					{
						System.out.println("今日任务完成");

						Intent releaseIntent = new Intent(AppConstant.Actions.PLAYER_ASK_SERVER_TO_RELEASE);
						sendBroadcast(releaseIntent);

						mp3Info.setRound(currentRound);
						isFinishedString = "1";

						AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
						View finishDialogView = getLayoutInflater()
								.inflate(R.layout.custom_alert_dialog, null);
						TextView descriptionTextView = (TextView) finishDialogView
								.findViewById(R.id.custom_alert_dialog_description_textview);

						System.out.println("当前的轮数为:" + mp3Info.getRound());

						if (currentRound.equals("11") || currentRound.equals("12") || currentRound.equals("13"))
						{
							descriptionTextView.setText("已完成 " + "第一轮" + " 的学习");
						}
						else if (currentRound.equals("2"))
						{
							descriptionTextView.setText("已完成 " + ROUND_2 + " 的学习");
						}
						else if (currentRound.equals("3"))
						{
							descriptionTextView.setText("已完成 " + ROUND_3 + " 的学习");
						}
						else if (currentRound.equals("4"))
						{
							descriptionTextView.setText("已完成 " + ROUND_4 + " 的学习");
						}

						builder.setView(finishDialogView);
						final AlertDialog finishDialog = builder.create();
						Button okButton = (Button) finishDialogView.findViewById(R.id.custom_alert_dialog_ok_button);
						okButton.setOnClickListener(new OnClickListener()
						{

							@Override
							public void onClick(View v)
							{
								finishDialog.dismiss();
								finish();
							}
						});

						finishDialog.show();
					}
					else
					{
						currentRound = mp3Info.getRound();
						if (currentRound.equals("12"))
						{
							Toast.makeText(mContext, "开始 " + ROUND_12 + " 的学习", Toast.LENGTH_SHORT).show();
						}
						else if (currentRound.equals("13"))
						{
							Toast.makeText(mContext, "开始 " + ROUND_13 + " 的学习", Toast.LENGTH_SHORT).show();
						}

						Intent roundIncIntent = new Intent(AppConstant.Actions.PLAYER_ASK_SERVER_TO_INCREASE_ROUND);
						sendBroadcast(roundIncIntent);
					}
				}

				currentPosTextView.setText(MillisecondConvert.convert(currentPos));
				seekBar.setProgress(currentPos);
				if (currentLrcIndex != newLrcIndex)
				{
					currentLrcIndex = newLrcIndex;
					setLrcLangListAndButton();
					getListView().setSelection(currentLrcIndex);
				}
			}
			else if (action.equals(AppConstant.Actions.PLAYERADAPTER_SEND_NEW_WORD))
			{
				for (int i = 0; i < wordInfos.size(); i++)
				{
					if (wordInfos.get(i).getWord().equals(intent.getStringExtra("word")))
					{
						wordInfos.remove(i);
						break;
					}
				}

				WordInfo wordInfo = new WordInfo();
				SentenceInfo sentenceInfo = new SentenceInfo();
				LrcContent selectedLrcContent = lrcContents.get(intent.getIntExtra("position", 0));
				wordInfo.setWord(intent.getStringExtra("word"));
				wordInfo.setMeaning(intent.getStringExtra("meaning"));
				wordInfo.setPronunciation(intent.getStringExtra("pron"));
				sentenceInfo.setEndPos(selectedLrcContent.getEndPos());
				sentenceInfo.setMp3Id(mp3Info.getId());
				sentenceInfo.setMp3Name(mp3Info.getName());
				sentenceInfo.setPosition(intent.getIntExtra("position", 0) + "");
				sentenceInfo.setSentence(selectedLrcContent.getEngLrc());
				sentenceInfo.setStartPos(selectedLrcContent.getStartPos());
				sentenceInfo.setTime(System.currentTimeMillis() + "");
				sentenceInfo.setTranslation(selectedLrcContent.getChsLrc());
				wordInfo.setSentenceInfo(sentenceInfo);
				wordInfos.add(wordInfo);
				dancibenListView.setAdapter(new MyListAdapter());
			}
			else if (action.equals(AppConstant.Actions.WORDBROWSER_DELETE_WORD))
			{
				wordInfos.remove(intent.getIntExtra("position", -1));
				dancibenListView.setAdapter(new MyListAdapter());
			}
			else if (action.equals(AppConstant.Actions.PLAYERADAPTER_SEND_NEW_SENTENCE))
			{

				SentenceInfo sentenceInfo = new SentenceInfo();
				LrcContent selectedLrcContent = lrcContents.get(intent.getIntExtra("position", 0));
				sentenceInfo.setEndPos(selectedLrcContent.getEndPos());
				sentenceInfo.setMp3Id(mp3Info.getId());
				sentenceInfo.setMp3Name(mp3Info.getName());
				sentenceInfo.setPosition(intent.getIntExtra("position", 0) + "");
				sentenceInfo.setSentence(selectedLrcContent.getEngLrc());
				sentenceInfo.setStartPos(selectedLrcContent.getStartPos());
				sentenceInfo.setTime(System.currentTimeMillis() + "");
				sentenceInfo.setTranslation(selectedLrcContent.getChsLrc());
				nanjuSentenceInfos.add(sentenceInfo);
				dancibenListView.setAdapter(new MyListAdapter());
			}
		}
	};

	private void initWidgets()
	{
		playButton = (Button) findViewById(R.id.player_start_pause_control_button);
		fenxiangButton = (Button) findViewById(R.id.player_fenxiang_button);
		titleTextView = (TextView) findViewById(R.id.player_title_textview);
		currentPosTextView = (TextView) findViewById(R.id.player_audio_current_position_textview);
		seekBar = (SeekBar) findViewById(R.id.player_seekbar);
		durationTextView = (TextView) findViewById(R.id.player_media_duration_textview);
		changLrcLangButton = (Button) findViewById(R.id.player_change_language_button);
		exitLayout = (LinearLayout) findViewById(R.id.player_exit_linearlayout);
		playSpeedTextView = (TextView) findViewById(R.id.player_play_speed_textview);
		singleRepeatButton = (Button) findViewById(R.id.player_single_sentence_repeat_control_button);
		forwardButton = (Button) findViewById(R.id.player_forward_control_button);
		backwardButton = (Button) findViewById(R.id.player_backward_control_button);
		dancibenButton = (Button) findViewById(R.id.player_danciben_button);

		dancibenButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (!dancibenMenu.isMenuShowing())
				{
					dancibenMenu.showMenu();
				}
				else
				{
					dancibenMenu.showContent();
				}
			}
		});

		titleTextView.setText(mp3Info.getName());
		durationTextView.setText(MillisecondConvert.convert(duration));
		seekBar.setMax(duration);
		seekBar.setProgress(currentPos);
		currentPosTextView.setText(MillisecondConvert.convert(currentPos));
		singleRepeatButton.setBackgroundResource((isSingleRepeat == true) ? R.drawable.bofang_danju_selected
				: R.drawable.bofang_danju);

		setLrcLangListAndButton();
		setSpeedView();

		fenxiangButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(AppConstant.Actions.PLAYER_ASK_SERVICE_TO_PAUSE);
				sendBroadcast(intent);

				fenxiangAlertDialog.show();

			}
		});

		changLrcLangButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				switch (lrcLangMode)
				{
				case AppConstant.PlayParms.LRC_LANG_MODE_BOTH:
					lrcLangMode = AppConstant.PlayParms.LRC_LANG_MODE_ENG;
					break;
				case AppConstant.PlayParms.LRC_LANG_MODE_ENG:
					lrcLangMode = AppConstant.PlayParms.LRC_LANG_MODE_CHS;
					break;
				case AppConstant.PlayParms.LRC_LANG_MODE_CHS:
					lrcLangMode = AppConstant.PlayParms.LRC_LANG_MODE_NULL;
					break;
				case AppConstant.PlayParms.LRC_LANG_MODE_NULL:
					lrcLangMode = AppConstant.PlayParms.LRC_LANG_MODE_BOTH;
					break;
				}
				setLrcLangListAndButton();
				Intent intent = new Intent(AppConstant.Actions.PLAYER_ASK_SERVICE_TO_SET_LRCLANG);
				intent.putExtra("lrcLangMode", lrcLangMode);
				sendBroadcast(intent);
			}

		});

		exitLayout.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				finish();
			}
		});

		forwardButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(AppConstant.Actions.PLAYER_ASK_SERVICE_TO_CHANGE_CURRENTPOS);
				intent.putExtra("newCurrentPos", lrcContents.get(currentLrcIndex + 1).getStartPos());
				sendBroadcast(intent);
			}
		});
		backwardButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(AppConstant.Actions.PLAYER_ASK_SERVICE_TO_CHANGE_CURRENTPOS);
				intent.putExtra("newCurrentPos", lrcContents.get(currentLrcIndex - 1).getStartPos());
				sendBroadcast(intent);
			}
		});

		singleRepeatButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent();
				if (isSingleRepeat == false)
				{
					intent.setAction(AppConstant.Actions.PLAYER_ASK_SERVICE_TO_SINGLEREPEAT_START);
					isSingleRepeat = true;
					singleRepeatButton.setBackgroundResource(R.drawable.bofang_danju_selected);
				}
				else
				{
					intent.setAction(AppConstant.Actions.PLAYER_ASK_SERVICE_TO_SINGLEREPEAT_STOP);
					isSingleRepeat = false;
					singleRepeatButton.setBackgroundResource(R.drawable.bofang_danju);
				}
				sendBroadcast(intent);

			}
		});

		playButton.setOnClickListener(new OnClickListener()
		{
			Intent intent = new Intent();

			@Override
			public void onClick(View v)
			{
				if (isPlay == false)
				{
					intent.setAction(AppConstant.Actions.PLAYER_ASK_SERVICE_TO_START);
					isPlay = true;

				}
				else
				{
					intent.setAction(AppConstant.Actions.PLAYER_ASK_SERVICE_TO_PAUSE);
					isPlay = false;
				}
				playButton.setBackgroundResource((isPlay == true) ? R.drawable.player_play : R.drawable.player_pause);
				sendBroadcast(intent);

			}
		});

		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{

			@Override
			public void onStopTrackingTouch(SeekBar seekBar)
			{
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar)
			{
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
			{
				Intent intent = new Intent(AppConstant.Actions.PLAYER_ASK_SERVICE_TO_CHANGE_CURRENTPOS);
				intent.putExtra("newCurrentPos", progress);
				if (fromUser)
				{
					sendBroadcast(intent);
				}
			}
		});

		playSpeedTextView.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(AppConstant.Actions.PLAYER_ASK_SERVICE_TO_SET_RATE);
				switch (currentPlaySpeed)
				{
				case AppConstant.PlayParms.PLAY_SPEED_1X:
					currentPlaySpeed = AppConstant.PlayParms.PLAY_SPEED_15X;
					break;
				case AppConstant.PlayParms.PLAY_SPEED_15X:
					currentPlaySpeed = AppConstant.PlayParms.PLAY_SPPED_2X;
					break;
				case AppConstant.PlayParms.PLAY_SPPED_2X:
					currentPlaySpeed = AppConstant.PlayParms.PLAY_SPEED_025X;
					break;
				case AppConstant.PlayParms.PLAY_SPEED_025X:
					currentPlaySpeed = AppConstant.PlayParms.PLAY_SPEED_05X;
					break;
				case AppConstant.PlayParms.PLAY_SPEED_05X:
					currentPlaySpeed = AppConstant.PlayParms.PLAY_SPEED_1X;
					break;
				}
				intent.putExtra("newPlaySpeed", currentPlaySpeed);
				sendBroadcast(intent);
				setSpeedView();
			}
		});

		dancibenMenu = new SlidingMenu(this);

		dancibenMenu.setMode(SlidingMenu.RIGHT);
		dancibenMenu.setFadeDegree(0.35f);
		dancibenMenu.setShadowDrawable(R.drawable.slidemenu_shadow_right);
		dancibenMenu.setShadowWidth(13);
		dancibenMenu.setBehindOffset(80);
		dancibenMenu.setMenu(R.layout.new_player_cehua_layout);
		dancibenMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);

		dancibenListView = (ListView) dancibenMenu.getMenu().findViewById(R.id.danciben_listview);
		shengciTextView = (TextView) dancibenMenu.getMenu().findViewById(R.id.new_player_cehua_shengci_textview);
		nanjuTextView = (TextView) dancibenMenu.getMenu().findViewById(R.id.new_player_cehua_nanju_textview);
		dancibenTitleTextView = (TextView) dancibenMenu.getMenu().findViewById(
				R.id.new_player_cehua_layout_title_textview);

		shengciTextView.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				isShengciSelected = true;
				setShengciAndNanjuTextview();
			}
		});
		nanjuTextView.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				isShengciSelected = false;
				setShengciAndNanjuTextview();
			}
		});
		dancibenListView.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
			{
				if (isShengciSelected)
				{
					Intent pauseIntent = new Intent(AppConstant.Actions.PLAYER_ASK_SERVICE_TO_PAUSE);
					sendBroadcast(pauseIntent);

					Intent startBrowserIntent = new Intent(NewLocalPlayer.this, WordsBrowser.class);
					startBrowserIntent.putExtra("wordInfos", (Serializable) wordInfos);
					startBrowserIntent.putExtra("position", position);
					startActivity(startBrowserIntent);
				}
				else
				{
					Intent intent = new Intent(AppConstant.Actions.PLAYER_ASK_SERVICE_TO_CHANGE_CURRENTPOS);
					intent.putExtra("newCurrentPos", nanjuSentenceInfos.get(position).getStartPos());
					sendBroadcast(intent);
				}
			}

		});
		dancibenListView.setAdapter(new MyListAdapter());

	}

	private void setSpeedView()
	{
		switch (currentPlaySpeed)
		{
		case AppConstant.PlayParms.PLAY_SPEED_025X:
			playSpeedTextView.setText("0.25x");
			break;
		case AppConstant.PlayParms.PLAY_SPEED_05X:
			playSpeedTextView.setText("0.5x");
			break;
		case AppConstant.PlayParms.PLAY_SPEED_1X:
			playSpeedTextView.setText("1x");
			break;
		case AppConstant.PlayParms.PLAY_SPEED_15X:
			playSpeedTextView.setText("1.5x");
			break;
		case AppConstant.PlayParms.PLAY_SPPED_2X:
			playSpeedTextView.setText("2x");
			break;
		}
	}

	private void setLrcLangListAndButton()
	{
		PlayerAdapter adapter = null;
		switch (lrcLangMode)
		{
		case AppConstant.PlayParms.LRC_LANG_MODE_ENG:
			adapter = new PlayerAdapter(this, engLrcList, R.layout.player_listitem_linearlayout, currentLrcIndex);
			changLrcLangButton.setText("英");
			break;

		case AppConstant.PlayParms.LRC_LANG_MODE_CHS:
			adapter = new PlayerAdapter(this, chsLrcList, R.layout.player_listitem_linearlayout, currentLrcIndex);
			changLrcLangButton.setText("中");
			break;
		case AppConstant.PlayParms.LRC_LANG_MODE_NULL:
			adapter = new PlayerAdapter(this, nullLrcList, R.layout.player_listitem_linearlayout, currentLrcIndex);
			lrcLangMode = AppConstant.PlayParms.LRC_LANG_MODE_NULL;
			changLrcLangButton.setText("无");
			break;
		case AppConstant.PlayParms.LRC_LANG_MODE_BOTH:
			adapter = new PlayerAdapter(this, bothLrcList, R.layout.player_listitem_linearlayout, currentLrcIndex);
			changLrcLangButton.setText("双");
			break;
		}
		setListAdapter(adapter);
	}

	private void setShengciAndNanjuTextview()
	{
		if (isShengciSelected)
		{
			shengciTextView.setTextColor(Color.parseColor("#00bad2"));
			nanjuTextView.setTextColor(Color.parseColor("#c75f5f5f"));
			dancibenTitleTextView.setText("VOCABULARY");
		}
		else
		{
			shengciTextView.setTextColor(Color.parseColor("#c75f5f5f"));
			nanjuTextView.setTextColor(Color.parseColor("#00bad2"));
			dancibenTitleTextView.setText("SENTENCE");
		}
		dancibenListView.setAdapter(new MyListAdapter());
	}

	private void setFenxiangAlertDialog()
	{
		alertDialogBuilder = new AlertDialog.Builder(NewLocalPlayer.this);
		LayoutInflater inflater = LayoutInflater.from(NewLocalPlayer.this);
		View fenxiangView = inflater.inflate(R.layout.player_fenxiang_dialog, null);
		alertDialogBuilder.setView(fenxiangView);
		fenxiangAlertDialog = alertDialogBuilder.create();

		fenxiangAlertDialog.setOnDismissListener(new OnDismissListener()
		{

			@Override
			public void onDismiss(DialogInterface dialog)
			{
				if (isShareSDKMode == false)
				{
					Intent intent = new Intent(AppConstant.Actions.PLAYER_ASK_SERVICE_TO_START);
					sendBroadcast(intent);
				}
			}
		});

		// 设置监听器
		Button exitButton = (Button) fenxiangView.findViewById(R.id.player_fenxiang_exit_button);
		ImageView playerFenxiangBiji = (ImageView) fenxiangView.findViewById(R.id.player_fenxiang_biji_imageview);
		ImageView playerFenxiangWenda = (ImageView) fenxiangView.findViewById(R.id.player_fenxiang_wenda_imageview);
		ImageView playerFenxiangPingjia = (ImageView) fenxiangView.findViewById(R.id.player_fenxiang_pingjia_imageview);
		ImageView playerFenxiangFenxiangjuzi = (ImageView) fenxiangView
				.findViewById(R.id.player_fenxiang_fenxiangjuzi_imageview);
		ImageView PlayerFenxiangFenxiangKecheng = (ImageView) fenxiangView
				.findViewById(R.id.player_fenxiang_fenxiangkecheng_imageview);
		ImageView playerShouchangJuziImageView = (ImageView) fenxiangView
				.findViewById(R.id.player_fenxiang_shoucangjuzi_imageview);

		playerShouchangJuziImageView.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				SentenceInfo sentenceInfo = new SentenceInfo();
				LrcContent selectedLrcContent = lrcContents.get(currentLrcIndex);
				sentenceInfo.setEndPos(selectedLrcContent.getEndPos());
				sentenceInfo.setMp3Id(mp3Info.getId());
				sentenceInfo.setMp3Name(mp3Info.getName());
				sentenceInfo.setPosition(currentLrcIndex + "");
				sentenceInfo.setSentence(selectedLrcContent.getEngLrc());
				sentenceInfo.setStartPos(selectedLrcContent.getStartPos());
				sentenceInfo.setTime(System.currentTimeMillis() + "");
				sentenceInfo.setTranslation(selectedLrcContent.getChsLrc());
				shouchangSentenceInfos.add(sentenceInfo);

				View toastView = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
						R.layout.shouchangchenggou_toast, null);
				Toast toast = new Toast(mContext);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.setView(toastView);
				toast.show();

			}
		});

		playerFenxiangBiji.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				Intent intent = new Intent(NewLocalPlayer.this, NoteListActivity.class);
				intent.putExtra("lid", mp3Info.getId());
				startActivity(intent);
			}
		});

		playerFenxiangWenda.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				Intent intent = new Intent(NewLocalPlayer.this, QuestionListActivity.class);
				intent.putExtra("lid", mp3Info.getId());
				startActivity(intent);
			}
		});

		playerFenxiangPingjia.setOnClickListener(new OnClickListener()
		{

			public void onClick(View v)
			{
				Intent intent = new Intent(NewLocalPlayer.this, CommentActivity.class);
				intent.putExtra("lid", mp3Info.getId());
				startActivity(intent);
			}
		});

		playerFenxiangFenxiangjuzi.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				LrcContent currentLrcContent = lrcContents.get(currentLrcIndex);
				Util.showShare(NewLocalPlayer.this, "今天我在坚果听力上看到这句话，说的太好了！！  ——" + currentLrcContent.getEngLrc() + "("
						+ currentLrcContent.getChsLrc() + ")");
				fenxiangAlertDialog.dismiss();
				isShareSDKMode = true;
			}
		});

		PlayerFenxiangFenxiangKecheng.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				Util.showShare(NewLocalPlayer.this, "今天我在坚果听力上听了这边文章，顿时感觉神清气爽！ ——" + mp3Info.getName());
				fenxiangAlertDialog.dismiss();
				isShareSDKMode = true;
			}
		});
		exitButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				fenxiangAlertDialog.dismiss();
			}
		});
	}

	private Handler fileCheckHandler = new Handler()
	{

		@SuppressWarnings("deprecation")
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			if (msg.what == -1)
			{
				loadingDialog.dismiss();
				Toast.makeText(mContext, "缓存失败，服务器貌似开小差叻", Toast.LENGTH_SHORT).show();
			}
			else
			{
				Diyijiemian.offlineSaver.addMp3Info(mp3Info);

				isNewLocalPlayerActive = true;

				setFenxiangAlertDialog();

				addSentenceButton = (Button) findViewById(R.id.player_download_button);
				addSentenceButton.setBackgroundDrawable(null);
				addSentenceButton.setText("难");
				addSentenceButton.setTextColor(Color.WHITE);
				addSentenceButton.setTextSize(20);
				addSentenceButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT));
				addSentenceButton.setOnClickListener(new OnClickListener()
				{

					@Override
					public void onClick(View v)
					{
						Intent intent = new Intent(AppConstant.Actions.PLAYERADAPTER_SEND_NEW_SENTENCE);
						intent.putExtra("position", currentLrcIndex);
						sendBroadcast(intent);
						Toast.makeText(NewLocalPlayer.this, "难句添加成功", Toast.LENGTH_SHORT).show();
					}
				});

				// Intent intent = new
				// Intent(AppConstant.Actions.SEND_MP3INFO_TO_SERVICE);
				// intent.putExtra("mp3Info", (Mp3Info)
				// intent.getSerializableExtra("mp3Info"));
				// sendBroadcast(intent);

				IntentFilter filter = new IntentFilter();
				filter.addAction(AppConstant.Actions.SERVICE_SEND_INIT_TO_PLAYER);
				filter.addAction(AppConstant.Actions.SERVICE_SEND_CURRENTPOS_TO_PLAYER);
				filter.addAction(AppConstant.Actions.PLAYERADAPTER_SEND_NEW_WORD);
				filter.addAction(AppConstant.Actions.PLAYERADAPTER_SEND_NEW_SENTENCE);
				filter.addAction(AppConstant.Actions.WORDBROWSER_DELETE_WORD);
				registerReceiver(newLocalPlayerReceiver, filter);

				sleepHandler = new Handler()
				{

					@Override
					public void handleMessage(Message msg)
					{
						super.handleMessage(msg);
						Intent intent = new Intent(AppConstant.Actions.PLAYER_GET_INIT_FROM_SERVICE);
						sendBroadcast(intent);
						sleepHandler.removeMessages(0);
					}

				};
				sleepHandler.sendEmptyMessageDelayed(0, AppConstant.PlayParms.SYNC_HANDLER_DELAY_MILLS);

			}
		}
	};

	private void fetchWordsPron()
	{
		final Handler fetchHandler = new Handler()
		{

			@Override
			public void handleMessage(Message msg)
			{
				super.handleMessage(msg);
				dancibenListView.setAdapter(new MyListAdapter());
			}

		};

		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				for (int i = 0; i < wordInfos.size(); i++)
				{
					WordInfo currentInfo = wordInfos.get(i);
					OnlineWordInfo onlineWordInfo = OnlineDictionaryXMLParser.parser(currentInfo.getWord());
					currentInfo.setPronunciation(onlineWordInfo.getPronunciation());
					currentInfo.setMeaning(onlineWordInfo.getTranslation());
				}
				fetchHandler.sendEmptyMessage(0);
			}
		}).start();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player);

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

		mp3Info = (Mp3Info) getIntent().getSerializableExtra("mp3Info");

		currentRound = mp3Info.getRound();

		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				File mp3File = new File(AppConstant.FilePath.MP3_FILE_PATH + mp3Info.getId() + ".mp3");
				File lrcFile = new File(AppConstant.FilePath.LRC_FILE_PATH + mp3Info.getId() + ".lrc");
				int mp3DownloadResult = 0;
				int lrcDownloadResult = 0;

				if (mp3File.exists() == false)
				{
					mp3DownloadResult = HttpDownloader.downloadFile(AppConstant.URL.NCC_NEUQ_MP3_URL + mp3Info.getId()
							+ ".mp3", AppConstant.FilePath.MP3_FILE_PATH, mp3Info.getId() + ".mp3");
				}
				if (lrcFile.exists() == false)
				{
					lrcDownloadResult = HttpDownloader.downloadFile(AppConstant.URL.NCC_NEUQ_LRC_URL + mp3Info.getId()
							+ ".lrc", AppConstant.FilePath.LRC_FILE_PATH, mp3Info.getId() + ".lrc");
				}

				if (mp3DownloadResult == -1 || lrcDownloadResult == -1)
				{
					fileCheckHandler.sendEmptyMessage(-1);
				}
				else
				{
					fileCheckHandler.sendEmptyMessage(1);
				}
			}
		}).start();

	}

	@Override
	protected void onRestart()
	{
		super.onRestart();
		fenxiangAlertDialog.dismiss();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		endTime = currentPos + "";

		new Thread(new Runnable()
		{
			private InputStream inputStream = null;
			private int responseCode = 0;
			private int result;
			private Handler sendHandler = new Handler()
			{

				@Override
				public void handleMessage(Message msg)
				{
					super.handleMessage(msg);
					if (msg.what == 1)
					{
						System.out.println("进度已保存");
						// Toast.makeText(mContext, "进度已保存",
						// Toast.LENGTH_SHORT).show();
					}
					else if (msg.what == 0)
					{
						Toast.makeText(mContext, "进度无法保存，服务器开小差了又", Toast.LENGTH_SHORT).show();
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
				params.put("starttime", startTime);
				params.put("time", time);
				params.put("endtime", endTime);
				params.put("duration", studyDuration + "");
				params.put("round", currentRound);
				params.put("iffinish", isFinishedString);

				String shoucangString = "";
				for (int i = 0; i < shouchangSentenceInfos.size(); i++)
				{
					SentenceInfo currentInfo = shouchangSentenceInfos.get(i);
					shoucangString += currentInfo.getSentence() + "@]" + currentInfo.getTranslation() + "@]"
							+ currentInfo.getPosition() + "@]" + currentInfo.getTime() + "@]"
							+ currentInfo.getStartPos() + "@]" + currentInfo.getEndPos();
					if (i != shouchangSentenceInfos.size() - 1)
						shoucangString += "#]";
				}
				String importantString = "";
				for (int i = 0; i < nanjuSentenceInfos.size(); i++)
				{
					importantString += nanjuSentenceInfos.get(i).getPosition();
					if (i != nanjuSentenceInfos.size() - 1)
						importantString += ",";
				}
				String wordString = "";
				for (int i = 0; i < wordInfos.size(); i++)
				{
					WordInfo currentInfo = wordInfos.get(i);
					wordString += currentInfo.getWord() + "@]" + currentInfo.getSentenceInfo().getSentence() + "@]"
							+ currentInfo.getSentenceInfo().getTranslation() + "@]"
							+ currentInfo.getSentenceInfo().getPosition() + "@]"
							+ currentInfo.getSentenceInfo().getTime() + "@]"
							+ currentInfo.getSentenceInfo().getStartPos() + "@]"
							+ currentInfo.getSentenceInfo().getEndPos();
					if (i != wordInfos.size() - 1)
					{
						wordString += "#]";
					}
				}
				params.put("ss", shoucangString);
				params.put("important", importantString);
				params.put("word", wordString);

				System.out.println("返回服务器的进度信息：" + params);

				try
				{
					HttpURLConnection urlConnection = (HttpURLConnection) HttpRequestUtil.sendPostRequest(
							AppConstant.URL.JINGTING_LISTENHISTORY_URL, params, headers);
					responseCode = urlConnection.getResponseCode();
					inputStream = urlConnection.getInputStream();
					result = Integer.parseInt(Toolkits.convertStreamToString(inputStream));
					if (responseCode != 200 || result == 0)
					{
						sendHandler.sendEmptyMessage(0);
					}
					else
					{
						sendHandler.sendEmptyMessage(1);
					}
				} catch (Exception e)
				{
					e.printStackTrace();
					sendHandler.sendEmptyMessage(0);
				}

			}
		}).start();

		Intent intent = new Intent(AppConstant.Actions.LOCALPLAYER_SHUTDOWN);
		sendBroadcast(intent);
		unregisterReceiver(newLocalPlayerReceiver);

		isNewLocalPlayerActive = false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{

		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			Intent intent = new Intent(AppConstant.Actions.LOCALPLAYER_SHUTDOWN);
			sendBroadcast(intent);
		}
		return super.onKeyDown(keyCode, event);
	}

	private class MyListAdapter extends BaseAdapter
	{

		@Override
		public int getCount()
		{
			return isShengciSelected ? wordInfos.size() : nanjuSentenceInfos.size();
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
			final int finalPos = position;
			convertView = getLayoutInflater().inflate(R.layout.danciben_listitem_with_slidemenu, null);
			TextView contentTextView = (TextView) convertView.findViewById(R.id.danciben_listitem_word_textview);
			TextView descriptionTextView = (TextView) convertView
					.findViewById(R.id.danciben_listitem_description_textview);
			ImageView deleteButton = (ImageView) convertView.findViewById(R.id.danciben_listitem_delete_imageview);
			deleteButton.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					if (isShengciSelected)
					{
						Intent intent = new Intent(AppConstant.Actions.PLAYER_ASK_SERVICE_TO_DELETE_WORD);
						intent.putExtra("position", finalPos);
						sendBroadcast(intent);

						Toast.makeText(NewLocalPlayer.this, "成功删除 " + wordInfos.get(finalPos).getWord(),
								Toast.LENGTH_SHORT).show();

						wordInfos.remove(finalPos);
					}
					else
					{
						Intent intent = new Intent(AppConstant.Actions.PLAYER_ASK_SERVICE_TO_DELETE_SENTENCE);
						intent.putExtra("position", finalPos);
						sendBroadcast(intent);

						Toast.makeText(NewLocalPlayer.this, "成功删除 " + nanjuSentenceInfos.get(finalPos).getSentence(),
								Toast.LENGTH_SHORT).show();

						nanjuSentenceInfos.remove(finalPos);
					}
					dancibenListView.setAdapter(new MyListAdapter());
				}
			});

			if (isShengciSelected)
			{
				WordInfo wordInfo = wordInfos.get(position);
				contentTextView.setGravity(Gravity.CENTER);
				descriptionTextView.setGravity(Gravity.CENTER);
				contentTextView.setText(wordInfo.getWord());
				descriptionTextView.setText(wordInfo.getMeaning());
			}
			else
			{
				SentenceInfo sentenceInfo = nanjuSentenceInfos.get(position);
				contentTextView.setGravity(Gravity.LEFT);
				descriptionTextView.setGravity(Gravity.LEFT);
				contentTextView.setText(sentenceInfo.getSentence());

				descriptionTextView.setText(sentenceInfo.getTranslation());
			}
			return convertView;
		}

	}
}
