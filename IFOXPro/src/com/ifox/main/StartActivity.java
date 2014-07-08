package com.ifox.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.ifox.parser.RemoteNewsImageLoader;
import com.ifox.util.NetworkConnected;

public class StartActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE) ;
		super.setContentView(R.layout.start_acti) ;
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN) ;

		new Handler().postDelayed(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Intent it = new Intent(StartActivity.this, MainActivity.class) ;
				StartActivity.this.startActivity(it) ;
				StartActivity.this.finish() ;
			}
		}, 1500) ;
		
	}
	
}
