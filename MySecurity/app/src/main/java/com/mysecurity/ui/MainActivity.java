package com.mysecurity.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mysecurity.R;
import com.mysecurity.appmanagement.AppInfoActivity;
import com.mysecurity.phoneinfomanagement.BatteryState;
import com.mysecurity.phoneinfomanagement.GetPhoneStateActivity;
import com.mysecurity.processmanagement.AppTaskProgressActivity;
import com.mysecurity.rubbishcleanmanagement.RubbishCleanActivity;
import com.mysecurity.util.MD5Encoder;

import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    protected static final int STOP = 100;
    private ImageView iv_inspection = null;
    private GridView gv_function = null;
    private GridViewAdapter gv_adapter = null;
    private SharedPreferences sp = null;
    private ListView lv_main_item = null;
    private LinearLayout layout_inspection = null;
    private Button bt_inspection = null;
    private ViewGroup mContainer;
    private ImageView imageView1;
    private LinearLayout layout_inspection_item;
    private RelativeLayout rv = null;
    private SQLiteDatabase db;
    private ImageView iv_cache = null;
    private ScrollView sv;
    private PackageManager pm = null;
    private Animation am = null;
    LinearInterpolator lin;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 100:
                    if (msg.what == STOP) {
//					 imageView2.removeAllViews();
//				     am. setRepeatCount ( -1 );
//				     am.setRepeatCount(Animation.INFINITE);
//				     am.setInterpolator(lin);
                        am.setRepeatCount(0);
                        bt_inspection.setClickable(true);
                    }
                    break;

                default:
                    break;
            }
            String str = (String) msg.obj;
            TextView tv = new TextView(getApplicationContext());
            tv.setTextSize(20);
            tv.setText(str);
            layout_inspection_item.setOrientation(LinearLayout.VERTICAL);
            layout_inspection_item.addView(tv);
            sv.scrollBy(0, 30);
        }

    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.ui_main);

        initUI();
    }

    public void initUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        layout_inspection = (LinearLayout) findViewById(R.id.layout_inspection);
        sp = getSharedPreferences("config", Context.MODE_PRIVATE);
        gv_function = (GridView) findViewById(R.id.gv_function);
        iv_inspection = (ImageView) findViewById(R.id.iv_inspection);
        iv_cache = (ImageView) findViewById(R.id.iv_cache);
        //iv_inspection.setVisibility(View.INVISIBLE);
        bt_inspection = (Button) findViewById(R.id.bt_inspection);
        layout_inspection_item = (LinearLayout) findViewById(R.id.layout_inspection_item);
        //rv = (RelativeLayout) findViewById(R.id.rv);
        //pm = this.getPackageManager();
        //sv = (ScrollView) findViewById(R.id.sv);
        bt_inspection.setOnClickListener(this);
        layout_inspection_item.setOnClickListener(this);

        gv_adapter = new GridViewAdapter(this);
        gv_function.setAdapter(gv_adapter);
        gv_function.setOnItemClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_inspection:
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.main_circle_bg_scan);
                iv_inspection.setImageDrawable(drawable);
                //设置为匀速
                lin = new LinearInterpolator();
                am = new RotateAnimation(0, +360,
                        Animation.RELATIVE_TO_SELF, 0f,
                        Animation.RELATIVE_TO_SELF, 0f);

                // 动画开始到结束的执行时间(1000 = 1 秒)
//		     am. setDuration ( 1000 );

                // 动画重复次数(-1 表示一直重复)
                am.setRepeatCount(-1);
                am.setRepeatCount(Animation.INFINITE);
                am.setInterpolator(lin);
                // 图片配置动画
//		     progressImage. setAnimation (am);
//		     am. startNow ();
                findViewById(R.id.iv_cache).startAnimation(am);
                String name = bt_inspection.getText().toString();
                if ("一键体验".equals(name)) {
                    applyRotation(0, 90, R.id.bt_inspection);
                    am.setDuration(1000);
                    bt_inspection.setText("返回");
                    showKill();
                    bt_inspection.setClickable(false);
                } else {
                    applyRotation(0, 90, R.id.layout_inspection_item);
                    bt_inspection.setText("一键体检");
                    am.setRepeatCount(0);
                    iv_cache.setVisibility(View.GONE);
                    iv_inspection.setImageResource(R.drawable.main_status_baohu);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 旋转listview的方法
     */
    private void applyRotation(float start, float end, final int viewId) {
        final float centerX = mContainer.getWidth() / 2.0f;
        final float centerY = mContainer.getHeight() / 2.0f;
        Rotate3dAnimation rotation =
                new Rotate3dAnimation(start, end, centerX, centerY, 200.0f, true);
        rotation.setDuration(500);
        rotation.setInterpolator(new AccelerateInterpolator());
        rotation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation arg0) {

                mContainer.post(new Runnable() {
                    public void run() {
                        if (viewId == R.id.bt_inspection) {
                            lv_main_item.setVisibility(View.GONE);
                            layout_inspection_item.setVisibility(View.VISIBLE);
                        } else if (viewId == R.id.layout_inspection_item) {
                            layout_inspection_item.setVisibility(View.GONE);
                            lv_main_item.setVisibility(View.VISIBLE);
                        }
                        Rotate3dAnimation rotatiomAnimation = new Rotate3dAnimation(-90, 0, centerX, centerY, 200.0f, false);
                        rotatiomAnimation.setDuration(500);
                        rotatiomAnimation.setInterpolator(new DecelerateInterpolator());

                        mContainer.startAnimation(rotatiomAnimation);
                    }
                });

            }

            public void onAnimationRepeat(Animation arg0) {
            }

            public void onAnimationStart(Animation arg0) {
            }
        });
        mContainer.startAnimation(rotation);
    }

    //查杀病毒
    private void showKill() {
        db = SQLiteDatabase.openDatabase("/sdcard/antivirus.db", null, SQLiteDatabase.OPEN_READONLY);
        new Thread() {

            @Override
            public void run() {
                super.run();
                List<PackageInfo> infos = getPackageManager().getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES
                        | PackageManager.GET_SIGNATURES);
                int virustotal = 0;
                for (PackageInfo info : infos) {
                    try {
                        sleep(500);
                        Message msg = Message.obtain();

                        //获取包名，根据包名获得应用程序的名字
                        String packname = info.packageName;
                        ApplicationInfo appinfo = pm.getPackageInfo(packname, 0).applicationInfo;
                        String appname = appinfo.loadLabel(pm).toString();
                        System.out.println(appname);
                        msg.obj = "正在扫描:" + appname;
                        handler.sendMessage(msg);
                        //获得签名
                        Signature[] signs = info.signatures;
                        String str = signs[0].toCharsString();
                        String md5 = MD5Encoder.encode(str);
                        System.out.println(md5);
                        Cursor cursor = db.rawQuery("select desc from datable where md5=?", new String[]{md5});
                        if (cursor.moveToNext()) {
                            String desc = cursor.getString(0);
                            msg = Message.obtain();
                            msg.obj = info.packageName + ":" + desc;
                            handler.sendMessage(msg);
                            virustotal++;
                        }
                        cursor.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                Message msg = Message.obtain();
                msg.what = STOP;
                msg.obj = "扫描完毕 ,共发现" + virustotal + "个病毒";
                handler.sendMessage(msg);
            }

        }.start();
    }

    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        switch (position) {
            case 0://远程防盗
                //Intent intent = new Intent(this,LostProtectedActivity.class);
                //startActivity(intent);
                break;
            case 1://进程管理
                Intent AppTaskProgressActivityintent = new Intent(this, AppTaskProgressActivity.class);
                startActivity(AppTaskProgressActivityintent);

                break;
            case 2://垃圾清理
                Intent RubbishCleanActivityintent = new Intent(this, RubbishCleanActivity.class);
                startActivity(RubbishCleanActivityintent);
                break;
            case 3://软件管理
                Intent AppInfoActivityintent = new Intent(this, AppInfoActivity.class);
                startActivity(AppInfoActivityintent);
                break;
            case 4://表示手机卫士
                System.out.println("安全备份");
                //Intent SafetyBackupsintent = new Intent(this,SafetyBackups.class);
                //startActivity(SafetyBackupsintent);
                break;
            case 5://表示手机卫士
                System.out.println("程序锁");
                //Intent AppLockintent = new Intent(this,AppLock.class);
                //startActivity(AppLockintent);
                break;
            case 6://获取电池信息
                Intent BatteryStateActivityintent = new Intent(this, BatteryState.class);
                startActivity(BatteryStateActivityintent);
                break;
            case 7://表示通信管理
                System.out.println("通信管理");
                //Intent CommunicationActivityintent = new Intent(this,CommunicationActivity.class);
                //startActivity(CommunicationActivityintent);
                break;
            case 8://手机所有信息
                Intent PhoneStateActivityintent = new Intent(this, GetPhoneStateActivity.class);
                startActivity(PhoneStateActivityintent);
                break;
            case 9://自启管理
                //Intent PhoneStateActivityintent = new Intent(this, GetPhoneStateActivity.class);
                //startActivity(PhoneStateActivityintent);
                break;
            default:
                break;
        }

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
