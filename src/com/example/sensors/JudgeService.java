package com.example.sensors;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

public class JudgeService extends Service {
	private SensorManager sensorMgr;
	private float acc, lux, min = 0, max = 0;
	private TextView tx;
	private int i;
	private static final String TAG = "MyLIGHTService";
	private final IBinder myBinder = new LocalBinder();

	@Override
	public IBinder onBind(Intent intent) {
		Log.e(TAG, "onBind()");
		return myBinder;
	}
	
	public class LocalBinder extends Binder {
		JudgeService getService() {
			return JudgeService.this;
		}
	}

	// 调用startService方法或者bindService方法时创建Service时（当前Service未创建）调用该方法
	@Override
	public void onCreate() {
		Log.e(TAG, "onCreate()");
	}

	// 调用startService方法启动Service时调用该方法
	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		System.out.println("进入Judge");
		acc = lux = min = max = 0;
		// setContentView(R.layout.lightactivity);
		// tx = (TextView) findViewById(R.id.TextView3);
		// 接收name值
		sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
		Sensor sensor = sensorMgr.getDefaultSensor(Sensor.TYPE_LIGHT);
		SensorEventListener lsn = new SensorEventListener() {
			@Override
			public void onSensorChanged(SensorEvent e) {
				// acc = e.accuracy;
				lux = e.values[0];
				System.out.println("lux:"+lux);
				if (lux < min) {
					min = lux;
					acc = max - lux;
				} else if (lux > max) {
					max = lux;
					acc = lux - min;
				}
				// tx.setText("acc=" + acc + "\nlux=" + lux);
				// if (i<100){
				// i++;
				// Note(lux + ";");
				// }
				// Message msg = new Message();
				// msg.getData().putString("result", result);
				// handler.sendMessage(msg);
			}

			@Override
			public void onAccuracyChanged(Sensor arg0, int arg1) {
				// TODO Auto-generated method stub

			}
		};
		// 注册listener，第三个参数是检测的精确度
		sensorMgr.registerListener(lsn, sensor,
				SensorManager.SENSOR_DELAY_NORMAL);
		Log.e(TAG, "onStart()");
	}

	// Service创建并启动后在调用stopService方法或unbindService方法时调用该方法
	@Override
	public void onDestroy() {
		Bundle bundle = new Bundle(); 
		if (acc >= 7) 
		    bundle.putInt("state",0);   
		else
			bundle.putInt("state", 1);
		Intent i = new Intent();  
	    i.putExtras(bundle);
	    i.setAction("com.example.sensors.JudgeService");
	    sendBroadcast(i);  
		System.out.println("送Judge");
		Log.e(TAG, "onDestroy()");
	}

}