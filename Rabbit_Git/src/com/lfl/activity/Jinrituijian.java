package com.lfl.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lfl.model.Mp3Info;
import com.lfl.utils.AppConstant;
import com.lfl.utils.HttpDownloader;
import com.lfl.utils.MillisecondConvert;
import com.lfl.utils.PullParseXML;
import com.lfl.utils.SlideListView;
import com.lfl.utils.Toolkits;
import com.lz.utils.HttpRequestUtil;
import com.lz.utils.StaticInfos;
import com.hare.activity.R;

@SuppressLint("HandlerLeak")
public class Jinrituijian extends Activity implements OnItemClickListener
{
	private ImageView searchImageView;
	private List<Mp3Info> mp3Infos = new ArrayList<Mp3Info>();
	private SlideListView listView;
	private TextView amountTextView;
	private ImageView touxiangImageView;
//	private TextView mottoTextView;
	private Context mContext;

	private void initWidegts()
	{
		amountTextView = (TextView) findViewById(R.id.jinrituijian_amount_textView);
		searchImageView = (ImageView) findViewById(R.id.jinrituijian_search_imageview);
		touxiangImageView = (ImageView) findViewById(R.id.jinrituijian_touxiang_imageview);
//		mottoTextView = (TextView) findViewById(R.id.jinrituijian_objective_textview);
		touxiangImageView.setImageBitmap(StaticInfos.portraitBm);
//		mottoTextView.setText(StaticInfos.nickname + "，快找找你喜欢的听力吧~");

		searchImageView.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(getApplicationContext(), Fanting_search.class);
				startActivity(intent);

			}
		});
	}

	private void setListView()
	{
		final Handler fetchHandler = new Handler()
		{

			@Override
			public void handleMessage(Message msg)
			{
				super.handleMessage(msg);
				if (msg.what == AppConstant.INTERACTION_STATUS.INTERACTION_SUCCESSFUL)
				{
					amountTextView.setText(mp3Infos.size() + "");
					listView.setAdapter(new MyListAdapter());
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
				mp3Infos = PullParseXML.parseOnlineMp3XML(AppConstant.URL.JINRITUIJIAN_MP3_LIST_URL, params, true);
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
		}).start();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jingrituijian);

		mContext = this;

		listView = (SlideListView) findViewById(R.id.jinrituijian_slideListView);
		listView.setOnItemClickListener(this);

		initWidegts();
		setListView();

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
	{
		Intent intent = new Intent(mContext, OnlinePlayer.class);
		intent.putExtra("mp3Info", mp3Infos.get(position));
		intent.putExtra("mode", "online");
		startActivity(intent);
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
			return mp3Infos.get(position);
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
			ViewHolder holder = new ViewHolder();
			convertView = getLayoutInflater().inflate(R.layout.jinrituijian_listitem, null);
			holder.jingLayout = (LinearLayout) convertView.findViewById(R.id.jinrituijian_listItem_jing_linearlayout);
			holder.shoucangLayout = (LinearLayout) convertView.findViewById(R.id.jinrituijian_listItem_shoucang_linearlayout);
			holder.shanchuLayout = (LinearLayout) convertView.findViewById(R.id.jinrituijian_listItem_shanchu_linearlayout);
			holder.title = (TextView) convertView.findViewById(R.id.jinrituijian_listItem_title_textView);
			holder.basis = (TextView) convertView.findViewById(R.id.jinrituijian_listItem_basis_textView);
			holder.duration = (TextView) convertView.findViewById(R.id.jinrituijian_listItem_duration_textView);
			if (position == mp3Infos.size())
			{
				// LinearLayout menuLayout = (LinearLayout)
				// convertView.findViewById(R.id.llayout_right);
				// TextView baseedOnTextView = (TextView) convertView
				// .findViewById(R.id.jinrituijian_listItem_basedOn_textView);
				// menuLayout.setLayoutParams(new LinearLayout.LayoutParams(0,
				// 0));
				// holder.duration.setLayoutParams(new
				// RelativeLayout.LayoutParams(0, 0));
				// holder.basis.setLayoutParams(new
				// RelativeLayout.LayoutParams(0, 0));
				// baseedOnTextView.setLayoutParams(new
				// RelativeLayout.LayoutParams(0, 0));
				// holder.title.setText("+ 更多");
				// RelativeLayout.LayoutParams params = new
				// RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				// LayoutParams.WRAP_CONTENT);
				// params.addRule(RelativeLayout.CENTER_IN_PARENT);
				// holder.title.setLayoutParams(params);

			}
			else
			{
				final Mp3Info mp3Info = mp3Infos.get(position);
				holder.title.setText(mp3Info.getName());
				holder.basis.setText("你的各种信息");
				holder.duration.setText(MillisecondConvert.convert(Integer.parseInt(mp3Infos.get(position)
						.getDuration())));
				holder.jingLayout.setOnClickListener(new OnClickListener()
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
								Toast.makeText(mContext, AppConstant.INTERACTION_STATUS.TOAST_INTERACTION_SUCCESSFUL,
										Toast.LENGTH_SHORT).show();
								break;

							case AppConstant.INTERACTION_STATUS.NETWORK_CONNECTION_EXCEPTION:
								Toast.makeText(mContext,
										AppConstant.INTERACTION_STATUS.TOAST_NETWORK_CONNECTION_EXCEPTION,
										Toast.LENGTH_SHORT).show();
								break;
							case AppConstant.INTERACTION_STATUS.SERVER_STATUS_EXCEPTION:
								Toast.makeText(mContext, AppConstant.INTERACTION_STATUS.TOAST_SERVER_STATUS_EXCEPTION,
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

											int mp3Ret = HttpDownloader.downloadFile(AppConstant.URL.NCC_NEUQ_MP3_URL
													+ mp3Info.getId() + ".mp3", AppConstant.FilePath.MP3_FILE_PATH,
													mp3Info.getId() + ".mp3");
											int lrcRet = HttpDownloader.downloadFile(AppConstant.URL.NCC_NEUQ_LRC_URL
													+ mp3Info.getId() + ".lrc", AppConstant.FilePath.LRC_FILE_PATH,
													mp3Info.getId() + ".lrc");
											int picRet = HttpDownloader.downloadFile(AppConstant.URL.NCC_NEUQ_PIC_URL
													+ mp3Info.getPic(), AppConstant.FilePath.PIC_FILE_PATH,
													mp3Info.getPic());
											if (mp3Ret != -1 && lrcRet != -1 && picRet != -1)
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
				holder.shoucangLayout.setOnClickListener(new OnClickListener()
				{

					@Override
					public void onClick(View v)
					{
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
				holder.shanchuLayout.setOnClickListener(new OnClickListener()
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
								mp3Infos.remove(finalPos);
								listView.setAdapter(new MyListAdapter());
								Toast.makeText(mContext, "成功删除 ", Toast.LENGTH_SHORT).show();
							}
							else if (msg.what == 0)
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
								params.put("lid", mp3Infos.get(finalPos).getId());

								try
								{
									HttpURLConnection urlConnection = (HttpURLConnection) HttpRequestUtil
											.sendGetRequest(AppConstant.URL.DEL_JINGTING_MP3_URL, params, headers);
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
			}
			return convertView;
		}

	}

	private class ViewHolder
	{
		public LinearLayout jingLayout;
		public LinearLayout shoucangLayout;
		public LinearLayout shanchuLayout;
		public TextView title;
		public TextView basis;
		public TextView duration;
	}

}
