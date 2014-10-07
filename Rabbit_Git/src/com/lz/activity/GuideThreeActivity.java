package com.lz.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lfl.activity.Diyijiemian;
import com.lz.my.service.NetService;
import com.lz.utils.Util;
import com.nut.activity.R;

public class GuideThreeActivity extends Activity {
	private Context context;
	private Handler handler;
	private NetService netService;
	private ViewPager guideThreeViewPager;
	private List<View> viewList;
	private ArrayList<View> dots;
	private LinearLayout guideThreeDotsLL;
	private Button guideThreeNextBT;
	//所有标签的CheckBox
	private CheckBox guideThreeLabel0;
	private CheckBox guideThreeLabel1;
	private CheckBox guideThreeLabel2;
	private CheckBox guideThreeLabel3;
	private CheckBox guideThreeLabel4;
	private CheckBox guideThreeLabel5;
	private CheckBox guideThreeLabel6;
	private CheckBox guideThreeLabel7;
	private CheckBox guideThreeLabel8;
	private CheckBox guideThreeLabel9;
	private CheckBox guideThreeLabel10;
	private CheckBox guideThreeLabel11;
	private CheckBox guideThreeLabel12;
	private CheckBox guideThreeLabel13;
	private CheckBox guideThreeLabel14;
	private CheckBox guideThreeLabel15;
	private CheckBox guideThreeLabel16;
	private CheckBox guideThreeLabel17;
	private CheckBox guideThreeLabel18;
	private CheckBox guideThreeLabel19;
	private CheckBox guideThreeLabel20;
	private CheckBox guideThreeLabel21;
	private CheckBox guideThreeLabel22;
	private CheckBox guideThreeLabel23;
	private CheckBox guideThreeLabel24;
	private CheckBox guideThreeLabel25;
	private CheckBox guideThreeLabel26;
	private CheckBox guideThreeLabel27;
	private CheckBox guideThreeLabel28;
	private CheckBox guideThreeLabel29;
	private CheckBox guideThreeLabel30;
	private CheckBox guideThreeLabel31;
	private CheckBox guideThreeLabel32;
	private CheckBox guideThreeLabel33;
	private CheckBox guideThreeLabel34;
	private CheckBox guideThreeLabel35;
	private CheckBox guideThreeLabel36;
	private CheckBox guideThreeLabel37;
	private CheckBox guideThreeLabel38;
	private CheckBox guideThreeLabel39;
	private ArrayList<CheckBox>labelCBs;
	private Dialog loadingDialog;
	
	private int oldPosition=0;
	
	private String[] tagArray;   //用于存储所有标签名
	private String label="";
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_guide_three);
		initView();
		initData();
	}
	
	private void initView() {
		
		context=getApplication();
		handler=new Handler();
		netService=new NetService();
		guideThreeViewPager=(ViewPager) findViewById(R.id.guide_three_viewpager);
		guideThreeDotsLL=(LinearLayout) findViewById(R.id.guide_three_dotsLL);
		labelCBs=new ArrayList<CheckBox>();
		//为viewPager准备数据
		viewList=new ArrayList<View>();
		LayoutInflater lf=getLayoutInflater().from(this);
		View v1=lf.inflate(R.layout.guide_three_view_pager_one, null);
		View v2=lf.inflate(R.layout.guide_three_view_pager_two, null);
		View v3=lf.inflate(R.layout.guide_three_view_pager_three, null);
		View v4=lf.inflate(R.layout.guide_three_view_pager_four, null);
		View v5=lf.inflate(R.layout.guide_three_view_pager_five, null);
		View v6=lf.inflate(R.layout.guide_three_view_pager_six, null);
		viewList.add(v1);
		viewList.add(v2);
		viewList.add(v3);
		viewList.add(v4);
		viewList.add(v5);
		viewList.add(v6);
		//找到view中的组件
			//拿到所有CheckBox
		guideThreeLabel0=(CheckBox) v1.findViewById(R.id.guide_three_label0);
		guideThreeLabel1=(CheckBox) v1.findViewById(R.id.guide_three_label1);
		guideThreeLabel2=(CheckBox) v1.findViewById(R.id.guide_three_label2);
		guideThreeLabel3=(CheckBox) v1.findViewById(R.id.guide_three_label3);
		guideThreeLabel4=(CheckBox) v1.findViewById(R.id.guide_three_label4);
		guideThreeLabel5=(CheckBox) v1.findViewById(R.id.guide_three_label5);
		guideThreeLabel6=(CheckBox) v1.findViewById(R.id.guide_three_label6);
		guideThreeLabel7=(CheckBox) v1.findViewById(R.id.guide_three_label7);
		
		guideThreeLabel8=(CheckBox) v2.findViewById(R.id.guide_three_label8);
		guideThreeLabel9=(CheckBox) v2.findViewById(R.id.guide_three_label9);
		guideThreeLabel10=(CheckBox) v2.findViewById(R.id.guide_three_label10);
		guideThreeLabel11=(CheckBox) v2.findViewById(R.id.guide_three_label11);
		guideThreeLabel12=(CheckBox) v2.findViewById(R.id.guide_three_label12);
		guideThreeLabel13=(CheckBox) v2.findViewById(R.id.guide_three_label13);
		guideThreeLabel14=(CheckBox) v2.findViewById(R.id.guide_three_label14);
		guideThreeLabel15=(CheckBox) v2.findViewById(R.id.guide_three_label15);
		
		guideThreeLabel16=(CheckBox) v3.findViewById(R.id.guide_three_label16);
		guideThreeLabel17=(CheckBox) v3.findViewById(R.id.guide_three_label17);
		guideThreeLabel18=(CheckBox) v3.findViewById(R.id.guide_three_label18);
		guideThreeLabel19=(CheckBox) v3.findViewById(R.id.guide_three_label19);
		guideThreeLabel20=(CheckBox) v3.findViewById(R.id.guide_three_label20);
		guideThreeLabel21=(CheckBox) v3.findViewById(R.id.guide_three_label21);
		guideThreeLabel22=(CheckBox) v3.findViewById(R.id.guide_three_label22);
		guideThreeLabel23=(CheckBox) v3.findViewById(R.id.guide_three_label23);
		
		guideThreeLabel24=(CheckBox) v4.findViewById(R.id.guide_three_label24);
		guideThreeLabel25=(CheckBox) v4.findViewById(R.id.guide_three_label25);
		guideThreeLabel26=(CheckBox) v4.findViewById(R.id.guide_three_label26);
		guideThreeLabel27=(CheckBox) v4.findViewById(R.id.guide_three_label27);
		guideThreeLabel28=(CheckBox) v4.findViewById(R.id.guide_three_label28);
		guideThreeLabel29=(CheckBox) v4.findViewById(R.id.guide_three_label29);
		guideThreeLabel30=(CheckBox) v4.findViewById(R.id.guide_three_label30);
		guideThreeLabel31=(CheckBox) v4.findViewById(R.id.guide_three_label31);
		
		guideThreeLabel32=(CheckBox) v5.findViewById(R.id.guide_three_label32);
		guideThreeLabel33=(CheckBox) v5.findViewById(R.id.guide_three_label33);
		guideThreeLabel34=(CheckBox) v5.findViewById(R.id.guide_three_label34);
		guideThreeLabel35=(CheckBox) v5.findViewById(R.id.guide_three_label35);
		guideThreeLabel36=(CheckBox) v5.findViewById(R.id.guide_three_label36);
		guideThreeLabel37=(CheckBox) v5.findViewById(R.id.guide_three_label37);
		guideThreeLabel38=(CheckBox) v5.findViewById(R.id.guide_three_label38);
		guideThreeLabel39=(CheckBox) v5.findViewById(R.id.guide_three_label39);
		
		//放入ArrayList
		labelCBs.add(guideThreeLabel0);
		labelCBs.add(guideThreeLabel1);
		labelCBs.add(guideThreeLabel2);
		labelCBs.add(guideThreeLabel3);
		labelCBs.add(guideThreeLabel4);
		labelCBs.add(guideThreeLabel5);
		labelCBs.add(guideThreeLabel6);
		labelCBs.add(guideThreeLabel7);
		labelCBs.add(guideThreeLabel8);
		labelCBs.add(guideThreeLabel9);
		labelCBs.add(guideThreeLabel10);
		labelCBs.add(guideThreeLabel11);
		labelCBs.add(guideThreeLabel12);
		labelCBs.add(guideThreeLabel13);
		labelCBs.add(guideThreeLabel14);
		labelCBs.add(guideThreeLabel15);
		labelCBs.add(guideThreeLabel16);
		labelCBs.add(guideThreeLabel17);
		labelCBs.add(guideThreeLabel18);
		labelCBs.add(guideThreeLabel19);
		labelCBs.add(guideThreeLabel20);
		labelCBs.add(guideThreeLabel21);
		labelCBs.add(guideThreeLabel22);
		labelCBs.add(guideThreeLabel23);
		labelCBs.add(guideThreeLabel24);
		labelCBs.add(guideThreeLabel25);
		labelCBs.add(guideThreeLabel26);
		labelCBs.add(guideThreeLabel27);
		labelCBs.add(guideThreeLabel28);
		labelCBs.add(guideThreeLabel29);
		labelCBs.add(guideThreeLabel30);
		labelCBs.add(guideThreeLabel31);
		labelCBs.add(guideThreeLabel32);
		labelCBs.add(guideThreeLabel33);
		labelCBs.add(guideThreeLabel34);
		labelCBs.add(guideThreeLabel35);
		labelCBs.add(guideThreeLabel36);
		labelCBs.add(guideThreeLabel37);
		labelCBs.add(guideThreeLabel38);
		labelCBs.add(guideThreeLabel39);
		
		guideThreeNextBT=(Button) v6.findViewById(R.id.guide_three_nextBT);
		
        //圆点
        dots = new ArrayList<View>();
        dots.add(findViewById(R.id.guide_three_dot_0));
        dots.add(findViewById(R.id.guide_three_dot_1));
        dots.add(findViewById(R.id.guide_three_dot_2));
        dots.add(findViewById(R.id.guide_three_dot_3));
        dots.add(findViewById(R.id.guide_three_dot_4));
        dots.add(findViewById(R.id.guide_three_dot_5));
        
        //所有标签
        tagArray=new String[]
        		{"american","economy","art","science","postgraduate","english_serials","football","music"
        		,"gossip","basketball","car","CET4","CET6","china","business","digital"
        		,"education","fashion","diet","game","history","ielts","international","internet"
        		,"speech","life","love","military","encourage","novel","poems","american_serials"
        		,"shopping","society","spoken_language","sports","TED","toefl","travel","school"};
        
		loadingDialog = new Dialog(GuideThreeActivity.this, R.style.loading_dialog_style);
		loadingDialog.setContentView(R.layout.loading_dialog);
		Window loadingDialogWindow = loadingDialog.getWindow();
		WindowManager.LayoutParams lParams = loadingDialogWindow.getAttributes();
		loadingDialogWindow.setGravity(Gravity.CENTER);
		lParams.alpha = 1f;
		loadingDialogWindow.setAttributes(lParams);
		loadingDialog.setCancelable(false); 
	}



	private void initData() {
		//为viewPager设置适配器
		guideThreeViewPager.setAdapter(new PagerAdapter() {
			
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0==arg1;
			}
			
			public int getCount() {
				return viewList.size();
			}

			public void destroyItem(ViewGroup container, int position,
					Object object) {
				container.removeView(viewList.get(position));
			}

			public Object instantiateItem(ViewGroup container, int position) {
				View v=viewList.get(position);
				container.addView(v);
				//为组件设置监听器
				return viewList.get(position);
			}
			
		});
		
		
		//为viewPager设置滚动监听器
		guideThreeViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			public void onPageSelected(int position) {
				if(position==5){
					guideThreeDotsLL.setVisibility(View.GONE);
				}else{
					guideThreeDotsLL.setVisibility(View.VISIBLE);
				}
				dots.get(position).setBackgroundResource(R.drawable.dot_focused);
				dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
				oldPosition = position;
			}
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		
		//为guideThreeNextButton设置监听器
		guideThreeNextBT.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				loadingDialog.show();
				//遍历数组，拿到label
				for(int i=0;i<labelCBs.size();i++){
					if(labelCBs.get(i).isChecked()){
							label=label+","+tagArray[i];
					}
				}
				if(label.equals(",")||label.equals("")){
					loadingDialog.cancel();
					Util.showToast(context, "请选择至少一个标签");
					return;
				}
				
				label=label.substring(1,label.length());
				//联网post标签
				new Thread(){
					public void run() {
						try {
							String flag=netService.postLabel(label);
							//置零
							label="";
							if(flag.equals("1")){
								loadingDialog.cancel();
								//进入第一界面
								Intent intent=new Intent(context, Diyijiemian.class);
								startActivity(intent);
								//关闭login
								ActivityManager am=(ActivityManager) getSystemService(ACTIVITY_SERVICE);
								finish();
							}else{
								handler.post(new Runnable() {
									public void run() {
										loadingDialog.cancel();
										Util.showToast(context, "网络似乎开小差了");
										return;
									}
								});
								
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						
					}
				}.start();

			}
		});
	}

}
