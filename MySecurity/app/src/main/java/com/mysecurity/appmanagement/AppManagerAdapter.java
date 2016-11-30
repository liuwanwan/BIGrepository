package com.mysecurity.appmanagement;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mysecurity.R;

import java.util.List;

public class AppManagerAdapter extends BaseAdapter {

	private static final String TAG = "AppManagerAdapter";
	private List<AppInfo> appinfos;
	private Context context;
	private static ImageView iv;
	private static TextView tv_name,tv_size;
   // private List<AppInfo> appinfo = null; 
	public AppManagerAdapter(List<AppInfo> appinfos, Context context) {
		this.appinfos = appinfos;
		this.context = context;
	}
    public void setApater(List<AppInfo> userAppinfo){
    	this.appinfos = userAppinfo;
    }
	public int getCount() {
		return appinfos.size();
	}

	public Object getItem(int position) {
		return appinfos.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		AppInfo info = appinfos.get(position);
		View view;
		if (convertView == null) {
			view = View.inflate(context, R.layout.app_item, null);
		}else{
			view = convertView;
		}
		iv = (ImageView) view.findViewById(R.id.iv_app_icon);
		tv_name = (TextView) view.findViewById(R.id.tv_app_name);
		tv_size = (TextView) view.findViewById(R.id.tv_app_memory);
		iv.setImageDrawable(info.getIcon());
		tv_name.setText(info.getAppname());
		tv_size.setText(info.getPacksize());
		return view;

	}

}
