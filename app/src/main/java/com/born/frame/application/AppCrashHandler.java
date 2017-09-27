package com.born.frame.application;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Looper;
import android.text.format.DateFormat;

import com.born.frame.common.CommonData;
import com.born.frame.utils.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AppCrashHandler implements UncaughtExceptionHandler {

	public static final String TAG = AppCrashHandler.class.getName();

	private Context mContext;
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	private static AppCrashHandler mInstance = new AppCrashHandler();

	private Map<String, String> mInfos = new HashMap<String, String>();
	private SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private AppCrashHandler() {
	}

	public static AppCrashHandler getInstance() {
		return mInstance;
	}

	public void init(Context context) {
		mContext = context;
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		Logger.e(TAG, "" + ex);
		if (!handleException(ex) && mDefaultHandler != null) {
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			Logger.d(TAG, "uncaughtException");
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(10);
		}
	}

	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				// Toast.makeText(mContext, "靠，我挂了，请重新启动或联系开发者。",
				// Toast.LENGTH_SHORT).show();
				Looper.loop();
			}
		}.start();
		collectDeviceInfo(mContext);
		saveCrashInfo2File(ex);
		return true;
	}

	public void collectDeviceInfo(Context ctx) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				String versionName = pi.versionName == null ? "null" : pi.versionName;
				String versionCode = pi.versionCode + "";
				mInfos.put("versionName", versionName);
				mInfos.put("versionCode", versionCode);
			}
		} catch (NameNotFoundException e) {
			Logger.e(TAG, "an error occured when collect package info", e);
		}
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				mInfos.put(field.getName(), field.get(null).toString());
				Logger.d(TAG, field.getName() + " : " + field.get(null));
			} catch (Exception e) {
				Logger.e(TAG, "an error occured when collect crash info", e);
			}
		}
	}

	private String saveCrashInfo2File(Throwable ex) {

		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : mInfos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			if ("TIME".equals(key)) {
				try {
					value = DateFormat.format("yyyy-MM-dd HH:mm:ss", Long.parseLong(value)).toString();
				} catch (Exception e) {
				}
			}
			sb.append(key + "=" + value + "\n");
		}

		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		sb.append(result);
		Logger.e(TAG, sb.toString());
		try {
			String time = mFormatter.format(new Date());
			String fileName = "crash-" + time + ".txt";
			String path = CommonData.LOGGER_DIR_SD;
			File dir = new File(path);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			FileOutputStream fos = new FileOutputStream(path + File.separator + fileName);
			fos.write(sb.toString().getBytes());
			fos.close();
			return fileName;
		} catch (Exception e) {
			Logger.e(TAG, "an error occured while writing file...", e);
		}
		return null;
	}
}
