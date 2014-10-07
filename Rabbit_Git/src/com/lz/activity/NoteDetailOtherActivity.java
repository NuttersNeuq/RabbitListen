package com.lz.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.lz.javabean.Note;
import com.lz.my.service.ImageService;
import com.lz.my.service.NoteService;
import com.lz.utils.AppConstant;
import com.lz.utils.StaticInfos;
import com.lz.utils.Util;
import com.nut.activity.R;

public class NoteDetailOtherActivity extends Activity {

	private ActionBar actionBar;
	private Context context;
	private Handler handler;
	
	private ImageService imageService;
	private NoteService noteService;
	
	private Bitmap portraitBitmap;

	private String portraitStr;
	
	private Note note;
	
	private TextView noteDetailOtherTitleTV;
	private TextView noteDetailOtherContentTV;
	private TextView noteDetailOtherNicknameTV;
	private TextView noteDetailOtherTimeTV;
	private TextView noteDetailOtherZCountTV;
	private TextView noteDetailOtherFCountTV;
	
	private ImageView noteDetailOtherPortraitIV;
	
	private ImageButton noteDetailOtherZanIB;
	private ImageButton noteDetailOtherFollowIB;
	
	private boolean zanTag=false;     //true代表点了赞  false代表没有点赞
	private boolean followTag=false;
	
	


	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_note_other_detail);
		// 初始化View
		initView();
		// 初始化数据
		initData();
	}
	
	private void initView() {
		actionBar=getActionBar();
		context=getApplication();
		handler=new Handler();
		
		imageService=new ImageService(context);
		noteService=new NoteService();
		
		note=(Note) getIntent().getSerializableExtra("note");
		
		noteDetailOtherContentTV=(TextView) findViewById(R.id.note_detail_other_contentTV);
		noteDetailOtherTitleTV=(TextView) findViewById(R.id.note_detail_other_titleTV);
		noteDetailOtherTimeTV=(TextView) findViewById(R.id.note_detail_other_timeTV);
		noteDetailOtherNicknameTV=(TextView) findViewById(R.id.note_detail_other_nicknameTV);
		noteDetailOtherFCountTV=(TextView) findViewById(R.id.note_detail_other_fcountTV);
		noteDetailOtherZCountTV=(TextView) findViewById(R.id.note_detail_other_zcountTV);
		
		noteDetailOtherPortraitIV=(ImageView) findViewById(R.id.note_detail_other_portraitIV);
		
		noteDetailOtherZanIB=(ImageButton) findViewById(R.id.note_detail_other_zanIB);
		noteDetailOtherFollowIB=(ImageButton) findViewById(R.id.note_detail_other_followIB);
	}
	
	private void initData() {
		//设置title
		Util.setTitle(context, actionBar, "他的笔记");
		
		//设置组件数据
		noteDetailOtherContentTV.setText(note.getContent());
		noteDetailOtherTitleTV.setText(note.getTitle());
		noteDetailOtherTimeTV.setText(Util.getTime(note.getTime()));
		noteDetailOtherNicknameTV.setText(note.getNickname());
		noteDetailOtherFCountTV.setText("收藏:"+note.getfCount());
		noteDetailOtherZCountTV.setText("赞:"+note.getzCount());
		
		portraitStr=AppConstant.PORTRAIT_URL+note.getPortrait();
		
		
		
		//设置默认头像
		noteDetailOtherPortraitIV.setImageResource(android.R.drawable.stat_notify_sync);
		
		//设置头像
		new Thread(){
			public void run() {
				try {
					portraitBitmap=imageService.getImage(portraitStr);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				handler.post(new Runnable() {
					public void run() {
						noteDetailOtherPortraitIV.setImageBitmap(portraitBitmap);
					}
				});
			}
			
		}.start();
		
		
		//判断是否点过赞和是否收藏过
		if(note.isIff()){
			noteDetailOtherFollowIB.setImageResource(R.drawable.stard_for_note);
			followTag=true;
		}
		
		if(note.isIfz()){
			noteDetailOtherZanIB.setImageResource(R.drawable.liked_for_note);
			zanTag=true;
		}
		noteDetailOtherPortraitIV.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(note.getNickname().equals(StaticInfos.nickname)){
					Intent intent=new Intent(context, PersonalInfoActivity.class);
					intent.putExtra("isPersonalTag", true);	//传入标签
					startActivity(intent);	
				}else{
					Intent intent=new Intent(context, PersonalInfoActivity.class);
					intent.putExtra("isPersonalTag", false);	//传入标签
					intent.putExtra("nickname", note.getNickname());	//传入nickname
					startActivity(intent);		
				}
			}
		});
		//设置监听器
		noteDetailOtherFollowIB.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//改变button状态
				if(followTag){
					noteDetailOtherFollowIB.setImageResource(R.drawable.star_for_note);
					followTag=false;
				}else{
					noteDetailOtherFollowIB.setImageResource(R.drawable.stard_for_note);
					followTag=true;
				}
				//发送get请求
				new Thread(){
					public void run() {
						noteService.sendFollowGetRequest(followTag,note.getNoteid());
					}
				}.start();
				//showToast,变换数字
				if(followTag){
					note.setfCount((Integer.parseInt(note.getfCount())+1)+"");
					Util.showToast(context, "收藏成功");
					noteDetailOtherFCountTV.setText("收藏:"+note.getfCount());
					note.setIff(true);
				}
				else {
					note.setfCount((Integer.parseInt(note.getfCount())-1)+"");
					Util.showToast(context, "取消收藏");
					noteDetailOtherFCountTV.setText("收藏:"+note.getfCount());
					note.setIff(false);
				}
				

			}
		});
		
		noteDetailOtherZanIB.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				//改变button状态
				if(zanTag){
					noteDetailOtherZanIB.setImageResource(R.drawable.like_for_note);
					zanTag=false;
				}else{
					noteDetailOtherZanIB.setImageResource(R.drawable.liked_for_note);
					zanTag=true;
				}
				//发送get请求
				new Thread(){
					public void run() {
						noteService.sendZanGetRequest(zanTag,note.getNoteid());
					}
				}.start();
				
				//showToast，变换数字
				if(zanTag){
					note.setzCount((Integer.parseInt(note.getzCount())+1)+"");
					Util.showToast(context, "点赞成功");
					noteDetailOtherZCountTV.setText("赞:"+note.getzCount());
					note.setIfz(true);
				}
				else{
					note.setzCount((Integer.parseInt(note.getzCount())-1)+"");
					Util.showToast(context, "取消点赞");
					noteDetailOtherZCountTV.setText("赞:"+note.getzCount());
					note.setIfz(false);
				}
				
			}
		});
		
	}
	


	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.nodisplay, menu);
		return true;
	}


	public boolean onOptionsItemSelected(MenuItem item) {
		int id=item.getItemId();
		switch (id) {
		case android.R.id.home:
			finish();
			return true;
		}
		return false;
	}
	
	
	
	
}
