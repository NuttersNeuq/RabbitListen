package com.lz.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.lz.javabean.Note;
import com.lz.my.service.NoteService;
import com.lz.utils.Util;
import com.nut.activity.R;

public class NotePostActivity extends Activity {
	private ActionBar actionBar;
	private Context context;
	private Handler handler;
	
	private Note note;
	private NoteService noteService;
	private String lid;
	private String noteid;
	
	private EditText notePostTtileTV;
	private EditText notePostContentTV;
	
	private	boolean tag=true;   //true代表新建笔记  false代表编辑笔记
			
	

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_note_post);
		initView();
		initData();
	}
	
	
	private void initView() {
		actionBar=getActionBar();
		context=getApplication();
		handler=new Handler();
		
		note=(Note) getIntent().getSerializableExtra("note");
		noteService=new NoteService();
		
		notePostContentTV=(EditText) findViewById(R.id.note_post_contentET);
		notePostTtileTV=(EditText) findViewById(R.id.note_post_titleET);
		
		
		
	}

	private void initData() {
		//判断是编辑还是新建
		if(note==null)//新建，从intent中获取lid
		{
			lid=getIntent().getStringExtra("lid");
			//设置title
			Util.setTitle(context, actionBar, "新建笔记");
			//设置标签
			tag=true;
			
		}else			//编辑，从note中获取lid
		{	
			lid=note.getLid();
			noteid=note.getNoteid();
			notePostTtileTV.setText(note.getTitle());
			notePostContentTV.setText(note.getContent());
			//设置title
			Util.setTitle(context, actionBar, "编辑笔记");
			//设置标签
			tag=false;
		}
		
	}


	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.note_post_activity, menu);
		return true;
	}


	public boolean onOptionsItemSelected(MenuItem item) {
		int id=item.getItemId();
		switch(id){
			case android.R.id.home:
				finish();
				return true;
			case R.id.menu_note_post_post:
				if(tag)	//新建
				{
					//发送post请求
					final String title=notePostTtileTV.getText().toString();
					final String content=notePostContentTV.getText().toString();
					//判断
					if(title.equals("")||content.equals("")){
						Util.showToast(context, "不能为空");
						break;
					}
					new Thread(){
						public void run() {
							noteService.newNotePostRequest(title,content,lid);
							handler.post(new Runnable() {								
								public void run() {
									Util.showToast(context, "发送成功");
									finish();	
								}
							});
						}
						
					}.start();	

				}else	//编辑
				{
					//发送post请求
					final String title=notePostTtileTV.getText().toString();
					final String content=notePostContentTV.getText().toString();
					if(title.equals("")||content.equals("")){
						Util.showToast(context, "不能为空");
						break;
					}
					new Thread(){
						public void run() {
							noteService.updateNotePostRequest(title,content,noteid);
							handler.post(new Runnable() {								
								public void run() {
									Util.showToast(context, "编辑成功");
									Intent data=new Intent();
									data.putExtra("title", title);
									data.putExtra("content", content);
									data.putExtra("time", System.currentTimeMillis());
									setResult(100, data);
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
