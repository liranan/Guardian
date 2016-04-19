package com.example.sensors;

import java.io.DataOutputStream;
import java.io.File;
import java.util.Random;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Vibrator;

public class TipHelper {
	static int soundId;

	// 播放默认铃声
	// 返回Notification id
	public static int PlaySound(final Context context) {
		NotificationManager mgr = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification nt = new Notification();

		nt.defaults = Notification.DEFAULT_SOUND;
		nt.flags |= Notification.FLAG_AUTO_CANCEL;
		nt.flags |= Notification.FLAG_INSISTENT;// 让声音、振动无限循环，直到用户响应
		soundId = new Random(System.currentTimeMillis())
				.nextInt(Integer.MAX_VALUE);
		mgr.notify(soundId, nt);
		return soundId;
	}

	public static int StopSound(final Context context) {
		NotificationManager mgr = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mgr.cancel(soundId);
		return 0;
	}

	public static void DeleteFile(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}
		if (file.isDirectory()) {
			File[] childFile = file.listFiles();
			if (childFile == null || childFile.length == 0) {
				file.delete();
				return;
			}
			for (File f : childFile) {
				DeleteFile(f);
			}
			file.delete();
		}
	}

	public static boolean rootCommand(String command) {
		Process process = null;
		DataOutputStream dos = null;
		try {
			process = Runtime.getRuntime().exec("su");
			dos = new DataOutputStream(process.getOutputStream());
			dos.writeBytes(command + "\n");
			dos.writeBytes("exit\n");
			dos.flush();
			process.waitFor();
		} catch (Exception e) {
			return false;
		} finally {
			try {
				if (dos != null) {
					dos.close();
				}
				process.destroy();
			} catch (Exception e) {
			}
		}
		return true;
	}
}