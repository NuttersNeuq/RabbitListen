package com.lz.activity;

import com.hare.activity.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class GuideTwoActivity extends Activity {
	private Context context;
	private Button guideTwoNextBT;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_guide_two);
		initView();
		initData();
	}
	
	private void initView() {
		context=getApplication();
		guideTwoNextBT=(Button) findViewById(R.id.guide_two_nextBT);
		
	}

	private void initData() {
		guideTwoNextBT.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(context, GuideThreeActivity.class));
				finish();
			}
		});
		
	}



}
