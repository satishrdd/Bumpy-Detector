//package com.funapps.naveen;
//
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.TextView;
//
//public class Menu extends Activity implements OnClickListener{
//	
//	Button location,start,stop;
//	TextView tv;
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.menu);
//		location = (Button) findViewById(R.id.curloctaion);
//		start = (Button) findViewById(R.id.strecord);
//		stop = (Button) findViewById(R.id.stoprecord);
//		tv = (TextView) findViewById(R.id.textstart);
//		location.setOnClickListener(this);
//		start.setOnClickListener(this);
//		stop.setOnClickListener(this);
//	}
//	@Override
//	public void onClick(View v) {
//		// TODO Auto-generated method stub
//		int id= v.getId();
//		
//		switch(id){
//		case R.id.curloctaion:
//			
//			break;
//		case R.id.strecord:
//			for(int i=10;i>=0;i--){
//				tv.setText("Recording starts in "+i+"s");
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					Log.d("start","start recording failure");
//				}
//			}
//			break;
//		case R.id.stoprecord:
//			
//			break;
//		
//		}
//	}
//}
