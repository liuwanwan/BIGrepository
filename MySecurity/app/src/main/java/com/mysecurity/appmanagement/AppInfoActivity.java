package com.mysecurity.appmanagement;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.mysecurity.MyApplication;
import com.mysecurity.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class AppInfoActivity extends Activity implements OnClickListener {
     protected static final int GET_ALL_APP_FINISH = 80;
	private ListView listview_app_manager = null;
     private AppInfoProvider app = null;
     private List<AppInfo> list = null;
     private List<AppInfo> userAppInfo = null;
     private AppManagerAdapter adapter = null;
     private LinearLayout layout_loading;
     private PopupWindow pop = null;
     private boolean flag=true;
     private LinearLayout layout_popup = null;
     private TextView textView_app_manager = null;

     private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case GET_ALL_APP_FINISH:
					//下载完成后把进度条设置隐藏
					layout_loading.setVisibility(View.INVISIBLE);
					adapter = new AppManagerAdapter(list,AppInfoActivity.this);
					listview_app_manager.setAdapter(adapter);
					//自定义listview右边滚动框换成别的图片
					try {
						Field f = AbsListView.class.getDeclaredField("mFastScroller");
						f.setAccessible(true);
						Object o=f.get(listview_app_manager);
						f=f.getType().getDeclaredField("mThumbDrawable");
						f.setAccessible(true);
						Drawable drawable=(Drawable) f.get(o);
						drawable=ContextCompat.getDrawable(AppInfoActivity.this,R.drawable.mmm);
						f.set(o,drawable);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
					break;
			}
		}

	 };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.app_manager);
		listview_app_manager = (ListView) findViewById(R.id.listview_app_manager);
		layout_loading = (LinearLayout) this
				.findViewById(R.id.layout_app_manager_loading);
		textView_app_manager = (TextView) findViewById(R.id.tv_app_manager);
		inintUI();
		//换成用户应用程序
		textView_app_manager.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				String appName = textView_app_manager.getText().toString();
				//如果是所有程序切换成用户程序,否则就切换成所有程序
				if("所有程序".equals(appName)){
					flag=false;
					textView_app_manager.setText("用户程序");
					userAppInfo = getUserApps(list);
					adapter.setApater(userAppInfo);
					adapter.notifyDataSetChanged();
				}else{
					flag=true;
					textView_app_manager.setText("所有程序");
					adapter.setApater(list);
					adapter.notifyDataSetChanged();
				}
			}
		});
		listview_app_manager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				//显示应用详细信息
				// adapter里面的getItem返回的值
				AppInfo appInfo =(AppInfo) (listview_app_manager.getItemAtPosition(position));
				//拿到我们自己定义的application对象
					MyApplication myApplication = (MyApplication) getApplication();
					//把appInfo对象设置进去
					myApplication.setAppInfo(appInfo);
					Intent intent = new Intent(AppInfoActivity.this, AppDetailActivity.class);
					startActivity(intent);
			}
		});
		listview_app_manager.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				dismisspop();
				//获取当前的view对象在窗体中的位置放置到数组里面
				int [] location = new int[2];
				view.getLocationInWindow(location);
				int i = location[0]+20;
				int j = location[1];

				View popView = View.inflate(AppInfoActivity.this, R.layout.app_pop_item, null);

				LinearLayout layout_share = (LinearLayout) popView.findViewById(R.id.layout_share);
				LinearLayout layout_uninstall = (LinearLayout) popView.findViewById(R.id.layout_uninstall);
				LinearLayout layout_start = (LinearLayout) popView.findViewById(R.id.layout_start);

				layout_share.setOnClickListener(AppInfoActivity.this);
				layout_uninstall.setOnClickListener(AppInfoActivity.this);
				layout_start.setOnClickListener(AppInfoActivity.this);
				//没看懂
				layout_share.setTag(position);
				layout_uninstall.setTag(position);
				layout_start.setTag(position);

				pop = new PopupWindow(popView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				//一定要给popupwindow设置背景颜色
				Drawable background=ContextCompat.getDrawable(AppInfoActivity.this,R.drawable.local_popup_bg);
				pop.setBackgroundDrawable(background);
				pop.showAtLocation(popView, Gravity.CENTER, i, j-350);
				ScaleAnimation scal = new ScaleAnimation(0.0f,1.0f, 0.0f, 1.0f);
				scal.setDuration(500);
				layout_popup = (LinearLayout) popView.findViewById(R.id.layout_popup);
				layout_popup.startAnimation(scal);
				return false;
			}
		});
		listview_app_manager.setOnScrollListener(new OnScrollListener(){

			public void onScrollStateChanged(AbsListView view, int scrollState) {
				dismisspop();
			}

			public void onScroll(AbsListView view, int firstVisibleItem,
								 int visibleItemCount, int totalItemCount) {
				dismisspop();
			}

		});
	}

	private void inintUI() {
		//		/当在获取所有的应用程序后，把进度条设置可见
		layout_loading.setVisibility(View.VISIBLE);
		app = new AppInfoProvider(this);
		new Thread(){
			public void run() {
				//获得所有的应用程序
				list = app.getAllApps();
				Message msg = new Message();
				msg.what = GET_ALL_APP_FINISH;
				handler.sendMessage(msg);
			}

		}.start();
	}
	//当pop不等于空的时候删除对话框
	public void dismisspop(){
		if(pop!=null){
			pop.dismiss();
			pop = null;
		}
	}
	public void onClick(View v) {
		// 判断当前列表的状态
		String titletext;
		AppInfo app;
		TextView tv;
		String packname;

		if(textView_app_manager.getText().toString().equals("所有程序")){
			int positon = (Integer) v.getTag();
			app = list.get(positon);
			packname = app.getPackname();
		}else{
			int positon = (Integer) v.getTag();
			app = userAppInfo.get(positon);
			packname = app.getPackname();
		}

		switch (v.getId()) {
			case R.id.layout_start:
				try {
					//获取所有的包信息
					PackageInfo info = getPackageManager().getPackageInfo(
							packname,
							PackageManager.GET_UNINSTALLED_PACKAGES
									| PackageManager.GET_ACTIVITIES);
					//获取所有的activity
					ActivityInfo [] activityinfos = info.activities;
					//因为有些是系统activity所以打不开，得判断下
					if(activityinfos.length>0){
						//第一个activity具备启动的能力,用第一个activity打开
						ActivityInfo startActivity = activityinfos[0];
						Intent intent  = new Intent();
						intent.setClassName(app.getPackname(), startActivity.name);
						startActivity(intent);
					}else {
						Toast.makeText(this, "当前应用程序无法启动", Toast.LENGTH_SHORT).show();
					}
				} catch (NameNotFoundException e) {
					Toast.makeText(this, "应用程序无法启动", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
				break;
			//分享程序
			case R.id.layout_share:
				Intent shareIntent = new Intent();
				shareIntent.setAction(Intent.ACTION_SEND);
				shareIntent.setType("text/plain");
				shareIntent.putExtra(Intent.EXTRA_SUBJECT, "分享");
				shareIntent.putExtra(Intent.EXTRA_TEXT, "推荐你使用一个程序"+packname);
				shareIntent = Intent.createChooser(shareIntent, "分享");
				startActivity(shareIntent);
				break;
			case R.id.layout_uninstall:
				if(app.isSystemApp()){
					Toast.makeText(this, "系统应用不能被删除", Toast.LENGTH_LONG).show();
				}else{
					if(!packname.equals(getPackageName())){
						String uristr = "package:"+packname;
						Uri uri = Uri.parse(uristr);
						Intent deleteIntent = new Intent();
						deleteIntent.setAction(Intent.ACTION_DELETE);
						deleteIntent.setData(uri);
						startActivityForResult(deleteIntent, 0);
						return;
					}else{
						Toast.makeText(this, "不能卸载自己",Toast.LENGTH_LONG).show();
						return;
					}

				}
				break;
		}

	}
	/**
	 * 判断某个应用程序是 不是三方的应用程序
	 * 返回为真就是用户应用程序
	 * 为假就是系统应用程序
	 */
	public boolean filterApp(ApplicationInfo info) {
		if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
			return true;
		} else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
			return true;
		}
		return false;
	}
	/**
	 * 获取所有的用户安装的app
	 */
	private List<AppInfo> getUserApps(List<AppInfo> appinfos) {
		List<AppInfo> userAppinfos = new ArrayList<AppInfo>();
		for (AppInfo appinfo : appinfos) {
			if (!appinfo.isSystemApp()) {
				userAppinfos.add(appinfo);
			}
		}
		return userAppinfos;
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		inintUI();
	}

}

