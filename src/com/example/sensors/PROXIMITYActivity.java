package com.example.sensors;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class PROXIMITYActivity extends Activity {
	private SensorManager sensorMgr;
	private float type;
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
//		setContentView(R.layout.proximityactivity);
//		tx = (TextView) findViewById(R.id.TextView4);
		sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
		Sensor sensor = sensorMgr.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		SensorEventListener lsn = new SensorEventListener() {
			@Override
			public void onSensorChanged(SensorEvent e) {
				type = e.values[0];
				if (i<100){
					i++;
					Note(type + ";");
				}
				if (type == 0.0)
					tx.setText("type=" + type + "\n靠近");
				else
					tx.setText("type=" + type + "\n远离");
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

	private void Note(final String note) {
		new Thread(new Runnable() {
			@SuppressLint("NewApi")
			@Override
			public void run() {
				try {
					File saveFile = new File("/sdcard/ProximityInf.txt");
					// 如果文件不存在，则创建一个文件
					if (!saveFile.exists()) {
						try {
							saveFile.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					saveFile.setWritable(Boolean.TRUE);
					RandomAccessFile OutStream = new RandomAccessFile(saveFile,
							"rwd");
					long filelength = OutStream.length();
					OutStream.seek(filelength);
					OutStream.writeBytes("\n" + note);
					OutStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}