package com.lfl.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lfl.model.Mp3Info;
import com.lfl.utils.MillisecondConvert;
import com.hare.activity.R;

public class Tingliku_bendi extends ListActivity
{
	private TextView bianjiTextView;
	private TextView blankTextView;

	private void initWidgets()
	{
		bianjiTextView = (TextView) findViewById(R.id.tingliku_bendi_bianji_textview);

		bianjiTextView.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(Tingliku_bendi.this, Tingliku_bendi_bianji.class);
				startActivity(intent);

			}
		});
	}

	private void setBlankOrNormalLayout()
	{
		if (Diyijiemian.offlineSaver.getDownloadMp3Infos().size() == 0)
		{
			blankTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT));
		}
		else
		{
			blankTextView.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
			setListAdapter(new MyListAdapter());
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tingliku_bendi);

		blankTextView = (TextView) findViewById(R.id.tingliku_bendi_blank_textview);

		initWidgets();

	}

	@Override
	protected void onResume()
	{
		super.onResume();
		setBlankOrNormalLayout();

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);
		Intent intent = new Intent(Tingliku_bendi.this, OnlinePlayer.class);
		intent.putExtra("mp3Info", Diyijiemian.offlineSaver.getDownloadMp3Infos().get(position));
		intent.putExtra("mode", "local");
		startActivity(intent);
	}

	private class MyListAdapter extends BaseAdapter
	{

		@Override
		public int getCount()
		{
			return Diyijiemian.offlineSaver.getDownloadMp3Infos().size();
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
			final Mp3Info mp3Info = Diyijiemian.offlineSaver.getDownloadMp3Infos().get(position);
			convertView = getLayoutInflater().inflate(R.layout.tingliku_bendi_listitem, null);
			Button delButton = (Button) convertView.findViewById(R.id.tingliku_bendi_listitem_delete_button);
			// ImageView imageView = (ImageView)
			// convertView.findViewById(R.id.tingliku_bendi_listitem_imageview);
			TextView titleTextView = (TextView) convertView.findViewById(R.id.tingliku_bendi_listitem_title_textview);
			TextView durationTextView = (TextView) convertView
					.findViewById(R.id.tingliku_bendi_listitem_duration_textview);
			TextView sizeTextView = (TextView) convertView.findViewById(R.id.tingliku_bendi_listitem_size_textview);
			TextView difficultyTextView = (TextView) convertView
					.findViewById(R.id.tingliku_bendi_listitem_difficulty_textview);

			titleTextView.setText(mp3Info.getName());
			durationTextView.setText(MillisecondConvert.convert(Integer.parseInt(mp3Info.getDuration())));
			sizeTextView.setText(mp3Info.getSize());
			difficultyTextView.setText(mp3Info.getDifficulty());

			// String picPath = AppConstant.FilePath.PIC_FILE_PATH +
			// mp3Info.getPic();

			// imageView.setBackground(Drawable.createFromPath(picPath));

			delButton.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					Toast.makeText(Tingliku_bendi.this, "ÎÄ¼þÒÑÉ¾³ý", Toast.LENGTH_LONG).show();
					Diyijiemian.offlineSaver.removeMp3Info(mp3Info);
					setBlankOrNormalLayout();
				}
			});
			return convertView;
		}

	}

}
