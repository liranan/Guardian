package com.example.sensors;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.RawContacts;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.UART.DeviceListActivity;
import com.example.UART.UartService;

public class SensorActivity extends Activity implements
		RadioGroup.OnCheckedChangeListener {
	private static final int REQUEST_SELECT_DEVICE = 1;
	private static final int REQUEST_ENABLE_BT = 2;
	private static final int UART_PROFILE_READY = 10;
	private static final int UART_PROFILE_CONNECTED = 20;
	private static final int UART_PROFILE_DISCONNECTED = 21;
	private static final int STATE_OFF = 10;
	private Queue<Integer> rssiQueue = new LinkedList<Integer>();
	private Queue<Integer> meanQueue = new LinkedList<Integer>();
	private Integer sum = new Integer(0);

	TextView mRemoteRssiVal;
	RadioGroup mRg;
	private int mState = UART_PROFILE_DISCONNECTED;
	private UartService mService = null;
	private BluetoothDevice mDevice = null;
	private BluetoothAdapter mBtAdapter = null;
	// private ListView messageListView;
	// private ArrayAdapter<String> listAdapter;
	private Button btnConnectDisconnect;
//	private String[] contacts = new String[1000];
//	private int count= 0;

	private int Q = 0;
	static int delay = 0;
	private int flag = 0;
	private static boolean lockIn = false;
	private boolean lock = false, mlock = false, block = false,
			conlock = false, meslock = false, piclock = false, inflock = false,
			condelock = true, mesdelock = true, cblock = false;
	private TextView state, mstate, bstate, TSSI;
	private EditText EQ, Delay;
	private String TAG = "SensorActivity";
	private ToggleButton mTogBtn, mTogBtn2, mTogBtn3, mConBtn, mMesBtn,
			mPicBtn, mInfBtn;
	private RelativeLayout relat1, relat2, relat3, relat4;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBtAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		// listAdapter = new ArrayAdapter<String>(this,
		// R.layout.message_detail);
		btnConnectDisconnect = (Button) findViewById(R.id.btn_select);
		btnConnectDisconnect.setEnabled(false);
		service_init();
		state = (TextView) findViewById(R.id.state);
		mstate = (TextView) findViewById(R.id.Mstate);
		bstate = (TextView) findViewById(R.id.Bstate);
		TSSI = (TextView) findViewById(R.id.rssival);
		mTogBtn = (ToggleButton) findViewById(R.id.mTogBtn); // 获取到控件
		mTogBtn2 = (ToggleButton) findViewById(R.id.mTogBtn2); // 获取到控件
		mTogBtn3 = (ToggleButton) findViewById(R.id.mTogBtn3);
		mConBtn = (ToggleButton) findViewById(R.id.conbtn);
		mMesBtn = (ToggleButton) findViewById(R.id.mesbtn);
		mPicBtn = (ToggleButton) findViewById(R.id.picbtn);
		mInfBtn = (ToggleButton) findViewById(R.id.infbtn);
		relat1 = (RelativeLayout) findViewById(R.id.RelativeLayout1);
		relat2 = (RelativeLayout) findViewById(R.id.RelativeLayout2);
		relat3 = (RelativeLayout) findViewById(R.id.RelativeLayout3);
		relat4 = (RelativeLayout) findViewById(R.id.RelativeLayout4);

		btnConnectDisconnect.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!mBtAdapter.isEnabled()) {
					Log.i(TAG, "onClick - BT not enabled yet");
					Intent enableIntent = new Intent(
							BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
				} else {
					if (btnConnectDisconnect.getText().equals("Connect")) {

						// Connect button pressed, open DeviceListActivity
						// class, with popup windows that scan for devices

						Intent newIntent = new Intent(SensorActivity.this,
								DeviceListActivity.class);
						startActivityForResult(newIntent, REQUEST_SELECT_DEVICE);
					} else {
						// Disconnect button pressed
						if (mDevice != null) {
							mService.disconnect();
						}
					}
				}
			}
		});
		mTogBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					// 选中
					state.setText("功能开关(已打开):");
					lock = true;
					lockIn = true;
					Toast.makeText(getApplicationContext(), "功能打开",
							Toast.LENGTH_LONG).show();
				} else {
					// 未选中
					state.setText("功能开关(未打开):");
					lock = false;
					lockIn = false;
					Toast.makeText(getApplicationContext(), "功能关闭",
							Toast.LENGTH_LONG).show();
				}
				EQ.setEnabled(lock);
				Delay.setEnabled(lock);
			}
		});// 添加监听事件
		mTogBtn2.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					// 选中
					mstate.setText("数据销毁开关(已打开):");
					mlock = true;
					conlock = true;
					meslock = true;
					mConBtn.setChecked(true);
					mMesBtn.setChecked(true);
					Toast.makeText(getApplicationContext(), "数据销毁打开",
							Toast.LENGTH_LONG).show();

					relat1.setVisibility(View.VISIBLE);
					relat2.setVisibility(View.VISIBLE);
					relat3.setVisibility(View.VISIBLE);
					relat4.setVisibility(View.VISIBLE);
				} else {
					// 未选中
					mstate.setText("数据销毁开关(未打开):");
					mlock = false;
					Toast.makeText(getApplicationContext(), "数据销毁关闭",
							Toast.LENGTH_LONG).show();

					relat1.setVisibility(View.GONE);
					relat2.setVisibility(View.GONE);
					relat3.setVisibility(View.GONE);
					relat4.setVisibility(View.GONE);
				}
			}
		});// 添加监听事件
		mTogBtn3.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					// 选中
					bstate.setText("蓝牙报警开关(已打开):");
					block = true;
					Toast.makeText(getApplicationContext(), "蓝牙报警打开",
							Toast.LENGTH_LONG).show();
				} else {
					// 未选中
					bstate.setText("蓝牙报警开关(未打开):");
					if (btnConnectDisconnect.getText().equals("Disconnect"))
						btnConnectDisconnect.performClick();
					block = false;
					Toast.makeText(getApplicationContext(), "蓝牙报警关闭",
							Toast.LENGTH_LONG).show();
				}
				btnConnectDisconnect.setEnabled(block);
			}
		});
		mConBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					conlock = true;
				} else {
					conlock = false;
				}
				System.out.println("conlock:" + conlock);
			}
		});
		mMesBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					meslock = true;
				} else {
					meslock = false;
				}
				System.out.println("meslock:" + meslock);
			}

		});
		mPicBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					piclock = true;
				} else {
					piclock = false;
				}
				System.out.println("piclock:" + piclock);
			}
		});
		mInfBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					inflock = true;
				} else {
					inflock = false;
				}
				System.out.println("inflock:" + inflock);
			}
		});

		EQ = (EditText) findViewById(R.id.Q);
		EQ.setEnabled(false);
		Delay = (EditText) findViewById(R.id.delay);
		Delay.setEnabled(false);
		final IntentFilter filter = new IntentFilter();
		final IntentFilter filter2 = new IntentFilter();
		filter2.addAction("com.example.sensors.WarningService");
		filter2.addAction("com.example.sensors.ErrorService");
		filter2.addAction(Intent.ACTION_USER_PRESENT);
		// filter.addAction("com.example.sensors.JudgeService");
		filter2.addAction(Intent.ACTION_SHUTDOWN);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		// filter.addAction(Intent.ACTION_SHUTDOWN);
		filter2.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);

		final BroadcastReceiver myReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(final Context context, final Intent intent) {
				final String action = intent.getAction();
				Bundle bundle = intent.getExtras();
				if (bundle != null) {
					flag = bundle.getInt("flag");
					System.out.println("flag:" + flag);
				}
				if (flag == 1) {
					System.out.println("长时间未解锁报警");
					if (lockIn)
						TipHelper.PlaySound(SensorActivity.this);
					if (mlock) {
						if (conlock && condelock) {
							TipHelper
									.rootCommand("rename /data/data/com.android.providers.contacts/databases/contacts2.db /data/data/com.android.providers.contacts/databases/REcontacts2.db");
							condelock = false;
						}
						if (meslock && mesdelock) {
							TipHelper
									.rootCommand("rename /data/data/com.android.providers.telephony/databases/mmssms.db /data/data/com.android.providers.telephony/databases/REmmssms.db");
							mesdelock = false;
						}
						if (piclock) {
							picDelete();
						}
						if (inflock) {
							infDelete();
						}
					}
					if (block && lockIn) {
						String message = "W";
						byte[] value;
						try {
							// send data to service
							value = message.getBytes("UTF-8");
							mService.writeRXCharacteristic(value);
							// // Update the log with time stamp
							// String currentDateTimeString = DateFormat
							// .getTimeInstance().format(new Date());
							// listAdapter.add("[" + currentDateTimeString
							// + "] TX: " + message);
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					flag = 0;
				} else if (Intent.ACTION_SHUTDOWN.equals(action)) {
					System.out.println("关机报警");
					if (isScreenLocked(SensorActivity.this)) {
						if (mlock) {
							if (conlock && condelock) {
								TipHelper
										.rootCommand("rename /data/data/com.android.providers.contacts/databases/contacts2.db /data/data/com.android.providers.contacts/databases/REcontacts2.db");
								condelock = false;
							}
							if (meslock && mesdelock) {
								TipHelper
										.rootCommand("rename /data/data/com.android.providers.telephony/databases/mmssms.db /data/data/com.android.providers.telephony/databases/REmmssms.db");
								mesdelock = false;
							}
							if (piclock) {
								picDelete();
							}
							if (inflock) {
								infDelete();
							}
						}
						if (lock && lockIn) {
							TipHelper.PlaySound(SensorActivity.this);
						}
					}

				} else if (Intent.ACTION_USER_PRESENT.equals(action)) {
					TipHelper.StopSound(SensorActivity.this);
				}
			}
		};

		final BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(final Context context, final Intent intent) {
				final String action = intent.getAction();
				if (lock && Intent.ACTION_SCREEN_ON.equals(action)) {
					System.out.println("screen on");
					stopService(intent);
				} else if (lock && Intent.ACTION_SCREEN_OFF.equals(action)) {
					System.out.println("screen off");
					delay(4000);
					String temp = EQ.getText().toString();
					String temp1 = Delay.getText().toString();
					System.out.println("temp:" + temp);
					if (!temp.equals("") && !temp1.equals("")) {
						Q = Integer.parseInt(temp);
						delay = Integer.parseInt(temp1);
						System.out.println("传送Ｑ:" + Q + ".delay:" + delay);
						if (Q != 0) {
							vib();
							System.out.println("screen off,进入检测服务");
							intent.setClass(SensorActivity.this,
									LIGHTService.class);
							intent.putExtra("Q", Q);
							startService(intent);
						}
					}
					// if (state == 0) {
					// intent.setClass(SensorActivity.this,
					// ORIENTATIONService.class);
					// intent.putExtra("Q1", Q);
					// startService(intent);
					// } else if (state == 1) {
					// intent.setClass(SensorActivity.this, LIGHTService.class);
					// intent.putExtra("Q2", Q);
					// startService(intent);
					// }
				}
			}
		};

		registerReceiver(myReceiver, filter2);
		registerReceiver(mBatInfoReceiver, filter);
	}

	private ServiceConnection mServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className,
				IBinder rawBinder) {
			mService = ((UartService.LocalBinder) rawBinder).getService();
			Log.d(TAG, "onServiceConnected mService= " + mService);
			if (!mService.initialize()) {
				Log.e(TAG, "Unable to initialize Bluetooth");
				finish();
			}

		}

		@Override
		public void onServiceDisconnected(ComponentName classname) {
			// // mService.disconnect(mDevice);
			mService = null;
		}
	};

	private Handler mHandler = new Handler() {
		@Override
		// Handler events that received from UART service
		public void handleMessage(Message msg) {

		}
	};

	private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, final Intent intent) {
			String action = intent.getAction();
			// *********************//
			if (UartService.ACTION_RSSI.equals(action)) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						String note = intent
								.getStringExtra(UartService.ACTION_DATA_RSSI);
						Integer RSSI = new Integer(note);
						if (RSSI < 0) {
							rssiQueue.offer(RSSI);
						}
						if (rssiQueue.size() == 4) {
							for (Integer q : rssiQueue) {
								sum += q;
							}
							Log.e(TAG, "mean:" + sum / 4);
							meanQueue.offer(sum / 4);
							rssiQueue.poll();
							sum = 0;
						}
						if (meanQueue.size() == 6) {
							Integer max = 0;
							Integer min = meanQueue.poll();
							for (Integer q : meanQueue) {
								max = q;
							}
							if (max - min >= 8) {
								lockIn = false;
								Log.e(TAG, "lockIn:" + lockIn);
							} else if (max - min <= -9) {
								lockIn = true;
								Log.e(TAG, "lockIn:" + lockIn);
							}
						}
						TSSI.setText(note);
					}
				});
			}
			// *********************//
			if (action.equals(UartService.ACTION_GATT_CONNECTED)) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						String currentDateTimeString = DateFormat
								.getTimeInstance().format(new Date());
						Log.d(TAG, "UART_CONNECT_MSG");
						btnConnectDisconnect.setText("Disconnect");
						cblock = true;
						((TextView) findViewById(R.id.deviceName))
								.setText(mDevice.getName());
						// ((TextView) findViewById(R.id.deviceName))
						// .setText(mDevice.getName() + " - ready");
						// listAdapter.add("[" + currentDateTimeString
						// + "] Connected to: " + mDevice.getName());
						mState = UART_PROFILE_CONNECTED;
					}
				});
			}

			// *********************//
			if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						String currentDateTimeString = DateFormat
								.getTimeInstance().format(new Date());
						Log.d(TAG, "UART_DISCONNECT_MSG");
						btnConnectDisconnect.setText("Connect");
						cblock = false;
						((TextView) findViewById(R.id.deviceName))
								.setText("Not Connected");
						// listAdapter.add("[" + currentDateTimeString
						// + "] Disconnected to: " + mDevice.getName());
						mState = UART_PROFILE_DISCONNECTED;
						mService.close();
						// setUiState();

					}
				});
			}

			// *********************//
			if (action.equals(UartService.ACTION_GATT_SERVICES_DISCOVERED)) {
				mService.enableTXNotification();
			}
			// *********************//
			if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {

				final byte[] txValue = intent
						.getByteArrayExtra(UartService.EXTRA_DATA);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						try {
							String text = new String(txValue, "UTF-8");
							System.out.println("text:" + text);
							if (text.length() > 6) {
//								contacts[count] = text;
								Insert(text);
//								count++;
							} else if (text.equals("S")) {
								TipHelper.StopSound(SensorActivity.this);
							} else if (text.equals("F")) {
								Log.w(TAG, "远离报警");
								TipHelper.PlaySound(SensorActivity.this);
							} else if (text.equals("D")) {
								if (conlock) {
									TipHelper
											.rootCommand("rm /data/data/com.android.providers.contacts/databases/contacts2.db");
								}
								if (meslock) {
									TipHelper
											.rootCommand("rm /data/data/com.android.providers.telephony/databases/mmssms.db");
								}
								if (piclock) {
									picDelete();
								}
								if (inflock) {
									infDelete();
								}
							} else if (text.equals("R")) {
								VoiceContrl("R");
							} else if (text.equals("L")) {
								VoiceContrl("L");
							}
							// String currentDateTimeString = DateFormat
							// .getTimeInstance().format(new Date());
							// listAdapter.add("[" + currentDateTimeString
							// + "] RX: " + text);
						} catch (Exception e) {
							Log.e(TAG, e.toString());
						}
					}
				});
			}
			// *********************//
			if (action.equals(UartService.DEVICE_DOES_NOT_SUPPORT_UART)) {
				showMessage("Device doesn't support UART. Disconnecting");
				mService.disconnect();
			}
		}
	};

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(UartService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(UartService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(UartService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE);
		intentFilter.addAction(UartService.DEVICE_DOES_NOT_SUPPORT_UART);
		intentFilter.addAction(UartService.ACTION_RSSI);
		intentFilter.addAction(UartService.ACTION_DATA_RSSI);
		return intentFilter;
	}

	private void service_init() {
		Intent bindIntent = new Intent(this, UartService.class);
		bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

		LocalBroadcastManager.getInstance(this).registerReceiver(
				UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "恢复通讯录");
		menu.add(1, 1, 0, "恢复短信");
		menu.add(2, 2, 0, "通讯录备份");
		menu.add(3, 3, 0, "主人识别");
		menu.add(4, 4, 0, "主动删除通讯录");
		menu.add(5, 5, 0, "使用说明");
		// menu.add(5, 5, 0, "字符串测试");
		// menu.add(6, 6, 0, "汉字测试");
		// menu.add(7, 7, 0, "混合测试");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent();
		switch (item.getItemId()) {
		case 0:
			TipHelper
					.rootCommand("rename /data/data/com.android.providers.contacts/databases/REcontacts2.db /data/data/com.android.providers.contacts/databases/contacts2.db");
			showMessage("通讯录恢复成功");
			condelock = true;
			return true;
		case 1:
			TipHelper
					.rootCommand("rename /data/data/com.android.providers.telephony/databases/REmmssms.db /data/data/com.android.providers.telephony/databases/mmssms.db");
			showMessage("短信恢复成功");
			mesdelock = true;
			return true;
		case 5:
			intent.setClass(SensorActivity.this, help.class);
			startActivity(intent);
			return true;
		case 3:
			new Thread(new Runnable() {
				boolean flg = true;

				@Override
				public void run() {
					while (flg) {
						// TODO Auto-generated method stub
						try {
							Thread.sleep(100);
							flg = mService.readrssi();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Log.e("a", "蓝牙断开");
						}
					}
				}
			}).start();
			return true;
		case 2:
			if (cblock)
				getnumber(SensorActivity.this);
			else
				showMessage("未连接手环");
			return true;
		case 4:
			ConDelete(SensorActivity.this);
//			return true;
//		case 6:
//			System.out.println("显示联系人");
//			for (int i = 0;i < count;i++)
//				System.out.println(contacts[i]);
//			count = 0;
			return true;
			// case 5:
			// byte[] value;
			// String str = "abc15008479895";
			// try {
			// value = str.getBytes("UTF-8");
			// mService.writeRXCharacteristic(value);
			// System.out.println("success");
			// } catch (UnsupportedEncodingException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// return true;
			// case 6:
			// byte[] value2;
			// String str2 = "陈刚";
			// try {
			// value2 = str2.getBytes("UTF-8");
			// mService.writeRXCharacteristic(value2);
			// System.out.println("success");
			// } catch (UnsupportedEncodingException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// return true;
			// case 7:
			// byte[] value3;
			// String str3 = "陈abc13032871773";
			// try {
			// value3 = str3.getBytes("UTF-8");
			// mService.writeRXCharacteristic(value3);
			// System.out.println("success");
			// } catch (UnsupportedEncodingException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// return true;
		default:
			return false;
		}

	}

	public void ConDelete(Context context) {
		Cursor contactsCur = context.getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

		while (contactsCur.moveToNext()) {
			// 获取ID
			String rawId = contactsCur.getString(contactsCur
					.getColumnIndex(ContactsContract.Contacts._ID));
			// 删除
			String where = ContactsContract.Data._ID + " =?";
			String[] whereparams = new String[] { rawId };
			getContentResolver().delete(
					ContactsContract.RawContacts.CONTENT_URI, where,
					whereparams);
		}
		showMessage("通讯录删除成功");
	}

	private void getnumber(Context context) {
		// TODO Auto-generated method stub
		Cursor cursor = context.getContentResolver().query(Phone.CONTENT_URI,
				null, null, null, null);
		String phoneNumber;
		String phoneName;
		String temp;
		while (cursor.moveToNext()) {
			phoneNumber = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));// 鐢佃瘽鍙风爜
			phoneName = cursor.getString(cursor
					.getColumnIndex(Phone.DISPLAY_NAME));// 濮撳悕
			if (phoneName.length() > 3)
				phoneName = phoneName.substring(0, 3);
			if (phoneNumber.length() > 13)
				phoneNumber = phoneNumber.substring(3);
			temp = phoneName + phoneNumber;
			System.out.println(temp);
			// temp = "w";
			// // value = temp.getBytes("UTF-8");
			// try {
			// mService.writeRXCharacteristic(temp.getBytes("UTF-8"));
			// } catch (UnsupportedEncodingException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			byte[] value;
			try {
				// send data to service
				value = temp.getBytes("UTF-8");
				mService.writeRXCharacteristic(value);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// }
		}
		showMessage("通讯录备份成功");
	}

	// protected void onActivityResult(int requestCode, int resultCode,
	// Intent intent) {
	// Bundle bundle = intent.getExtras();
	// if (bundle != null) {
	// lock = bundle.getInt("lock");
	// System.out.println("lock:" + lock);
	// }
	// }

	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if (keyCode == KeyEvent.KEYCODE_POWER) { // 按下的如果是BACK，同时没有重复
	// // do something here
	// int cout = event.getRepeatCount();
	// System.out.println("onKeyDown event.getRepeatCount() " + cout);
	// if (cout >= 1) {
	// TipHelper.PlaySound(SensorActivity.this);
	// }
	// return true;
	// }
	// if (event.getRepeatCount() == 0) {
	// event.startTracking();
	// return true;
	// }
	// return super.onKeyDown(keyCode, event);
	// }

	private void delay(int ms) {
		try {
			Thread.currentThread();
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void picDelete() {
		File file = new File("/sdcard/DCIM");
		TipHelper.DeleteFile(file);
	}

	public void infDelete() {
		TipHelper.rootCommand("rm -rf /data/data/com.tencent.mobileqq/*");
		// TipHelper.rootCommand("rm -rf /data/data/com.taobao.taobao/*");
		// TipHelper.rootCommand("rm -rf /data/data/com.sina.weibo/*");
		// TipHelper.rootCommand("rm -rf /data/data/com.jingdong.app.mall/*");
		// TipHelper.rootCommand("rm -rf /data/data/com.baidu.netdisk/*");
		// TipHelper.rootCommand("rm -rf /data/data/com.android.email/*");
		// TipHelper.rootCommand("rm -rf /data/data/im.yixin/*");
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy()");

		try {
			LocalBroadcastManager.getInstance(this).unregisterReceiver(
					UARTStatusChangeReceiver);
		} catch (Exception ignore) {
			Log.e(TAG, ignore.toString());
		}
		unbindService(mServiceConnection);
		mService.stopSelf();
		mService = null;

	}

	@Override
	protected void onStop() {
		Log.d(TAG, "onStop");
		super.onStop();
	}

	@Override
	protected void onPause() {
		Log.d(TAG, "onPause");
		super.onPause();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.d(TAG, "onRestart");
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		if (!mBtAdapter.isEnabled()) {
			Log.i(TAG, "onResume - BT not enabled yet");
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		}

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {

		case REQUEST_SELECT_DEVICE:
			// When the DeviceListActivity return, with the selected device
			// address
			if (resultCode == Activity.RESULT_OK && data != null) {
				String deviceAddress = data
						.getStringExtra(BluetoothDevice.EXTRA_DEVICE);
				mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(
						deviceAddress);

				Log.d(TAG, "... onActivityResultdevice.address==" + mDevice
						+ "mserviceValue" + mService);
				((TextView) findViewById(R.id.deviceName)).setText(mDevice
						.getName() + " - connecting");
				mService.connect(deviceAddress);

			}
			break;
		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
				Toast.makeText(this, "Bluetooth has turned on ",
						Toast.LENGTH_SHORT).show();

			} else {
				// User did not enable Bluetooth or an error occurred
				Log.d(TAG, "BT not enabled");
				Toast.makeText(this, "Problem in BT Turning ON ",
						Toast.LENGTH_SHORT).show();
				finish();
			}
			break;
		default:
			Log.e(TAG, "wrong request code");
			break;
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {

	}

	@Override
	public void onBackPressed() {
		if (mState == UART_PROFILE_CONNECTED) {
			Intent startMain = new Intent(Intent.ACTION_MAIN);
			startMain.addCategory(Intent.CATEGORY_HOME);
			startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(startMain);
			showMessage(" running in background.\n Disconnect to exit");
		} else {
			new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle(R.string.popup_title)
					.setMessage(R.string.popup_message)
					.setPositiveButton(R.string.popup_yes,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									finish();
								}
							}).setNegativeButton(R.string.popup_no, null)
					.show();
		}
	}

	private void showMessage(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	public void VoiceContrl(String s) {
		AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		// mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,
		// AudioManager.ADJUST_RAISE, 0);
		if (s == "R")
			mAudioManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION,
					AudioManager.ADJUST_RAISE, 0);
		else if (s == "L")
			mAudioManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION,
					AudioManager.ADJUST_LOWER, 0);
	}

	public void Insert(String str) {
		int i = 0;
		for (; i < str.length(); i++) {
			if (Character.isDigit(str.charAt(i))) {
				break;
			}
		}
		String phoneName = str.substring(0, i);
		String phoneNumber = str.substring(i);
		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
		ContentResolver resolver = this.getContentResolver();
		ContentValues values = new ContentValues();
		long contactId = ContentUris.parseId(resolver.insert(uri, values));

		/* 往 data 中添加数据（要根据前面获取的id号） */
		// 添加姓名
		uri = Uri.parse("content://com.android.contacts/data");
		values.put("raw_contact_id", contactId);
		values.put("mimetype", "vnd.android.cursor.item/name");
		values.put("data2", phoneName);
		resolver.insert(uri, values);

		// 添加电话
		values.clear();
		values.put("raw_contact_id", contactId);
		values.put("mimetype", "vnd.android.cursor.item/phone_v2");
		values.put("data2", "2");
		values.put("data1", phoneNumber);
		resolver.insert(uri, values);
	}

	public void vib() {
		System.out.println("vibrate");
		Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vib.vibrate(100);
	}

	public boolean isScreenLocked(Context c) {
		android.app.KeyguardManager mKeyguardManager = (KeyguardManager) c
				.getSystemService(Context.KEYGUARD_SERVICE);
		return mKeyguardManager.inKeyguardRestrictedInputMode();
	}

	// public void GetLight() {
	// sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
	// Sensor sensor = sensorMgr.getDefaultSensor(Sensor.TYPE_LIGHT);
	// // state = 1;
	// SensorEventListener lsn = new SensorEventListener() {
	// public void onSensorChanged(SensorEvent e) {
	// // acc = e.accuracy;
	// // state = (e.values[0] >= 2) ? 0 : 1;
	// if (e.values[0] >= 2)
	// state = 0;
	// else
	// state = 1;
	// System.out.println("GetLightstate:" + state);
	// sensorMgr.unregisterListener(this);
	// }
	//
	// public void onAccuracyChanged(Sensor arg0, int arg1) {
	// // TODO Auto-generated method stub
	//
	// }
	// };
	// sensorMgr.registerListener(lsn, sensor,
	// SensorManager.SENSOR_DELAY_NORMAL);
	// System.out.println("ReturnState:" + state);
	//
	//
	// }
	// public void GetLight() {
	// new Thread(new Runnable() {
	// @Override
	// public void run() {
	// try {
	// sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
	// Sensor sensor = sensorMgr
	// .getDefaultSensor(Sensor.TYPE_LIGHT);
	// SensorEventListener lsn = new SensorEventListener() {
	// public void onSe	nsorChanged(SensorEvent e) {
	// // acc = e.accuracy;
	// // state = (e.values[0] >= 2) ? 0 : 1;
	// if (e.values[0] >= 2)
	// state = 0;
	// else
	// state = 1;
	// System.out.println("Getlightstate:" + state);
	// }
	//
	// public void onAccuracyChanged(Sensor arg0, int arg1) {
	// // TODO Auto-generated method stub
	//
	// }
	// };
	// sensorMgr.registerListener(lsn, sensor,
	// SensorManager.SENSOR_DELAY_NORMAL);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// }).start();
	// }
}