package com.lfl.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.lfl.model.WordInfo;
import com.lfl.opensl.audio.OSLESMediaPlayer;
import com.lfl.utils.AppConstant;
import com.hare.activity.R;

/**
 * 默认为统一来源、离线
 * 
 * @author FIRE_TRAY
 * 
 */
@SuppressLint("HandlerLeak")
public class WordsBrowser extends Activity
{
	private ViewPager viewPager;
	private List<WordInfo> wordInfos;
	private List<View> viewList = new ArrayList<View>();
	private TextView previousTextView;
	private TextView nextTextView;
	private OSLESMediaPlayer mediaPlayer = new OSLESMediaPlayer();
	private TextView delTextView;
	/**
	 * 默认为统一来源
	 */
	private boolean isSameResource = true;
	/**
	 * 默认为离线
	 */
	private boolean isOnlineMode = false;

	/**
	 * 防止重复加载
	 */
	private boolean isCurrentMp3Loaded = false;

	// private void fetchWordsMeaning()
	// {
	// final Handler fetchHandler = new Handler()
	// {
	//
	// @Override
	// public void handleMessage(Message msg)
	// {
	// super.handleMessage(msg);
	// int temp = viewPager.getCurrentItem();
	// viewPager.setAdapter(new MyViewPagerAdapter());
	// viewPager.setCurrentItem(temp);
	// }
	//
	// };
	//
	// new Thread(new Runnable()
	// {
	//
	// @Override
	// public void run()
	// {
	// for (int i = 0; i < wordInfos.size(); i++)
	// {
	// WordInfo currentInfo = wordInfos.get(i);
	// currentInfo.setPronunciation(OnlineDictionaryXMLParser.parser(currentInfo.getWord()).getPronunciation());
	// }
	// fetchHandler.sendEmptyMessage(0);
	// }
	// }).start();
	// }

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.danci_browse);

		isOnlineMode = getIntent().getBooleanExtra("isOnlineMode", false);
		isSameResource = getIntent().getBooleanExtra("isSameResource", true);

		wordInfos = (List<WordInfo>) getIntent().getSerializableExtra("wordInfos");
		delTextView = (TextView) findViewById(R.id.danci_browse_delete_textview);
		viewPager = (ViewPager) findViewById(R.id.danci_browse_viewpager);
		previousTextView = (TextView) findViewById(R.id.previous_word_textview);
		nextTextView = (TextView) findViewById(R.id.next_word_textview);

		delTextView.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (wordInfos.size() != 0)
				{
					int index = viewPager.getCurrentItem() % wordInfos.size();
					wordInfos.remove(index);
					viewList.remove(index);
					if (viewList.size() == 0)
					{
						viewList.add(getLayoutInflater().inflate(R.layout.wordbrowser_blank_layout, null));
					}
					viewPager.setAdapter(new MyViewPagerAdapter());
					viewPager.arrowScroll(100 * viewList.size());
					Intent intent = new Intent(AppConstant.Actions.WORDBROWSER_DELETE_WORD);
					intent.putExtra("position", index);
					sendBroadcast(intent);

					Toast.makeText(WordsBrowser.this, "删除成功", Toast.LENGTH_SHORT).show();
				}
			}
		});
		previousTextView.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				viewPager.arrowScroll(1);
			}
		});

		nextTextView.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				viewPager.arrowScroll(2);
			}
		});

		if (wordInfos.size() != 0)
		{
			for (int i = 0; i < wordInfos.size(); i++)
			{
				final WordInfo wInfo = wordInfos.get(i);

				View view = getLayoutInflater().inflate(R.layout.word_browser_viewpager_load_layout, null);
				TextView wordTextView = (TextView) view.findViewById(R.id.word_textview);
				TextView meaningTextView = (TextView) view.findViewById(R.id.meaning_textview);
				final TextView sentenceTextView = (TextView) view.findViewById(R.id.sentence_and_translation_textview);
				TextView positionTextView = (TextView) view.findViewById(R.id.position_textview);

				wordTextView.setTypeface(Typeface.createFromAsset(getAssets(), "font/segoeui.ttf"));
				wordTextView.setText(wInfo.getWord() + "\n" + wInfo.getPronunciation());
				meaningTextView.setText(wInfo.getMeaning());
				sentenceTextView.setText(wInfo.getSentenceInfo().getSentence() + "\n"
						+ wInfo.getSentenceInfo().getTranslation());
				positionTextView.setText(wInfo.getSentenceInfo().getMp3Name());

				sentenceTextView.setOnClickListener(new OnClickListener()
				{
					int startPos = wInfo.getSentenceInfo().getStartPos();
					int endPos = wInfo.getSentenceInfo().getEndPos();
					String mp3Path = (isOnlineMode ? AppConstant.URL.NCC_NEUQ_MP3_URL
							: AppConstant.FilePath.SDCARD_ROOT) + wInfo.getSentenceInfo().getMp3Id() + ".mp3";

					@SuppressLint("HandlerLeak")
					Handler handler = new Handler()
					{

						@Override
						public void handleMessage(Message msg)
						{
							super.handleMessage(msg);
							if (mediaPlayer.getPosition() > endPos)
							{
								sentenceTextView.setTextColor(Color.parseColor("#777777"));
								mediaPlayer.stop();
								// if (!isSameResource)
								// mediaPlayer.releaseAudioPlayer();
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
								if (isSameResource == false && isCurrentMp3Loaded == false)
								{
									mediaPlayer.releaseAudioPlayer();
									mediaPlayer.createAudioPlayer(mp3Path);
									isCurrentMp3Loaded = true;
								}
								mediaPlayer.seekTo(startPos);
								mediaPlayer.play();
								handler.sendEmptyMessage(0);
							}
						}).start();

					}
				});
				viewList.add(view);
			}
		}
		else
		{
			View view = getLayoutInflater().inflate(R.layout.wordbrowser_blank_layout, null);
			viewList.add(view);
		}

		viewPager.setAdapter(new MyViewPagerAdapter());
		viewPager.setCurrentItem(getIntent().getIntExtra("position", 0));
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		if (!isSameResource)
			mediaPlayer.releaseAudioPlayer();
		Intent resumePlayIntent = new Intent(AppConstant.Actions.WORDBROWSER_ASK_TO_RESUME);
		sendBroadcast(resumePlayIntent);
	}

	private class MyViewPagerAdapter extends PagerAdapter
	{
		private boolean isViewLoaded[];

		public MyViewPagerAdapter()
		{
			viewPager.removeAllViews();
			isViewLoaded = new boolean[viewList.size()];
			Arrays.fill(isViewLoaded, false);
		}

		@Override
		public int getCount()
		{
			return viewList.size();

		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1)
		{
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object)
		{
			if (viewList.size() != position)
			{
				container.removeView(viewList.get(position));
			}

		}

		@Override
		public int getItemPosition(Object object)
		{
			return super.getItemPosition(object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position)
		{
			((ViewPager) container).addView(viewList.get(position));
			return viewList.get(position);
		}
	}

}
