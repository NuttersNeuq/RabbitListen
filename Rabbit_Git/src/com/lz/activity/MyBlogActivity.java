package com.lz.activity;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.lz.fragment.MyBlogFragment;
import com.lz.fragment.MyBlogReplyFragment;
import com.lz.listener.MyTabListener;
import com.lz.utils.Util;
import com.hare.activity.R;

public class MyBlogActivity extends FragmentActivity {
	private ActionBar actionBar;
	private Context context;
	private boolean isPersonalTag;
	private String uid;
	
    private static final int TAB_INDEX_COUNT=2;
    private static final int TAB_INDEX_ONE=0;
    private static final int TAB_INDEX_TWO=1;	//我的帖子，我的回复
    
	private Fragment myBlogFragment=new MyBlogFragment();	
	private Fragment myBlogReplyFragment=new MyBlogReplyFragment();
    
	private ViewPager mViewPager;
	private ViewPagerAdapter mViewPagerAdapter;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_blog);
		//初始化View
		initView();
		//初始化数据
		initData();
	}
	
	private void initView() {
		actionBar=getActionBar();
		context=getApplication();
		mViewPager = (ViewPager)findViewById(R.id.my_blog_pager); 
		
		isPersonalTag=getIntent().getBooleanExtra("isPersonalTag", true);
		uid=getIntent().getStringExtra("uid");
		
		//将uid值传过去
		Bundle args=new Bundle();
		args.putString("uid", uid);
		myBlogFragment.setArguments(args);
		myBlogReplyFragment.setArguments(args);
	}
	
	private void initData(){
		//设置Tab
		setUpActionBar();
		//设置ViewPager
		setUpViewPager();
	}
	
	private void setUpActionBar() {
		//如果开启的是个人页面
		if(isPersonalTag){
			//设置标题
			Util.setTitle(context, actionBar, "我的帖子");
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			actionBar.addTab(actionBar.newTab().setText("我的帖子").setTabListener(new MyTabListener(mViewPager)));
			actionBar.addTab(actionBar.newTab().setText("我的回复").setTabListener(new MyTabListener(mViewPager)));
		//如果开启的是他人页面
		}else {
			//设置标题
			Util.setTitle(context, actionBar, "他的帖子");
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			actionBar.addTab(actionBar.newTab().setText("他的帖子").setTabListener(new MyTabListener(mViewPager)));
			actionBar.addTab(actionBar.newTab().setText("他的回复").setTabListener(new MyTabListener(mViewPager)));
		}

		
	}

	private void setUpViewPager() {
		//ViewPager设置适配器
	      mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());  
	      mViewPager.setAdapter(mViewPagerAdapter);  
	    //设置滑动监听器
	      mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {  
	            public void onPageSelected(int position) {  
	                actionBar.setSelectedNavigationItem(position);  
	            }  
	            public void onPageScrollStateChanged(int state) {  
	                switch(state) {  
	                    case ViewPager.SCROLL_STATE_IDLE:  
	                        break;  
	                    case ViewPager.SCROLL_STATE_DRAGGING:  
	                        break;  
	                    case ViewPager.SCROLL_STATE_SETTLING:  
	                        break;  
	                    default:  
	                        break;  
	                }  
	            }  
	        });  
	}
	
    public class ViewPagerAdapter extends FragmentPagerAdapter {  
    	  
        public ViewPagerAdapter(android.support.v4.app.FragmentManager fragmentManager) {  
            super(fragmentManager);  
        }  
        
        public android.support.v4.app.Fragment getItem(int position) {  
        	Fragment mFragment=null;
            switch (position) {  
                case TAB_INDEX_ONE:  
                    return myBlogFragment; 
                case TAB_INDEX_TWO:  
                    return myBlogReplyFragment;  
            } 
            throw new IllegalStateException("No fragment at position " + position);  
        }  
  
        public int getCount() {  
            return TAB_INDEX_COUNT;  
        }  
          
        public CharSequence getPageTitle(int position) {  
            String tabLabel = null;  
            
            switch (position) {  
                case TAB_INDEX_ONE:  
                    tabLabel = "我的帖子";  
                    break;  
                case TAB_INDEX_TWO:  
                    tabLabel = "我的回复";  
                    break;  
            }  
            return tabLabel;  
        }  
    }
    
	//设置menu属性
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.nodisplay, menu);
		return super.onCreateOptionsMenu(menu);
		
	}
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case android.R.id.home:
			finish();
			break;
		}
		return false;
	}


	

	
}
