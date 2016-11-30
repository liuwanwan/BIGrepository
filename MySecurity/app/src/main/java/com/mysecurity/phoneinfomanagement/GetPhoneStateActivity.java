package com.mysecurity.phoneinfomanagement;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.mysecurity.R;

public class GetPhoneStateActivity extends Activity{
    private PhoneInfo phoneInfo;
    private TextView tvFlyMode,tvPhoneNumber, tvNetworkType, tvSimID, tvIMEI, tvPhoneType, tvPhoneBrand, tvSystemVersion, tvTotleMemory, tvWidthHeight, tvMacAddress, tvCPUInfo;
    private TextView tvCpuCoreNum,tvCpuFrequency,tvRom,tvSDCard;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.phone_info);
        phoneInfo=new PhoneInfo(this);
        //tvFlyMode = (TextView) findViewById(R.id.textFly);
        //tvFlyMode.setText(phoneInfo.isAirModeOpen()+"");
        tvPhoneNumber=(TextView)findViewById(R.id.textPhoneNumber);
        tvPhoneNumber.setText(phoneInfo.getPhoneNumber());
        tvNetworkType = (TextView) findViewById(R.id.textNetworkType);
        tvNetworkType.setText(phoneInfo.getNetWorkType()+"G");
        tvSimID = (TextView) findViewById(R.id.textSimNum);
        tvSimID.setText(phoneInfo.getIMSI());
        tvIMEI = (TextView) findViewById(R.id.textIMEI);
        tvIMEI.setText(phoneInfo.getIMEI());
        tvPhoneType = (TextView) findViewById(R.id.textPhoneType);
        tvPhoneType.setText(phoneInfo.getModel());
        tvPhoneBrand = (TextView) findViewById(R.id.textPhoneBrand);
        tvPhoneBrand.setText(phoneInfo.getBrand());
        tvSystemVersion = (TextView) findViewById(R.id.textSystemType);
        tvSystemVersion.setText(phoneInfo.getVersion());
        tvTotleMemory = (TextView) findViewById(R.id.textPhoneMemory);
        tvTotleMemory.setText(phoneInfo.getTotalMemory());
        tvWidthHeight = (TextView) findViewById(R.id.textWidthHeight);
        tvWidthHeight.setText(phoneInfo.getScreenWidth()+"x"+phoneInfo.getScreenHeight());
        tvMacAddress = (TextView) findViewById(R.id.textMacAddress);
        tvMacAddress.setText(phoneInfo.getMacAddress());
        tvCPUInfo = (TextView) findViewById(R.id.textPhoneCPU);
        tvCPUInfo.setText(phoneInfo.getCpuName());
        tvCpuCoreNum = (TextView) findViewById(R.id.textCPUNum);
        tvCpuCoreNum.setText(phoneInfo.getCpuCoreNum()+"个");
        tvCpuFrequency = (TextView) findViewById(R.id.textCPUFrequency);
        tvCpuFrequency.setText(phoneInfo.getMinCpuFreq()+"-"+phoneInfo.getMaxCpuFreq());
        tvRom = (TextView) findViewById(R.id.textRom);
        tvRom.setText(phoneInfo.getRomMemroy()[0]+"/"+phoneInfo.getRomMemroy()[1]);
        tvSDCard = (TextView) findViewById(R.id.textSDCard);
        tvSDCard.setText(phoneInfo.getSDCardMemory()[0]+"/"+phoneInfo.getSDCardMemory()[1]);
    }
}
