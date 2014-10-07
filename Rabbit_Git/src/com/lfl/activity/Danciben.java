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
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.hare.activity.R;
import com.lfl.model.OnlineWordInfo;
import com.lfl.model.SentenceInfo;
import com.lfl.model.WordInfo;
import com.lfl.opensl.audio.OSLESMediaPlayer;
import com.lfl.utils.AppConstant;
import com.lfl.utils.OnlineDictionaryXMLParser;
import com.lfl.utils.PullParseXML;
import com.lfl.utils.SlideListView;
import com.lfl.utils.Toolkits;
import com.lz.utils.HttpRequestUtil;
import com.lz.utils.StaticInfos;

@SuppressLint("HandlerLeak")
public class Danciben extends Activity
{
	private SlideListView listView;
	private List<WordInfo> wordInfos;
	private OSLESMediaPlayer mediaPlayer = new OSLESMediaPlayer();
	private Context mContext;
	private Dialog loadingDialog;
	private LinearLayout blankLayout;

	private OnItemClickListener onItemClickListener = new OnItemClickListener()
	{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
		{
			View jieshiView = getLayoutInflater().inflate(R.layout.danciben_jieshi_popview, null);
			View menuBackgroundView = getLayoutInflater().inflate(R.layout.popupmenu_black_background, null);
			RelativeLayout jieshiBlankLayout = (RelativeLayout) jieshiView
					.findViewById(R.id.danciben_jieshi_popview_blank_relativelayout);
			final PopupWindow backgroundWindow = new PopupWindow(menuBackgroundView,
					LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
			final PopupWindow jieshiPopupWindow = new PopupWindow(jieshiView, LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT, true);
			jieshiPopupWindow.setAnimationStyle(R.style.AnimBottom);
			jieshiPopupWindow.setTouchable(true);
			backgroundWindow.setAnimationStyle(R.style.AnimPopupMenuBackground);

			jieshiPopupWindow.setOnDismissListener(new OnDismissListener()
			{

				@Override
				public void onDismiss()
				{
					backgroundWindow.dismiss();
					mediaPlayer.stop();
					mediaPlayer.releaseAudioPlayer();
				}
			});

			jieshiBlankLayout.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					jieshiPopupWindow.dismiss();
				}
			});

			backgroundWindow.showAtLocation(arg1, Gravity.CENTER, 0, 0);
			jieshiPopupWindow.showAtLocation(arg1, Gravity.BOTTOM, 0, 0);

			TextView wordTextView = (TextView) jieshiView.findViewById(R.id.word_textview);
			TextView meaingTextView = (TextView) jieshiView.findViewById(R.id.meaning_textview);
			final TextView sentenceTextView = (TextView) jieshiView
					.findViewById(R.id.sentence_and_translation_textview);
			TextView positionTextView = (TextView) jieshiView.findViewById(R.id.position_textview);

			final WordInfo wInfo = wordInfos.get(position);
			wordTextView.setTypeface(Typeface.createFromAsset(getAssets(), "font/segoeui.ttf"));
			wordTextView.setText(wInfo.getWord() + "\n" + wInfo.getPronunciation());
			meaingTextView.setText(wInfo.getMeaning());
			sentenceTextView.setText(wInfo.getSentenceInfo().getSentence() + "\n"
					+ wInfo.getSentenceInfo().getTranslation());
			positionTextView.setText(wInfo.getSentenceInfo().getMp3Name());
			sentenceTextView.setOnClickListener(new OnClickListener()
			{
				int stopPos = wInfo.getSentenceInfo().getEndPos();
				int startPos = wInfo.getSentenceInfo().getStartPos();
				String mp3Name = wInfo.getSentenceInfo().getMp3Id() + ".mp3";

				boolean isLoaded = false;

				Handler handler = new Handler()
				{

					@Override
					public void handleMessage(Message msg)
					{
						super.handleMessage(msg);
						if (mediaPlayer.getPosition() > stopPos)
						{
							mediaPlayer.seekTo(startPos);
							mediaPlayer.stop();
							handler.removeMessages(0);
							sentenceTextView.setTextColor(Color.parseColor("#777777"));

						}
						else
						{
							handler.sendEmptyMessageDelayed(0, 500);
						}
					}

				};

				@Override
				public void onClick(View v)
				{
					sentenceTextView.setTextColor(Color.parseColor("#94B2BB"));
					new Thread(new Runnable()
					{

						@Override
						public void run()
						{
							if (!isLoaded)
							{
								mediaPlayer.releaseAudioPlayer();

								System.out.println("单词所属听力的位置：" + AppConstant.URL.NCC_NEUQ_MP3_URL + mp3Name);

								mediaPlayer.createAudioPlayer(AppConstant.URL.NCC_NEUQ_MP3_URL + mp3Name);
								isLoaded = true;
							}
							mediaPlayer.play();
							mediaPlayer.seekTo(startPos);
							handler.sendEmptyMessage(0);
						}
					}).start();
				}
			});
		}

	};

	/**
	 * 附带刷adapter功能
	 */
	private void getWordsMeaningAndPron()
	{
		final Handler wordsHandler = new Handler()
		{

			@Override
			public void handleMessage(Message msg)
			{
				super.handleMessage(msg);
				listView.setAdapter(new MyAdapter());
			}

		};

		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				for (int i = 0; i < wordInfos.size(); i++)
				{
					WordInfo wInfo = wordInfos.get(i);
					OnlineWordInfo onlineWordInfo = OnlineDictionaryXMLParser.parser(wInfo.getWord());
					String wordMeaning = onlineWordInfo.getTranslation();
					String pron = onlineWordInfo.getPronunciation();
					wInfo.setMeaning(wordMeaning);
					wInfo.setPronunciation(pron);
				}
				wordsHandler.sendEmptyMessage(1);
			}
		}).start();
	};

	private void fetchDataFromServer()
	{
		final Handler fetchDataHandler = new Handler()
		{

			@Override
			public void handleMessage(Message msg)
			{
				super.handleMessage(msg);
				if (msg.what == AppConstant.INTERACTION_STATUS.INTERACTION_SUCCESSFUL)
				{
					if (wordInfos.size() != 0)
					{
						Comparator<WordInfo> comparator = new Comparator<WordInfo>()
						{

							@Override
							public int compare(WordInfo lhs, WordInfo rhs)
							{
								return lhs.getWord().compareTo(rhs.getWord());
							}
						};
						Collections.sort(wordInfos, comparator);
						blankLayout.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
						listView.setAdapter(new MyAdapter());
						getWordsMeaningAndPron();
					}
					loadingDialog.dismiss();
				}
				else if (msg.what == AppConstant.INTERACTION_STATUS.NETWORK_CONNECTION_EXCEPTION)
				{
					loadingDialog.dismiss();
					Toast.makeText(mContext, AppConstant.INTERACTION_STATUS.TOAST_NETWORK_CONNECTION_EXCEPTION,
							Toast.LENGTH_LONG).show();
				}
				else if (msg.what == AppConstant.INTERACTION_STATUS.SERVER_STATUS_EXCEPTION)
				{
					loadingDialog.dismiss();
					Toast.makeText(mContext, AppConstant.INTERACTION_STATUS.TOAST_SERVER_STATUS_EXCEPTION,
							Toast.LENGTH_LONG).show();
				}
			}

		};

		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				HashMap<String, String> params = new HashMap<String, String>();
				wordInfos = PullParseXML.parseOnlineWordsXML(AppConstant.URL.ALL_WORDS_LIST_URL, params, true);

				System.out.println("获取的wordInfos" + wordInfos);

				if (wordInfos.size() != 0)
				{
					String result = wordInfos.get(0).getWord();
					if (result.equals(AppConstant.INTERACTION_STATUS.NETWORK_CONNECTION_EXCEPTION + ""))
					{
						fetchDataHandler.sendEmptyMessage(AppConstant.INTERACTION_STATUS.NETWORK_CONNECTION_EXCEPTION);
					}
					else if (result.equals(AppConstant.INTERACTION_STATUS.SERVER_STATUS_EXCEPTION + ""))
					{
						fetchDataHandler.sendEmptyMessage(AppConstant.INTERACTION_STATUS.SERVER_STATUS_EXCEPTION);
					}
					else
					{
						fetchDataHandler.sendEmptyMessage(AppConstant.INTERACTION_STATUS.INTERACTION_SUCCESSFUL);
					}
				}
				else
				{
					fetchDataHandler.sendEmptyMessage(AppConstant.INTERACTION_STATUS.INTERACTION_SUCCESSFUL);
				}

			}
		}).start();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.danciben_act);

		mContext = this;

		loadingDialog = new Dialog(this, R.style.loading_dialog_style);
		loadingDialog.setContentView(R.layout.loading_dialog);
		Window loadingDialogWindow = loadingDialog.getWindow();
		WindowManager.LayoutParams lParams = loadingDialogWindow.getAttributes();
		loadingDialogWindow.setGravity(Gravity.CENTER);
		lParams.alpha = 1f;
		loadingDialogWindow.setAttributes(lParams);
		loadingDialog.show();

		blankLayout = (LinearLayout) findViewById(R.id.danciben_blank_linearlayout);
		listView = (SlideListView) findViewById(R.id.danciben_act_listview);
		fetchDataFromServer();

		listView.setOnItemClickListener(onItemClickListener);
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
			return wordInfos.size();
		}

		@Override
		public Object getItem(int position)
		{
			return wordInfos.get(position);
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
			convertView = getLayoutInflater().inflate(R.layout.danciben_act_listitem, null);
			final WordInfo currentWordInfo = wordInfos.get(position);
			TextView contentTextView = (TextView) convertView.findViewById(R.id.danciben_act_listitem_content_textview);
			TextView capitalTextView = (TextView) convertView.findViewById(R.id.danciben_act_listitem_capital_textview);
			Button delWordButton = (Button) convertView.findViewById(R.id.danciben_act_listitem_duihao_button);

			delWordButton.setOnClickListener(new OnClickListener()
			{
				private InputStream inputStream = null;
				private int responseCode = 0;
				private int result;
				private Handler delWordHandler = new Handler()
				{

					@Override
					public void handleMessage(Message msg)
					{
						super.handleMessage(msg);
						if (msg.what == 1)
						{
							wordInfos.remove(finalPos);
							if (wordInfos.size() != 0)
							{
								listView.setAdapter(new MyAdapter());
							}
							else
							{
								blankLayout
										.setLayoutParams(new LinearLayout.LayoutParams(
												LinearLayout.LayoutParams.MATCH_PARENT,
												LinearLayout.LayoutParams.MATCH_PARENT));
							}
							Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
						}
						else
						{
							Toast.makeText(mContext, "删除失败，服务器开小差叻", Toast.LENGTH_SHORT).show();
						}
					}

				};

				@Override
				public void onClick(View v)
				{
					new Thread(new Runnable()
					{

						@Override
						public void run()
						{
							HashMap<String, String> headers = new HashMap<String, String>();
							HashMap<String, String> params = new HashMap<String, String>();
							headers.put("Cookie", "PHPSESSID=" + StaticInfos.phpsessid);
							params.put("type", "del");
							params.put("sid", currentWordInfo.getId());

							try
							{
								HttpURLConnection urlConnection = (HttpURLConnection) HttpRequestUtil.sendGetRequest(
										AppConstant.URL.ALL_WORDS_LIST_URL, params, headers);
								responseCode = urlConnection.getResponseCode();
								inputStream = urlConnection.getInputStream();
								result = Integer.parseInt(Toolkits.convertStreamToString(inputStream));
								if (responseCode != 200 || result == 0)
								{
									delWordHandler.sendEmptyMessage(0);
								}
								else
								{
									delWordHandler.sendEmptyMessage(1);
								}
							} catch (Exception e)
							{
								delWordHandler.sendEmptyMessage(0);
								e.printStackTrace();
							}

						}
					}).start();
				}
			});

			if (position != 0)
			{
				if (currentWordInfo.getWord().charAt(0) == wordInfos.get(finalPos - 1).getWord().charAt(0))
				{
					capitalTextView.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
				}
				else
				{
					capitalTextView.setText(currentWordInfo.getWord().charAt(0) + "");
				}
			}
			else
			{
				capitalTextView.setText(currentWordInfo.getWord().charAt(0) + "");
			}

			contentTextView.setText(currentWordInfo.getWord() + "\n" + currentWordInfo.getMeaning());
			return convertView;
		}

	}

}
