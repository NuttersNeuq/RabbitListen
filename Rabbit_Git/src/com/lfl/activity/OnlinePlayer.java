package com.lfl.activity;

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
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
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
import com.lfl.opensl.audio.OSLESMediaPlayer;
import com.lfl.slidemenu.SlidingMenu;
import com.lfl.utils.AppConstant;
import com.lfl.utils.HttpDownloader;
import com.lfl.utils.LrcProcess;
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

@SuppressLint("HandlerLeak")
public class OnlinePlayer extends ListActivity
{

	private OSLESMediaPlayer mediaPlayer = new OSLESMediaPlayer();
	private List<LrcContent> lrcContents;
	private List<String> chsLrcList = new ArrayList<String>();
	private List<String> engLrcList = new ArrayList<String>();
	private List<String> bothLrcList = new ArrayList<String>();
	private List<String> nullLrcList = new ArrayList<String>();
	private List<SentenceInfo> shouchangSentenceInfos = new ArrayList<SentenceInfo>();
	private List<SentenceInfo> nanjuSentenceInfos = new ArrayList<SentenceInfo>();
	private TextView titleTextView, currentPosTextView, durationTextView;
	private Button changLrcLangButton, playButton;
	private TextView playSpeedTextView;
	private SeekBar seekBar;
	private Button singleRepeatButton, fenxiangButton;
	private LinearLayout exitLayout;
	private Mp3Info mp3Info;
	private Button nextSentenceButton, previousSentenceButton;
	private Dialog loadingDialog;
	private List<WordInfo> wordInfos = new ArrayList<WordInfo>();

	private SlidingMenu dancibenMenu;
	private Button dancibenButton;
	private ListView dancibenListView;
	private TextView shengciTextView, nanjuTextView;
	private TextView dancibenTitleTextView;
	private boolean isOnlineMode = false;

	private String time;
	private int studyDuration;

	private Button downloadButton;
	private boolean isSingleRepeat = false;
	private int lrcRepeatIndex = -1;
	private int currentLrcLangMode = AppConstant.PlayParms.LRC_LANG_MODE_BOTH;
	private int currentLrcIndex = 0;
	private int duration = -1;
	private int currentPos = -1;
	private boolean isShengciSelected = true;

	private AlertDialog.Builder alertDialogBuilder;
	private AlertDialog fenxiangAlertDialog;
	private static boolean isShareSDKMode = false;
	private static Context mContext;
	private static boolean isOnlinePlayerActive = false;

	private BroadcastReceiver receiver = new BroadcastReceiver()
	{

		@Override
		public void onReceive(Context context, Intent intent)
		{
			String action = intent.getAction();
			if (action.equals(AppConstant.Actions.PLAYERADAPTER_SEND_NEW_LRCINDEX))
			{
				mediaPlayer.seekTo(lrcContents.get(intent.getIntExtra("newLrcIndex", currentLrcIndex)).getStartPos());
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

				LrcContent mLrcContent = lrcContents.get(intent.getIntExtra("position", 0));
				SentenceInfo mSentenceInfo = new SentenceInfo();
				WordInfo mWordInfo = new WordInfo();
				mWordInfo.setWord(intent.getStringExtra("word"));
				mWordInfo.setMeaning(intent.getStringExtra("meaning"));
				mWordInfo.setPronunciation(intent.getStringExtra("pron"));
				mSentenceInfo.setEndPos(mLrcContent.getEndPos());
				mSentenceInfo.setMp3Name(mp3Info.getName());
				mSentenceInfo.setPosition(intent.getIntExtra("position", 0) + "");
				mSentenceInfo.setSentence(mLrcContent.getEngLrc());
				mSentenceInfo.setStartPos(mLrcContent.getStartPos());
				mSentenceInfo.setTime(System.currentTimeMillis() + "");
				mSentenceInfo.setTranslation(mLrcContent.getChsLrc());
				mWordInfo.setSentenceInfo(mSentenceInfo);

				wordInfos.add(mWordInfo);
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
			else if (action.equals(AppConstant.Actions.PLAYERADAPTER_ASK_PLAYER_TO_PAUSE))
			{
				mediaPlayer.pause();
				syncHandler.removeCallbacks(syncRunnable);
			}
			else if (action.equals(AppConstant.Actions.WORDBROWSER_ASK_TO_RESUME))
			{
				mediaPlayer.play();
				mediaPlayer.seekTo(currentPos);
				syncHandler.post(syncRunnable);
			}
			else if (action.equals(AppConstant.Actions.PLAYERADAPTER_ASK_PLAYER_TO_RESUME)
					|| action.equals(AppConstant.Actions.SHARESDK_ASK_ONLINEPLAYER_TO_RESUME))
			{
				mediaPlayer.play();
				syncHandler.post(syncRunnable);
			}
			else if (action.equals(AppConstant.Actions.WORDBROWSER_DELETE_WORD))
			{
				int delIndex = intent.getIntExtra("position", -1);
				wordInfos.remove(delIndex);
				dancibenListView.setAdapter(new MyListAdapter());
			}
		}
	};

	private Thread internetThread = new Thread(new Runnable()
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
					setLrcLists();
					duration = Integer.parseInt(mp3Info.getDuration());
					setLrcHandler.sendEmptyMessage(0);
					mediaPlayer.play();
					syncHandler.post(syncRunnable);
					time = System.currentTimeMillis() + "";

					mediaPlayer.play();
					playButton.setBackgroundResource(R.drawable.player_play);

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

			try
			{
				/**
				 * 自定义超时为15 secs
				 */
				netExceptionHandler.sendEmptyMessageDelayed(0, 15000);
				if (isOnlineMode)
				{
					lrcContents = new LrcProcess(AppConstant.URL.NCC_NEUQ_LRC_URL + mp3Info.getId() + ".lrc", true)
							.getLrcList();
					mediaPlayer.createAudioPlayer(AppConstant.URL.NCC_NEUQ_MP3_URL + mp3Info.getId());
				}
				else
				{
					lrcContents = new LrcProcess(AppConstant.FilePath.LRC_FILE_PATH + mp3Info.getId() + ".lrc", false)
							.getLrcList();
					mediaPlayer.createAudioPlayer(AppConstant.FilePath.MP3_FILE_PATH + mp3Info.getId() + ".mp3");
				}

				HashMap<String, String> headers = new HashMap<String, String>();
				HashMap<String, String> params = new HashMap<String, String>();
				headers.put("Cookie", "PHPSESSID=" + StaticInfos.phpsessid);
				params.put("lid", mp3Info.getId());
				try
				{
					// HttpURLConnection urlConnection = (HttpURLConnection)
					// HttpRequestUtil.sendGetRequest(
					// AppConstant.URL.CURRENT_WORD_LIST_URL, params, headers);
					// responseCode = urlConnection.getResponseCode();
					// inputStream = urlConnection.getInputStream();
					// result =
					// Integer.parseInt(Toolkits.convertStreamToString(inputStream));
					wordInfos = PullParseXML.parseOnlineWordsXML(AppConstant.URL.CURRENT_WORD_LIST_URL, params, true);

					if (wordInfos.size() != 0)
					{
						if (wordInfos.get(0).getWord()
								.equals(AppConstant.INTERACTION_STATUS.SERVER_STATUS_EXCEPTION + "")
								|| wordInfos.get(0).getWord()
										.equals(AppConstant.INTERACTION_STATUS.NETWORK_CONNECTION_EXCEPTION + ""))
						{
							fetchWordsAndSensHandler.sendEmptyMessage(0);
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
			} catch (Exception e)
			{
				netExceptionHandler.sendEmptyMessage(0);
				e.printStackTrace();
			}

		}
	});

	private Handler netExceptionHandler = new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			if (duration == -1)
				Toast.makeText(mContext, "网络似乎开小差了", Toast.LENGTH_LONG).show();
			loadingDialog.dismiss();
		}

	};

	private Handler setLrcHandler = new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			setListAdapter(new PlayerAdapter(mContext, bothLrcList, R.layout.player_listitem_linearlayout, 0));
			seekBar.setMax(duration);
			durationTextView.setText(MillisecondConvert.convert(duration));
			lrcContents.get(lrcContents.size() - 1).setEndPos(duration);
		}

	};

	private Handler syncHandler = new Handler();
	private Runnable syncRunnable = new Runnable()
	{

		@Override
		public void run()
		{
			studyDuration += AppConstant.PlayParms.SYNC_HANDLER_DELAY_MILLS;
			int posInt = mediaPlayer.getPosition();
			if (currentLrcIndex != getCurrentLrcIndex())
			{
				currentLrcIndex = getCurrentLrcIndex();
				setLrcListAndButton();
				setSelection(currentLrcIndex);
			}
			currentPos = posInt;
			String posString = MillisecondConvert.convert(posInt);
			seekBar.setProgress(posInt);
			currentPosTextView.setText(posString);

			if (isSingleRepeat && lrcRepeatIndex != currentLrcIndex)
				mediaPlayer.seekTo(lrcContents.get(lrcRepeatIndex).getStartPos());

			syncHandler.postDelayed(syncRunnable, AppConstant.PlayParms.SYNC_HANDLER_DELAY_MILLS);
		}
	};

	public static void resumePlaying()
	{
		if (isOnlinePlayerActive)
		{
			Intent intent = new Intent(AppConstant.Actions.SHARESDK_ASK_ONLINEPLAYER_TO_RESUME);
			mContext.sendBroadcast(intent);
			isShareSDKMode = false;
		}

	}

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

		mContext = OnlinePlayer.this;
		isOnlinePlayerActive = true;

		setFenxiangAlertDialog();

		IntentFilter filter = new IntentFilter();
		filter.addAction(AppConstant.Actions.PLAYERADAPTER_SEND_NEW_LRCINDEX);
		filter.addAction(AppConstant.Actions.PLAYERADAPTER_SEND_NEW_WORD);
		filter.addAction(AppConstant.Actions.PLAYERADAPTER_ASK_PLAYER_TO_PAUSE);
		filter.addAction(AppConstant.Actions.PLAYERADAPTER_ASK_PLAYER_TO_RESUME);
		filter.addAction(AppConstant.Actions.PLAYERADAPTER_SEND_NEW_SENTENCE);
		filter.addAction(AppConstant.Actions.WORDBROWSER_ASK_TO_RESUME);
		filter.addAction(AppConstant.Actions.SHARESDK_ASK_ONLINEPLAYER_TO_RESUME);
		filter.addAction(AppConstant.Actions.WORDBROWSER_DELETE_WORD);
		registerReceiver(receiver, filter);

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

		downloadButton = (Button) findViewById(R.id.player_download_button);
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
		nextSentenceButton = (Button) findViewById(R.id.player_forward_control_button);
		previousSentenceButton = (Button) findViewById(R.id.player_backward_control_button);
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
		playButton.setBackgroundResource(R.drawable.player_play);

		downloadButton.setOnClickListener(new OnClickListener()
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
						Diyijiemian.offlineSaver.setLogDate(Toolkits.getCurrrentMoment());
					}
						break;
					case -1:
					{
						result = mp3Info.getName() + " 下载失败叻";
					}
						break;
					}
					Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
				}

			};

			@Override
			public void onClick(View v)
			{
				if (Diyijiemian.offlineSaver.isMp3InfoLoaded(mp3Info) == false)
				{
					Toast.makeText(mContext, "听力开始下载啦", Toast.LENGTH_SHORT).show();
					new Thread(new Runnable()
					{

						@Override
						public void run()
						{
							int mp3Ret = HttpDownloader.downloadFile(AppConstant.URL.NCC_NEUQ_MP3_URL + mp3Info.getId()
									+ ".mp3", AppConstant.FilePath.MP3_FILE_PATH, mp3Info.getId() + ".mp3");
							int lrcRet = HttpDownloader.downloadFile(AppConstant.URL.NCC_NEUQ_LRC_URL + mp3Info.getId()
									+ ".lrc", AppConstant.FilePath.LRC_FILE_PATH, mp3Info.getId() + ".lrc");
							int picRet = HttpDownloader.downloadFile(
									AppConstant.URL.NCC_NEUQ_PIC_URL + mp3Info.getPic(),
									AppConstant.FilePath.PIC_FILE_PATH, mp3Info.getPic());

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
				if (fromUser)
				{
					mediaPlayer.seekTo(progress);
				}
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

		changLrcLangButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				switch (currentLrcLangMode)
				{
				case AppConstant.PlayParms.LRC_LANG_MODE_BOTH:
					currentLrcLangMode = AppConstant.PlayParms.LRC_LANG_MODE_ENG;
					break;

				case AppConstant.PlayParms.LRC_LANG_MODE_ENG:
					currentLrcLangMode = AppConstant.PlayParms.LRC_LANG_MODE_CHS;
					break;
				case AppConstant.PlayParms.LRC_LANG_MODE_CHS:
					currentLrcLangMode = AppConstant.PlayParms.LRC_LANG_MODE_NULL;
					break;
				case AppConstant.PlayParms.LRC_LANG_MODE_NULL:
					currentLrcLangMode = AppConstant.PlayParms.LRC_LANG_MODE_BOTH;
					break;
				}
				setLrcListAndButton();
			}
		});

		playButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (mediaPlayer.isPlaying())
				{
					mediaPlayer.pause();
					playButton.setBackgroundResource(R.drawable.player_pause);
					syncHandler.removeCallbacks(syncRunnable);
				}
				else
				{
					mediaPlayer.play();
					playButton.setBackgroundResource(R.drawable.player_play);
					syncHandler.post(syncRunnable);
				}
			}
		});

		previousSentenceButton.setOnTouchListener(new OnTouchListener()
		{

			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if (event.getAction() == MotionEvent.ACTION_DOWN)
				{
					previousSentenceButton.setBackgroundResource(R.drawable.play_playbuttonqian_selected);
				}
				else if (event.getAction() == MotionEvent.ACTION_UP)
				{
					previousSentenceButton.setBackgroundResource(R.drawable.play_playbuttonqian);
					if (currentLrcIndex >= 0)
					{
						if (currentLrcIndex == 0)
						{
							mediaPlayer.seekTo(lrcContents.get(lrcContents.size() - 1).getStartPos());
						}
						else
						{
							mediaPlayer.seekTo(lrcContents.get(currentLrcIndex - 1).getStartPos());
						}
					}
				}
				return false;
			}
		});

		nextSentenceButton.setOnTouchListener(new OnTouchListener()
		{

			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if (event.getAction() == MotionEvent.ACTION_DOWN)
				{
					nextSentenceButton.setBackgroundResource(R.drawable.play_playbuttonhou_selected);
				}
				else if (event.getAction() == MotionEvent.ACTION_UP)
				{
					nextSentenceButton.setBackgroundResource(R.drawable.play_playbuttonhou);
					if (currentLrcIndex >= 0)
					{
						if (currentLrcIndex == lrcContents.size() - 1)
						{
							mediaPlayer.seekTo(0);
						}
						else
						{
							mediaPlayer.seekTo(lrcContents.get(currentLrcIndex + 1).getStartPos());
						}
					}
				}
				return false;
			}
		});

		singleRepeatButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (!isSingleRepeat)
				{
					singleRepeatButton.setBackgroundResource(R.drawable.bofang_danju_selected);
					isSingleRepeat = true;
					lrcRepeatIndex = getCurrentLrcIndex();
				}
				else
				{
					singleRepeatButton.setBackgroundResource(R.drawable.bofang_danju);
					isSingleRepeat = false;
				}
			}
		});

		playSpeedTextView.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				String currentRate = playSpeedTextView.getText().toString().trim();
				if (currentRate.equals("1x"))
				{
					mediaPlayer.setRate(AppConstant.PlayParms.PLAY_SPEED_15X);
					playSpeedTextView.setText("1.5x");
				}
				else if (currentRate.equals("1.5x"))
				{
					mediaPlayer.setRate(AppConstant.PlayParms.PLAY_SPPED_2X);
					playSpeedTextView.setText("2x");
				}
				else if (currentRate.equals("2x"))
				{
					mediaPlayer.setRate(AppConstant.PlayParms.PLAY_SPEED_025X);
					playSpeedTextView.setText("0.25x");
				}
				else if (currentRate.equals("0.25x"))
				{
					mediaPlayer.setRate(AppConstant.PlayParms.PLAY_SPEED_05X);
					playSpeedTextView.setText("0.5x");
				}
				else if (currentRate.equals("0.5x"))
				{
					mediaPlayer.setRate(AppConstant.PlayParms.PLAY_SPEED_1X);
					playSpeedTextView.setText("1x");
				}
			}
		});

		fenxiangButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				fenxiangAlertDialog.show();
				mediaPlayer.pause();
				syncHandler.removeCallbacks(syncRunnable);
			}
		});
		if (getIntent().getStringExtra("mode").equals("online"))
		{
			isOnlineMode = true;
		}
		// else
		// {
		// time = System.currentTimeMillis() + "";
		// lrcContents = new LrcProcess(AppConstant.FilePath.LRC_FILE_PATH +
		// mp3Info.getId() + ".lrc", false)
		// .getLrcList();
		// setLrcLists();
		// duration = Integer.parseInt(mp3Info.getDuration());
		// mediaPlayer.releaseAudioPlayer();
		// mediaPlayer.createAudioPlayer(AppConstant.FilePath.MP3_FILE_PATH +
		// mp3Info.getId() + ".mp3");
		// setLrcHandler.sendEmptyMessage(0);
		// mediaPlayer.play();
		// syncHandler.post(syncRunnable);
		// loadingDialog.dismiss();
		//
		//
		// }
		internetThread.start();

		dancibenMenu = new SlidingMenu(this);

		dancibenMenu.setMode(SlidingMenu.RIGHT);
		dancibenMenu.setFadeDegree(0.35f);
		dancibenMenu.setBehindOffset(80);
		dancibenMenu.setShadowDrawable(R.drawable.slidemenu_shadow_right);
		dancibenMenu.setShadowWidth(13);
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
					mediaPlayer.pause();
					syncHandler.removeCallbacks(syncRunnable);

					Intent startBrowserIntent = new Intent(mContext, WordsBrowser.class);
					startBrowserIntent.putExtra("wordInfos", (Serializable) wordInfos);
					startBrowserIntent.putExtra("isOnlineMode", true);
					startBrowserIntent.putExtra("position", position);
					startActivity(startBrowserIntent);
				}
				else
				{
					mediaPlayer.seekTo(nanjuSentenceInfos.get(position).getStartPos());
				}
			}

		});
		// dancibenListView.setAdapter(new MyListAdapter());

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
		isOnlinePlayerActive = false;
		mediaPlayer.releaseAudioPlayer();
		unregisterReceiver(receiver);

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
						System.out.println("信息已保存");
						// Toast.makeText(mContext, "信息已保存",
						// Toast.LENGTH_SHORT).show();
					}
					else if (msg.what == 0)
					{
						Toast.makeText(mContext, "信息无法保存，服务器开小差了又", Toast.LENGTH_SHORT).show();
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
				params.put("time", time);
				params.put("duration", studyDuration + "");

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
					if (responseCode != 200 || result != 1)
					{
						System.out.println("返回值问题：" + Toolkits.convertStreamToString(inputStream));
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

		super.onDestroy();
	}

	private void setFenxiangAlertDialog()
	{
		alertDialogBuilder = new AlertDialog.Builder(mContext);
		LayoutInflater inflater = LayoutInflater.from(mContext);
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
					mediaPlayer.play();
					syncHandler.post(syncRunnable);
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

				fenxiangAlertDialog.dismiss();

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
				Intent intent = new Intent(mContext, NoteListActivity.class);
				intent.putExtra("lid", mp3Info.getId());
				startActivity(intent);
			}
		});

		playerFenxiangWenda.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				Intent intent = new Intent(mContext, QuestionListActivity.class);
				intent.putExtra("lid", mp3Info.getId());
				startActivity(intent);
			}
		});

		playerFenxiangPingjia.setOnClickListener(new OnClickListener()
		{

			public void onClick(View v)
			{
				Intent intent = new Intent(mContext, CommentActivity.class);
				intent.putExtra("lid", mp3Info.getId());
				startActivity(intent);
			}
		});

		playerFenxiangFenxiangjuzi.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				LrcContent currentLrcContent = lrcContents.get(currentLrcIndex);
				Util.showShare(OnlinePlayer.this, "今天我在坚果听力上看到这句话，说的太好了！！  ——" + currentLrcContent.getEngLrc() + "("
						+ currentLrcContent.getChsLrc() + ")");
				fenxiangAlertDialog.dismiss();
				isShareSDKMode = true;
			}
		});

		PlayerFenxiangFenxiangKecheng.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				Util.showShare(OnlinePlayer.this, "今天我在坚果听力上听了这边文章，顿时感觉神清气爽！ ——" + mp3Info.getName());
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

	private void setLrcLists()
	{
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
	}

	private void setLrcListAndButton()
	{
		PlayerAdapter adapter = null;
		switch (currentLrcLangMode)
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
			changLrcLangButton.setText("无");
			break;
		case AppConstant.PlayParms.LRC_LANG_MODE_BOTH:
			adapter = new PlayerAdapter(this, bothLrcList, R.layout.player_listitem_linearlayout, currentLrcIndex);
			changLrcLangButton.setText("双");
			break;
		}
		setListAdapter(adapter);
		setSelection(currentLrcIndex);
	}

	private int getCurrentLrcIndex()
	{
		int ret = -1;
		int posInt = mediaPlayer.getPosition();
		for (int i = 0; i < lrcContents.size(); i++)
		{
			LrcContent tmp = lrcContents.get(i);
			if (tmp.getStartPos() <= posInt && tmp.getEndPos() >= posInt)
			{
				ret = i;
				break;
			}
		}
		return ret;
	}

	private void setShengciAndNanjuTextview()
	{
		if (isShengciSelected)
		{
			shengciTextView.setTextColor(Color.parseColor("#00bad2"));;
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

						Toast.makeText(mContext, "成功删除 " + wordInfos.get(finalPos).getWord(), Toast.LENGTH_SHORT)
								.show();

						wordInfos.remove(finalPos);
					}
					else
					{
						Intent intent = new Intent(AppConstant.Actions.PLAYER_ASK_SERVICE_TO_DELETE_SENTENCE);
						intent.putExtra("position", finalPos);
						sendBroadcast(intent);

						Toast.makeText(mContext, "成功删除 " + nanjuSentenceInfos.get(finalPos).getSentence(),
								Toast.LENGTH_SHORT).show();

						nanjuSentenceInfos.remove(finalPos);
					}
					dancibenListView.setAdapter(new MyListAdapter());
				}
			});

			if (isShengciSelected)
			{
				WordInfo wordInfo = wordInfos.get(position);
				contentTextView.setText(wordInfo.getWord());
				descriptionTextView.setText(wordInfo.getMeaning());
			}
			else
			{
				SentenceInfo sentenceInfo = nanjuSentenceInfos.get(position);
				contentTextView.setText(sentenceInfo.getSentence());
				descriptionTextView.setText(sentenceInfo.getTranslation());
			}
			return convertView;
		}

	}
}
