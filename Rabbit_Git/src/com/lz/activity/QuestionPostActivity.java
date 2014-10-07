package com.lz.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.lz.my.service.QuestionService;
import com.lz.utils.Util;
import com.nut.activity.R;

public class QuestionPostActivity extends Activity {
	private ActionBar actionBar;
	private Context context;
	private Handler handler;
	
	private EditText questionPostTitleET;
	private EditText questionPostContentET;
	
	private QuestionService questionService;
	
	private String lid;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_question_post);
		initView();
		initData();
	}
	
	private void initView() {
		actionBar=getActionBar();
		context=getApplication();
		handler=new Handler();
		
		questionService=new QuestionService();
		questionPostTitleET=(EditText) findViewById(R.id.question_post_titleET);
		questionPostContentET=(EditText) findViewById(R.id.question_post_contentET);
		
		lid=getIntent().getStringExtra("lid");
		System.out.println("lid====="+lid);
		
		
	}
	
	private void initData() {
		//设置title
		Util.setTitle(context, actionBar, "提问");
		
	}
	
	//设置menu
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.question_post_activity, menu);
		return true;
	}


	public boolean onOptionsItemSelected(MenuItem item) {
		int id=item.getItemId();
		switch(id){
			case android.R.id.home:
				finish();
				return true;
			case R.id.menu_question_post_post:
				if(lid==null){
					//发送post请求
					final String title=questionPostTitleET.getText().toString();
					final String content=questionPostContentET.getText().toString();
					new Thread(){
						public void run() {
							questionService.newQuestionPostRequest("0",title,content);
							handler.post(new Runnable() {
								public void run() {
									Util.showToast(context, "发送成功");
									finish();
								}
							});
						}
						
					}.start();
				}else{
					//发送post请求
					final String title=questionPostTitleET.getText().toString();
					final String content=questionPostContentET.getText().toString();
					if(title.equals("")||content.equals("")){
						Util.showToast(context, "不能为空");
						break;
					}
					
					new Thread(){
						public void run() {
							questionService.newQuestionPostRequest(lid,title,content);
							handler.post(new Runnable() {
								public void run() {
									Util.showToast(context, "发送成功");
									finish();
								}
							});
						}
						
					}.start();
					
				}

				

		}
		
		return false;
	}




}
