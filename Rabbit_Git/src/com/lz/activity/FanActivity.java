package com.lz.activity;

import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hare.activity.R;
import com.lz.javabean.Fan;
import com.lz.my.service.FanService;
import com.lz.my.service.ImageService;
import com.lz.mylistview.MyListView;
import com.lz.mylistview.MyListView.OnRefreshListener;
import com.lz.utils.AppConstant;
import com.lz.utils.StaticInfos;
import com.lz.utils.Util;

public class FanActivity extends Activity {
	private int fanTag;	//0为粉丝 1为关注
	
	private ActionBar actionBar;
	private Context context;
	private Handler handler;
	
	private MyListViewAdapter adapter;
	private MyListView fanLV;
	private MyOnItemClickListener myOnItemClickListener;
	
	private List<Fan> fans;
	private FanService fanService;
	private ImageService imageService;
	private Bitmap[]portraits;
	private String uid;	//从上一页面intent过来的uid值。
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fan_activity);
		initView();
		initDate();
	}
	
	private void initView() {
		actionBar = getActionBar();
		context = getApplication();
		handler=new Handler();
		fanTag=getIntent().getIntExtra("fanTag",0);//得到判断标签
		
		fanLV=(MyListView) findViewById(R.id.fanLV);
		adapter=new MyListViewAdapter();
		myOnItemClickListener=new MyOnItemClickListener();
		
		fanService=new FanService();
		imageService=new ImageService(context);
		
		uid=getIntent().getStringExtra("uid");
		
	}
	
	private void initDate() {
		//设置标题
		if(fanTag==0)
			Util.setTitle(context, actionBar, "粉丝");
		else 
			Util.setTitle(context, actionBar, "关注");
		//连接服务器获取Fan对象的List集合,并对listview中的组件进行赋值
		new Thread(){
	
			public void run() {
				try {
					//get fans
					fans = fanService.getFans(fanTag,uid);
					//get portraits of the fans
					portraits=new Bitmap[fans.size()];
					for(int i=0;i<fans.size();i++){
						Fan fanForPor=fans.get(i);
						String portraitStr=AppConstant.PORTRAIT_URL+fanForPor.getPortrait();
						portraits[i]=imageService.getImage(portraitStr);	
					}
					
				} catch (Exception e) {
					System.out.println(e.toString());
					handler.post(new Runnable() {
						public void run() {
						Toast.makeText(context, "网络连接错误", Toast.LENGTH_SHORT).show();	
						}
					});
				}
				//为listview添加适配器
				handler.post(new Runnable() {
					public void run() {
						//将联网获得的tempQuestions赋值给全局变量questions
						fanLV.setAdapter(adapter);
						fanLV.setOnItemClickListener(myOnItemClickListener);
					}
				});
			}			
		}.start();
		
		//设置滚动监听
		fanLV.setonRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				new AsyncTask<Void, Void, Void>() {
					protected Void doInBackground(Void... params) {
						try {
							//get fans
							fans = fanService.getFans(fanTag,uid);
							//get portraits of the fans
							portraits=new Bitmap[fans.size()];
							for(int i=0;i<fans.size();i++){
								Fan fanForPor=fans.get(i);
								String portraitStr=AppConstant.PORTRAIT_URL+fanForPor.getPortrait();
								portraits[i]=imageService.getImage(portraitStr);	
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						return null;
					}
					protected void onPostExecute(Void result) {
						adapter.notifyDataSetChanged();
						fanLV.onRefreshComplete();
					}
				}.execute();
				
				
			}
		});
	}
	
	//设置item点击监听器
	private class MyOnItemClickListener implements OnItemClickListener{
		
		public void onItemClick(AdapterView<?> arg0, View view, int position,long id) {
			Fan fan=fans.get((int)id);
			if(fan.getNickname().equals(StaticInfos.nickname)){
				Intent intent=new Intent(context, PersonalInfoActivity.class);
				intent.putExtra("isPersonalTag", true);	//传入标签
				startActivity(intent);	
			}else{
				Intent intent=new Intent(context, PersonalInfoActivity.class);
				intent.putExtra("isPersonalTag", false);	//传入标签
				intent.putExtra("uid", fans.get((int)id).getUid());	//传入uid
				startActivity(intent);		
			}
			

			
		}
	}
	//设置ListView的适配器
	private class MyListViewAdapter extends BaseAdapter {
		public int getCount() {
			if(fans==null)
				return 0;
			else
				return fans.size();
		}
		public Object getItem(int position) {
			return position;
		}
		
		public long getItemId(int position) {
			return position;
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
			return initListView(position, convertView, parent);
		}
	}
	
	private View initListView(int position, View convertView,ViewGroup parent){
		if(fans==null||fans.size()==0)
			return null;
		//拿到fan
		Fan fan=fans.get(position);
		//拿到view
		View view =convertView==null?View.inflate(context, R.layout.item_fan, null):convertView;
		TextView nicknameTV=(TextView) view.findViewById(R.id.fan_nicknameTV);
		TextView mottoTV=(TextView) view.findViewById(R.id.fan_motoTV);
		ImageView portraitIB=(ImageView) view.findViewById(R.id.fan_portraitIB);
		//设置数据
		nicknameTV.setText(fan.getNickname());
		mottoTV.setText(fan.getMoto());
		if(position<portraits.length)
			portraitIB.setImageBitmap(portraits[position]);
		return view;
	}
	
	
	
	//设置Menu
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.nodisplay, menu);
		return true;
	}
	public boolean onOptionsItemSelected(MenuItem item) {
		int id=item.getItemId();
		switch (id) {
		case android.R.id.home:
			finish();
			break;
		}
		return false;
	}
}
