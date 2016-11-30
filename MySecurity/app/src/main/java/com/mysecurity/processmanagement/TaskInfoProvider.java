package com.mysecurity.processmanagement;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug.MemoryInfo;
import android.support.v4.content.ContextCompat;

import com.mysecurity.R;

public class TaskInfoProvider
{
    private PackageManager packageManager;
    private ActivityManager activityManager;
    private Drawable defaultIcon;

    public TaskInfoProvider(Context context)
    {
        defaultIcon =ContextCompat.getDrawable(context,R.mipmap.ic_launcher);
        packageManager = context.getPackageManager();
        activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
    }

    public List<TaskInfo> getAllTask(
            List<RunningAppProcessInfo> runningAppProcessInfos)
    {
        List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();
        for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcessInfos)
        {
            TaskInfo taskInfo = new TaskInfo();
            int id = runningAppProcessInfo.pid;
            taskInfo.setId(id);
            String packageName = runningAppProcessInfo.processName;
            taskInfo.setPackageName(packageName);
            try
            {
                // ApplicationInfo是AndroidMainfest文件里面整个Application节点的封装
                ApplicationInfo applicationInfo = packageManager
                        .getPackageInfo(packageName, 0).applicationInfo;
                // 应用的图标
                Drawable icon = applicationInfo.loadIcon(packageManager);
                taskInfo.setIcon(icon);
                // 应用的名字
                String name = applicationInfo.loadLabel(packageManager)
                        .toString();
                taskInfo.setName(name);

                // 设置是否为系统应用
                taskInfo.setSystemProcess(!filterApp(applicationInfo));
            }
            catch (Exception e)
            {
                e.printStackTrace();

                //当遇到没有界面的和图标的一些进程时候的处理方式
                taskInfo.setName(packageName);
                taskInfo.setSystemProcess(true);
                taskInfo.setIcon(defaultIcon);
            }

            // 可以返回一个内存信息的数组，传进去的id有多少个，就返回多少个对应id的内存信息
            MemoryInfo[] memoryInfos = activityManager
                    .getProcessMemoryInfo(new int[] { id });
            // 拿到占用的内存空间
            int memory = memoryInfos[0].getTotalPrivateDirty();
            taskInfo.setMemory(memory);
            taskInfos.add(taskInfo);
            taskInfo = null;
        }
        return taskInfos;
    }

    // 判断某一个应用程序是不是用户的应用程序，如果是返回true，否则返回false
    public boolean filterApp(ApplicationInfo info)
    {
        // 有些系统应用是可以更新的，如果用户自己下载了一个系统的应用来更新了原来的，
        // 它就不是系统应用啦，这个就是判断这种情况的
        if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0)
        {
            return true;
        }
        else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0)// 判断是不是系统应用
        {
            return true;
        }
        return false;
    }
    }
