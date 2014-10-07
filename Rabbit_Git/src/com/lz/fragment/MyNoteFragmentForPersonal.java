package com.lz.fragment;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lz.activity.NoteDetailMyActivity;
import com.lz.activity.NoteDetailOtherActivity;
import com.lz.javabean.Note;
import com.lz.my.service.NoteService;
import com.lz.mylistview.MyListView;
import com.lz.mylistview.MyListView.OnRefreshListener;
import com.lz.utils.StaticInfos;
import com.lz.utils.Util;
import com.hare.activity.R;
/**
 * 
 * 用于我的主页中的界面
 * 
 * @author Dean
 *
 */

public class MyNoteFragmentForPersonal extends Fragment{
	private View rootView;
	private Context context;
	private Handler handler;
	
	private MyListViewAdapter adapter;
	private MyOnItemClickListener myOnItemClickListener;
	private MyListView noteListLV;
    private TextView moreTextView; //查看更多
    private LinearLayout loadProgressBar;//正在加载进度条
    private final int pageType=1;//msg的标签
	
	private List<Note> notes;
	private List<Note> addNotes;
	private NoteService noteService;
	
	private String uid;
	private boolean isPersonalTag=true;
	private int limit=10;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//拿到fragment的view
		rootView = inflater.inflate(R.layout.activity_note, null);
		initView();
		initData();
		return rootView;
	}
	
	private void initView() {
		context = getActivity().getApplication();
		handler=  new Handler(){
	        public void handleMessage(Message msg) {
	            switch (msg.what) {
	            case pageType:
	                //通知适配器，发现改变操作
	                adapter.notifyDataSetChanged();
	                noteListLV.onRefreshComplete();
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
				
		noteListLV = (MyListView) rootView.findViewById(R.id.note_listLV);
		addPageMore();
		adapter=new MyListViewAdapter();
		myOnItemClickListener=new MyOnItemClickListener();
		noteService=new NoteService();
		//获取uid
		uid=getArguments().getString("uid");
		isPersonalTag=getArguments().getBoolean("isPersonalTag");
	}

	private void initData() {
		// 连接服务器获取Note对象的List集合,并对listview中的组件进行赋值
		new AsyncTask<Void, Void, Void>() {
			protected Void doInBackground(Void... params) {
				
				try {
						notes=noteService.getIndexNotes(uid,""+limit);
				} catch (Exception e) {
					e.printStackTrace();
					handler.post(new Runnable(){
						public void run() {
							Toast.makeText(context, "网络连接错误", Toast.LENGTH_LONG).show();
						}
					});
				}
				return null;
				
			}

			protected void onPostExecute(Void result) {

				// 将联网获得的tempNotes赋值给全局变量notes
				noteListLV.setAdapter(adapter);
				noteListLV.setOnItemClickListener(myOnItemClickListener);
			}
		}.execute();
		
			
		// 设置滚动刷新
		noteListLV.setonRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				new AsyncTask<Void, Void, Void>() {
					protected Void doInBackground(Void... params) {
						try {
							notes=noteService.getIndexNotes(uid,""+limit);
						} catch (Exception e) {
							e.printStackTrace();
						}
						return null;
					}

					protected void onPostExecute(Void result) {
						adapter.notifyDataSetChanged();
						noteListLV.onRefreshComplete();
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
			else{
				Note note=notes.get((int)id);
				if(uid.equals(StaticInfos.uid)&&isPersonalTag){
					Intent intent=new Intent(context, NoteDetailMyActivity.class);
					intent.putExtra("note", note);
					startActivity(intent);		
				}else{
					Intent intent=new Intent(context, NoteDetailOtherActivity.class);
					intent.putExtra("note", note);
					startActivity(intent);	
				}
			}
		}
	}

	
	//设置ListView适配器
	private class MyListViewAdapter extends BaseAdapter {

		public int getCount() {
				if(notes==null)
					return 0;
				else
					return notes.size();
		}

		public Object getItem(int position) {

			return position;
		}

		public long getItemId(int position) {

			return position;
			
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View view =initListView(position, convertView, parent);		
			
			return view;
			
		}
		

	}
	
	//初始化shareQuestionListLV的item中的组件
	protected View initListView(int position, View convertView, ViewGroup parent) {
		
		//拿到view
		View view =convertView==null?View.inflate(context, R.layout.item_note_list, null):convertView;
		//对view中的组件设置值
		TextView titleTV=(TextView) view.findViewById(R.id.note_list_titleTV);
		TextView contentTV=(TextView) view.findViewById(R.id.note_list_contentTV);
		TextView nicknameTV=(TextView) view.findViewById(R.id.note_list_nicknameTV);
		TextView timeTV=(TextView) view.findViewById(R.id.note_list_timeTV);
		TextView zCountTV=(TextView) view.findViewById(R.id.note_list_zcountTV);
		
		Note note=notes.get(position);
		titleTV.setText(note.getTitle());
		contentTV.setText(note.getContent());
		nicknameTV.setText(note.getNickname());
		timeTV.setText(Util.getTime(note.getTime()));
		zCountTV.setText(note.getzCount());
		
		
		return view;
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
                        	notes=noteService.getIndexNotes(uid,""+limit);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //将addBlogs添加到blogs中去
                        //加载数据：加载相关数据
                        Message msg=handler.obtainMessage(pageType);
                        handler.sendMessage(msg);
                    }
                }).start();
            }
        });
        noteListLV.addFooterView(view);
    }
    
	public void onResume() {
		super.onResume();
		new AsyncTask<Void, Void, Void>() {
			protected Void doInBackground(Void... params) {
				try {
					notes=noteService.getIndexNotes(uid,""+limit);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
			protected void onPostExecute(Void result) {
				adapter.notifyDataSetChanged();
				noteListLV.onRefreshComplete();
			}
		}.execute();
	}
    
    
    
    
    
}
