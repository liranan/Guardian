package com.example.sensors;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class LIGHTService extends Service {
	private SensorManager sensorMgr;
	private Sensor sensor;
	private float acc, min = 0, max = 0, Q;
	private int i = -1;
	private long t1, t2;
	private static final String TAG = "MyLIGHTService";
	private final IBinder myBinder = new LocalBinder();
	private SensorEventListener lsn = new SensorEventListener() {
		@Override
		public void onSensorChanged(SensorEvent e) {
			// acc = e.accuracy;
			t2 = System.currentTimeMillis();
			float lux = e.values[0];
			if (i == -1)
				lux = 0;
			System.out.println("lux:" + lux + ".i:" + ++i);
			if (i == 2 && (t2 - t1) < 2000) {
				if (lux > 5) {
					System.out.println("ѡ���򴫸���");
					sensorMgr.unregisterListener(this);
					Intent intent = new Intent();
					intent.setClass(LIGHTService.this,
							ORIENTATIONService.class);
					intent.putExtra("Q", (int) Q);
					startService(intent);
				}
			} else if (i == 2 || i == 3) {
				i = 2;
				System.out.println("ѡ����ߴ�����");
				if (lux < min) {
					min = lux;
					acc = max - lux;
				} else if (lux > max) {
					max = lux;
					acc = lux - min;
				}
				if (acc > Q) {
					System.out.println("��Q=" + Q + ".acc=" + acc);
					sensorMgr.unregisterListener(this);
					Intent intent = new Intent();
					System.out.println("����Warning����");
					intent.setClass(LIGHTService.this, WarningService.class);
					stopService(intent);
					startService(intent);
				}
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

	@Override
	public IBinder onBind(Intent intent) {
		Log.e(TAG, "onBind()");
		return myBinder;
	}

	// ����startService��������bindService����ʱ����Serviceʱ����ǰServiceδ���������ø÷���
	@Override
	public void onCreate() {
		sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensor = sensorMgr.getDefaultSensor(Sensor.TYPE_LIGHT);
		Log.e(TAG, "onCreate()");
	}

	// ����startService��������Serviceʱ���ø÷���
	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		Log.e(TAG, "onStart()");
		acc = min = max = Q = 0;
		i = -1;
		super.onStart(intent, startId);
		Q = intent.getExtras().getInt("Q");
		System.out.println("GETLIGHTQ:" + Q);

		t1 = System.currentTimeMillis();
		sensorMgr.registerListener(lsn, sensor,
				SensorManager.SENSOR_DELAY_NORMAL);

	}

	// Service�������������ڵ���stopService������unbindService����ʱ���ø÷���
	@Override
	public void onDestroy() {
		Log.e(TAG, "onDestroy()");
	}

	// �ṩ���ͻ��˷���
	public class LocalBinder extends Binder {
		LIGHTService getService() {
			return LIGHTService.this;
		}
	}
}