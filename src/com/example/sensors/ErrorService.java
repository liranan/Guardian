package com.example.sensors;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

public class ErrorService extends Service {
	private int flag = 0;
	@Override
	public void onCreate() {
		System.out.println("到了Error服务");
		try {
			Thread.currentThread();
			Thread.sleep(SensorActivity.delay*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Bundle bundle = new Bundle();  
		if (isScreenLocked(this)){
			flag = 1;
		}
	    bundle.putInt("flag",flag);  
	    Intent i = new Intent();  
	    i.putExtras(bundle);  
	    i.setAction("com.example.sensors.ErrorService");
	    sendBroadcast(i);  
		System.out.println("送flag锁:"+flag);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public final static boolean isScreenLocked(Context c) {  
	    android.app.KeyguardManager mKeyguardManager = (KeyguardManager) c.getSystemService(Context.KEYGUARD_SERVICE);  
	    return mKeyguardManager.inKeyguardRestrictedInputMode();  
	} 

}
