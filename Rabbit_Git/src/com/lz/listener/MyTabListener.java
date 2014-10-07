package com.lz.listener;

import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;


public class MyTabListener implements TabListener {
	private ViewPager mViewPager;
	
	
	
	public MyTabListener(ViewPager mViewPager){
		this.mViewPager=mViewPager;
	}
	
	public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
		
	}

	public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
		//点击tab后调到ViewPager的哪一个页面
		mViewPager.setCurrentItem(tab.getPosition());  
		
	}

	public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {

		
	}
}
