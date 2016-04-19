package com.example.sensors;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;

public class WarningActivity extends Activity{
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("������");
		wakeUpAndUnlock(this);
		System.out.println("������");
	}
	
	public static void wakeUpAndUnlock(Context context){  
        KeyguardManager km= (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);  
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");  
        //����  
        kl.disableKeyguard();  
        //��ȡ��Դ����������  
        PowerManager pm=(PowerManager) context.getSystemService(Context.POWER_SERVICE);  
        //��ȡPowerManager.WakeLock����,����Ĳ���|��ʾͬʱ��������ֵ,������LogCat���õ�Tag  
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK,"bright");  
        //������Ļ  
        wl.acquire();  
        //�ͷ�  
        wl.release();  
    }  
}
