package com.lz.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lz.javabean.Blog;
import com.lz.javabean.Notification;
import com.lz.javabean.Question;
import com.lz.utils.StaticInfos;
import com.lz.utils.Util;
import com.nut.activity.R;

public class TreeActivity extends Activity {
	private ActionBar actionBar;
	private Context context;
	
	private LinearLayout notifyListLL;
	private ImageButton treeNoteIB;
	private ImageButton treeBlogIB;
	private ImageButton treeQuestionIB;
	
	private ListView notifyListLV;
	private notifyListLVAdapter adapter;
	private MyOnItemClickListener myOnItemClickListener;
	
	private TextView notifyNickname;
	private TextView notifyTitle;
	private TextView notifyInfo;
	private List<Notification>notifications;
	
	private SharedPreferences notifyPreferences;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tree);
		initView();
		initData();
	}

	private void initView() {
		actionBar = getActionBar();
		context = getApplication();
		notifications=new ArrayList<Notification>();
		
		treeNoteIB=(ImageButton) findViewById(R.id.tree_noteBT);
		treeBlogIB=(ImageButton) findViewById(R.id.tree_blogBT);
		treeQuestionIB=(ImageButton) findViewById(R.id.tree_questionBT);
		
		notifyListLV=(ListView)findViewById(R.id.notify_listLV);
		adapter=new notifyListLVAdapter();
		myOnItemClickListener=new MyOnItemClickListener();
		
		notifyPreferences=getSharedPreferences("notify"+StaticInfos.uid, MODE_WORLD_WRITEABLE);
		//获取消息提示列表
		try{
			Map notifyMap=notifyPreferences.getAll();
			//1#qid~uid#nickname#头像#title#content#time#iff#from                 true            1为问题  2为帖子
			Set<Entry<String,String>> entrys=notifyMap.entrySet();
			for(Entry<String,String> entry:entrys){
				String[] items=entry.getKey().split("#");
				String isNotReadStr=entry.getValue();
				Notification notification=new Notification();
				notification.setType(items[0]);
				notification.setTypeid(items[1]);
				notification.setUid(items[2]);
				notification.setNickName(items[3]);
				notification.setPortrait(items[4]);
				notification.setTitle(items[5]);
				notification.setContent(items[6]);
				notification.setTime(items[7]);
				notification.setIftype(items[8]);
				notification.setFrom(items[9]);
				notification.setNotRead(Boolean.parseBoolean(isNotReadStr));
				notifications.add(notification);
			}
			
			System.out.println(notifications.size());
		}catch(Exception e){
			e.getStackTrace();
			System.out.println("异常异常--------------》"+e.toString());
		}
	}

	private void initData() {
		// 设置标题
		Util.setTitle(context, actionBar, "坚果树");
		//设置监听器
		treeNoteIB.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(context, TreeNoteActivity.class));
			}
		});
		treeQuestionIB.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(context, QuestionListActivity.class));
			}
		});
		treeBlogIB.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				startActivity(new Intent(context, BlogListActivity.class));
			}
		});
		//设置adapter
		notifyListLV.setAdapter(adapter);
		notifyListLV.setOnItemClickListener(myOnItemClickListener);
	}
	//设置item点击监听器
	private class MyOnItemClickListener implements OnItemClickListener{
		public void onItemClick(AdapterView<?> arg0, View view, int position,long id) {
			Notification notification=notifications.get(position);
			//写入数据，更改true值
			try {	
				String key = notification.getType() + "#"
						+ notification.getTypeid() + "#"
						+ notification.getUid() + "#"
						+ notification.getNickName() + "#"
						+ notification.getPortrait() + "#"
						+ notification.getTitle() + "#"
						+ notification.getContent() + "#"
						+ notification.getTime() + "#"
						+ notification.getIftype() + "#"
						+ notification.getFrom();
				Editor editor = notifyPreferences.edit();
				editor.putString(key, "false");
				editor.commit();
				notification.setNotRead(false);
				adapter.notifyDataSetChanged();
			} catch (Exception e) {
				e.printStackTrace();
			}
			//封装成Blog或者是Question
			if(notification.getType().equals("1")){
				Question question=new Question();
				question.setQid(notification.getTypeid());
				question.setIff(Boolean.parseBoolean(notification.getIftype()));
				question.setContent(notification.getContent());
				question.setTitle(notification.getTitle());
				question.setTime(Long.parseLong(notification.getTime()));
				question.setNickname(notification.getNickName());
				question.setPortrait(notification.getPortrait());
				Intent intent=new Intent(context, QuestionDetailActivity.class);
				intent.putExtra("question", question);
				startActivity(intent);
			}else{
				Blog blog=new Blog();
				blog.setBid(notification.getTypeid());
				blog.setIfz(Boolean.parseBoolean(notification.getIftype()));
				blog.setContent(notification.getContent());
				blog.setTitle(notification.getTitle());
				blog.setTime(Long.parseLong(notification.getTime()));
				blog.setNickname(notification.getNickName());
				blog.setPortrait(notification.getPortrait());
				Intent intent=new Intent(context, BlogDetailActivity.class);
				intent.putExtra("blog",blog);
				startActivity(intent);	
			}
		}
	}
	
	private class notifyListLVAdapter extends BaseAdapter {
		
		public View getView(int position, View convertView, ViewGroup parent) {
			return initListView(position, convertView, parent);
		}

		public long getItemId(int position) {
			return position;
		}

		public Object getItem(int position) {
			return notifications.get(position);
		}
		
		public int getCount() {
			if(notifications==null){
				return 0;
			}else{
				return notifications.size();
			}
		}
	}
	
	private View initListView(int position, View convertView,ViewGroup parent) {
		Notification notification=notifications.get(position);
		View v=View.inflate(context, R.layout.item_notify, null);
		notifyNickname=(TextView) v.findViewById(R.id.notify_list_nickname);
		notifyTitle=(TextView) v.findViewById(R.id.notify_list_title);
		notifyInfo=(TextView) v.findViewById(R.id.notify_list_info);
		notifyListLL=(LinearLayout) v.findViewById(R.id.notify_listLL);
		if(notification.getType().equals("1"))
			notifyInfo.setText("回答了该问题：");
		else 
			notifyInfo.setText("回答了该帖子");
		if(!notification.isNotRead())
			notifyListLL.setBackgroundResource(R.drawable.notify_item_bg);
		notifyNickname.setText(notification.getFrom());
		notifyTitle.setText(notification.getTitle());
		return v;
	}
	
	

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.tree_activity, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case android.R.id.home:
			finish();
			return true;

		}
		return false;
	}
	
}
