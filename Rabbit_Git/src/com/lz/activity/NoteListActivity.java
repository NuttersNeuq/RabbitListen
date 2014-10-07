package com.lz.activity;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.lz.fragment.AllNoteFragment;
import com.lz.fragment.MyNoteFragment;
import com.lz.listener.MyTabListener;
import com.lz.utils.Util;
import com.hare.activity.R;

public class NoteListActivity extends FragmentActivity{


	private ActionBar actionBar;
	private Context context;
	private String lid;
	
    private static final int TAB_INDEX_COUNT=2;
    private static final int TAB_INDEX_ONE=0;
    private static final int TAB_INDEX_TWO=1;
    

	private Fragment myNoteFragment=new MyNoteFragment();
	private Fragment allNoteFrament=new AllNoteFragment();
	
	private ViewPager mViewPager;
	private ViewPagerAdapter mViewPagerAdapter;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_note_list);
		//初始化View
		initView();
		//初始化数据
		initData();
	}

	private void initView() {
		actionBar=getActionBar();
		context=getApplication();
		mViewPager = (ViewPager)findViewById(R.id.note_list_pager); 
		//从分享页面的笔记按钮页面得来，并传到fragment
		lid=getIntent().getStringExtra("lid");
		Bundle args=new Bundle();
		args.putString("lid", lid);
		myNoteFragment.setArguments(args);
		allNoteFrament.setArguments(args);
	}
	
	private void initData() {

		//设置Tab
		setUpActionBar();
		//设置ViewPager
		setUpViewPager();
		
	}
	
	private void setUpActionBar() {
		//设置标题
		Util.setTitle(context, actionBar, "笔记");
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.addTab(actionBar.newTab().setText("我的笔记").setTabListener(new MyTabListener(mViewPager)));
		actionBar.addTab(actionBar.newTab().setText("所有笔记").setTabListener(new MyTabListener(mViewPager)));
		
	}

	private void setUpViewPager() {
		//ViewPager设置适配器
	      mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());  
	      mViewPager.setAdapter(mViewPagerAdapter);  
	    //设置滑动监听器
	      mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {  
	            public void onPageSelected(int position) {  
	                final ActionBar actionBar = getActionBar();  
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
	
    public class ViewPagerAdapter extends FragmentStatePagerAdapter{  
        public ViewPagerAdapter(android.support.v4.app.FragmentManager fragmentManager) {  
            super(fragmentManager);  
        }  
        
        public android.support.v4.app.Fragment getItem(int position) {  
        	Fragment mFragment=null;
            switch (position) {  
                case TAB_INDEX_ONE:  
                    return myNoteFragment; 
                case TAB_INDEX_TWO:  
                    return allNoteFrament;  
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
                    tabLabel = "我的笔记";  
                    break;  
                case TAB_INDEX_TWO:  
                    tabLabel = "所有笔记";  
                    break;  
            }  
            return tabLabel;  
        }  
    }
	
	
	//设置menu属性
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.note_list_activity, menu);
		return super.onCreateOptionsMenu(menu);
		
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		int id=item.getItemId();
		switch (id) {
		case android.R.id.home:
			finish();
			return true;
			
		case R.id.menu_note_list_post:
			Intent intent=new Intent(context, NotePostActivity.class);
			intent.putExtra("lid", lid);
			startActivity(intent);
			return true;
		}
		return false;
	}


}
