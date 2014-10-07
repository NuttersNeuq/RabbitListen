package com.lz.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lz.javabean.Answer;
import com.lz.javabean.Question;
import com.lz.my.service.ImageService;
import com.lz.my.service.QuestionService;
import com.lz.mylistview.MyListView;
import com.lz.mylistview.MyListView.OnRefreshListener;
import com.lz.utils.AppConstant;
import com.lz.utils.StaticInfos;
import com.lz.utils.Util;
import com.nut.activity.R;

public class QuestionDetailActivity extends Activity {
	private ActionBar actionBar;
	private Context context;
	private Handler handler;
	
	private MyListView questionDetailLV;
	private MyListViewAdapter adapter;
	private MyOnItemClickListener myOnItemClickListener;
    private TextView moreTextView; //查看更多
    private LinearLayout loadProgressBar;//正在加载进度条
    private final int pageType=1;//msg的标签
	
	private Question question;
	private List<Answer> answers;
	
	private QuestionService questionService;
	private ImageService imageService;

//	private List<Bitmap> portraitList;
	private Bitmap	portraitOfHost;
	
	
	private MyOnClickListener myOnClickListener;
	
	private boolean followTag=false;
	//回复用到的组件
	private RelativeLayout questionDetailRL;
	private RelativeLayout questionDetailReplyRL;
	private EditText questionDetailReplyContentET;
	private Button questionDetailReplyButtonBT;
	//回复用的数据
	private String ansidForReply;
	private String nickNameForReply;
	
	private int limit=7;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_question_detail);
		// 初始化View
		initView();
		// 初始化数据
		initData();
		//为questionDetailRL设置触摸监听器
		questionDetailRL.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				questionDetailReplyRL.setVisibility(View.GONE);
				InputMethodManager imm=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				return true;
			}
		});
	}
	
	
	private void initView() {
		actionBar = getActionBar();
		context = getApplication();
		handler=  new Handler(){
	        public void handleMessage(Message msg) {
	            switch (msg.what) {
	            case pageType:
	                //通知适配器，发现改变操作
	                adapter.notifyDataSetChanged();
	                questionDetailLV.onRefreshComplete();
	                //再次显示"加载更多"
	                moreTextView.setVisibility(View.VISIBLE);
	                //再次隐藏“进度条”
	                loadProgressBar.setVisibility(View.GONE);
	                break;
	            default:
	                break;
	            }
	        }
	    };

		
		questionDetailReplyRL=(RelativeLayout) findViewById(R.id.question_detail_replyLL);
		questionDetailRL=(RelativeLayout) findViewById(R.id.question_detailRL);
		questionDetailReplyContentET=(EditText) findViewById(R.id.question_detail_reply_contentET);
		questionDetailReplyButtonBT=(Button) findViewById(R.id.question_detail_reply_buttonBT);
		
		questionDetailLV=(MyListView) findViewById(R.id.question_detailLV);
		addPageMore();
		adapter=new MyListViewAdapter();
		myOnItemClickListener=new MyOnItemClickListener();
		myOnClickListener=new MyOnClickListener();
		//拿到所有service
		imageService=new ImageService(context);
		questionService=new QuestionService();
		question=(Question) getIntent().getSerializableExtra("question");
	}

	private void initData() {

		
		//设置标题
		Util.setTitle(context, actionBar, "问题详情");
		
		//连接服务器获取Question对象的List集合,并对listview中的组件进行赋值
		new Thread(){
	
			public void run() {
				try {
					answers = questionService.getAnswers(question.getQid(),limit+"");
/*					//获取answer头像
					portraitList=new ArrayList<Bitmap>();
					for(int i=0;i<answers.size();i++){
						Answer answerForPor=answers.get(i);
						String portraitStr=com.lz.utils.AppConstant.PORTRAIT_URL+answerForPor.getPortrait();
						portraitList.add(imageService.getImage(portraitStr));
					}*/
					//获取楼主头像
					portraitOfHost=imageService.getImage(AppConstant.PORTRAIT_URL+question.getPortrait());
				} catch (Exception e) {
					
					handler.post(new Runnable() {
						public void run() {
						Toast.makeText(context, "网络连接错误", Toast.LENGTH_LONG).show();	
						}
					});
					
				}
				//为listview添加适配器
				handler.post(new Runnable() {
					public void run() {
						questionDetailLV.setAdapter(adapter);
					}
					
				});
			}			
			
		}.start();
		
		//设置监听器
		questionDetailLV.setOnItemClickListener(myOnItemClickListener);
		
		// 设置滚动刷新
		questionDetailLV.setonRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				new AsyncTask<Void, Void, Void>() {
					protected Void doInBackground(Void... params) {
						try {
							answers = questionService.getAnswers(question.getQid(),limit+"");
/*							//获取answer头像
							portraitList=new ArrayList<Bitmap>();
							for(int i=0;i<answers.size();i++){
								Answer answerForPor=answers.get(i);
								String portraitStr=com.lz.utils.AppConstant.PORTRAIT_URL+answerForPor.getPortrait();
								portraitList.add(imageService.getImage(portraitStr));
							}*/
						} catch (Exception e) {
							e.printStackTrace();
						}
						return null;
					}
					protected void onPostExecute(Void result) {
						adapter.notifyDataSetChanged();
						questionDetailLV.onRefreshComplete();
					}
				}.execute();
			}
		});	
		

		
	}
	
	//设置item点击监听器
	private class MyOnItemClickListener implements OnItemClickListener{
		public void onItemClick(AdapterView<?> arg0, View view, int position,long id) {
			questionDetailReplyRL.setVisibility(View.GONE);
			//关闭键盘
			InputMethodManager imm=(InputMethodManager)getSystemService(context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}


	//设置适配器
	private class MyListViewAdapter extends BaseAdapter{
		public int getCount() {
			if(answers==null){
				return 1;
			}else
				return answers.size()+1;
		}

		public Object getItem(int position) {
			return position;
		}


		public long getItemId(int position) {
			return position;
		}


		public View getView(int position, View convertView, ViewGroup parent) {
			View v ;
			if(position==0){
				v=View.inflate(context, R.layout.item_question_detail_first, null);
				//获取组件
				 TextView questionDetailTitleTV=(TextView) v.findViewById(R.id.question_detail_titleTV);
				 TextView questionDetailContentTV=(TextView) v.findViewById(R.id.question_detail_contentTV);
				 TextView questionDetailTimeTV=(TextView) v.findViewById(R.id.question_detail_tiemTV);
				 TextView questionDetailNicknameTV=(TextView) v.findViewById(R.id.question_detail_nicknameTV);
				 final ImageView questionDetailPortraitIV=(ImageView) v.findViewById(R.id.question_detail_portraitIV);
				 Button questionDetailFollowBT=(Button) v.findViewById(R.id.quesiton_detail_followBT);
				 Button questionDetailReplyBT=(Button)v.findViewById(R.id.question_detail_replyBT);
				//为button设置监听器
				 questionDetailFollowBT.setOnClickListener(myOnClickListener);
				 questionDetailReplyBT.setOnClickListener(myOnClickListener);
				 //判断是否关注过
				 if(question.getIff()){
					 questionDetailFollowBT.setBackgroundResource(R.drawable.forbutton2);
					 followTag=true;
				 }
				//设置值
				 questionDetailTitleTV.setText(question.getTitle());
				 questionDetailContentTV.setText(question.getContent());
				 questionDetailTimeTV.setText(Util.getTime(question.getTime()));
				 questionDetailNicknameTV.setText(question.getNickname());
				 questionDetailPortraitIV.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						if(question.getNickname().equals(StaticInfos.nickname)){
							Intent intent=new Intent(context, PersonalInfoActivity.class);
							intent.putExtra("isPersonalTag", true);	//传入标签
							startActivity(intent);	
						}else{
							Intent intent=new Intent(context, PersonalInfoActivity.class);
							intent.putExtra("isPersonalTag", false);	//传入标签
							intent.putExtra("nickname", question.getNickname());	//传入nickname
							startActivity(intent);		
						}
					}
				});
					// 设置默认头像
				 questionDetailPortraitIV.setImageBitmap(portraitOfHost);
			}else{
				
				v=initListView(position, convertView, parent);
			}
			
			
			return v;
		}


		private View initListView(int position, View convertView,ViewGroup parent) {
/*			if(portraitList.size()==0)
				return convertView;*/
			 View v=v=View.inflate(context, R.layout.item_question_detail_after, null);
			 TextView questionDetailAnsNicknameTV=(TextView) v.findViewById(R.id.question_detail_ans_nicknameTV);
			 TextView questionDetailAnsTimeTV=(TextView) v.findViewById(R.id.question_detail_ans_timeTV);
			 TextView questionDetailAnsContentTV=(TextView) v.findViewById(R.id.question_detail_ans_contentTV);
			 final ImageView questionDetailAnsPortraitIV=(ImageView) v.findViewById(R.id.question_detail_ans_portraitIV);
			 ImageButton questionDetailAnsZanBT=(ImageButton) v.findViewById(R.id.question_detail_ans_zanBT);
			 ImageButton questionDetailAnsReplyBT=(ImageButton) v.findViewById(R.id.question_detail_ans_replyBT);
			 questionDetailAnsZanBT.setTag(R.id.tag_position, position);
			 questionDetailAnsReplyBT.setTag(position);
			//为Button设置监听器
			 questionDetailAnsZanBT.setOnClickListener(myOnClickListener);
			 questionDetailAnsReplyBT.setOnClickListener(myOnClickListener);
			 
			 final Answer answer=answers.get(position-1);
			 //判断是否赞过
			 if(answer.isIfz()){
				 questionDetailAnsZanBT.setImageResource(R.drawable.liked);
				 questionDetailAnsZanBT.setTag(R.id.tag_zan_tag,true);
			 }else{
				 questionDetailAnsZanBT.setTag(R.id.tag_zan_tag, false);
			 }
			 if(answer.getTo().equals("")||answer.getTo()==null){
				 questionDetailAnsContentTV.setText(answer.getContent());
			 }else{
				 questionDetailAnsContentTV.setText("@"+answer.getTo()+":"+answer.getContent());
			 }
			 questionDetailAnsTimeTV.setText(Util.getTime(answer.getTime()));
			 questionDetailAnsNicknameTV.setText(answer.getNickname());
			 questionDetailAnsPortraitIV.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if(answer.getNickname().equals(StaticInfos.nickname)){
						Intent intent=new Intent(context, PersonalInfoActivity.class);
						intent.putExtra("isPersonalTag", true);	//传入标签
						startActivity(intent);	
					}else{
						Intent intent=new Intent(context, PersonalInfoActivity.class);
						intent.putExtra("isPersonalTag", false);	//传入标签
						intent.putExtra("nickname", answer.getNickname());	//传入nickname
						startActivity(intent);		
					}
				}
			});
			 //设置头像
/*			 if(position<=portraitList.size())
				 questionDetailAnsPortraitIV.setImageBitmap(portraitList.get(position-1));*/
			 //设置头像
			 new Thread(){
				public void run() {
					String portraitStr=com.lz.utils.AppConstant.PORTRAIT_URL+answer.getPortrait();
					try {
						 final Bitmap bm=imageService.getImage(portraitStr);
						 handler.post(new Runnable() {
							public void run() {
								questionDetailAnsPortraitIV.setImageBitmap(bm);
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			 }.start();
			return v;
		}
	}
	
	//设置menu
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
	
	public class MyOnClickListener implements View.OnClickListener{
		public void onClick(View v) {
			int position;
			final String ansid;
			InputMethodManager imm;
			switch(v.getId()){
				case R.id.question_detail_ans_zanBT:
					 position=(Integer) v.getTag(R.id.tag_position);
					 boolean zanTag=(Boolean) v.getTag(R.id.tag_zan_tag);
					 Answer answer=answers.get(position-1);
					 ansid=answer.getAnsid();
					 if(zanTag){
						 //取消点赞
						 new Thread(){
							public void run() {
								questionService.sendZanAnsGetRequest(ansid,false);
							}
						 }.start();
						 //改变图片，并提示
						 ImageButton ib=(ImageButton) v;
						 ib.setImageResource(R.drawable.like);
						 v.setTag(R.id.tag_zan_tag, false);
						 answer.setIfz(false);
						 Util.showToast(context, "取消点赞");

					 }else{
						 //点赞
						 new Thread(){

							public void run() {
								questionService.sendZanAnsGetRequest(ansid,true);
							}
							 
						 }.start();
						 //改变图片，并提示
						 ImageButton ib=(ImageButton) v;
						 ib.setImageResource(R.drawable.liked);
						 v.setTag(R.id.tag_zan_tag, true);
						 answer.setIfz(true);
						 Util.showToast(context, "点赞成功");
					 }
					 break;
				case R.id.question_detail_ans_replyBT:
					 position=(Integer) v.getTag();
					 Answer answer1=answers.get(position-1);
					 ansidForReply=answer1.getAnsid();
					 nickNameForReply=answer1.getNickname();
					//使回复窗口显示出来
					 questionDetailReplyRL.setVisibility(View.VISIBLE);
					 questionDetailReplyContentET.requestFocus();
					 Util.ToggleSoftInput(context);
					 //为回复窗口中的回复设置监听器
					 questionDetailReplyButtonBT.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {	
							if(questionDetailReplyContentET.getText().toString().equals("")){
								Util.showToast(context, "内容不能为空");
								return;
							}
							questionDetailReplyRL.setVisibility(View.GONE);
							Util.ToggleSoftInput(context);
							 new Thread(){
									public void run() {
										try {
											questionService.sendReplyAnswerPostRequest(question.getQid(),questionDetailReplyContentET.getText().toString(),ansidForReply,nickNameForReply);
											questionDetailReplyContentET.setText("");
											answers = questionService.getAnswers(question.getQid(),limit + "");
/*											// 获取answer头像
											portraitList = new ArrayList<Bitmap>();
											for (int i = 0; i < answers.size(); i++) {
												Answer answerForPor = answers.get(i);
												String portraitStr = com.lz.utils.AppConstant.PORTRAIT_URL+ answerForPor.getPortrait();
												portraitList.add(imageService.getImage(portraitStr));
											}*/
											handler.post(new Runnable() {
												public void run() {
													Util.showToast(context,"发送成功");
													adapter.notifyDataSetChanged();
												}
											});
										} catch (Exception e) {
											e.printStackTrace();
										}
									}						 
								 }.start();	
						}
					});
					break;
				case R.id.quesiton_detail_followBT:
					if(followTag){
						//取消关注
						followTag=false;
						new Thread(){
							public void run() {
								//发送取消关注请求
								questionService.sendFollowGetRequest(question.getQid(),followTag);
							}
						}.start();
						//更改图标
						v.setBackgroundResource(R.drawable.forbutton);
						Util.showToast(context, "取消关注");
						question.setIff(false);
					}else{
						//关注
						followTag=true;
						new Thread(){
							public void run() {
								//发送关注请求
								questionService.sendFollowGetRequest(question.getQid(),followTag);
							}
							
						}.start();
						//更改图标
						v.setBackgroundResource(R.drawable.forbutton2);
						Util.showToast(context, "关注成功");
						question.setIff(true);
					}
					break;
				case R.id.question_detail_replyBT:
					//回复楼主
					//是回复窗口显示出来
					 questionDetailReplyRL.setVisibility(View.VISIBLE);
					 questionDetailReplyContentET.requestFocus();
					 Util.ToggleSoftInput(context);
					 //为回复窗口中的回复设置监听器
					 questionDetailReplyButtonBT.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							if(questionDetailReplyContentET.getText().toString().equals("")){
								Util.showToast(context, "内容不能为空");
								return;
							}
							questionDetailReplyRL.setVisibility(View.GONE);
							Util.ToggleSoftInput(context);
							 new Thread(){
									public void run() {
										try{
											questionService.sendReplyQuestionPostRequest(question.getQid(),questionDetailReplyContentET.getText().toString());
											questionDetailReplyContentET.setText("");
											answers = questionService.getAnswers(question.getQid(),limit + "");
/*											// 获取answer头像
											portraitList = new ArrayList<Bitmap>();
											for (int i = 0; i < answers.size(); i++) {
												Answer answerForPor = answers.get(i);
												String portraitStr = com.lz.utils.AppConstant.PORTRAIT_URL+ answerForPor.getPortrait();
												portraitList.add(imageService.getImage(portraitStr));
											}*/
											handler.post(new Runnable() {
												public void run() {
													Util.showToast(context, "发送成功");
													adapter.notifyDataSetChanged();
												}
											});
										}catch(Exception e){
											e.getStackTrace();
										}
									}						 
								 }.start();	
						}
					});
					break;
			}
		}
	}
	
	private void addPageMore() {
        View view=View.inflate(context, R.layout.list_page_load, null);
        moreTextView=(TextView)view.findViewById(R.id.more_id);
        loadProgressBar=(LinearLayout)view.findViewById(R.id.load_id);
        moreTextView.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //隐藏"加载更多"
                moreTextView.setVisibility(View.GONE);
                //显示进度条
                loadProgressBar.setVisibility(View.VISIBLE);
                new Thread(new Runnable() {
                    public void run() {
                       //联网获取数据
                        try {
                        	limit=limit+10;
							answers = questionService.getAnswers(question.getQid(),limit+"");
							/*	//获取answer头像
							portraitList=new ArrayList<Bitmap>();
							for(int i=0;i<answers.size();i++){
								Answer answerForPor=answers.get(i);
								String portraitStr=com.lz.utils.AppConstant.PORTRAIT_URL+answerForPor.getPortrait();
								portraitList.add(imageService.getImage(portraitStr));
							}*/
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        
                        //加载数据：加载相关数据
                        Message msg=handler.obtainMessage(pageType);
                        handler.sendMessage(msg);
                    }
                }).start();
            }
        });
        questionDetailLV.addFooterView(view);
	}
}
