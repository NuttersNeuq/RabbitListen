package com.lz.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.lz.utils.StaticInfos;
import com.hare.activity.R;

public class GuideOneActvity extends Activity {
	private Context context;
	private Button guideOneNextBT;
	private TextView guideOneNicknameTV;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_guide_one);
		initView();
		initData();
		
		
	}

	private void initView() {
		context=getApplication();
		guideOneNextBT=(Button) findViewById(R.id.guide_one_nextBT);
		guideOneNicknameTV=(TextView) findViewById(R.id.guide_one_nicknameTV);
		
	}

	private void initData() {
		guideOneNicknameTV.setText("你好哇，"+StaticInfos.nickname+"\n很高兴见到你!");
		guideOneNextBT.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(context, GuideTwoActivity.class));
				finish();
			}
		});
	}
}
