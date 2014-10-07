package com.lz.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
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
import com.lz.javabean.Blog;
import com.lz.my.service.BlogService;
import com.lz.my.service.ImageService;
import com.lz.mylistview.MyListView;
import com.lz.mylistview.MyListView.OnRefreshListener;
import com.lz.utils.AppConstant;
import com.lz.utils.StaticInfos;
import com.lz.utils.Util;
import com.hare.activity.R;

public class BlogDetailActivity extends Activity {
	private ActionBar actionBar;
	private Context context;
	private Handler handler;
	
	private MyListView blogDetailLV;
	private MyListViewAdapter adapter;
	private MyOnItemClickListener myOnItemClickListener;
    private TextView moreTextView; //查看更多
    private LinearLayout loadProgressBar;//正在加载进度条
    private final int pageType=1;//msg的标签
	
    
	private Blog blog;
	private List<Answer> answers;
	
	private BlogService blogService;
	private ImageService imageService;

	//private List<Bitmap> portraitList;
	private Bitmap	portraitOfHost;
	
	private MyOnClickListener myOnClickListener;
	
	private boolean zanTag=false;
	//回复用到的组件
	private RelativeLayout blogDetailRL;
	private RelativeLayout blogDetailReplyRL;
	private EditText blogDetailReplyContentET;
	private Button blogDetailReplyButtonBT;
	//回复用的数据
	private String ansidForReply;
	private String nickNameForReply;
	
	private int limit=7;
	private Dialog loadingDialog;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blog_detail);
		// 初始化View
		initView();
		// 初始化数据
		initData();
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
	                blogDetailLV.onRefreshComplete();
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
		
		blogDetailReplyRL=(RelativeLayout) findViewById(R.id.blog_detail_replyRL);
		blogDetailRL=(RelativeLayout) findViewById(R.id.blog_detailRL);
		blogDetailReplyContentET=(EditText) findViewById(R.id.blog_detail_reply_contentET);
		blogDetailReplyButtonBT=(Button) findViewById(R.id.blog_detail_reply_buttonBT);
		
		blogDetailLV=(MyListView) findViewById(R.id.blog_detailLV);
		addPageMore();
		adapter=new MyListViewAdapter();
		myOnClickListener=new MyOnClickListener();
		myOnItemClickListener=new MyOnItemClickListener();
		
		//拿到所有service
		imageService=new ImageService(context);
		blogService=new BlogService();
		blog=(Blog) getIntent().getSerializableExtra("blog");
		
	}
	
	private void initData() {
		//设置标题
		Util.setTitle(context, actionBar, "帖子正文");
		//连接服务器获取blog对象的List集合,并对listview中的组件进行赋值
		new Thread(){
			public void run() {
				try {
					answers = blogService.getAnswers(blog.getBid(),limit+"");
/*					//获取answer头像
					portraitList=new ArrayList<Bitmap>();
					for(int i=0;i<answers.size();i++){
						Answer answerForPor=answers.get(i);
						String portraitStr=com.lz.utils.AppConstant.PORTRAIT_URL+answerForPor.getPortrait();
						portraitList.add(imageService.getImage(portraitStr));
					}*/
					//获取楼主头像
					portraitOfHost=imageService.getImage(AppConstant.PORTRAIT_URL+blog.getPortrait());
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
						blogDetailLV.setAdapter(adapter);
					}
				});
			}			
		}.start();
		//为listview设置监听器
		blogDetailLV.setOnItemClickListener(myOnItemClickListener);
		// 设置滚动刷新
		blogDetailLV.setonRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				new AsyncTask<Void, Void, Void>() {
					protected Void doInBackground(Void... params) {
						try {
							answers = blogService.getAnswers(blog.getBid(),limit+"");
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
						blogDetailLV.onRefreshComplete();
					}
				}.execute();
			}
		});
	}
	//设置item点击监听器
	private class MyOnItemClickListener implements OnItemClickListener{
		public void onItemClick(AdapterView<?> arg0, View view, int position,long id) {
			blogDetailReplyRL.setVisibility(View.GONE);
			//关闭键盘
			InputMethodManager imm=(InputMethodManager)getSystemService(context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}
	private class MyListViewAdapter extends BaseAdapter {
		public int getCount() {
			if(answers==null)
				return 1;
			else
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
				v=View.inflate(getApplication(), R.layout.item_blog_detail_first, null);
				//获取组件
				 TextView blogDetailTitleTV=(TextView) v.findViewById(R.id.blog_detail_titleTV);
				 TextView blogDetailContentTV=(TextView) v.findViewById(R.id.blog_detail_contentTV);
				 TextView blogDetailTimeTV=(TextView) v.findViewById(R.id.blog_detail_timeTV);
				 TextView blogDetailNicknameTV=(TextView) v.findViewById(R.id.blog_detail_nicknameTV);
				 final ImageView blogDetailPortraitIV=(ImageView) v.findViewById(R.id.blog_detail_portraitIV);
				 Button blogDetailZanBT=(Button) v.findViewById(R.id.blog_detail_zanBT);
				 Button blogDetailReplyBT=(Button)v.findViewById(R.id.blog_detail_replyBT);
				 
				//为button设置监听器
				 blogDetailZanBT.setOnClickListener(myOnClickListener);
				 blogDetailReplyBT.setOnClickListener(myOnClickListener);
				 blogDetailPortraitIV.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						if(blog.getNickname().equals(StaticInfos.nickname)){
							Intent intent=new Intent(context, PersonalInfoActivity.class);
							intent.putExtra("isPersonalTag", true);	//传入标签
							startActivity(intent);	
						}else{
							Intent intent=new Intent(context, PersonalInfoActivity.class);
							intent.putExtra("isPersonalTag", false);	//传入标签
							intent.putExtra("nickname",blog.getNickname());	//传入nickname
							startActivity(intent);		
						}
					}
				});
				 //判断是否关注过
				 if(blog.isIfz()){
					 blogDetailZanBT.setBackgroundResource(R.drawable.forbutton2);
					 zanTag=true;
				 }
				//设置值
				 blogDetailTitleTV.setText(blog.getTitle());
				 blogDetailContentTV.setText(blog.getContent());
				 blogDetailTimeTV.setText(Util.getTime(blog.getTime()));
				 blogDetailNicknameTV.setText(blog.getNickname());
				// 设置默认头像
				 blogDetailPortraitIV.setImageBitmap(portraitOfHost);
			}else{
				v=initListView(position, convertView, parent);
			}
			return v;
		}
	}
	
	//设置menu
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.nodisplay, menu);
		return true;
	}
	
	public View initListView(int position, View convertView, ViewGroup parent) {
/*		if(portraitList.size()==0)
			return convertView;*/
		 final Answer answer=answers.get(position-1);
		 View v=v=View.inflate(context, R.layout.item_blog_detail_after, null);
		 TextView blogDetailAnsNicknameTV=(TextView) v.findViewById(R.id.blog_detail_ans_nicknameTV);
		 TextView blogDetailAnsTimeTV=(TextView) v.findViewById(R.id.blog_detail_ans_timeTV);
		 TextView blogDetailAnsContentTV=(TextView) v.findViewById(R.id.blog_detail_ans_contentTV);
		 final ImageView blogDetailAnsPortraitIV=(ImageView) v.findViewById(R.id.blog_detail_ans_portraitIV);
		 ImageButton blogDetailAnsZanIB=(ImageButton) v.findViewById(R.id.blog_detail_ans_zanBT);
		 ImageButton blogDetailAnsReplyIB=(ImageButton) v.findViewById(R.id.blog_detail_ans_replyBT);
		 blogDetailAnsZanIB.setTag(R.id.tag_position,position);
		 blogDetailAnsReplyIB.setTag(position);
		//为Button设置监听器
		 blogDetailAnsZanIB.setOnClickListener(myOnClickListener);
		 blogDetailAnsReplyIB.setOnClickListener(myOnClickListener);
		 blogDetailAnsPortraitIV.setOnClickListener(new OnClickListener() {
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
		 
		 //判断是否赞过
		 if(answer.isIfz()){
			 blogDetailAnsZanIB.setImageResource(R.drawable.liked);
			 blogDetailAnsZanIB.setTag(R.id.tag_zan_tag,true);
		 }else{
			 blogDetailAnsZanIB.setTag(R.id.tag_zan_tag, false);
		 }
		 //判断是否回复楼主
		 if(answer.getTo()==null||answer.getTo().equals("")){
			 blogDetailAnsContentTV.setText(answer.getContent());
		 }else{
			 blogDetailAnsContentTV.setText("@"+answer.getTo()+":"+answer.getContent());
		 }
		 
		 blogDetailAnsTimeTV.setText(Util.getTime(answer.getTime()));
		 blogDetailAnsNicknameTV.setText(answer.getNickname());
		 
		 //设置头像
		 new Thread(){
			public void run() {
				String portraitStr=com.lz.utils.AppConstant.PORTRAIT_URL+answer.getPortrait();
				try {
					 final Bitmap bm=imageService.getImage(portraitStr);
					 handler.post(new Runnable() {
						public void run() {
							blogDetailAnsPortraitIV.setImageBitmap(bm);
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		 }.start();
/*		// 设置头像
		 if(position>=1&&position<=portraitList.size())
			 blogDetailAnsPortraitIV.setImageBitmap(portraitList.get(position-1));*/
		 //设置监听器
		return v;
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
				case R.id.blog_detail_ans_zanBT:
					 position=(Integer) v.getTag(R.id.tag_position);
					 boolean zanAnsTag=(Boolean) v.getTag(R.id.tag_zan_tag);
					 Answer answer=answers.get(position-1);
					 ansid=answer.getAnsid();
					 if(zanAnsTag){
						 //取消点赞
						 new Thread(){
							public void run() {
								blogService.sendZanAnsGetRequest(ansid,false);
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
								blogService.sendZanAnsGetRequest(ansid,true);
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
				case R.id.blog_detail_ans_replyBT:
					 position=(Integer) v.getTag();
					 Answer answer1=answers.get(position-1);
					 ansidForReply=answer1.getAnsid();
					 nickNameForReply=answer1.getNickname();
					//使回复窗口显示出来
					 blogDetailReplyRL.setVisibility(View.VISIBLE);
					 blogDetailReplyContentET.requestFocus();
					 Util.ToggleSoftInput(context);//打开键盘
					 //为回复窗口中的回复设置监听器
					 blogDetailReplyButtonBT.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {		
							if(blogDetailReplyContentET.getText().toString().equals("")){
								Util.showToast(context, "内容不能为空");
								return;
							}
							blogDetailReplyRL.setVisibility(View.GONE);
							Util.ToggleSoftInput(context);
							 new Thread(){
									public void run() {
										try{
										blogService.sendReplyAnswerPostRequest(blog.getBid(),blogDetailReplyContentET.getText().toString(),ansidForReply,nickNameForReply);
										blogDetailReplyContentET.setText("");
										answers = blogService.getAnswers(blog.getBid(),limit+"");
/*										//获取answer头像
										portraitList=new ArrayList<Bitmap>();
										for(int i=0;i<answers.size();i++){
											Answer answerForPor=answers.get(i);
											String portraitStr=com.lz.utils.AppConstant.PORTRAIT_URL+answerForPor.getPortrait();
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
				case R.id.blog_detail_zanBT:
					if(zanTag){
						//取消赞
						zanTag=false;
						new Thread(){
							public void run() {
								//发送取消赞请求
								blogService.sendZanGetRequest(blog.getBid(),zanTag);
							}
							
						}.start();
						
						//更改图标
						v.setBackgroundResource(R.drawable.forbutton);
						Util.showToast(context, "取消点赞");
						blog.setIfz(false);
					}else{
						//赞
						zanTag=true;
						new Thread(){
							public void run() {
								//发送点赞请求
								blogService.sendZanGetRequest(blog.getBid(),zanTag);
							}
							
						}.start();
						//更改图标
						v.setBackgroundResource(R.drawable.forbutton2);
						Util.showToast(context, "点赞成功");
						blog.setIfz(true);
					}
					break;
				case R.id.blog_detail_replyBT:
					//是回复窗口显示出来
					 blogDetailReplyRL.setVisibility(View.VISIBLE);
					 blogDetailReplyContentET.requestFocus();
					 Util.ToggleSoftInput(context);	//显示键盘
					 //为回复窗口中的回复设置监听器
					 blogDetailReplyButtonBT.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							if(blogDetailReplyContentET.getText().toString().equals("")){
								Util.showToast(context, "内容不能为空");
								return;
							}
							blogDetailReplyRL.setVisibility(View.GONE);
							Util.ToggleSoftInput(context);
							 new Thread(){
									public void run() {
										try {
											blogService.sendReplyQuestionPostRequest(blog.getBid(),blogDetailReplyContentET.getText().toString());
											blogDetailReplyContentET.setText("");
											answers = blogService.getAnswers(blog.getBid(),limit+"");
											//获取answer头像
/*											int len=portraitList.size();
											for(int i=len;i<answers.size();i++){
												Answer answerForPor=answers.get(i);
												String portraitStr=com.lz.utils.AppConstant.PORTRAIT_URL+answerForPor.getPortrait();
												portraitList.add(imageService.getImage(portraitStr));
											}*/
											handler.post(new Runnable() {
												public void run() {
													Util.showToast(context, "发送成功");
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
			}
		}
	}
	
    private void addPageMore(){
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
							answers = blogService.getAnswers(blog.getBid(),limit+"");
							//获取answer头像
/*							portraitList=new ArrayList<Bitmap>();
							for(int i=0;i<answers.size();i++){
								Answer answerForPor=answers.get(i);
								String portraitStr=com.lz.utils.AppConstant.PORTRAIT_URL+answerForPor.getPortrait();
								portraitList.add(imageService.getImage(portraitStr));
							}*/
							//将addBlogs添加到blogs中去
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
        blogDetailLV.addFooterView(view);
    }
}
