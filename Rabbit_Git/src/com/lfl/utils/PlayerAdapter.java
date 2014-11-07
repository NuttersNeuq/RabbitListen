package com.lfl.utils;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

import com.lfl.model.OnlineWordInfo;
import com.hare.activity.R;

@SuppressLint("HandlerLeak")
public class PlayerAdapter extends BaseAdapter
{

	private List<String> adapterList;
	private LayoutInflater mInflater;
	private Context mContext;
	private int mResource;

	private int currentLrcIndex = -0x7fffffff;

	public PlayerAdapter(Context c, List<String> list, int resource, int lrcIndex)
	{
		adapterList = list;
		mContext = c;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mResource = resource;

		currentLrcIndex = lrcIndex;

	}

	@Override
	public int getCount()
	{
		return adapterList.size();
	}

	@Override
	public Object getItem(int arg0)
	{
		return adapterList.get(arg0);
	}

	@Override
	public long getItemId(int arg0)
	{
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		final int finalPos = position;
		convertView = this.mInflater.inflate(this.mResource, parent, false);
		String content = adapterList.get(position);
		TextView textView = (TextView) convertView.findViewById(R.id.player_listitem_lrc_textview);

		textView.setText(content, BufferType.SPANNABLE);
		textView.setMovementMethod(LinkMovementMethod.getInstance());
		textView.setHighlightColor(Color.parseColor("#00000000"));

		LinearLayout playLayout = (LinearLayout) convertView.findViewById(R.id.player_listitem_play_linearlayout);
		ImageView innerImageView = (ImageView) convertView.findViewById(R.id.player_listitem_inner_imageview);

		if (currentLrcIndex == position)
		{
			innerImageView.setBackgroundColor(Color.parseColor("#9000bad2"));
		}
		else
		{
			innerImageView.setBackgroundColor(Color.parseColor("#00000000"));
		}

		playLayout.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent();
				intent.setAction(AppConstant.Actions.PLAYERADAPTER_SEND_NEW_LRCINDEX);
				intent.putExtra("newLrcIndex", finalPos);
				mContext.sendBroadcast(intent);
			}
		});

		getEachWord(textView, position);

		return convertView;
	}

	private void getEachWord(TextView tView, int textViewPosition)
	{

		String wholtText = tView.getText().toString().trim() + " ";
		Spannable spans = (Spannable) tView.getText();
		int start = 0;
		int end = 0;
		while (start < wholtText.length())
		{
			for (int i = start; i < wholtText.length(); i++)
			{
				char c = wholtText.charAt(i);
				if (!(c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z'))
				{
					end = i;
					break;
				}
			}
			spans.setSpan(getClickableSpan(), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			start = end + 1;
		}
	}

	private ClickableSpan getClickableSpan()
	{
		return new ClickableSpan()
		{
			int wordPosition = -1;

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View widget)
			{
				final TextView tv = (TextView) widget;

				for (int i = 0; i < adapterList.size(); i++)
				{
					if (tv.getText().toString().equals(adapterList.get(i)))
					{
						wordPosition = i;
						break;
					}
				}
				final String s = tv.getText().subSequence(tv.getSelectionStart(), tv.getSelectionEnd()).toString();
				if (s.length() > 0)
				{

					Intent pauseIntent = new Intent(AppConstant.Actions.PLAYERADAPTER_ASK_PLAYER_TO_PAUSE);
					mContext.sendBroadcast(pauseIntent);

					View dancijieshiView = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
							.inflate(R.layout.player_dancijieshi_popview, null);
					final TextView descriptionTextView = (TextView) dancijieshiView
							.findViewById(R.id.dancijieshi_popview_description_textview);
					final PopupWindow dancibenWindow = new PopupWindow(dancijieshiView, LayoutParams.MATCH_PARENT,
							LayoutParams.WRAP_CONTENT, true);
					dancibenWindow.setBackgroundDrawable(new BitmapDrawable());
					dancibenWindow.setOutsideTouchable(true);
					dancibenWindow.setTouchable(true);
					dancibenWindow.showAtLocation(widget, Gravity.BOTTOM, 0, 0);
					dancibenWindow.setOnDismissListener(new OnDismissListener()
					{

						@Override
						public void onDismiss()
						{
							Intent resumeIntent = new Intent(AppConstant.Actions.PLAYERADAPTER_ASK_PLAYER_TO_RESUME);
							mContext.sendBroadcast(resumeIntent);
						}
					});
					LinearLayout sentenceLayout = (LinearLayout) dancijieshiView
							.findViewById(R.id.dancijieshi_popview_add_sentence_linearLayout);
//					LinearLayout detailLayout = (LinearLayout) dancijieshiView
//							.findViewById(R.id.dancijieshi_popview_word_detail_linearLayout);
					final LinearLayout addWordLayout = (LinearLayout) dancijieshiView
							.findViewById(R.id.dancijieshi_popview_add_word_linearLayout);
					final TextView wordTextView = (TextView) dancijieshiView
							.findViewById(R.id.dancijieshi_popview_word_textview);
					wordTextView.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "font/segoeui.ttf"));
					wordTextView.setText(s);
					sentenceLayout.setOnClickListener(new OnClickListener()
					{

						@Override
						public void onClick(View v)
						{
							Intent intent = new Intent(AppConstant.Actions.PLAYERADAPTER_SEND_NEW_SENTENCE);
							intent.putExtra("position", wordPosition);
							mContext.sendBroadcast(intent);
							Toast.makeText(mContext, "难句加入成功", Toast.LENGTH_SHORT).show();
							dancibenWindow.dismiss();
						}
					});
//					detailLayout.setOnClickListener(new OnClickListener()
//					{
//
//						@Override
//						public void onClick(View v)
//						{
//
//						}
//					});

					new Thread(new Runnable()
					{
						private String wordMeaningString;
						private String pronString = "";
						private Handler getTransHandler = new Handler()
						{

							@Override
							public void handleMessage(Message msg)
							{
								super.handleMessage(msg);
//								if (msg.what != 1)
//								{
//									Toast.makeText(mContext, "加载失败，服务器开小差了", Toast.LENGTH_SHORT).show();
//								}
//								else
//								{
									System.out.println("获取的解释：" + wordMeaningString);
									descriptionTextView.setText(wordMeaningString);
									wordTextView.setText(s + " " + pronString);
									addWordLayout.setOnClickListener(new OnClickListener()
									{

										@Override
										public void onClick(View v)
										{
											Intent intent = new Intent();
											intent.setAction(AppConstant.Actions.PLAYERADAPTER_SEND_NEW_WORD);
											intent.putExtra("word", s);
											intent.putExtra("position", wordPosition);
											intent.putExtra("meaning", wordMeaningString);
											intent.putExtra("pron", pronString);
											mContext.sendBroadcast(intent);
											Toast.makeText(mContext, "单词添加成功", Toast.LENGTH_SHORT).show();
											dancibenWindow.dismiss();
										}
									});
//								}
							}
						};

						@Override
						public void run()
						{
							OnlineWordInfo onlineWordInfo = OnlineDictionaryXMLParser.parser(s);
							wordMeaningString = onlineWordInfo.getTranslation();
							pronString = onlineWordInfo.getPronunciation();
							getTransHandler.sendEmptyMessage(1);
//							if (onlineWordInfo.getPronunciation().equals("-1"))
//							{
//								getTransHandler.sendEmptyMessage(0);
//							}
//							else {
//								wordMeaningString = onlineWordInfo.getTranslation();
//								getTransHandler.sendEmptyMessage(1);
//							}

//							HashMap<String, String> headers = new HashMap<String, String>();
//							HashMap<String, String> params = new HashMap<String, String>();
//							headers.put("Cookie", "PHPSESSID=" + StaticInfos.phpsessid);
//							params.put("word", s);
//
//							try
//							{
//								HttpURLConnection urlConnection = (HttpURLConnection) HttpRequestUtil.sendGetRequest(
//										AppConstant.URL.WORD_MEANING_LIST_URL, params, headers);
//								responseCode = urlConnection.getResponseCode();
//								inputStream = urlConnection.getInputStream();
//								wordMeaningString = Toolkits.convertStreamToString(inputStream);
//								if (responseCode != 200)
//								{
//									getTransHandler.sendEmptyMessage(0);
//								}
//								else
//								{
//									getTransHandler.sendEmptyMessage(1);
//								}
//							} catch (Exception e)
//							{
//								e.printStackTrace();
//								getTransHandler.sendEmptyMessage(0);
//							}
						}
					}).start();
				}
			}

			@Override
			public void updateDrawState(TextPaint ds)
			{
				ds.setColor(Color.parseColor("#aa433939"));
				// ds.setColor(Color.parseColor("#42c0fb"));
				ds.setUnderlineText(false);
			}

		};
	}

}
