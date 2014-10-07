package com.lz.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.lz.my.service.ImageService;
import com.lz.my.service.NetService;
import com.lz.utils.AppConstant;
import com.lz.utils.StaticInfos;
import com.lz.utils.Util;
import com.nut.activity.R;

public class PersonalInfoActivity extends Activity {
	private Context context;
	private ActionBar actionBar;
	private Handler handler;
	private NetService netService;
	private ImageService imageService;

	private LinearLayout layout;
	private GraphicalView graphicalView;
	private MyListViewAdapter adapter;
	private ListView personalInfoListLV;

	private ImageView personalInfoPortraitIV;
	private Bitmap portraitBitmap;
	private String portraitStr;
	
	private TextView personalInfoNicknameTV;
	private Button personalInfoFanBT;
	private Button personalInfoFanedBT;
	private Button personalInfoLabelBT;
	private Button personalInfoShareOrFollowBT;
	private TextView personalInfoMottoTV;
	private TextView personalInfoTimeFromTV;
	private TextView personalInfoTimeAllTV;
	private TextView personalInfoTimeTodayTV;
	private TextView personalInfoJingtingCountTV;
	private TextView personalInfoFantingCountTV;
	private TextView personalInfoResistDaysTV;

	private HashMap<String, String> infoMap;
	
	private String uid;				 //从xml中解析得到或者通过intent得到
	private String nickname;		//用于他人页面的nickname
	private boolean isPersonalTag;	 //从上一个页面intent过来的值，判断是个人页面还是他人页面   true为个人页面
	
	private String motto="";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_info);
		initView();
		initData();
	}

	private void initView() {
		isPersonalTag=getIntent().getBooleanExtra("isPersonalTag", true);
		
		context = getApplication();
		actionBar = getActionBar();
		handler = new Handler();
		netService = new NetService();
		imageService=new ImageService(context);

		layout = (LinearLayout) findViewById(R.id.personal_info_graphicalLL);
		personalInfoListLV = (ListView) findViewById(R.id.personal_info_listLV);

		// 找到各个组件
		personalInfoPortraitIV = (ImageView) findViewById(R.id.personal_info_portraitIV);
		personalInfoNicknameTV = (TextView) findViewById(R.id.personal_info_nicknameTV);
		personalInfoFanBT = (Button) findViewById(R.id.personal_info_fanBT);
		personalInfoFanedBT = (Button) findViewById(R.id.personal_info_fanedBT);
		personalInfoLabelBT = (Button) findViewById(R.id.personal_info_study_labelBT);
		personalInfoMottoTV = (TextView) findViewById(R.id.personal_info_motoTV);
		personalInfoTimeFromTV = (TextView) findViewById(R.id.personal_info_moerduo_timefromTV);
		personalInfoTimeAllTV = (TextView) findViewById(R.id.personal_info_moerduo_timeallTV);
		personalInfoTimeTodayTV = (TextView) findViewById(R.id.personal_info_moerduo_timetodayTV);
		personalInfoJingtingCountTV = (TextView) findViewById(R.id.personal_info_jingting_countTV);
		personalInfoFantingCountTV = (TextView) findViewById(R.id.personal_info_fanting_countTV);
		personalInfoResistDaysTV = (TextView) findViewById(R.id.personal_info_resist_daysTV);
		personalInfoShareOrFollowBT = (Button) findViewById(R.id.personal_info_share_or_followBT);
	}

	private void initData() {
		// 设置title
		if(isPersonalTag)
			Util.setTitle(context, actionBar, "我的主页");
		else
			Util.setTitle(context, actionBar, "他的主页");
		
		//获取uid的值
		if(isPersonalTag){
			uid=StaticInfos.uid;
			personalInfoShareOrFollowBT.setText("分享");;
		}else{
			uid=getIntent().getStringExtra("uid");
			nickname=getIntent().getStringExtra("nickname");
			TextView personalInfoMyeffortwordTV=(TextView) findViewById(R.id.personal_info_myeffortwordTV);
			personalInfoMyeffortwordTV.setText("他的努力");
		}
		
		// 联网获取数据
		new Thread() {
			public void run() {
				try {
					if(isPersonalTag)
					{
						//打开自己主页
						infoMap = netService.getPersonalInfos();
						portraitBitmap=StaticInfos.portraitBm;
					}
					else
					{	
						if(uid==null){
							//打开他人主页
							infoMap=netService.getOtherInfosByNickname(nickname);
							// 获取头像
							portraitStr=AppConstant.PORTRAIT_URL+infoMap.get("portrait");
							portraitBitmap = imageService.getImage(portraitStr);
						}else{
							//打开他人主页
							infoMap=netService.getOtherInfos(uid);
							// 获取头像
							portraitStr=AppConstant.PORTRAIT_URL+infoMap.get("portrait");
							portraitBitmap = imageService.getImage(portraitStr);
						}

						if(infoMap.get("iff").equals("1"))
							personalInfoShareOrFollowBT.setText("取消关注");
						else
							personalInfoShareOrFollowBT.setText("关注");
							
					}
				} catch (Exception e) {
					Log.d("NetException", "PersonalInfoException :"+e.toString());
					e.printStackTrace();
				}
				
				if (infoMap == null) {
					handler.post(new Runnable() {
						public void run() {
							finish();
							Util.showToast(context, "网络不给力啊");
						}
					});
				}else{
					handler.post(new Runnable() {
						public void run() {
							setData();
							
						}
					});
				}
			}
		}.start();

		// 点击item后的反应
		personalInfoListLV.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0:
					Intent intent1=new Intent(context, MyBlogActivity.class);
					intent1.putExtra("uid", uid);
					if(isPersonalTag)
						intent1.putExtra("isPersonalTag", true);
					else 
						intent1.putExtra("isPersonalTag", false);
					startActivity(intent1);
					break;
				case 1:
					Intent intent2=new Intent(context, MyQuestionActivity.class);
					intent2.putExtra("uid", uid);
					if(isPersonalTag)
						intent2.putExtra("isPersonalTag", true);
					else 
						intent2.putExtra("isPersonalTag", false);
					startActivity(intent2);
					break;
				case 2:
					Intent intent3=new Intent(context, MyNoteActivity.class);
					intent3.putExtra("uid", uid);
					if(isPersonalTag)
						intent3.putExtra("isPersonalTag", true);
					else 
						intent3.putExtra("isPersonalTag", false);
					startActivity(intent3);
					break;
				}
			}
		});

		personalInfoFanBT.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(context, FanActivity.class);
				intent.putExtra("fanTag", 0);
				intent.putExtra("uid", uid);	//uid作为参数get给服务器
				startActivity(intent);
			}
		});

		personalInfoFanedBT.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(context, FanActivity.class);
				intent.putExtra("fanTag", 1);	
				intent.putExtra("uid", uid);	//uid作为参数get给服务器
				startActivity(intent);
			}
		});
		
		personalInfoLabelBT.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(context, PersonalInfoLabel.class));
			}
		});
		
		personalInfoMottoTV.setOnClickListener(new OnClickListener() {
			String returnCode="0";
			public void onClick(View v) {
				if(isPersonalTag){
					AlertDialog.Builder builder=new AlertDialog.Builder(PersonalInfoActivity.this);
					View editView=View.inflate(context, R.layout.dialog_edit_motto_view, null);
					builder.setView(editView);
					builder.create();
					final AlertDialog dialog=builder.show();
					dialog.show();
					
					Button dialogEditMottoCancelBT=(Button) editView.findViewById(R.id.dialog_edit_motto_cancelBT);
					Button dialogEditMottoConfirmBT=(Button) editView.findViewById(R.id.dialog_edit_motto_okBT);
					final EditText dialogEidtMottoContentET=(EditText) editView.findViewById(R.id.dialog_edit_motto_contentET);
					
					dialogEditMottoCancelBT.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							dialog.cancel();
						}
					});
					
					dialogEditMottoConfirmBT.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							motto=dialogEidtMottoContentET.getText().toString();
							//提交motto的修改
								//判断
							if(motto.equals("")){
								Util.showToast(context, "不能为空");
								return;
							}
							new Thread(){
								public void run() {
									try {
										returnCode=netService.postMotto(motto);
										handler.post(new Runnable() {
											public void run() {
												if(returnCode.equals("1")){
													Util.showToast(context, "修改成功");
													personalInfoMottoTV.setText(motto);
													StaticInfos.motto=motto;
													dialog.cancel();
												}else{
													Util.showToast(context, "修改失败");
													return;
												}
											}
										});
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}.start();
						}
					});
				}else
					return;
			}
		});
		
		//设置点击分享，或者关注按钮后的监听器
		personalInfoShareOrFollowBT.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//判断是否是我的主页
				if(isPersonalTag){
					showShare("连续使用坚果听力"+infoMap.get("resistdays")+"天");
				}else{
					//不是，判断是取消关注还是关注
					if(infoMap.get("iff").equals("1")){
						//取消关注
						new Thread(){
							public void run() {
							try {
								netService.personalInfoFollow(false,uid);
								//更改图标
								handler.post(new Runnable() {
									public void run() {
										personalInfoShareOrFollowBT.setText("关注");
										Util.showToast(context, "取消关注成功");
										infoMap.put("iff", "0");
									}
								});
							} catch (Exception e) {
								e.printStackTrace();
							}	
							}
						}.start();
					}else{
						//关注
						new Thread(){
							public void run() {
							try {
								netService.personalInfoFollow(true,uid);
								//更改图标
								handler.post(new Runnable() {
									public void run() {
										personalInfoShareOrFollowBT.setText("取消关注");
										Util.showToast(context, "关注成功");
										infoMap.put("iff", "1");
									}
								});
							} catch (Exception e) {
								e.printStackTrace();
							}		
							}
						}.start();
					}
				}
			}
		});
	}

	private GraphicalView getGraphicalView(ArrayList<String[]> dataList) {
		// 设置Dataset
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		// 添加数据
		TimeSeries series = new TimeSeries("");
		
		for(int i=0;i<dataList.size();i++){
			series.add(new Date(Long.parseLong(dataList.get(i)[0])),Integer.parseInt(dataList.get(i)[1])); 
		}
		
		int length = series.getItemCount();
		dataset.addSeries(series);
		// 设置渲染器属性
		XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
		mRenderer.setOrientation(XYMultipleSeriesRenderer.Orientation.HORIZONTAL);
		// 设置Y轴属性
		mRenderer.setPointSize(5f);// 设置点的大小
		mRenderer.setYAxisMin(0);// 设置y轴最小值是0
		mRenderer.setYAxisMax(120);
		
/*		// 设置X轴属性
		mRenderer.setXLabels(length); // 设置显示个数
		
*/		// 设置缩放属性
		mRenderer.setFitLegend(true);
		mRenderer.setZoomEnabled(false, false);// 设置缩放
		mRenderer.setPanEnabled(true, false);// 设置移动
		// 设置网格属性
		mRenderer.setShowGridY(true);
		mRenderer.setGridColor(0xffd4d4d4);
		// 设置x，y轴文字属性
		mRenderer.setLabelsTextSize(20);// 设置标签的文字大小
		mRenderer.setLabelsColor(0xd9d9d9);// 设置标签的文字颜色
		// 设置x，y轴属性
		mRenderer.setAxesColor(0xffd0d0d0);// 设置轴的颜色
		// 设置Lengend属性
		mRenderer.setShowLegend(false);
		// 设置视图属性
		mRenderer.setMargins(new int[]{ 0, 0, 0, 0 });
		mRenderer.setMarginsColor(0xfff4f4f4);
		//mRenderer.setXLabels(length);
		// 设置折线属性
		XYSeriesRenderer r = new XYSeriesRenderer();// (类似于一条线对象)
		// 设置折线颜色
		r.setColor(0xff55bdca);// 设置颜色
		r.setLineWidth(2);// 设置线宽
		// 设置点的属性
		r.setPointStyle(PointStyle.CIRCLE);// 设置点的样式
		r.setFillPoints(true);// 填充点（显示的点是空心还是实心）
		r.setDisplayChartValues(true);// 将点的值显示出来
		r.setChartValuesSpacing(10);// 显示的点的值与图的距离
		r.setChartValuesTextSize(25);// 点的值的文字大小
		// 设置填充物属性
		// r.setFillBelowLine(true);//是否填充折线图的下方
		// r.setFillBelowLineColor(Color.GREEN);//填充的颜色，如果不设置就默认与线的颜色一致
		
		mRenderer.addSeriesRenderer(r);
		// 通过工厂拿到GraphicalView
		GraphicalView view = ChartFactory.getTimeChartView(context ,dataset,mRenderer,"MM.dd");
		view.setBackgroundColor(0xfff4f4f4);// 与如下两条代码效果一样
		// mRenderer.setBackgroundColor(Color.BLUE);
		// mRenderer.setApplyBackgroundColor(true);
		return view;
		
	}

	private class MyListViewAdapter extends BaseAdapter {

		public int getCount() {
			return 3;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;

		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(context, R.layout.item_personal_info_list,
					null);
			TextView personalInfoListFormTV = (TextView) view
					.findViewById(R.id.personal_info_list_formTV);
			TextView personalInfoListItemCountTV = (TextView) view
					.findViewById(R.id.personal_info_list_item_countTV);
			if(isPersonalTag){
				switch (position) {
				case 0:
					personalInfoListFormTV.setText("我的帖子");
					personalInfoListItemCountTV.setText(infoMap.get("itemmyblogcount"));
					break;
				case 1:
					personalInfoListFormTV.setText("我的问题");
					personalInfoListItemCountTV.setText(infoMap.get("itemmyquescount"));
					break;
				case 2:
					personalInfoListFormTV.setText("我的笔记");
					personalInfoListItemCountTV.setText(infoMap.get("itemmynotecount"));
					break;
				}
			}else
			{
				switch (position) {
				case 0:
					personalInfoListFormTV.setText("他的帖子");
					personalInfoListItemCountTV.setText(infoMap.get("itemmyblogcount"));
					break;
				case 1:
					personalInfoListFormTV.setText("他的问题");
					personalInfoListItemCountTV.setText(infoMap.get("itemmyquescount"));
					break;
				case 2:
					personalInfoListFormTV.setText("他的笔记");
					personalInfoListItemCountTV.setText(infoMap.get("itemmynotecount"));
					break;
				}
			}
			return view;
		}
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.nodisplay, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case android.R.id.home:
			finish();
			break;
		}
		return false;
	}
	
	private void setData() {
		// 将曲线图显示出来
		graphicalView = getGraphicalView(generateCharMap());
		layout.addView(graphicalView);
		// 更新主页数据
		personalInfoNicknameTV.setText(infoMap.get("nickname"));
		personalInfoFanBT.setText(infoMap.get("fan"));
		personalInfoFanedBT.setText(infoMap.get("faned"));
		personalInfoLabelBT.setText(infoMap.get("label"));
		personalInfoMottoTV.setText(infoMap.get("motto"));
		personalInfoTimeFromTV.setText("From "+ infoMap.get("timefrom"));
		personalInfoTimeAllTV.setText(infoMap.get("timeall"));
		personalInfoTimeTodayTV.setText(infoMap.get("timetoday") + "min");
		personalInfoJingtingCountTV.setText(infoMap.get("jingtingcount"));
		personalInfoFantingCountTV.setText(infoMap.get("fantingcount"));
		personalInfoResistDaysTV.setText(infoMap.get("resistdays"));
		personalInfoPortraitIV.setImageBitmap(portraitBitmap);
		uid=infoMap.get("uid");
		// 为listview设置适配器
		adapter = new MyListViewAdapter();
		personalInfoListLV.setAdapter(adapter);
	}

	private ArrayList<String[]> generateCharMap() {
		ArrayList<String []> dataList=new ArrayList<String[]>();
		String all = infoMap.get("chat");
		if(all!=null&&!all.equals("")){
			String[] highParts = all.split("#");
			for(int i = 0; i < highParts.length-1; i++) {
					String[] lowParts = highParts[i].split("\\*");
					String[]item={lowParts[0],lowParts[1]};
					dataList.add(item);
			}
		}
		return dataList;
	}
	
	// 设置要分享的文本和图片即可
	private void showShare(String content)
	{
		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(getString(R.string.share));
		// text是分享文本，所有平台都需要这个字段
		oks.setText("#坚果听力#" + content);
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		// 启动分享GUI
		oks.show(this);
	}

}
