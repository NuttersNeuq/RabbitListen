package com.lfl.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lz.activity.LoginActivity;
import com.nut.activity.R;

public class Help extends Activity
{
	private ViewPager viewPager;
	private List<View> viewList = new ArrayList<View>();
	private ImageView[] spots;
	private ViewGroup group;

	/**
	 * 设置选中的tip的背景
	 * 
	 * @param selectItems
	 */
	private void setImageBackground(int selectItems)
	{
		for (int i = 0; i < spots.length; i++)
		{
			if (i == selectItems)
			{
				spots[i].setImageResource(R.drawable.help_spot_seleted);
			}
			else
			{
				spots[i].setImageResource(R.drawable.help_spot);
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help_general);

		viewPager = (ViewPager) findViewById(R.id.help_viewpager);
		group = (ViewGroup) findViewById(R.id.help_viewgroup);

		View helpView_1 = getLayoutInflater().inflate(R.layout.help_1, null);
		View helpView_2 = getLayoutInflater().inflate(R.layout.help_2, null);
		View helpView_3 = getLayoutInflater().inflate(R.layout.help_3, null);
		View helpView_4 = getLayoutInflater().inflate(R.layout.help_4, null);
		View helpView_5 = getLayoutInflater().inflate(R.layout.help_5, null);
		View helpView_6 = getLayoutInflater().inflate(R.layout.help_6, null);
		View helpView_7 = getLayoutInflater().inflate(R.layout.help_7, null);

		viewList.add(helpView_1);
		viewList.add(helpView_2);
		viewList.add(helpView_3);
		viewList.add(helpView_4);
		viewList.add(helpView_5);
		viewList.add(helpView_6);
		viewList.add(helpView_7);
		
		Button okButton = (Button) helpView_7.findViewById(R.id.help_7_ok_button);
		okButton.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				/**
				 * 此处输入对button的处理
				 */
				Intent intent=new Intent(Help.this, LoginActivity.class);
				startActivity(intent);
				finish();
			}
		});

		spots = new ImageView[viewList.size()];
		for (int i = 0; i < spots.length; i++)
		{
			ImageView imageView = new ImageView(this);
			imageView.setLayoutParams(new LayoutParams(5, 5));
			spots[i] = imageView;
			spots[i].setPadding(5, 5, 5, 5);
			if (i == 0)
			{
				spots[i].setImageResource(R.drawable.help_spot_seleted);
			}
			else
			{
				spots[i].setImageResource(R.drawable.help_spot);
			}
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			layoutParams.leftMargin = 5;
			layoutParams.rightMargin = 5;
			group.addView(imageView, layoutParams);
		}

		viewPager.setOnPageChangeListener(new OnPageChangeListener()
		{

			@Override
			public void onPageSelected(int arg0)
			{
				setImageBackground(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2)
			{

			}

			@Override
			public void onPageScrollStateChanged(int arg0)
			{

			}
		});

		viewPager.setAdapter(new MyViewPagerAdapter());
		viewPager.setCurrentItem(0);
	}

	private class MyViewPagerAdapter extends PagerAdapter
	{

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
			container.removeView(viewList.get(position));
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
