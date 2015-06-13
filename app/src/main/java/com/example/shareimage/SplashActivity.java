package com.example.shareimage;


import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class SplashActivity extends Activity {
	private ImageView ivSplash;
	private Button btnCloseAd;
	private ProgressBar pbMain;
	private Handler handler;
	private boolean isAutoClose=true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		ivSplash = (ImageView) findViewById(R.id.imageViewSplash);
		btnCloseAd = (Button) findViewById(R.id.buttonCloseAd);
		pbMain = (ProgressBar) findViewById(R.id.progressBarSplash);
		
		handler = new Handler();
		showSplash();
	}

	public void showSplash(){
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				handler.post(new Runnable() {
					@Override
					public void run() {
//						if (isAutoClose) {
						Intent i= new Intent(getApplicationContext(), MainActivity.class);
						startActivity(i);
						finish();
//						}
					}
				});
			}
		});
		t.start();
	}
	
	public void closeSplash(View v){
		

					
		
	
	}
	
}
