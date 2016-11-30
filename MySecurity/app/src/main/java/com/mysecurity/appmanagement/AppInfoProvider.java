package com.mysecurity.appmanagement;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;

import com.mysecurity.util.TextFormater;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class AppInfoProvider {
	private static final String TAG = "AppInfoProvider";
	private Context context;
	private PackageManager packmanager;
	private long cachesize; //缓存大小
	private long datasize;  //数据大小
	private long codesize;  //应用程序大小
	private long totalsize; //总大小
	private String totalMemory="0.0MB";
	public AppInfoProvider(Context context) {
		this.context = context;
		packmanager = context.getPackageManager();
	}
	public List<AppInfo> getAllApps(){
		List<AppInfo> appinfos = new ArrayList<AppInfo>();
		List<PackageInfo> packinfos = packmanager.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
		for(PackageInfo info :packinfos){
			AppInfo myApp = new AppInfo();
			String packname = info.packageName;
			myApp.setPackname(packname);
			ApplicationInfo appinfo = info.applicationInfo;
			Drawable icon = appinfo.loadIcon(packmanager);
			myApp.setIcon(icon);
			queryPacakgeSize(packname);
			myApp.setPacksize(totalMemory);
			String appname = appinfo.loadLabel(packmanager).toString();
			myApp.setAppname(appname);
			 if(filterApp(appinfo)){
				 myApp.setSystemApp(false);
			 }else{
				 myApp.setSystemApp(true);
			 }
			appinfos.add(myApp);
		}
		return appinfos;
	}
	public void queryPacakgeSize(String pkgName) {
		if (pkgName != null) {

			//通过反射机制获得该隐藏函数
			Method getPackageSizeInfo = null;
			try {
				getPackageSizeInfo = packmanager.getClass().getDeclaredMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
			//调用该函数，并且给其分配参数 ，待调用流程完成后会回调PkgSizeObserver类的函数
			try {
				getPackageSizeInfo.invoke(packmanager, pkgName, new PkgSizeObserver());
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}

		}
	}

    public boolean filterApp(ApplicationInfo info) {
        if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
            return true;
        } else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
            return true;
        }
        return false;
    }
	//aidl文件形成的Bindler机制服务类
	public class PkgSizeObserver extends IPackageStatsObserver.Stub{

		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
				throws RemoteException {
			cachesize = pStats.cacheSize  ; //缓存大小
			datasize = pStats.dataSize  ;  //数据大小
			codesize = pStats.codeSize  ;  //应用程序大小
			totalsize = cachesize + datasize + codesize ;
			totalMemory = TextFormater.dataSizeFormat(totalsize);
		}
	}
}
