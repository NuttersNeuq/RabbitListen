package com.lz.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.lz.my.service.BlogService;
import com.lz.utils.Util;
import com.hare.activity.R;

public class BlogPostActivity extends Activity {
	private ActionBar actionBar;
	private Context context;
	private Handler handler=new Handler(){
		public void handleMessage(Message msg) {
			if(msg.what==1){
				Util.showToast(context, "发送成功");
				finish();
			}
			
		}
	};
	
	private EditText blogPostTitleET;
	private EditText blogPostContentET;
	private BlogService blogService;
	

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blog_post);
		initView();
		initData();
	}
	

	private void initView() {
		actionBar = getActionBar();
		context = getApplication();
		
		blogService=new BlogService();
		blogPostTitleET=(EditText) findViewById(R.id.blog_post_titleET);
		blogPostContentET=(EditText) findViewById(R.id.blog_post_contentET);
		
	}

	private void initData() {
		Util.setTitle(context, actionBar, "发帖");
		
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.blog_post_activity, menu);
		return true;
	}


	public boolean onOptionsItemSelected(MenuItem item) {
		int id=item.getItemId();
		switch(id){
			case android.R.id.home:
				finish();
				return true;
			case R.id.menu_blog_post_post:
				//发送post请求
				final String title=blogPostTitleET.getText().toString();
				final String content=blogPostContentET.getText().toString();
				if(title.equals("")||content.equals("")){
					Util.showToast(context, "不能为空");
					break;
				}
				
				new Thread(){
					public void run() {
						blogService.newBlogPostRequest(title,content);
						Message msg=new Message();
						msg.what=1;
						handler.sendMessage(msg);
					}
					
				}.start();
				

		}
		
		return false;
	}


}
