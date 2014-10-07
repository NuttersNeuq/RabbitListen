package com.lfl.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;

import com.lfl.model.LrcContent;
import com.lfl.model.Mp3Info;
import com.lfl.model.WordInfo;
import com.lfl.opensl.audio.OSLESMediaPlayer;
import com.lfl.utils.AppConstant;
import com.lfl.utils.AppConstant.FilePath;
import com.lfl.utils.LrcProcess;

@SuppressLint("HandlerLeak")
public class PlayService extends Service
{
	private boolean isLocalPlayerActive = false;
	private boolean isSingleRepeat = false;
	private int lrcLangMode = AppConstant.PlayParms.LRC_LANG_MODE_BOTH;
	/**
	 * 兼具两个功能
	 */
	private int lrcRepeatIndex = 0;
	private int currentPos = -1;
	private int currentPlaySpeed = AppConstant.PlayParms.PLAY_SPEED_1X;
	private int duration;
	private OSLESMediaPlayer mediaPlayer;

	private boolean isPlayServiceLoaded = false;

	private Mp3Info mp3Info;
	private ArrayList<LrcContent> lrcContents;
	private List<WordInfo> wordInfos = new ArrayList<WordInfo>();
	private boolean isNanju[];
	private int recoveryPoint = 0;

	private int sentenceRepeatCount = 0;
	private int currentLrcIndex = 0;

	private BroadcastReceiver receiver = new BroadcastReceiver()
	{

		@Override
		public void onReceive(Context context, Intent intent)
		{
			String action = intent.getAction();
			System.out.println("service's action: " + action);
			/**
			 * ACT13
			 */
			if (action.equals(AppConstant.Actions.PLAYER_GET_INIT_FROM_SERVICE))
			{
				isLocalPlayerActive = true;
				isPlayServiceLoaded = true;
				lrcContents = new LrcProcess(FilePath.LRC_FILE_PATH + mp3Info.getId() + ".lrc", false).getLrcList();
				isNanju = new boolean[lrcContents.size()];
				Arrays.fill(isNanju, false);
				recoveryPoint = 0;
				currentPos = 0;
				lrcLangMode = AppConstant.PlayParms.LRC_LANG_MODE_BOTH;
				currentPlaySpeed = AppConstant.PlayParms.PLAY_SPEED_1X;
				duration = Integer.parseInt(mp3Info.getDuration());
				recoveryPoint = Integer.parseInt(mp3Info.getStartTime());
				isSingleRepeat = false;
				wordInfos.clear();
				lrcContents.get(lrcContents.size() - 1).setEndPos(duration);
				mediaPlayer.releaseAudioPlayer();
				mediaPlayer.createAudioPlayer(AppConstant.FilePath.MP3_FILE_PATH + mp3Info.getId() + ".mp3");

				Intent sendMp3InfoIntent = new Intent();
				sendMp3InfoIntent.setAction(AppConstant.Actions.SERVICE_SEND_INIT_TO_PLAYER);
				// sendMp3InfoIntent.putExtra("mp3Info", mp3Info);
				sendMp3InfoIntent.putExtra("wordInfos", (Serializable) wordInfos);
				sendMp3InfoIntent.putExtra("isSingleRepeat", isSingleRepeat);
				sendMp3InfoIntent.putExtra("currentPos", currentPos);
				sendMp3InfoIntent.putExtra("lrcLangMode", lrcLangMode);
				sendMp3InfoIntent.putExtra("currentPlaySpeed", currentPlaySpeed);
				sendMp3InfoIntent.putExtra("duration", duration);
				sendMp3InfoIntent.putExtra("lrcContents", lrcContents);
				sendMp3InfoIntent.putExtra("currentLrcPos", getCurrentLrcIndex());
				sendBroadcast(sendMp3InfoIntent);
			}
			/**
			 * ACT14
			 */
			else if (action.equals(AppConstant.Actions.HEADSET_GET_INIT_STATUS_FROM_SERVICE))
			{

				Intent sendInitStatusIntent = new Intent();
				sendInitStatusIntent.setAction(AppConstant.Actions.SERVICE_GIVE_INIT_STATUS_TO_HEADSET);
				sendInitStatusIntent.putExtra("isPlayServiceLoaded", isPlayServiceLoaded);
				sendInitStatusIntent.putExtra("from", intent.getStringExtra("from"));
				sendInitStatusIntent.putExtra("isAuto", intent.getBooleanExtra("isAuto", false));
				if (isPlayServiceLoaded)
				{
					sendInitStatusIntent.putExtra("mp3Info", mp3Info);
				}
				sendBroadcast(sendInitStatusIntent);

			}
			/**
			 * ACT12
			 */
			else if (action.equals(AppConstant.Actions.SEND_MP3INFO_TO_SERVICE))
			{
				mp3Info = (Mp3Info) intent.getSerializableExtra("mp3Info");
				System.out.println("Service获取的MP3为：" + mp3Info);
			}
			/**
			 * ACT2
			 */
			else if (action.equals(AppConstant.Actions.PLAYER_ASK_SERVICE_TO_START))
			{
				if (isLocalPlayerActive)
				{
					// mediaPlayer.play();
					// mediaPlayer.seekTo(currentPos);
					System.out.println("开始的时间：" + recoveryPoint);
					syncHandler.post(syncRunnable);
				}
			}
			/**
			 * ACT1
			 */
			else if (action.equals(AppConstant.Actions.PLAYER_ASK_SERVICE_TO_PAUSE))
			{
				if (isLocalPlayerActive)
				{
					recoveryPoint = mediaPlayer.getPosition();
					syncHandler.removeCallbacks(syncRunnable);
					mediaPlayer.pause();
				}
			}
			/**
			 * ACT5
			 */
			else if (action.equals(AppConstant.Actions.PLAYER_ASK_SERVICE_TO_CHANGE_CURRENTPOS))
			{
				mediaPlayer.seekTo(intent.getIntExtra("newCurrentPos", 0));
			}
			/**
			 * ACT6
			 */
			else if (action.equals(AppConstant.Actions.PLAYER_ASK_SERVICE_TO_SET_RATE))
			{
				currentPlaySpeed = intent.getIntExtra("newPlaySpeed", AppConstant.PlayParms.PLAY_SPEED_1X);
				mediaPlayer.setRate(currentPlaySpeed);
			}
			/**
			 * ACT7
			 */
			else if (action.equals(AppConstant.Actions.PLAYER_ASK_SERVICE_TO_SET_LRCLANG))
			{
				lrcLangMode = intent.getIntExtra("lrcLangMode", AppConstant.PlayParms.LRC_LANG_MODE_BOTH);
			}
			/**
			 * ACT3
			 */
			else if (action.equals(AppConstant.Actions.PLAYER_ASK_SERVICE_TO_SINGLEREPEAT_START))
			{
				isSingleRepeat = true;
				lrcRepeatIndex = getCurrentLrcIndex();
			}
			/**
			 * ACT4
			 */
			else if (action.equals(AppConstant.Actions.PLAYER_ASK_SERVICE_TO_SINGLEREPEAT_STOP))
			{
				isSingleRepeat = false;
			}
			/**
			 * ACT16
			 */
//			else if (action.equals(AppConstant.Actions.PLAYERADAPTER_SEND_NEW_WORD))
//			{
				// if (isLocalPlayerActive)
				// {
				// WordInfo wordInfo = new WordInfo();
				// SentenceInfo sentenceInfo = new SentenceInfo();
				// LrcContent selectedLrcContent =
				// lrcContents.get(intent.getIntExtra("position", 0));
				// wordInfo.setWord(intent.getStringExtra("word"));
				// wordInfo.setMeaning("单词的解释(待开发)");
				// sentenceInfo.setEndPos(selectedLrcContent.getEndPos());
				// sentenceInfo.setMp3Id(mp3Info.getId());
				// sentenceInfo.setMp3Name(mp3Info.getName());
				// sentenceInfo.setPosition(intent.getIntExtra("position", 0) +
				// "");
				// sentenceInfo.setSentence(selectedLrcContent.getEngLrc());
				// sentenceInfo.setStartPos(selectedLrcContent.getStartPos());
				// sentenceInfo.setTime(Toolkits.getCurrentDate());
				// sentenceInfo.setTranslation(selectedLrcContent.getChsLrc());
				// wordInfo.setSentenceInfo(sentenceInfo);
				// wordInfos.add(wordInfo);
				// }
//			}
			/**
			 * ACT21
			 */
			else if (action.equals(AppConstant.Actions.PLAYERADAPTER_SEND_NEW_SENTENCE))
			{
				if (isLocalPlayerActive)
				{
					isNanju[intent.getIntExtra("position", -1)] = true;
				}
			}
			/**
			 * ACT22
			 */
			else if (action.equals(AppConstant.Actions.PLAYER_ASK_SERVICE_TO_DELETE_SENTENCE))
			{
				isNanju[intent.getIntExtra("position", -1)] = false;
			}
			/**
			 * ACT17
			 */
			else if (action.equals(AppConstant.Actions.PLAYERADAPTER_SEND_NEW_LRCINDEX))
			{
				if (mediaPlayer.isPlaying() && isLocalPlayerActive)
				{
					mediaPlayer.seekTo(lrcContents.get(intent.getIntExtra("newLrcIndex", getCurrentLrcIndex()))
							.getStartPos());

				}
			}
			/**
			 * ACT9 & ACT23
			 */
//			else if (action.equals(AppConstant.Actions.PLAYER_ASK_SERVICE_TO_DELETE_WORD)
//					|| action.equals(AppConstant.Actions.WORDBROWSER_DELETE_WORD))
//			{
				// wordInfos.remove(intent.getIntExtra("position", -1));
//			}
			/**
			 * ACT18
			 */
			else if (action.equals(AppConstant.Actions.PLAYERADAPTER_ASK_PLAYER_TO_PAUSE))
			{
				if (isLocalPlayerActive)
				{
					recoveryPoint = mediaPlayer.getPosition();
					syncHandler.removeCallbacks(syncRunnable);
					mediaPlayer.pause();
				}
			}
			/**
			 * ACT19
			 */
			else if (action.equals(AppConstant.Actions.PLAYERADAPTER_ASK_PLAYER_TO_RESUME))
			{
				if (isLocalPlayerActive)
				{
					syncHandler.post(syncRunnable);
					// mediaPlayer.play();
				}
			}
			/**
			 * ACT24
			 */
			else if (action.equals(AppConstant.Actions.WORDBROWSER_ASK_TO_RESUME))
			{
				if (isLocalPlayerActive)
				{
					// mediaPlayer.createAudioPlayer(AppConstant.FilePath.SDCARD_ROOT
					// + mp3Info.getId() + ".mp3");
					// mediaPlayer.play();
					// mediaPlayer.seekTo(currentPos);
					syncHandler.post(syncRunnable);
				}
			}
			/**
			 * ACT20
			 */
			else if (action.equals(AppConstant.Actions.LOCALPLAYER_SHUTDOWN))
			{
				mediaPlayer.releaseAudioPlayer();
				recoveryPoint = currentPos;
				System.out.println("创建恢复点：" + recoveryPoint);
				syncHandler.removeCallbacks(syncRunnable);
				mediaPlayer.pause();
				isLocalPlayerActive = false;
			}
			/**
			 * ACT 26 不随着activity声明走
			 */
			else if (action.equals(AppConstant.Actions.PLAYER_ASK_SERVER_TO_RELEASE))
			{
				mediaPlayer.stop();
				mediaPlayer.releaseAudioPlayer();
				isPlayServiceLoaded = false;
				recoveryPoint = 0;
				syncHandler.removeCallbacks(syncRunnable);
			}
			/**
			 * ACT 27
			 */
			else if (action.equals(AppConstant.Actions.PLAYER_ASK_SERVER_TO_INCREASE_ROUND))
			{
				mp3Info.roundIncrease();
				mediaPlayer.seekTo(0);
			}
		}

	};

	private Handler syncHandler = new Handler();
	private Runnable syncRunnable = new Runnable()
	{

		@Override
		public void run()
		{
			if (mediaPlayer.isPlaying())
			{
				currentLrcIndex = getCurrentLrcIndex();
				int posInt = mediaPlayer.getPosition();
				if (isSingleRepeat && lrcRepeatIndex != currentLrcIndex)
				{
					posInt = lrcContents.get(lrcRepeatIndex).getStartPos();
					mediaPlayer.seekTo(posInt);
				}
				if (mp3Info.getRound().equals("12") && lrcRepeatIndex != currentLrcIndex)
				{
					sentenceRepeatCount++;
					if (sentenceRepeatCount < 3)
					{
						posInt = lrcContents.get(lrcRepeatIndex).getStartPos();
						mediaPlayer.seekTo(posInt);
					}
					else if (sentenceRepeatCount == 3)
					{
						lrcRepeatIndex = currentLrcIndex;
						sentenceRepeatCount = 0;
					}
				}
				else if (!mp3Info.getRound().equals("11") && !mp3Info.getRound().equals("12"))
				{
					if (lrcRepeatIndex != currentLrcIndex)
					{
						if (isNanju[lrcRepeatIndex])
						{
							sentenceRepeatCount++;
							if (sentenceRepeatCount < 2)
							{
								posInt = lrcContents.get(lrcRepeatIndex).getStartPos();
								mediaPlayer.seekTo(posInt);
							}
							else if (sentenceRepeatCount == 2)
							{
								lrcRepeatIndex = currentLrcIndex;
								sentenceRepeatCount = 0;
							}
						}
						else
						{
							lrcRepeatIndex = currentLrcIndex;
						}
					}
				}
				currentPos = posInt;
				Intent intent = new Intent(AppConstant.Actions.SERVICE_SEND_CURRENTPOS_TO_PLAYER);
				intent.putExtra("currentPos", currentPos);
				intent.putExtra("currentLrcIndex", currentLrcIndex);

				sendBroadcast(intent);
				syncHandler.postDelayed(syncRunnable, AppConstant.PlayParms.SYNC_HANDLER_DELAY_MILLS);
			}
			else
			{
				mediaPlayer.seekTo(recoveryPoint);
				mediaPlayer.play();
				syncHandler.post(syncRunnable);
			}
		}
	};


	@Override
	public void onCreate()
	{
		super.onCreate();
		IntentFilter filter = new IntentFilter();
		filter.addAction(AppConstant.Actions.HEADSET_GET_INIT_STATUS_FROM_SERVICE);
		filter.addAction(AppConstant.Actions.PLAYER_GET_INIT_FROM_SERVICE);
		// filter.addAction(AppConstant.Actions.PLAYER_ASK_SERVICE_TO_ADD_WORD);
		filter.addAction(AppConstant.Actions.PLAYER_ASK_SERVICE_TO_CHANGE_CURRENTPOS);
		// filter.addAction(AppConstant.Actions.PLAYER_ASK_SERVICE_TO_DELETE_WORD);
		filter.addAction(AppConstant.Actions.PLAYER_ASK_SERVICE_TO_PAUSE);
		filter.addAction(AppConstant.Actions.PLAYER_ASK_SERVICE_TO_SET_LRCLANG);
		filter.addAction(AppConstant.Actions.PLAYER_ASK_SERVICE_TO_SET_RATE);
		filter.addAction(AppConstant.Actions.PLAYER_ASK_SERVICE_TO_SINGLEREPEAT_START);
		filter.addAction(AppConstant.Actions.PLAYER_ASK_SERVICE_TO_SINGLEREPEAT_STOP);
		filter.addAction(AppConstant.Actions.PLAYER_ASK_SERVICE_TO_START);
		filter.addAction(AppConstant.Actions.SEND_MP3INFO_TO_SERVICE);
		// filter.addAction(AppConstant.Actions.PLAYERADAPTER_SEND_NEW_WORD);
		filter.addAction(AppConstant.Actions.PLAYERADAPTER_SEND_NEW_LRCINDEX);
		filter.addAction(AppConstant.Actions.PLAYERADAPTER_ASK_PLAYER_TO_PAUSE);
		filter.addAction(AppConstant.Actions.PLAYERADAPTER_ASK_PLAYER_TO_RESUME);
		filter.addAction(AppConstant.Actions.LOCALPLAYER_SHUTDOWN);
		// filter.addAction(AppConstant.Actions.WORDBROWSER_DELETE_WORD);
		filter.addAction(AppConstant.Actions.WORDBROWSER_ASK_TO_RESUME);
		filter.addAction(AppConstant.Actions.PLAYER_ASK_SERVICE_TO_DELETE_SENTENCE);
		filter.addAction(AppConstant.Actions.PLAYERADAPTER_SEND_NEW_SENTENCE);
		filter.addAction(AppConstant.Actions.PLAYER_ASK_SERVER_TO_RELEASE);
		filter.addAction(AppConstant.Actions.PLAYER_ASK_SERVER_TO_INCREASE_ROUND);
		registerReceiver(receiver, filter);

		mediaPlayer = new OSLESMediaPlayer();
		mediaPlayer.createEngine();
	}

	@Override
	public IBinder onBind(Intent arg0)
	{
		return null;
	}

	@Override
	public void onDestroy()
	{
		syncHandler.removeCallbacks(syncRunnable);
		unregisterReceiver(receiver);
		mediaPlayer.releaseAudioPlayer();
		mediaPlayer.releaseEngine();
		super.onDestroy();
	}

	private int getCurrentLrcIndex()
	{
		int ret = -1;
		int posInt = mediaPlayer.getPosition();
		if (posInt < 0)
		{
			System.out.println("mediaPlayer无法返回位置");
			return ret;
		}
		for (int i = 0; i < lrcContents.size(); i++)
		{
			LrcContent tmp = lrcContents.get(i);
			if (tmp.getStartPos() <= posInt && tmp.getEndPos() >= posInt)
			{
				ret = i;
				break;
			}
		}
		if (ret == -1)
		{
			System.out.println("没找到当前歌词位置");
		}
		return ret;
	}
}
