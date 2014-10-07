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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lz.javabean.Blog;
import com.lz.my.service.BlogService;
import com.lz.my.service.ImageService;
import com.lz.mylistview.MyListView;
import com.lz.mylistview.MyListView.OnRefreshListener;
import com.lz.utils.AppConstant;
import com.lz.utils.Util;
import com.hare.activity.R;

public class BlogListActivity extends Activity {
	
	private ActionBar actionBar;
	private Context context;
	private Handler handler;
	
	private MyListView blogListLV;
	private MyListViewAdapter adapter;
	private MyOnItemClickListener myOnItemClickListener;
	
    private TextView moreTextView; //查看更多
    private LinearLayout loadProgressBar;//正在加载进度条
    private final int pageType=1;//msg的标签
	
	private List<Blog> blogs;
	
	//private List<Bitmap> portraitList;
	
	private BlogService blogService;
	private ImageService imageService;
	
	private int limit=10;
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blog_list);
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
	                blogListLV.onRefreshComplete();
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
	    
		blogListLV=(MyListView) findViewById(R.id.blog_listLV);
		
		addPageMore();//在ListView中添加"加载更多"
		
		adapter=new MyListViewAdapter();
		myOnItemClickListener=new MyOnItemClickListener();
		
		blogService=new BlogService();
		imageService=new ImageService(context);
	}

	private void initData() {
		//设置title
		Util.setTitle(context, actionBar, "帖子");
		//连接服务器获取Blog对象的List集合,并对listview中的组件进行赋值
				new Thread(){
					public void run() {
						try {
							//get blogs
							blogs = blogService.getBlogs(limit+"");
							
							/*							//拿到所有头像
							portraitList=new ArrayList<Bitmap>();
							for(int i=0;i<blogs.size();i++){
								Blog blogForPor=blogs.get(i);
								String portraitStr=AppConstant.PORTRAIT_URL+blogForPor.getPortrait();
								portraitList.add(imageService.getImage(portraitStr));	
							}
							*/
							
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
								blogListLV.setAdapter(adapter);
								blogListLV.setOnItemClickListener(myOnItemClickListener);
							}
						});
					}			
				}.start();
		
		// 设置滚动刷新
		blogListLV.setonRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				new AsyncTask<Void, Void, Void>() {
					protected Void doInBackground(Void... params) {
						try {
							//get blogs
							blogs = blogService.getBlogs(limit+"");
							/*							//拿到所有头像
							portraitList=new ArrayList<Bitmap>();
							for(int i=0;i<blogs.size();i++){
								Blog blogForPor=blogs.get(i);
								String portraitStr=AppConstant.PORTRAIT_URL+blogForPor.getPortrait();
								portraitList.add(imageService.getImage(portraitStr));	
							}*/
						} catch (Exception e) {
							e.printStackTrace();
						}
						return null;
					}
					protected void onPostExecute(Void result) {
						adapter.notifyDataSetChanged();
						blogListLV.onRefreshComplete();
					}
				}.execute();
			}
		});
	}


	//设置item点击监听器
	private class MyOnItemClickListener implements OnItemClickListener{
		public void onItemClick(AdapterView<?> arg0, View view, int position,long id) {
			if (id==-1)
				return;
			else{
				Intent intent=new Intent(context, BlogDetailActivity.class);
				intent.putExtra("blog", blogs.get((int)id));
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
			return blogs.get(position);
		}
		public int getCount() {
			if(blogs==null)
				return 0;
			else{
				return blogs.size();
			}
		}
	}
	
	//初始化blogListLV的item中的组件
	protected View initListView(int position, View convertView, ViewGroup parent) {
/*		if (blogs.size()==0||blogs==null)
			return convertView;*/
		//拿到view
		View view =convertView==null?View.inflate(context, R.layout.item_blog_list, null):convertView;
		//获取TextView
		TextView titleTV=(TextView) view.findViewById(R.id.blog_list_titleTV);
		TextView contentTV=(TextView) view.findViewById(R.id.blog_list_contentTV);
		TextView nicknameTV=(TextView) view.findViewById(R.id.blog_list_nicknameTV);
		TextView timeTV=(TextView) view.findViewById(R.id.blog_list_timeTV);
		TextView zCountTV=(TextView) view.findViewById(R.id.blog_list_zcountTV);
		TextView rCountTV=(TextView) view.findViewById(R.id.blog_list_rcountTV);
		//获取ImageView
		final ImageView portraitIV=(ImageView) view.findViewById(R.id.blog_list_portraitIV);
		//赋值
		final Blog blog=blogs.get(position);
		titleTV.setText(blog.getTitle());
		contentTV.setText(blog.getContent());
		nicknameTV.setText(blog.getNickname());
		timeTV.setText(Util.getTime(blog.getTime()));
		zCountTV.setText(blog.getzCount());
		rCountTV.setText(blog.getrCount());
/*		//设置默认头像
		if(position<portraitList.size())
			portraitIV.setImageBitmap(portraitList.get(position));*/
		 //设置头像
		 new Thread(){
			public void run() {
				String portraitStr=com.lz.utils.AppConstant.PORTRAIT_URL+blog.getPortrait();
				try {
					 final Bitmap bm=imageService.getImage(portraitStr);
					 handler.post(new Runnable() {
						public void run() {
							portraitIV.setImageBitmap(bm);
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		 }.start();
		return view;
	}
	//设置menu属性
	public boolean onOptionsItemSelected(MenuItem item) {
		int id=item.getItemId();
		switch (id) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.menu_blog_list_post:
			Intent intent=new Intent(context, BlogPostActivity.class);
			startActivity(intent);
			return true;
		}
		return false;
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.blog_list_activity, menu);
		return super.onCreateOptionsMenu(menu);
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
                        	blogs=blogService.getBlogs(limit+"");
/*							//拿到所有头像
                        	portraitList=new ArrayList<Bitmap>();
							for(int i=0;i<blogs.size();i++){
								Blog blogForPor=blogs.get(i);
								String portraitStr=AppConstant.PORTRAIT_URL+blogForPor.getPortrait();
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
        blogListLV.addFooterView(view);
    }

	protected void onRestart() {
		super.onRestart();
		new AsyncTask<Void, Void, Void>() {
			protected Void doInBackground(Void... params) {
				try {
					//get blogs
					blogs = blogService.getBlogs(limit+"");
/*					//拿到所有头像
					portraitList=new ArrayList<Bitmap>();
					for(int i=0;i<blogs.size();i++){
						Blog blogForPor=blogs.get(i);
						String portraitStr=AppConstant.PORTRAIT_URL+blogForPor.getPortrait();
						portraitList.add(imageService.getImage(portraitStr));	
					}*/
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
			protected void onPostExecute(Void result) {
				adapter.notifyDataSetChanged();
				blogListLV.onRefreshComplete();
			}
			
		}.execute();
	}
}
