package com.mysecurity.phoneinfomanagement;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.mysecurity.R;

public class BatteryState extends Activity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.phone_battery_state);
		new Thread(){
			public void run() {
				super.run();
				try {
					sleep(3000);
					Intent intent = new Intent(BatteryState.this,GetBatteryStateActivity.class);
					startActivity(intent);
					finish();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}.start();
		
	}

}
