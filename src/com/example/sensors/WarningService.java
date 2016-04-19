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
		System.out.println("到达了");
		wakeUpAndUnlock(WarningService.this);
		System.out.println("唤醒了");
//		Bundle bundle = new Bundle();  
//	    bundle.putInt("lock",1);  
//	    Intent i = new Intent();  
//	    i.putExtras(bundle);  
//	    i.setAction("com.example.sensors.WarningService");
//	    sendBroadcast(i);  
//		System.out.println("送lock锁");
		Intent intent = new Intent();
		intent.setClass(WarningService.this, ErrorService.class);
		stopService(intent);
		startService(intent);
		System.out.println("进入Eroor服务");
	}

	public static void wakeUpAndUnlock(Context context) {
		KeyguardManager km = (KeyguardManager) context
				.getSystemService(Context.KEYGUARD_SERVICE);
		// KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
		// 解锁
		// kl.disableKeyguard();
		// 获取电源管理器对象
		PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);
		// 获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
		PowerManager.WakeLock wl = pm.newWakeLock(
				PowerManager.ACQUIRE_CAUSES_WAKEUP
						| PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
		// 点亮屏幕
		wl.acquire();
		// 释放
//		wl.release();
	}
	

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}
