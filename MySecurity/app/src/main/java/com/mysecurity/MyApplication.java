package com.mysecurity;

import android.app.Application;

import com.mysecurity.appmanagement.AppInfo;
import com.mysecurity.processmanagement.TaskInfo;

public class MyApplication extends Application
{
	private AppInfo appInfo;
	private TaskInfo taskInfo;
	public AppInfo getAppInfo()
	{
		return appInfo;
	}
	public void setAppInfo(AppInfo appInfo)
	{
		this.appInfo = appInfo;
	}
	public TaskInfo getTaskInfo()
	{
		return taskInfo;
	}
	public void setTaskInfo(TaskInfo taskInfo)
	{
		this.taskInfo = taskInfo;
	}
	
}
