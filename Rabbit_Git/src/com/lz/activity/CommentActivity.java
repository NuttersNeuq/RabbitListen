package com.lz.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract.CommonDataKinds.Nickname;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hare.activity.R;
import com.lz.my.service.ImageService;
import com.lz.my.service.NetService;
import com.lz.utils.StaticInfos;
import com.lz.utils.Util;

public class CommentActivity extends Activity {
	private ActionBar actionBar;
	private Context context;
	private Handler handler;
	
	private NetService netService;
	
	private Button commentStar1BT;
	private Button commentStar2BT;
	private Button commentStar3BT;
	private Button commentStar4BT;
	private Button commentStar5BT;
	
	private Button commentHeart1BT;
	private Button commentHeart2BT;
	private Button commentHeart3BT;
	private Button commentHeart4BT;
	private Button commentHeart5BT;
	
	private TextView commentDifficultyTV;
	private TextView commentLikeTV;
	private TextView nicknameTV;
	private ImageView potraitIV;
	
	
	private MyOnClickListener myOnClickListener;
	
	private String lid;
	private String difficulty;
	private String like;
	
	private boolean difficultyTag1=false;
	private boolean difficultyTag2=false;
	private boolean difficultyTag3=false;
	private boolean difficultyTag4=false;
	private boolean difficultyTag5=false;
	
	private boolean likeTag1=false;
	private boolean likeTag2=false;
	private boolean likeTag3=false;
	private boolean likeTag4=false;
	private boolean likeTag5=false;
	
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment);
		initView();
		initData();
	}

	private void initView() {
		actionBar=getActionBar();
		context=getApplication();
		handler=new Handler();
		
		netService=new NetService();
		
		//拿到控件
		commentStar1BT=(Button) findViewById(R.id.comment_star1BT);
		commentStar2BT=(Button) findViewById(R.id.comment_star2BT);
		commentStar3BT=(Button) findViewById(R.id.comment_star3BT);
		commentStar4BT=(Button) findViewById(R.id.comment_star4BT);
		commentStar5BT=(Button) findViewById(R.id.comment_star5BT);
		
		commentHeart1BT=(Button) findViewById(R.id.comment_heart1BT);
		commentHeart2BT=(Button) findViewById(R.id.comment_heart2BT);
		commentHeart3BT=(Button) findViewById(R.id.comment_heart3BT);
		commentHeart4BT=(Button) findViewById(R.id.comment_heart4BT);
		commentHeart5BT=(Button) findViewById(R.id.comment_heart5BT);
		
		commentDifficultyTV=(TextView) findViewById(R.id.comment_difficultyTV);
		commentLikeTV=(TextView) findViewById(R.id.comment_likeTV);
		
		myOnClickListener=new MyOnClickListener();
		

		nicknameTV=(TextView) findViewById(R.id.comment_info_areaLL_nickname);
		potraitIV=(ImageView) findViewById(R.id.comment_info_areaLL_potrait);
		
		
		//lid从上个页面获取lid
		lid=getIntent().getStringExtra("lid");
	}

	
	private void initData() {
		Util.setTitle(context, actionBar, "评价");
		
		nicknameTV.setText(StaticInfos.nickname);
		
		try {
			potraitIV.setImageBitmap(StaticInfos.portraitBm);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		//添加监听器
		commentHeart1BT.setOnClickListener(myOnClickListener);
		commentHeart2BT.setOnClickListener(myOnClickListener);
		commentHeart3BT.setOnClickListener(myOnClickListener);
		commentHeart4BT.setOnClickListener(myOnClickListener);
		commentHeart5BT.setOnClickListener(myOnClickListener);
		commentStar1BT.setOnClickListener(myOnClickListener);
		commentStar2BT.setOnClickListener(myOnClickListener);
		commentStar3BT.setOnClickListener(myOnClickListener);
		commentStar4BT.setOnClickListener(myOnClickListener);
		commentStar5BT.setOnClickListener(myOnClickListener);
	}
	
	private class MyOnClickListener implements View.OnClickListener{

		public void onClick(View v) {
			int id=v.getId();
			switch (id) {
			case R.id.comment_star1BT:
				if(!difficultyTag1){
					commentStar1BT.setBackgroundResource(R.drawable.star_filled);
					difficultyTag1=true;
					difficulty="1";
					commentDifficultyTV.setText("太简单");
				}
				else{
					commentStar1BT.setBackgroundResource(R.drawable.star_empty);
					commentStar2BT.setBackgroundResource(R.drawable.star_empty);
					commentStar3BT.setBackgroundResource(R.drawable.star_empty);
					commentStar4BT.setBackgroundResource(R.drawable.star_empty);
					commentStar5BT.setBackgroundResource(R.drawable.star_empty);
					difficultyTag1=false;
					difficultyTag2=false;
					difficultyTag3=false;
					difficultyTag4=false;
					difficultyTag5=false;
					difficulty="0";
					commentDifficultyTV.setText("");
				}
				break;
				
			case R.id.comment_star2BT:
				if(!difficultyTag2){
					commentStar1BT.setBackgroundResource(R.drawable.star_filled);
					commentStar2BT.setBackgroundResource(R.drawable.star_filled);
					difficultyTag1=true;
					difficultyTag2=true;
					difficulty="2";
					commentDifficultyTV.setText("较简单");
				}else{
					commentStar2BT.setBackgroundResource(R.drawable.star_empty);
					commentStar3BT.setBackgroundResource(R.drawable.star_empty);
					commentStar4BT.setBackgroundResource(R.drawable.star_empty);
					commentStar5BT.setBackgroundResource(R.drawable.star_empty);
					difficultyTag2=false;
					difficultyTag3=false;
					difficultyTag4=false;
					difficultyTag5=false;
					difficulty="1";
					commentDifficultyTV.setText("太简单");
				}
				break;
				
			case R.id.comment_star3BT:
				if(!difficultyTag3){
					commentStar1BT.setBackgroundResource(R.drawable.star_filled);
					commentStar2BT.setBackgroundResource(R.drawable.star_filled);
					commentStar3BT.setBackgroundResource(R.drawable.star_filled);
					difficultyTag1=true;
					difficultyTag2=true;
					difficultyTag3=true;
					difficulty="3";
					commentDifficultyTV.setText("中等难");
				}else{
					commentStar3BT.setBackgroundResource(R.drawable.star_empty);
					commentStar4BT.setBackgroundResource(R.drawable.star_empty);
					commentStar5BT.setBackgroundResource(R.drawable.star_empty);
					difficultyTag3=false;
					difficultyTag4=false;
					difficultyTag5=false;
					difficulty="2";
					commentDifficultyTV.setText("较简单");
				}
				break;
			case R.id.comment_star4BT:
				if(!difficultyTag4){
					commentStar1BT.setBackgroundResource(R.drawable.star_filled);
					commentStar2BT.setBackgroundResource(R.drawable.star_filled);
					commentStar3BT.setBackgroundResource(R.drawable.star_filled);
					commentStar4BT.setBackgroundResource(R.drawable.star_filled);
					difficultyTag1=true;
					difficultyTag2=true;
					difficultyTag3=true;
					difficultyTag4=true;
					difficulty="4";
					commentDifficultyTV.setText("比较难");
				}else {
					commentStar4BT.setBackgroundResource(R.drawable.star_empty);
					commentStar5BT.setBackgroundResource(R.drawable.star_empty);
					difficultyTag4=false;
					difficultyTag5=false;
					difficulty="3";
					commentDifficultyTV.setText("中等难");
				}
				break;
				
			case R.id.comment_star5BT:
				if(!difficultyTag5){
					commentStar1BT.setBackgroundResource(R.drawable.star_filled);
					commentStar2BT.setBackgroundResource(R.drawable.star_filled);
					commentStar3BT.setBackgroundResource(R.drawable.star_filled);
					commentStar4BT.setBackgroundResource(R.drawable.star_filled);
					commentStar5BT.setBackgroundResource(R.drawable.star_filled);
					difficultyTag1=true;
					difficultyTag2=true;
					difficultyTag3=true;
					difficultyTag4=true;
					difficultyTag5=true;
					difficulty="5";
					commentDifficultyTV.setText("特别难");
				}else{
					commentStar5BT.setBackgroundResource(R.drawable.star_empty);
					difficultyTag5=false;
					difficulty="4";
					commentDifficultyTV.setText("比较难");
				}
				break;
				case R.id.comment_heart1BT:
					if(!likeTag1){
						commentHeart1BT.setBackgroundResource(R.drawable.heart_filled);
						likeTag1=true;
						like="1";
						commentLikeTV.setText("很差");
					}
					else{
						commentHeart1BT.setBackgroundResource(R.drawable.heart_empty);
						commentHeart2BT.setBackgroundResource(R.drawable.heart_empty);
						commentHeart3BT.setBackgroundResource(R.drawable.heart_empty);
						commentHeart4BT.setBackgroundResource(R.drawable.heart_empty);
						commentHeart5BT.setBackgroundResource(R.drawable.heart_empty);
						likeTag1=false;
						likeTag2=false;
						likeTag3=false;
						likeTag4=false;
						likeTag5=false;
						like="0";
						commentLikeTV.setText("");
					}
					break;
					
				case R.id.comment_heart2BT:
					if(!likeTag2){
						commentHeart1BT.setBackgroundResource(R.drawable.heart_filled);
						commentHeart2BT.setBackgroundResource(R.drawable.heart_filled);
						likeTag1=true;
						likeTag2=true;
						like="2";
						commentLikeTV.setText("较差");
					}else{
						commentHeart2BT.setBackgroundResource(R.drawable.heart_empty);
						commentHeart3BT.setBackgroundResource(R.drawable.heart_empty);
						commentHeart4BT.setBackgroundResource(R.drawable.heart_empty);
						commentHeart5BT.setBackgroundResource(R.drawable.heart_empty);
						likeTag2=false;
						likeTag3=false;
						likeTag4=false;
						likeTag5=false;
						like="1";
						commentLikeTV.setText("很差");
					}
					break;
					
				case R.id.comment_heart3BT:
					if(!likeTag3){
						commentHeart1BT.setBackgroundResource(R.drawable.heart_filled);
						commentHeart2BT.setBackgroundResource(R.drawable.heart_filled);
						commentHeart3BT.setBackgroundResource(R.drawable.heart_filled);
						likeTag1=true;
						likeTag2=true;
						likeTag3=true;
						like="3";
						commentLikeTV.setText("还行");
					}else{
						commentHeart3BT.setBackgroundResource(R.drawable.heart_empty);
						commentHeart4BT.setBackgroundResource(R.drawable.heart_empty);
						commentHeart5BT.setBackgroundResource(R.drawable.heart_empty);
						likeTag3=false;
						likeTag4=false;
						likeTag5=false;
						like="2";
						commentLikeTV.setText("较差");
					}
					break;
				case R.id.comment_heart4BT:
					if(!likeTag4){
						commentHeart1BT.setBackgroundResource(R.drawable.heart_filled);
						commentHeart2BT.setBackgroundResource(R.drawable.heart_filled);
						commentHeart3BT.setBackgroundResource(R.drawable.heart_filled);
						commentHeart4BT.setBackgroundResource(R.drawable.heart_filled);
						likeTag1=true;
						likeTag2=true;
						likeTag3=true;
						likeTag4=true;
						like="4";
						commentLikeTV.setText("推荐");
					}else {
						commentHeart4BT.setBackgroundResource(R.drawable.heart_empty);
						commentHeart5BT.setBackgroundResource(R.drawable.heart_empty);
						likeTag4=false;
						likeTag5=false;
						like="3";
						commentLikeTV.setText("还行");
					}
					break;
					
				case R.id.comment_heart5BT:
					if(!likeTag5){
						commentHeart1BT.setBackgroundResource(R.drawable.heart_filled);
						commentHeart2BT.setBackgroundResource(R.drawable.heart_filled);
						commentHeart3BT.setBackgroundResource(R.drawable.heart_filled);
						commentHeart4BT.setBackgroundResource(R.drawable.heart_filled);
						commentHeart5BT.setBackgroundResource(R.drawable.heart_filled);
						likeTag1=true;
						likeTag2=true;
						likeTag3=true;
						likeTag4=true;
						likeTag5=true;
						like="5";
						commentLikeTV.setText("力荐");
					}else{
						commentHeart5BT.setBackgroundResource(R.drawable.heart_empty);
						likeTag5=false;
						like="4";
						commentLikeTV.setText("推荐");
					}
					break;
			}
		}
		
	}
	
	//设置menu
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.comment, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		int id=item.getItemId();
		switch (id) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.menu_comment_post:
			//联网提交
			new Thread(){
				public void run() {
					try {
						if(difficulty==null||like==null){
							handler.post(new Runnable() {
								public void run() {
									Util.showToast(context, "请选择难度和喜爱程度");
									
								}
							});
							
						}else{
							
							netService.getComment(lid,difficulty,like);
							handler.post(new Runnable() {
								public void run() {
									Util.showToast(context, "评价成功");
									finish();
								}
							});
							
						}

					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("异常+++++"+e.toString());
					}
				}
			}.start();
			
			return true;
		}
		return false;
	}
	
	
}
