package com.lz.activity;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;

import com.lfl.activity.Diyijiemian;
import com.lz.my.service.NetService;
import com.lz.utils.Util;
import com.hare.activity.R;

public class PersonalInfoLabel extends Activity {
	private Context context;
	private ActionBar actionBar;
	private NetService netService;
	private Handler handler;

	// 所有标签的CheckBox
	private CheckBox personalInfoLabel0;
	private CheckBox personalInfoLabel1;
	private CheckBox personalInfoLabel2;
	private CheckBox personalInfoLabel3;
	private CheckBox personalInfoLabel4;
	private CheckBox personalInfoLabel5;
	private CheckBox personalInfoLabel6;
	private CheckBox personalInfoLabel7;
	private CheckBox personalInfoLabel8;
	private CheckBox personalInfoLabel9;
	private CheckBox personalInfoLabel10;
	private CheckBox personalInfoLabel11;
	private CheckBox personalInfoLabel12;
	private CheckBox personalInfoLabel13;
	private CheckBox personalInfoLabel14;
	private CheckBox personalInfoLabel15;
	private CheckBox personalInfoLabel16;
	private CheckBox personalInfoLabel17;
	private CheckBox personalInfoLabel18;
	private CheckBox personalInfoLabel19;
	private CheckBox personalInfoLabel20;
	private CheckBox personalInfoLabel21;
	private CheckBox personalInfoLabel22;
	private CheckBox personalInfoLabel23;
	private CheckBox personalInfoLabel24;
	private CheckBox personalInfoLabel25;
	private CheckBox personalInfoLabel26;
	private CheckBox personalInfoLabel27;
	private CheckBox personalInfoLabel28;
	private CheckBox personalInfoLabel29;
	private CheckBox personalInfoLabel30;
	private CheckBox personalInfoLabel31;
	private CheckBox personalInfoLabel32;
	private CheckBox personalInfoLabel33;
	private CheckBox personalInfoLabel34;
	private CheckBox personalInfoLabel35;
	private CheckBox personalInfoLabel36;
	private CheckBox personalInfoLabel37;
	private CheckBox personalInfoLabel38;
	private CheckBox personalInfoLabel39;
	private ArrayList<CheckBox> labelCBs;
	private String[] tagArray;   //用于存储所有标签名
	private String label="";
	private String[]labeled;     //已经选择过的标签

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_info_label);
		initView();
		initData();

	}

	private void initView() {
		context = getApplication();
		actionBar = getActionBar();
		netService=new NetService();
		handler=new Handler();
		labelCBs=new ArrayList<CheckBox>();
		// 拿到所有CheckBox
		personalInfoLabel0 = (CheckBox) findViewById(R.id.personal_info_label0);
		personalInfoLabel1 = (CheckBox) findViewById(R.id.personal_info_label1);
		personalInfoLabel2 = (CheckBox) findViewById(R.id.personal_info_label2);
		personalInfoLabel3 = (CheckBox) findViewById(R.id.personal_info_label3);
		personalInfoLabel4 = (CheckBox) findViewById(R.id.personal_info_label4);
		personalInfoLabel5 = (CheckBox) findViewById(R.id.personal_info_label5);
		personalInfoLabel6 = (CheckBox) findViewById(R.id.personal_info_label6);
		personalInfoLabel7 = (CheckBox) findViewById(R.id.personal_info_label7);
		personalInfoLabel8 = (CheckBox) findViewById(R.id.personal_info_label8);
		personalInfoLabel9 = (CheckBox) findViewById(R.id.personal_info_label9);
		personalInfoLabel10 = (CheckBox) findViewById(R.id.personal_info_label10);
		personalInfoLabel11 = (CheckBox) findViewById(R.id.personal_info_label11);
		personalInfoLabel12 = (CheckBox) findViewById(R.id.personal_info_label12);
		personalInfoLabel13 = (CheckBox) findViewById(R.id.personal_info_label13);
		personalInfoLabel14 = (CheckBox) findViewById(R.id.personal_info_label14);
		personalInfoLabel15 = (CheckBox) findViewById(R.id.personal_info_label15);
		personalInfoLabel16 = (CheckBox) findViewById(R.id.personal_info_label16);
		personalInfoLabel17 = (CheckBox) findViewById(R.id.personal_info_label17);
		personalInfoLabel18 = (CheckBox) findViewById(R.id.personal_info_label18);
		personalInfoLabel19 = (CheckBox) findViewById(R.id.personal_info_label19);
		personalInfoLabel20 = (CheckBox) findViewById(R.id.personal_info_label20);
		personalInfoLabel21 = (CheckBox) findViewById(R.id.personal_info_label21);
		personalInfoLabel22 = (CheckBox) findViewById(R.id.personal_info_label22);
		personalInfoLabel23 = (CheckBox) findViewById(R.id.personal_info_label23);
		personalInfoLabel24 = (CheckBox) findViewById(R.id.personal_info_label24);
		personalInfoLabel25 = (CheckBox) findViewById(R.id.personal_info_label25);
		personalInfoLabel26 = (CheckBox) findViewById(R.id.personal_info_label26);
		personalInfoLabel27 = (CheckBox) findViewById(R.id.personal_info_label27);
		personalInfoLabel28 = (CheckBox) findViewById(R.id.personal_info_label28);
		personalInfoLabel29 = (CheckBox) findViewById(R.id.personal_info_label29);
		personalInfoLabel30 = (CheckBox) findViewById(R.id.personal_info_label30);
		personalInfoLabel31 = (CheckBox) findViewById(R.id.personal_info_label31);
		personalInfoLabel32 = (CheckBox) findViewById(R.id.personal_info_label32);
		personalInfoLabel33 = (CheckBox) findViewById(R.id.personal_info_label33);
		personalInfoLabel34 = (CheckBox) findViewById(R.id.personal_info_label34);
		personalInfoLabel35 = (CheckBox) findViewById(R.id.personal_info_label35);
		personalInfoLabel36 = (CheckBox) findViewById(R.id.personal_info_label36);
		personalInfoLabel37 = (CheckBox) findViewById(R.id.personal_info_label37);
		personalInfoLabel38 = (CheckBox) findViewById(R.id.personal_info_label38);
		personalInfoLabel39 = (CheckBox) findViewById(R.id.personal_info_label39);

		// 放入ArrayList
		labelCBs.add(personalInfoLabel0);
		labelCBs.add(personalInfoLabel1);
		labelCBs.add(personalInfoLabel2);
		labelCBs.add(personalInfoLabel3);
		labelCBs.add(personalInfoLabel4);
		labelCBs.add(personalInfoLabel5);
		labelCBs.add(personalInfoLabel6);
		labelCBs.add(personalInfoLabel7);
		labelCBs.add(personalInfoLabel8);
		labelCBs.add(personalInfoLabel9);
		labelCBs.add(personalInfoLabel10);
		labelCBs.add(personalInfoLabel11);
		labelCBs.add(personalInfoLabel12);
		labelCBs.add(personalInfoLabel13);
		labelCBs.add(personalInfoLabel14);
		labelCBs.add(personalInfoLabel15);
		labelCBs.add(personalInfoLabel16);
		labelCBs.add(personalInfoLabel17);
		labelCBs.add(personalInfoLabel18);
		labelCBs.add(personalInfoLabel19);
		labelCBs.add(personalInfoLabel20);
		labelCBs.add(personalInfoLabel21);
		labelCBs.add(personalInfoLabel22);
		labelCBs.add(personalInfoLabel23);
		labelCBs.add(personalInfoLabel24);
		labelCBs.add(personalInfoLabel25);
		labelCBs.add(personalInfoLabel26);
		labelCBs.add(personalInfoLabel27);
		labelCBs.add(personalInfoLabel28);
		labelCBs.add(personalInfoLabel29);
		labelCBs.add(personalInfoLabel30);
		labelCBs.add(personalInfoLabel31);
		labelCBs.add(personalInfoLabel32);
		labelCBs.add(personalInfoLabel33);
		labelCBs.add(personalInfoLabel34);
		labelCBs.add(personalInfoLabel35);
		labelCBs.add(personalInfoLabel36);
		labelCBs.add(personalInfoLabel37);
		labelCBs.add(personalInfoLabel38);
		labelCBs.add(personalInfoLabel39);
        //所有标签
        tagArray=new String[]
        		{"american","economy","art","science","postgraduate","english_serials","football","music"
        		,"gossip","basketball","car","CET4","CET6","china","business","digital"
        		,"education","fashion","diet","game","history","ielts","international","internet"
        		,"speech","life","love","military","encourage","novel","poems","american_serials"
        		,"shopping","society","spoken_language","sports","TED","toefl","travel","school"};
	}

	private void initData() {
		// 设置标题
		Util.setTitle(context, actionBar, "学习标签");
		//拿到已经选择标签
		new Thread(){
			public void run(){
				try {
					labeled=netService.getPersonalInfoLabel();
					handler.post(new Runnable() {
						public void run() {
							//for循环找到arraylist对应
							for(int i=0;i<labeled.length;i++){
								System.out.println("label------------>"+labeled[i]);
								for(int j=0;j<tagArray.length;j++){
									if(labeled[i].equals(tagArray[j])){
										//使对应checkbox变为选中状态
										labelCBs.get(j).setChecked(true);;
									}
								}
							}
						}
					});
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}.start();
	}

	// 设置menu
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.menu_personal_info_label_post:
			//遍历List，拿到label
			for(int i=0;i<labelCBs.size();i++){
				if(labelCBs.get(i).isChecked()){
						label=label+","+tagArray[i];
				}
			}
			
			if(label.equals(",")||label.equals("")){
				Util.showToast(context, "请选择至少一个标签");
				return false;
			}
			//联网post标签
			new Thread(){
				public void run() {
					try {
						String flag=netService.postLabel(label);
						//置零
						label="";
						if(flag.equals("1")){
							handler.post(new Runnable() {
								public void run() {
									Util.showToast(context, "修改成功");
									return;
								}
							});
						}else{
							handler.post(new Runnable() {
								public void run() {
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
		return false;
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.personal_info_label, menu);
		return true;
	}
}
