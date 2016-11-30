package com.mysecurity.processmanagement;

import android.graphics.drawable.Drawable;

public class TaskInfo
{
	private String name;
	private Drawable icon;
	private int id;
	//以KB作为单位
	private int memory;
	private boolean isCheck;
	private String packageName;
	
	//是否为系统进程
	private boolean isSystemProcess;
	
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public Drawable getIcon()
	{
		return icon;
	}
	public void setIcon(Drawable icon)
	{
		this.icon = icon;
	}
	public int getId()
	{
		return id;
	}
	public void setId(int id)
	{
		this.id = id;
	}
	public boolean isCheck()
	{
		return isCheck;
	}
	public void setCheck(boolean isCheck)
	{
		this.isCheck = isCheck;
	}
	public String getPackageName()
	{
		return packageName;
	}
	public void setPackageName(String packageName)
	{
		this.packageName = packageName;
	}
	public int getMemory()
	{
		return memory;
	}
	public void setMemory(int memory)
	{
		this.memory = memory;
	}
	public boolean isSystemProcess()
	{
		return isSystemProcess;
	}
	public void setSystemProcess(boolean isSystemProcess)
	{
		this.isSystemProcess = isSystemProcess;
	}

}

