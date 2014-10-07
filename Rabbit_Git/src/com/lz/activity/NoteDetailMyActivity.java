package com.lz.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.sharesdk.framework.TitleLayout;

import com.lz.javabean.Note;
import com.lz.my.service.ImageService;
import com.lz.my.service.NoteService;
import com.lz.utils.Util;
import com.nut.activity.R;

public class NoteDetailMyActivity extends Activity {
	private ActionBar actionBar;
	private Context context;
	private Handler handler;
	
	private ImageService imageService;
	private NoteService noteService;
	
	private Bitmap portraitBitmap;
	private String portraitStr;
	
	private Note note;
	
	private TextView noteDetailMyTitleTV;
	private TextView noteDetailMyContentTV;
	private TextView noteDetailMyNicknameTV;
	private TextView noteDetailMyTimeTV;
	private TextView noteDetailMyZCountTV;
	private TextView noteDetailMyFCountTV;
	
	private ImageView noteDetailMyPortraitIV;


	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_note_my_detail);
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
		
		noteDetailMyContentTV=(TextView) findViewById(R.id.note_detail_my_contentTV);
		noteDetailMyTitleTV=(TextView) findViewById(R.id.note_detail_my_titleTV);
		noteDetailMyTimeTV=(TextView) findViewById(R.id.note_detail_my_timeTV);
		noteDetailMyNicknameTV=(TextView) findViewById(R.id.note_detail_my_nicknameTV);
		noteDetailMyFCountTV=(TextView) findViewById(R.id.note_detail_my_fcountTV);
		noteDetailMyZCountTV=(TextView) findViewById(R.id.note_detail_my_zcountTV);
		
		noteDetailMyPortraitIV=(ImageView) findViewById(R.id.note_detail_my_portraitIV);
	}
	
	private void initData() {
		//设置title
		Util.setTitle(context, actionBar, "我的笔记");
		//设置组件数据
		noteDetailMyContentTV.setText(note.getContent());
		noteDetailMyTitleTV.setText(note.getTitle());
		noteDetailMyTimeTV.setText(Util.getTime(note.getTime()));
		noteDetailMyNicknameTV.setText(note.getNickname());
		noteDetailMyFCountTV.setText(note.getfCount());
		noteDetailMyZCountTV.setText(note.getzCount());
		
		portraitStr=com.lz.utils.AppConstant.PORTRAIT_URL+note.getPortrait();
		
		
		
		//设置默认头像
		noteDetailMyPortraitIV.setImageResource(android.R.drawable.stat_notify_sync);
		
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
						noteDetailMyPortraitIV.setImageBitmap(portraitBitmap);
					}
				});
			}
			
		}.start();
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
	
	public void onNoteDetailMyEditClick(View v){
		//编辑我的笔记
		Intent intent=new Intent(context, NotePostActivity.class);
		intent.putExtra("note", note);
		startActivityForResult(intent, 100);
	}
	public void onNoteDetailMyDeleteClick(View v){
		
		AlertDialog.Builder builder=new AlertDialog.Builder(NoteDetailMyActivity.this);
		
		View confirmView=View.inflate(context, R.layout.dialog_confirm_view, null);
		
		builder.setView(confirmView);
		
		builder.create();
		
		final AlertDialog dialog=builder.show();
		
		dialog.show();
		
		Button dialogConfirmViewCancelBT=(Button) confirmView.findViewById(R.id.dialog_confirm_view_cancelBT);
		Button dialogConfirmViewConfirmBT=(Button) confirmView.findViewById(R.id.dialog_confirm_view_confirmBT);
		
		dialogConfirmViewCancelBT.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dialog.cancel();
			}
		});
		
		dialogConfirmViewConfirmBT.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//删除我的笔记
				new Thread(){
					public void run() {
						noteService.deleteMyNote(note.getNoteid());
					}
				}.start();
				Util.showToast(context, "删除成功");
				finish();
			}
		});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(data==null){
			return;
		}
			
		noteDetailMyTitleTV.setText(data.getStringExtra("title"));
		noteDetailMyContentTV.setText(data.getStringExtra("content"));
		noteDetailMyTimeTV.setText(Util.getTime(data.getLongExtra("time", System.currentTimeMillis())));
		
		note.setTitle(data.getStringExtra("title"));
		note.setContent(data.getStringExtra("content"));
		note.setTime(data.getLongExtra("time", System.currentTimeMillis()));
	}
}
