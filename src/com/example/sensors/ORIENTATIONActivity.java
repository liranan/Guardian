package com.example.sensors;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class ORIENTATIONActivity extends Activity {
	private SensorManager sensorMgr;
	private int x, y, z, xmin, xmax, xacc, ymin, ymax, yacc, zmin, zmax, zacc, Q1;
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
		System.out.println("ʹ�÷��򴫸���");
//		setContentView(R.layout.orientationactivity);
		Bundle bundle = this.getIntent().getExtras();
        //����nameֵ
        Q1 = bundle.getInt("Q1");
//		tx = (TextView) findViewById(R.id.TextView2);
		sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
		Sensor sensor = sensorMgr.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		SensorEventListener lsn = new SensorEventListener() {
			@Override
			public void onSensorChanged(SensorEvent e) {
				x = (int)e.values[0];
				y = (int)e.values[1];
				z = (int)e.values[2];
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
				if (xacc > Q1 && yacc > Q1 && zacc > Q1){
					System.out.println("��Q1=" + Q1);
					sensorMgr.unregisterListener(this);
					Intent intent = new Intent();
					intent.setClass(ORIENTATIONActivity.this,
							WarningActivity.class);
					startActivity(intent);
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
		// ע��listener�������������Ǽ��ľ�ȷ��
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
//							"/sdcard/OrientationInf.txt");
//					// ����ļ������ڣ��򴴽�һ���ļ�
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
//					System.out.println("д��");
//					OutStream.close();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}).start();
//	}
}