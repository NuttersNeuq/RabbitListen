package com.lz.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.lz.my.service.NetService;
import com.lz.utils.Util;
import com.nut.activity.R;

public class FeedBackActivity extends Activity {
	private Context context;
	private ActionBar actionBar;
	private Handler handler;
	private TextView titleTV;
	private TextView contentTV;
	private NetService netService;
	

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feed_back);
		initView();
		initData();
	}
	
	private void initView() {
		context=getApplication();
		actionBar=getActionBar();
		handler=new Handler();
		netService=new NetService();
		titleTV=(TextView) findViewById(R.id.feedback_titleET);
		contentTV=(TextView) findViewById(R.id.feedback_contentET);
	}
	
	private void initData() {
		//设置actionBar的title
		Util.setTitle(context, actionBar, "反馈");
		
	}


	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.feed_back, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		int id=item.getItemId();
		switch (id) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.menu_feed_back_post:
			//增加判断，不能为空
			if(titleTV.getText().toString().equals("")||contentTV.getText().toString().equals("")){
				Util.showToast(context, "内容不能为空");
				return false;
			}else{
				new Thread(){
					public void run() {
						try {
							String isSuccess=netService.postFeedBack(titleTV.getText().toString(),contentTV.getText().toString());
							if(isSuccess.equals("1")){
								handler.post(new Runnable() {
									public void run() {
										Util.showToast(context, "反馈成功");
										finish();
									}
								});
							}else
							{
								handler.post(new Runnable() {
									public void run() {
										Util.showToast(context, "网络似乎短路了");
										finish();
									}
								});
							}

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}.start();
			}
			return true;

		}
		return false;
	}
}
