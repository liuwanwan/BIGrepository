package com.mysecurity.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mysecurity.R;

public class GridViewAdapter extends BaseAdapter {
	private LayoutInflater inflater=null;
	private Context context = null;
	private SharedPreferences sp = null;
    private static String names [] = {"手机防盗","进程管理","垃圾清理","软件管理","安全备份","程序加锁","电池信息","通信管理","设备信息","自启管理"};
    private static int icons [] = {R.drawable.ic_firewall_location_query,R.drawable.main_protection_icon,R.drawable.main_clean_icon,
    	R.drawable.main_software_icon,R.drawable.kn_sync_history,R.drawable.main_private_space_icon,
    	R.drawable.main_battery_icon,R.drawable.main_sms_icon,R.drawable.main_software_recommand_icon,R.drawable.main_software_recommand_icon};
	
    public GridViewAdapter(Context context) {
    
		this.context = context;
		inflater = LayoutInflater.from(this.context);
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
	}

	public int getCount() {
		return names.length;
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		//convertView 相当于缓存一样，只要我们判断一下它是不是为null，就可以知道现在这个view有没有绘制过出来
		//如果没有，那么就重新绘制，如果有，那么就可以使用缓存啦，这样就可以大大的节省view绘制的时间了，进行了优化，使ListView更加流畅
		MainViews views;
		View view;
		if(convertView == null)
		{
			views = new MainViews();
			view = inflater.inflate(R.layout.ui_function_item, null);
			views.iv_function_item = (ImageView) view.findViewById(R.id.iv_function_item);
			views.tv_function_item = (TextView) view.findViewById(R.id.tv_function_item);
			views.iv_function_item.setImageResource(icons[position]);
			views.tv_function_item.setText(names[position]);

			view.setTag(views);
		}
		else
		{
			view = convertView;
			views = (MainViews) view.getTag();
			views.iv_function_item = (ImageView) view.findViewById(R.id.iv_function_item);
			views.tv_function_item = (TextView) view.findViewById(R.id.tv_function_item);
			views.iv_function_item.setImageResource(icons[position]);
			views.tv_function_item.setText(names[position]);
		}
		views.tv_function_item.setTextColor(Color.rgb(255, 255, 255));
		if(position==0){
			String name = sp.getString("lost_name", "");
			if(!"".equals(name)){
				views.tv_function_item.setText(name);
			}
		}
		return view;
	}
	//一个存放所有要绘制的控件的类
	private class MainViews
	{
		ImageView iv_function_item;
		TextView tv_function_item;
	}
}
