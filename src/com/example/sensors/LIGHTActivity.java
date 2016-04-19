package com.example.sensors;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class LIGHTActivity extends Activity {
	private SensorManager sensorMgr;
	private float acc , lux , min = 0 , max = 0 , Q2;
	private TextView tx;
	private int i;

	// private Handler handler = new Handler() {
	// @Override
	// public void handleMessage(Message msg) {
	// String result = msg.getData().getString("result");
	// tx.setText(result);
	// }
	// };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("使用光线传感器");
//		setContentView(R.layout.lightactivity);
//		tx = (TextView) findViewById(R.id.TextView3);
		Bundle bundle = this.getIntent().getExtras();
        //接收name值
        Q2 = bundle.getInt("Q2");
		sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
		Sensor sensor = sensorMgr.getDefaultSensor(Sensor.TYPE_LIGHT);
		SensorEventListener lsn = new SensorEventListener() {
			@Override
			public void onSensorChanged(SensorEvent e) {
//				acc = e.accuracy;
				lux = e.values[0];
				if (lux < min){
					min = lux;
					acc = max - lux;
				}
				else if (lux > max){
					max = lux;
				    acc = lux - min;
				}
				if (acc > Q2){
					System.out.println("到Q1=" + Q2 + ".acc=" + acc);
					sensorMgr.unregisterListener(this);
					Intent intent = new Intent();
					intent.setClass(LIGHTActivity.this,
							WarningActivity.class);
					startActivity(intent);
				}
//				tx.setText("acc=" + acc + "\nlux=" + lux);
//				if (i<100){
//					i++;
//					Note(lux + ";");
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
	}

//	private void Note(final String note) {
//		new Thread(new Runnable() {
//			@SuppressLint("NewApi")
//			@Override
//			public void run() {
//				try {
//					File saveFile = new File(
//							"/sdcard/LightInf.txt");
//					// 如果文件不存在，则创建一个文件
//					if (!saveFile.exists()) {
//						try {
//							saveFile.createNewFile();
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//					}
//					saveFile.setWritable(Boolean.TRUE);
//					RandomAccessFile OutStream = new RandomAccessFile(saveFile,
//							"rwd");
//					long filelength = OutStream.length();
//					OutStream.seek(filelength);
//					OutStream.writeBytes("\n" + note);
//					OutStream.close();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}).start();
//	}
}