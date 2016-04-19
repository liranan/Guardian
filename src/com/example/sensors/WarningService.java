package com.example.sensors;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;

public class WarningService extends Service {
	@Override
	public void onCreate(){
		stopForeground(false);
		System.out.println("������");
		wakeUpAndUnlock(WarningService.this);
		System.out.println("������");
//		Bundle bundle = new Bundle();  
//	    bundle.putInt("lock",1);  
//	    Intent i = new Intent();  
//	    i.putExtras(bundle);  
//	    i.setAction("com.example.sensors.WarningService");
//	    sendBroadcast(i);  
//		System.out.println("��lock��");
		Intent intent = new Intent();
		intent.setClass(WarningService.this, ErrorService.class);
		stopService(intent);
		startService(intent);
		System.out.println("����Eroor����");
	}

	public static void wakeUpAndUnlock(Context context) {
		KeyguardManager km = (KeyguardManager) context
				.getSystemService(Context.KEYGUARD_SERVICE);
		// KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
		// ����
		// kl.disableKeyguard();
		// ��ȡ��Դ����������
		PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);
		// ��ȡPowerManager.WakeLock����,����Ĳ���|��ʾͬʱ��������ֵ,������LogCat���õ�Tag
		PowerManager.WakeLock wl = pm.newWakeLock(
				PowerManager.ACQUIRE_CAUSES_WAKEUP
						| PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
		// ������Ļ
		wl.acquire();
		// �ͷ�
//		wl.release();
	}
	

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}
