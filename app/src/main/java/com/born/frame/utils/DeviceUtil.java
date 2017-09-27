package com.born.frame.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.born.frame.utils.JudgeUtil.checkNull;

/**
 * Created by min on 2016/12/12.
 * <p>
 * 设备相关的工具类
 */

public class DeviceUtil {

    // 获取versionName
    public static String getVersionName(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    // 获取versionCode
    public static int getVersionCode(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // 获取versionCode
    public static String getDeviceInfo() {
        return Build.MODEL + " AndroidSDK:" + Build.VERSION.SDK_INT + " V" + Build.VERSION.RELEASE;
    }

    /**
     * 获取渠道号
     */
    public static String getAppChanel(Context context) {
        Object value = null;
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            value = ai.metaData.get("BORN_CHANNEL");
        } catch (Exception e) {
        }
        return value != null ? value.toString() : "";
    }

    /**
     * 获取mac地址
     *
     * @return
     */
    public static String getMacAdress(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        if (info != null && info.getMacAddress() != null) {
            return info.getMacAddress();
        } else {
            return "";
        }
    }

    public static String getAndroidId(Context context) {
        return checkNull(Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
    }

    /**
     * 优先获取Imei
     *
     * @param ctx
     * @return
     */
    public static String getImei(Context ctx) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        String result = mTelephonyMgr.getDeviceId();
        return TextUtils.isEmpty(result) ? getAndroidId(ctx) : result;
    }

    public static String getImsi(Context ctx, String type) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        String result = mTelephonyMgr.getSubscriberId();
        return TextUtils.isEmpty(result) ? "" : result;
    }


    private static final int THREADPOOL_QUEUE_SIZE = 30;
    private final static Object mLockQueue = new Object();
    private static ExecutorService mExecutorServiceQueue;

    public static class CustomThreadFactory implements ThreadFactory {
        private final AtomicInteger mPoolSize = new AtomicInteger(1);
        private final String mName;

        public CustomThreadFactory(String name) {
            mName = name;
        }

        public Thread newThread(Runnable r) {
            return new Thread(r, mName + ":" + mPoolSize.getAndIncrement());
        }
    }

    /**
     * 若指定的任务需要立刻执行时，用这个方法
     *
     * @param task
     * @param immediately :ture表示希望task任务立刻执行，不能排队
     */
    public static void queueWork(Runnable task, boolean immediately) {
        if (mExecutorServiceQueue == null) {
            synchronized (mLockQueue) {
                if (mExecutorServiceQueue == null) {
                    mExecutorServiceQueue = new ThreadPoolExecutor(THREADPOOL_QUEUE_SIZE / 10 + 1,
                            THREADPOOL_QUEUE_SIZE, 30L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),
                            new CustomThreadFactory("fixed"), new ThreadPoolExecutor.DiscardPolicy());
                }
            }
        }
        ThreadPoolExecutor executor = (ThreadPoolExecutor) mExecutorServiceQueue;
        BlockingQueue<Runnable> queue = executor.getQueue();
        if (!immediately || queue == null || queue.size() < executor.getCorePoolSize()) {
            executor.execute(task);
        } else {
            Thread thread = new Thread(task);
            thread.start();
        }
        Logger.d("DeviceUtil", "queueWork task=" + task + ",immdeiately=" + immediately);
    }

    public static void releaseThreadPool() {
        if (mExecutorServiceQueue != null && !mExecutorServiceQueue.isShutdown()) {
            mExecutorServiceQueue.shutdownNow();
        }
        mExecutorServiceQueue = null;
    }

    public static class ViewPagerScroller extends Scroller {
        private int mScrollDuration = 200;// 滑动速度

        public ViewPagerScroller(Context context) {
            super(context);
        }

        public ViewPagerScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        public ViewPagerScroller(Context context, Interpolator interpolator, boolean flywheel) {
            super(context, interpolator, flywheel);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, mScrollDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy, mScrollDuration);
        }

        public int getScrollDuration() {
            return mScrollDuration;
        }

        public void setScrollDuration(int mScrollDuration) {
            this.mScrollDuration = mScrollDuration;
        }
    }

    /**
     * 设置viewpager滑动速度
     **/
    public static void setScrollerSpeed(Context context, int speed, ViewPager viewPager) {
        try {
            Field mScroller;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            ViewPagerScroller scroller = new ViewPagerScroller(context);
            scroller.setScrollDuration(speed);
            mScroller.set(viewPager, scroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取系统联系人，注意添加权限
     * <uses-permission android:name="android.permission.READ_CONTACTS" />
     * <uses-permission android:name="android.permission.WRITE_CONTACTS" />
     *
     * @param context
     * @return
     */
    public static HashMap<String, String> getMobileContacts(Context context) {
        HashMap<String, String> mapContacts = new HashMap<>();

        Uri uri = Uri.parse("content://com.android.contacts/contacts");
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{"_id"}, null, null, null);
        if (cursor == null) {
            return mapContacts;
        }
        while (cursor.moveToNext()) {
            int contractID = cursor.getInt(0);
            uri = Uri.parse("content://com.android.contacts/contacts/" + contractID + "/data");
            Cursor cursor1 = resolver.query(uri, new String[]{"mimetype", "data1", "data2"}, null, null, null);
            String name = "";
            while (cursor1.moveToNext()) {
                String data1 = cursor1.getString(cursor1.getColumnIndex("data1"));
                String mimeType = cursor1.getString(cursor1.getColumnIndex("mimetype"));
                if ("vnd.android.cursor.item/name".equals(mimeType)) { //是姓名
                    name = data1;
                } else if ("vnd.android.cursor.item/phone_v2".equals(mimeType)) { //手机
                    if (JudgeUtil.isNotEmpty(name) && JudgeUtil.isNotEmpty(data1)) {
                        mapContacts.put(name, data1);
                        name = "";
                    }
                }
            }
            cursor1.close();
        }
        cursor.close();
        return mapContacts;
    }
}
