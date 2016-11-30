package com.mysecurity.appmanagement;

import android.app.Activity;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Bundle;
import android.os.RemoteException;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mysecurity.MyApplication;
import com.mysecurity.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AppDetailActivity extends Activity {
    private TextView tv_app_detail_name;
    private ScrollView sv_app_detail_desc;
    //全局变量，保存当前查询包得信息
    private long cachesize; //缓存大小
    private long datasize;  //数据大小
    private long codesize;  //应用程序大小
    private long totalsize; //总大小
    private TextView tv_storage_total, tv_storage_software, tv_storage_data, tv_storage_cache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_detail);
        tv_storage_total = (TextView) findViewById(R.id.tv_storage_total_value); //缓存大小
        tv_storage_software = (TextView) findViewById(R.id.tv_storage_software_value); //数据大小
        tv_storage_data = (TextView) findViewById(R.id.tv_storage_data_value); // 应用程序大小
        tv_storage_cache = (TextView) findViewById(R.id.tv_storage_cache_value);
        // 拿到自己定义的application对象，然后再拿到设置在里面的taskinfo对象
        MyApplication myApplication = (MyApplication) getApplication();
        AppInfo appInfo = myApplication.getAppInfo();
//更新显示当前包得大小信息
        queryPacakgeSize(appInfo.getPackname());
    }

    public void queryPacakgeSize(String pkgName) {
        if (pkgName != null) {
            //使用放射机制得到PackageManager类的隐藏函数getPackageSizeInfo
            PackageManager pm = getPackageManager();  //得到pm对象

            //通过反射机制获得该隐藏函数
            Method getPackageSizeInfo = null;
            try {
                getPackageSizeInfo = pm.getClass().getDeclaredMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            //调用该函数，并且给其分配参数 ，待调用流程完成后会回调PkgSizeObserver类的函数
            try {
                getPackageSizeInfo.invoke(pm, pkgName, new PkgSizeObserver());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

        }
    }
    //aidl文件形成的Bindler机制服务类
    public class PkgSizeObserver extends IPackageStatsObserver.Stub{

        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
                throws RemoteException {
            cachesize = pStats.cacheSize  ; //缓存大小
            datasize = pStats.dataSize  ;  //数据大小
            codesize = pStats.codeSize  ;  //应用程序大小
            totalsize = cachesize + datasize + codesize ;

         }
    }
}
