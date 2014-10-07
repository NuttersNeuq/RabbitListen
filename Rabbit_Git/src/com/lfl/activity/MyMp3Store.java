package com.lfl.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import com.hare.activity.R;

@SuppressLint("HandlerLeak")
public class MyMp3Store extends Activity
{
	private LinearLayout shoucangLayout, dingyueLayout, jingtingLayout, bendiLayout;
//	private TextView bendiSizeTextView;
//	private TextView shoucangSizeTextView;
//	private TextView dingyueSizeTextView;
//	private TextView jingtingSizeTextView;
	private Context mContext;
//	private int shoucangSize = -1;
//	private int dingyueSize = -1;
//	private int jingtingSize = -1;

//	private void fetchListSize()
//	{
//		final Handler fetchHandler = new Handler()
//		{
//
//			@Override
//			public void handleMessage(Message msg)
//			{
//				super.handleMessage(msg);
//				if (msg.what == 1)
//				{
//					/**
//					 * set all kinds of Size;
//					 */
//					shoucangSizeTextView.setText("材料 " + shoucangSize);
//					dingyueSizeTextView.setText("材料 " + dingyueSize);
//					jingtingSizeTextView.setText("材料 " + jingtingSize); 
//				}
//				else
//				{
//					shoucangSizeTextView.setText("材料 " + shoucangSize);
//					dingyueSizeTextView.setText("材料 " + dingyueSize);
//					jingtingSizeTextView.setText("材料 " + jingtingSize); 
//					Toast.makeText(mContext, "抓取材料个数失败了", Toast.LENGTH_SHORT).show();
//				}
//			}
//		};
//		new Thread(new Runnable()
//		{
//			private InputStream inputStream = null;
//			private int responseCode = 0;
//			private String result;
//
//			@Override
//			public void run()
//			{
//				HashMap<String, String> headers = new HashMap<String, String>();
//				HashMap<String, String> params = new HashMap<String, String>();
//				headers.put("Cookie", "PHPSESSID=" + StaticInfos.phpsessid);
//				params.put("??", "??");
//				try
//				{
//					HttpURLConnection urlConnection = (HttpURLConnection) HttpRequestUtil.sendGetRequest("??", params,
//							headers);
//					responseCode = urlConnection.getResponseCode();
//					inputStream = urlConnection.getInputStream();
//					result = Toolkits.convertStreamToString(inputStream);
//					if (result.equals("0") || responseCode != 200)
//					{
//						fetchHandler.sendEmptyMessage(0);
//					}
//					else
//					{
//						fetchHandler.sendEmptyMessage(1);
//					}
//				} catch (Exception e)
//				{
//					e.printStackTrace();
//					fetchHandler.sendEmptyMessage(0);
//				}
//			}
//		}).start();
//	}

	private void initWidgets()
	{
		shoucangLayout = (LinearLayout) findViewById(R.id.tingliku_shoucang_linearlayout);
		dingyueLayout = (LinearLayout) findViewById(R.id.tingliku_dingyue_linearlayout);
		jingtingLayout = (LinearLayout) findViewById(R.id.tingliku_jingting_linearlayout);
		bendiLayout = (LinearLayout) findViewById(R.id.tingliku_bendi_linearlayout);
//		bendiSizeTextView = (TextView) findViewById(R.id.tingliku_bendi_size_textview);
//		shoucangSizeTextView = (TextView) findViewById(R.id.tingliku_shoucang_size_textview);
//		dingyueSizeTextView = (TextView) findViewById(R.id.tingliku_dingyue_size_textview);
//		jingtingSizeTextView = (TextView) findViewById(R.id.tingliku_jingting_size_textview);

//		bendiSizeTextView.setText("材料  " + Diyijiemian.offlineSaver.getDownloadListSize());

		shoucangLayout.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(mContext, Tingliku_Shoucang.class);
				startActivity(intent);
			}
		});

		dingyueLayout.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(mContext, Fanting_dingyue.class);
				startActivity(intent);
			}
		});
		jingtingLayout.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(mContext, Tingliku_JingTing.class);
				startActivity(intent);
			}
		});
		bendiLayout.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(mContext, Tingliku_bendi.class);
				startActivity(intent);
			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tingliku);

		mContext = this;

		initWidgets();
//		fetchListSize();
	}

}
