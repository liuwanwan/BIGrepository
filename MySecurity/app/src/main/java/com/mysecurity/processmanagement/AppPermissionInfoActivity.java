package com.mysecurity.processmanagement;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mysecurity.MyApplication;
import com.mysecurity.R;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
public class AppPermissionInfoActivity extends Activity
{
    private TextView tv_app_detail_name;
    private ScrollView sv_app_detail_desc;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.process_app_permission);

        tv_app_detail_name = (TextView) findViewById(R.id.tv_app_detail_name);
        sv_app_detail_desc = (ScrollView) findViewById(R.id.sv_app_detail_desc);

        // 拿到自己定义的application对象，然后再拿到设置在里面的taskinfo对象
        MyApplication myApplication = (MyApplication) getApplication();
        TaskInfo taskInfo = myApplication.getTaskInfo();
        if (taskInfo != null)
        {
            tv_app_detail_name.setText(taskInfo.getName());
            try
            {
                /**
                 * 因为我们获取那个权限列表这个功能，Android是不对外开放的， 但是我们可以通过反射来进程获取
                 * 其实Android的内部已经帮我们写好了这个类的啦，
                 * 它就是android.widget.AppSecurityPermissions
                 * 它里面有一个方法getPermissionsView
                 * 返回的就是一个权限列表的view对象，所以我们只要通过反射，调用一下这个方法就可以的啦
                 */

                // 拿到这个类的对象
                Class<?> clazz = getClass().getClassLoader().loadClass(
                        "android.widget.AppSecurityPermissions");
                // 拿到它的构造方法，它的构造方法里面有两个参数，一个是Context，一个是String类型的包名
                Constructor<?> constructor = clazz.getConstructor(new Class[] {
                        Context.class, String.class });
                //通过构造方法的对象，new一个对象出来，记得要把context和包名这两个参数传递进去
                Object object = constructor.newInstance(new Object[] { this,
                        taskInfo.getPackageName() });
                //拿到我们的想要调用的方法getPermissionsView，这个方法是没有参数的，所以我们new一个空的数组
                Method method = clazz.getDeclaredMethod("getPermissionsView",
                        new Class[] {});
                //调用这个方法，返回一个view对象啦
                View view = (View) method.invoke(object, new Object[] {});
                //然后就把返回的view对象设置进去就可以的啦
                sv_app_detail_desc.addView(view);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

}
