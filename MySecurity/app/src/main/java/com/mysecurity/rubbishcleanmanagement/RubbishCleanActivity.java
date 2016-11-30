package com.mysecurity.rubbishcleanmanagement;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mysecurity.R;

public class RubbishCleanActivity extends Activity implements OnClickListener {
    private LinearLayout clean_cache = null;
    private LinearLayout clean_sdcard = null;
    private ImageView iv_clean_cache = null;
    private TextView tv_clean_cache = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.clean_rubbish);
		clean_cache = (LinearLayout) findViewById(R.id.clean_cache);
		clean_sdcard = (LinearLayout) findViewById(R.id.clean_sdcard);
		iv_clean_cache = (ImageView) findViewById(R.id.iv_clean_cache);
		tv_clean_cache = (TextView) findViewById(R.id.tv_clean_cache);
		clean_sdcard.setOnClickListener(this);
		clean_cache.setOnClickListener(this);
	}
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.clean_cache:
			iv_clean_cache.setImageResource(R.drawable.clean_cache_icon_pressed);
			tv_clean_cache.setTextColor(Color.WHITE);
			Intent intent = new Intent(this,CacheCleanActivity.class);
			startActivity(intent);
			break;

        case R.id.clean_sdcard:
        	Intent CleanSDCardintent = new Intent(this,CleanSDCardActivity.class);
			startActivity(CleanSDCardintent);
			break;
		}
		
	}

}
