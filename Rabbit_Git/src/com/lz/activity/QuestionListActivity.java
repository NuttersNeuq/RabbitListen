package com.lz.activity;

import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lz.javabean.Question;
import com.lz.my.service.QuestionService;
import com.lz.mylistview.MyListView;
import com.lz.mylistview.MyListView.OnRefreshListener;
import com.lz.utils.Util;
import com.nut.activity.R;

public class QuestionListActivity extends Activity {
	private ActionBar actionBar;
	private Context context;
	private Handler handler;//设置handler对象
	
	private MyListViewAdapter adapter;
	private MyOnItemClickListener myOnItemClickListener;
	private MyListView questionListLV;
	
    private TextView moreTextView; //查看更多
    private LinearLayout loadProgressBar;//正在加载进度条
    private final int pageType=1;//msg的标签
    
	private List<Question> questions;
	private QuestionService questionService;//获取QuestionService对象
	
	private String lid;//设置lid
	private int limit=10;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_question_list);
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
	                questionListLV.onRefreshComplete();
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
		
		questionListLV=(MyListView) findViewById(R.id.question_listLV);
		
		addPageMore();
		adapter=new MyListViewAdapter();
		myOnItemClickListener=new MyOnItemClickListener();
		questionService=new QuestionService();
		
		//获取lid；
		lid=getIntent().getStringExtra("lid");	
		
	}

	private void initData() {
		//设置title
		Util.setTitle(context, actionBar, "所有问题");
		
		//连接服务器获取Question对象的List集合,并对listview中的组件进行赋值
		new Thread(){
	
			public void run() {
				try {
					questions = questionService.getQuestions(lid,limit+"");
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
						//将联网获得的tempQuestions赋值给全局变量questions
						questionListLV.setAdapter(adapter);
						questionListLV.setOnItemClickListener(myOnItemClickListener);
					}
					
				});
				
			}			
			
		}.start();
		
		
		// 设置滚动刷新
		questionListLV.setonRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				new AsyncTask<Void, Void, Void>() {
					protected Void doInBackground(Void... params) {
						try {
							questions = questionService.getQuestions(lid,limit+"");
							Thread.sleep(1000);
						} catch (Exception e) {
							e.printStackTrace();
						}
						return null;
					}

					protected void onPostExecute(Void result) {
						adapter.notifyDataSetChanged();
						questionListLV.onRefreshComplete();
					}
				}.execute();
			}
		});
		
	}


	//设置item点击监听器
	private class MyOnItemClickListener implements OnItemClickListener{
		public void onItemClick(AdapterView<?> arg0, View view, int position,long id) {
			if(id==-1)
				return;
			else {	
				Intent intent=new Intent(context, QuestionDetailActivity.class);
				intent.putExtra("question", questions.get((int)id));
				startActivity(intent);
			}
		}
		
	}
	

	private class MyListViewAdapter extends BaseAdapter {

		public View getView(int position, View convertView, ViewGroup parent) {
			return initListView(position, convertView, parent);
		}
		
		public long getItemId(int position) {
			return position;
		}

		public Object getItem(int position) {
			return questions.get(position);
		}
		
		public int getCount() {
			if(questions==null){
				return 0;
			}else{
				return questions.size();
			}
		}

	}
	
	//初始化QuestionListLV的item中的组件
	
	protected View initListView(int position, View convertView, ViewGroup parent) {
		//拿到view
		View view =convertView==null?View.inflate(context, R.layout.item_question_list, null):convertView;
		
		//对view中的组件设置值
		TextView titleTV=(TextView) view.findViewById(R.id.question_list_titleTV);
		TextView contentTV=(TextView) view.findViewById(R.id.question_list_contentTV);
		TextView nicknameTV=(TextView) view.findViewById(R.id.question_list_nicknameTV);
		TextView timeTV=(TextView) view.findViewById(R.id.question_list_timeTV);
		TextView fCountTV=(TextView) view.findViewById(R.id.question_list_fcountTV);
		TextView rCountTV=(TextView) view.findViewById(R.id.question_list_rcountTV);
		
		Question question=questions.get(position);
		titleTV.setText(question.getTitle());
		contentTV.setText(question.getContent());
		nicknameTV.setText(question.getNickname());
		timeTV.setText(Util.getTime(question.getTime()));
		fCountTV.setText(question.getFcount());
		rCountTV.setText(question.getAnscount());
		
		
		return view;
	}




	//设置menu属性
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.question_list_activity, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		int id=item.getItemId();
		switch (id) {
		case android.R.id.home:
			finish();
			return true;
			
		case R.id.menu_question_list_post:
			Intent intent=new Intent(context, QuestionPostActivity.class);
			intent.putExtra("lid", lid);
			startActivity(intent);
			return true;
		}
		return false;
	}
	
    /**
     * 在ListView中添加"加载更多"
     */
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
                        	questions=questionService.getQuestions(lid,""+limit);
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
        questionListLV.addFooterView(view);
    }

    
    
	protected void onRestart() {
		super.onRestart();
		new AsyncTask<Void, Void, Void>() {
			protected Void doInBackground(Void... params) {
				try {
					questions = questionService.getQuestions(lid,limit+"");
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
			protected void onPostExecute(Void result) {
				adapter.notifyDataSetChanged();
				questionListLV.onRefreshComplete();
			}
		}.execute();
	}
}
