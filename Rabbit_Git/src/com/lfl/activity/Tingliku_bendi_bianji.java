package com.lfl.activity;

import java.util.Arrays;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lfl.model.Mp3Info;
import com.lfl.utils.MillisecondConvert;
import com.nut.activity.R;

public class Tingliku_bendi_bianji extends ListActivity
{
	private TextView selectAllTextView;
	private TextView deleteTextView;

	private boolean isSelected[] = new boolean[Diyijiemian.offlineSaver.getDownloadListSize()];
	private int count = 0;
	private boolean isSelectedAll = false;

	private void initWidgets()
	{
		deleteTextView = (TextView) findViewById(R.id.tingliku_bendi_bianji_ok_textview);
		selectAllTextView = (TextView) findViewById(R.id.tingliku_bendi_bianji_quanxuan_textview);

		deleteTextView.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				for (int i = 0; i < isSelected.length; i++)
				{
					if (isSelected[i])
					{
						Diyijiemian.offlineSaver.removeMp3Info(Diyijiemian.offlineSaver.getDownloadMp3Infos().get(i));
						setListAdapter(new MyListAdapter());
					}
				}
				Toast.makeText(Tingliku_bendi_bianji.this, "成功删除 " + count + " 个文件", Toast.LENGTH_SHORT).show();
				finish();
			}
		});

		selectAllTextView.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (isSelectedAll == false)
				{
					isSelectedAll = true;
					Arrays.fill(isSelected, true);
					setListAdapter(new MyListAdapter());
					selectAllTextView.setText("取消全选");
					deleteTextView.setText("确定（" + isSelected.length + "）");
					count = isSelected.length;
				}
				else
				{
					isSelectedAll = false;
					Arrays.fill(isSelected, false);
					setListAdapter(new MyListAdapter());
					selectAllTextView.setText("全选");
					deleteTextView.setText("确定（" + 0 + "）");
					count = 0;
				}
			}
		});
		Arrays.fill(isSelected, false);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tingliku_bendi_bianji);

		initWidgets();
		setListAdapter(new MyListAdapter());
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		ImageView spotView = (ImageView) v.findViewById(R.id.tingliku_bendi_bianji_listitem_listitem_spot_imageview);
		if (isSelected[position] == false)
		{
			spotView.setImageResource(R.drawable.tingliku_xiazai_bianji_spot_selected);
			isSelected[position] = true;
			count++;
		}
		else
		{
			spotView.setImageResource(R.drawable.tingliku_xiazai_bianji_spot);
			isSelected[position] = false;
			count--;
		}
		if (count == isSelected.length)
		{
			selectAllTextView.setText("取消全选");
			isSelectedAll = false;
		}
		deleteTextView.setText("确定（" + count + "）");
		super.onListItemClick(l, v, position, id);
	}

	private class MyListAdapter extends BaseAdapter
	{

		@Override
		public int getCount()
		{
			return Diyijiemian.offlineSaver.getDownloadListSize();
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
			convertView = getLayoutInflater().inflate(R.layout.tingliku_bendi_bianji_listitem, null);
			Mp3Info currentInfo = Diyijiemian.offlineSaver.getDownloadMp3Infos().get(position);
			ImageView spotView = (ImageView) convertView
					.findViewById(R.id.tingliku_bendi_bianji_listitem_listitem_spot_imageview);
			TextView titleTextView = (TextView) convertView
					.findViewById(R.id.tingliku_bendi_bianji_listitem_title_textview);
			TextView durationTextView = (TextView) convertView
					.findViewById(R.id.tingliku_bendi_bianji_listitem_listitem_duration_textview);
			TextView sizeTextView = (TextView) convertView
					.findViewById(R.id.tingliku_bendi_bianji_listitem_size_textview);
			TextView difficultyTextView = (TextView) convertView
					.findViewById(R.id.tingliku_bendi_bianji_listitem_difficulty_textview);

			titleTextView.setText(currentInfo.getName());
			durationTextView.setText(MillisecondConvert.convert(Integer.parseInt(currentInfo.getDuration())));
			sizeTextView.setText(currentInfo.getSize() + "MB");
			difficultyTextView.setText("Lev " + currentInfo.getDifficulty());

			if (isSelected[position])
				spotView.setImageResource(R.drawable.tingliku_xiazai_bianji_spot_selected);
			else
				spotView.setImageResource(R.drawable.tingliku_xiazai_bianji_spot);
			return convertView;
		}

	}
}
