package com.lfl.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;

import com.nut.activity.R;

@SuppressWarnings("deprecation")
public class Fanting extends TabActivity
{
	private RadioGroup radioGroup;
	private TabHost tabHost;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fanting);


		radioGroup = (RadioGroup) findViewById(R.id.fanting_tab_radiogroup);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId)
			{
				RadioButton firstButton;
				RadioButton secondButton;
				RadioButton thirdButton;
				RadioButton forthButton;
				Drawable firstYDrawable, firstNDrawable;
				Drawable secondYDrawable, secondNDrawable;
				Drawable thirdYDrawable, thirdNDrawable;
				Drawable forthYDrawable, forthNDrawable;

				firstButton = (RadioButton) findViewById(R.id.fanting_tuijian_radiobutton);
				secondButton = (RadioButton) findViewById(R.id.fanting_dingyue_radiobutton);
				thirdButton = (RadioButton) findViewById(R.id.fanting_paihang_radiobutton);
				forthButton = (RadioButton) findViewById(R.id.fanting_search_radiobutton);

				firstNDrawable = getResources().getDrawable(R.drawable.fanting_tab_tuijian);
				firstYDrawable = getResources().getDrawable(R.drawable.fanting_tab_tuijian_selected);
				secondNDrawable = getResources().getDrawable(R.drawable.fanting_tab_dingyue);
				secondYDrawable = getResources().getDrawable(R.drawable.fanting_tab_dingyue_selected);
				thirdNDrawable = getResources().getDrawable(R.drawable.fanting_tab_paihang);
				thirdYDrawable = getResources().getDrawable(R.drawable.fanting_tab_paihang_selected);
				forthNDrawable = getResources().getDrawable(R.drawable.fanting_tab_search);
				forthYDrawable = getResources().getDrawable(R.drawable.fanting_tab_search_selected);

				firstNDrawable.setBounds(0, 0, firstNDrawable.getMinimumWidth(), firstNDrawable.getMinimumHeight());
				firstYDrawable.setBounds(0, 0, firstYDrawable.getMinimumWidth(), firstYDrawable.getMinimumHeight());

				secondNDrawable.setBounds(0, 0, secondNDrawable.getMinimumWidth(), secondNDrawable.getMinimumHeight());
				secondYDrawable.setBounds(0, 0, secondYDrawable.getMinimumWidth(), secondYDrawable.getMinimumHeight());

				thirdNDrawable.setBounds(0, 0, thirdNDrawable.getMinimumWidth(), thirdNDrawable.getMinimumHeight());
				thirdYDrawable.setBounds(0, 0, thirdYDrawable.getMinimumWidth(), thirdYDrawable.getMinimumHeight());

				forthNDrawable.setBounds(0, 0, forthNDrawable.getMinimumWidth(), forthNDrawable.getMinimumHeight());
				forthYDrawable.setBounds(0, 0, forthYDrawable.getMinimumWidth(), forthYDrawable.getMinimumHeight());

				switch (checkedId)
				{
				case R.id.fanting_tuijian_radiobutton:
					firstButton.setCompoundDrawables(null, firstYDrawable, null, null);
					secondButton.setCompoundDrawables(null, secondNDrawable, null, null);
					thirdButton.setCompoundDrawables(null, thirdNDrawable, null, null);
					forthButton.setCompoundDrawables(null, forthNDrawable, null, null);
					firstButton.setTextColor(Color.parseColor("#00bad2"));
					secondButton.setTextColor(Color.parseColor("#8f8a8a"));
					thirdButton.setTextColor(Color.parseColor("#8f8a8a"));
					forthButton.setTextColor(Color.parseColor("#8f8a8a"));
					tabHost.setCurrentTab(0);
					break;
				case R.id.fanting_dingyue_radiobutton:
					firstButton.setCompoundDrawables(null, firstNDrawable, null, null);
					secondButton.setCompoundDrawables(null, secondYDrawable, null, null);
					thirdButton.setCompoundDrawables(null, thirdNDrawable, null, null);
					forthButton.setCompoundDrawables(null, forthNDrawable, null, null);
					firstButton.setTextColor(Color.parseColor("#8f8a8a"));
					secondButton.setTextColor(Color.parseColor("#00bad2"));
					thirdButton.setTextColor(Color.parseColor("#8f8a8a"));
					forthButton.setTextColor(Color.parseColor("#8f8a8a"));
					tabHost.setCurrentTab(1);
					break;
				case R.id.fanting_paihang_radiobutton:
					firstButton.setCompoundDrawables(null, firstNDrawable, null, null);
					secondButton.setCompoundDrawables(null, secondNDrawable, null, null);
					thirdButton.setCompoundDrawables(null, thirdYDrawable, null, null);
					forthButton.setCompoundDrawables(null, forthNDrawable, null, null);
					firstButton.setTextColor(Color.parseColor("#8f8a8a"));
					secondButton.setTextColor(Color.parseColor("#8f8a8a"));
					thirdButton.setTextColor(Color.parseColor("#00bad2"));
					forthButton.setTextColor(Color.parseColor("#8f8a8a"));
					tabHost.setCurrentTab(2);
					break;
				case R.id.fanting_search_radiobutton:
					firstButton.setCompoundDrawables(null, firstNDrawable, null, null);
					secondButton.setCompoundDrawables(null, secondNDrawable, null, null);
					thirdButton.setCompoundDrawables(null, thirdNDrawable, null, null);
					forthButton.setCompoundDrawables(null, forthYDrawable, null, null);
					firstButton.setTextColor(Color.parseColor("#8f8a8a"));
					secondButton.setTextColor(Color.parseColor("#8f8a8a"));
					thirdButton.setTextColor(Color.parseColor("#8f8a8a"));
					forthButton.setTextColor(Color.parseColor("#00bad2"));
					tabHost.setCurrentTab(3); 
					break;
				}
			}
		});

		tabHost = getTabHost();

		TabHost.TabSpec spec;
		Intent intent;

		intent = new Intent().setClass(this, Fanting_tuijian.class);
		spec = tabHost.newTabSpec("fanting_tuijian").setIndicator("fanting_tuijian").setContent(intent);
		tabHost.addTab(spec);
		intent = new Intent().setClass(this, Fanting_dingyue.class);
		spec = tabHost.newTabSpec("fanting_dingyue").setIndicator("fanting_dingyue").setContent(intent);
		tabHost.addTab(spec);
		intent = new Intent().setClass(this, Fanting_paihang.class);
		spec = tabHost.newTabSpec("fanting_paihang").setIndicator("fanting_paihang").setContent(intent);
		tabHost.addTab(spec);
		intent = new Intent().setClass(this, Fanting_search.class);
		spec = tabHost.newTabSpec("fanting_search").setIndicator("fanting_search").setContent(intent);
		tabHost.addTab(spec);

		tabHost.setCurrentTab(0);
		
	}
	
}
