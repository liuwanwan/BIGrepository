package com.mysecurity.phoneinfomanagement;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/11/28.
 */

public class PhoneInfo {
    private static PhoneInfo instance;

    private TelephonyManager tm;
    private Activity act;

    public PhoneInfo(Activity act) {
        tm = (TelephonyManager) act.getSystemService(Context.TELEPHONY_SERVICE);
        this.act = act;
    }

    public static PhoneInfo getInstance(Activity act) {
        if (instance == null) {
            instance = new PhoneInfo(act);
        } else if (instance.act != act) {
            instance = new PhoneInfo(act);
        }
        return instance;
    }

    /**
     * 是否处于飞行模式
     */
    public boolean isAirModeOpen() {
        return (Settings.System.getInt(act.getContentResolver(),
                Settings.System.AIRPLANE_MODE_ON, 0) == 1 ? true : false);
    }

    //获取手机号码,有的手机无法获取手机号
    public String getPhoneNumber() {
        String s;
        if (tm == null)
            s = null;
        else if (tm.getLine1Number().equals(""))
            s = "无法获取";
        else
            s = tm.getLine1Number();
        return s;
        //return tm == null ? null : tm.getLine1Number();
    }

    /**
     * 获取网络类型（暂时用不到）
     */
    public int getNetWorkType() {
        return tm == null ? 0 : tm.getNetworkType();
    }

    /**
     * 获取手机sim卡的序列号（IMSI）
     */
    public String getIMSI() {
        return tm == null ? null : tm.getSubscriberId();
    }

    /**
     * 获取手机IMEI
     */
    public String getIMEI() {
        return tm == null ? null : tm.getDeviceId();
    }

    /**
     * 获取手机型号
     */
    public static String getModel() {
        return Build.MODEL;
    }

    /**
     * 获取手机品牌
     */
    public static String getBrand() {
        return Build.BRAND;
    }

    /**
     * 获取手机系统版本
     */
    public static String getVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获得手机系统总内存
     */
    public String getTotalMemory() {
        String str1 = "/proc/meminfo";// 系统内存信息文件
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;

        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(
                    localFileReader, 8192);
            str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小

            arrayOfString = str2.split("\\s+");

            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
            localBufferedReader.close();

        } catch (IOException e) {
        }
        return Formatter.formatFileSize(act, initial_memory);// Byte转换为KB或者MB，内存大小规格化
    }

    /**
     * 获取手机屏幕宽
     */
    public int getScreenWidth() {
        return act.getWindowManager().getDefaultDisplay().getWidth();
    }

    /**
     * 获取手机屏高宽
     */
    public int getScreenHeight() {
        return act.getWindowManager().getDefaultDisplay().getHeight();
    }

    /**
     * 获取应用包名
     */
    public String getPackageName() {
        return act.getPackageName();
    }

    /**
     * 获取手机MAC地址
     * 只有手机开启wifi才能获取到mac地址
     */
    public String getMacAddress() {
        String result = "";
        WifiManager wifiManager = (WifiManager) act.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        result = wifiInfo.getMacAddress();
        return result;
    }

    /**
     * 获取手机CPU型号
     */
    public static String getCpuName() {
        try {
            FileReader fr = new FileReader("/proc/cpuinfo");
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            String[] array = text.split(":\\s+", 2);
            for (int i = 0; i < array.length; i++) {
            }
            return array[1];
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //CPU个数
    public int getCpuCoreNum() {
        class CpuFilter implements FileFilter {
            public boolean accept(File pathname) {
                //Check if filename is "cpu", followed by a single digit number
                if (Pattern.matches("cpu[0-9]", pathname.getName())) {
                    return true;
                }
                return false;
            }
        }
        try {
            //Get directory containing CPU info
            File dir = new File("/sys/devices/system/cpu/");
            //Filter to only list the devices we care about
            File[] files = dir.listFiles(new CpuFilter());
            return files.length;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    //获取CPU最大频率
    public static String getMaxCpuFreq() {
        String result = "";
        ProcessBuilder cmd;
        try {
            String[] args = {"/system/bin/cat",
                    "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"};
            cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[24];
            while (in.read(re) != -1) {
                result = result + new String(re);
            }
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            result = "N/A";
        }
        return result.trim();
    }

    // 获取CPU最小频率（单位KHZ）
    public static String getMinCpuFreq() {
        String result = "";
        ProcessBuilder cmd;
        try {
            String[] args = {"/system/bin/cat",
                    "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq"};
            cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[24];
            while (in.read(re) != -1) {
                result = result + new String(re);
            }
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            result = "N/A";
        }
        return result.trim();
    }

    //Rom大小，总大小，可用大小
    public String[] getRomMemroy() {
        String[] romInfo = new String[2];
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize;
        long totalBlocks;
        long availableBlocks;
        // 由于API18（Android4.3）以后getBlockSize过时并且改为了getBlockSizeLong
// 因此这里需要根据版本号来使用那一套API
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSizeLong();
            totalBlocks = stat.getBlockCountLong();
            availableBlocks = stat.getAvailableBlocksLong();
        } else {
            blockSize = stat.getBlockSize();
            totalBlocks = stat.getBlockCount();
            availableBlocks = stat.getAvailableBlocks();
        }
        romInfo[0] = Formatter.formatFileSize(act, totalBlocks * blockSize);
        romInfo[1] = Formatter.formatFileSize(act, blockSize * availableBlocks);
        return romInfo;
    }

    //sdCard大小
    public String[] getSDCardMemory() {
        String[] sdCardInfo = new String[2];
        String state = Environment.getExternalStorageState();
        long blockSize;
        long totalBlocks;
        long availableBlocks;
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(sdcardDir.getPath());
            // 由于API18（Android4.3）以后getBlockSize过时并且改为了getBlockSizeLong
// 因此这里需要根据版本号来使用那一套API
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                blockSize = stat.getBlockSizeLong();
                totalBlocks = stat.getBlockCountLong();
                availableBlocks = stat.getAvailableBlocksLong();
            } else {
                blockSize = stat.getBlockSize();
                totalBlocks = stat.getBlockCount();
                availableBlocks = stat.getAvailableBlocks();
            }

            sdCardInfo[0] = Formatter.formatFileSize(act, totalBlocks * blockSize);//总大小
            sdCardInfo[1] = Formatter.formatFileSize(act, blockSize * availableBlocks);//可用大小
        }
        return sdCardInfo;
    }

    /**
     * 获取Application中的meta-data内容
     */
    public String getMetaData(String name) {
        String result = "";
        try {
            ApplicationInfo appInfo = act.getPackageManager()
                    .getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            result = appInfo.metaData.getString(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}