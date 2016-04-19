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

public class ORIENTATIONService extends Service {
	private SensorManager sensorMgr;
	private int x, y, z, xmin, xmax, xacc, ymin, ymax, yacc, zmin, zmax, zacc, Q;
	private int i;
	private static final String TAG = "MyORIENTATIONService";
	private final IBinder myBinder = new LocalBinder();

	@Override
	public IBinder onBind(Intent intent) {
		Log.e(TAG, "onBind()");
		return myBinder;
	}

	// 调用startService方法或者bindService方法时创建Service时（当前Service未创建）调用该方法
	@Override
	public void onCreate() {
		// setContentView(R.layout.lightactivity);
		// tx = (TextView) findViewById(R.id.TextView3);
		// 接收name值
		Log.e(TAG, "onCreate()");
	}

	// 调用startService方法启动Service时调用该方法
	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		x=y=z=xmin=xmax=xacc=ymin=ymax=yacc=zmin=zmax=zacc= Q =0;
		super.onStart(intent, startId);
		Q = intent.getExtras().getInt("Q");
		System.out.println("GETORIENTATIONQ:" + Q);
//		setContentView(R.layout.orientationactivity);
//		tx = (TextView) findViewById(R.id.TextView2);
		sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
		Sensor sensor = sensorMgr.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		SensorEventListener lsn = new SensorEventListener() {
			@Override
			public void onSensorChanged(SensorEvent e) {
				x = (int)e.values[0];
				y = (int)e.values[1];
				z = (int)e.values[2];
				System.out.println("x,y,z:"+x+" "+y+" "+z);
				if (x < xmin){
					xmin = x;
					xacc = xmax - x;
				}
				else if (x > xmax){
					xmax = x;
				    xacc = x - xmin;
				}
				
				if (y < ymin){
					ymin = y;
					yacc = ymax - y;
				}
				else if (y > ymax){
					ymax = y;
				    yacc = y - ymin;
				}
				
				if (z < zmin){
					zmin = z;
					zacc = zmax - z;
				}
				else if (z > zmax){
					zmax = z;
				    zacc = z - zmin;
				}
				if (xacc > Q && yacc > Q && zacc > Q){
					System.out.println("到Q=" + Q + "  xacc,yacc,zacc:"+xacc+" "+yacc+" "+zacc);
					sensorMgr.unregisterListener(this);
//					Intent intent = new Intent();
//					intent.setClass(ORIENTATIONService.this,
//							WarningActivity.class);
//					startService(intent);
					Intent intent = new Intent();
					System.out.println("进入Warning服务");
					intent.setClass(ORIENTATIONService.this, WarningService.class);
					stopService(intent);
					startService(intent);
				}
//				tx.setText("x=" + x + "\n" + "y=" + y + "\n" + "z=" + z);
//				if (i<100){
//					i++;
//					Note(x + " " + y + " " + z + ";");
//				}
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
		Log.e(TAG, "onDestroy()");
	}

	// 提供给客户端访问
	public class LocalBinder extends Binder {
		ORIENTATIONService getService() {
			return ORIENTATIONService.this;
		}
	}
}