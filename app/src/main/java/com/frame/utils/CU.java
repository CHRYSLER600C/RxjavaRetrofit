package com.frame.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Base64;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.MetaDataUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.UriUtils;
import com.frame.R;
import com.frame.activity.BaseActivity;
import com.frame.application.App;
import com.frame.common.CommonData;
import com.frame.dataclass.bean.NameValue;
import com.frame.dataclass.bean.PickerItem;
import com.frame.dataclass.bean.PickerValue;
import com.frame.httputils.OkHttpUtil;
import com.frame.other.ICallBack;
import com.frame.view.dialog.PickerDialog;
import com.gyf.immersionbar.ImmersionBar;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Cookie;

/**
 * Created by min on 2016/12/12.
 * CommonUtil：通用工具类
 */
public class CU {

    /*
     * AES/CBC/NoPadding 要求 密钥必须是16位的；Initialization vector (IV) 必须是16位
     * 待加密内容的长度必须是16的倍数，如果不是16的倍数，就会出如下异常：
     * javax.crypto.IllegalBlockSizeException: Input length not multiple of 16
     * bytes
     *
     * 由于固定了位数，所以对于被加密数据有中文的, 加、解密不完整
     *
     * 可 以看到，在原始数据长度为16的整数n倍时，假如原始数据长度等于16*n，则使用NoPadding时加密后数据长度等于16*n，
     * 其它情况下加密数据长 度等于16*(n+1)。在不足16的整数倍的情况下，假如原始数据长度等于16*n+m[其中m小于16]，
     * 除了NoPadding填充之外的任何方 式，加密数据长度都等于16*(n+1).
     */
    static final String algorithmStr = "AES/ECB/PKCS5Padding";
    static final String AESKEY = "0123456789abcdef"; // SYS_PARAM_APP_PWD_KEY

    /**
     * 泛型转换工具方法 eg:object ==> map<String, String>
     *
     * @param object Object
     * @param <T>    转换得到的泛型对象
     * @return T
     */
    @SuppressWarnings("unchecked")
    public static <T> T cast(Object object) {
        return (T) object;
    }

    public static String getDeviceInfo() {
        return Build.MODEL + " AndroidSDK:" + Build.VERSION.SDK_INT + " V" + Build.VERSION.RELEASE;
    }

    /**
     * 获取渠道号
     */
    public static String getAppChanel() {
        return MetaDataUtils.getMetaDataInApp("UMENG_CHANNEL");
    }

    /**
     * 动画显示提示框
     *
     * @param dlg
     */
    public static void showAnimatDialog(Dialog dlg) {
        if (ObjectUtils.isNotEmpty(dlg) && !dlg.isShowing()) {
            Window window = dlg.getWindow();
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
            window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
            window.setWindowAnimations(R.style.AnimationBottomInOut); // 添加动画
            dlg.show();
        }
    }

    /**
     * 获取随机rgb颜色值
     */
    public static int randomColor() {
        Random random = new Random();
        //0-190, 如果颜色值过大,就越接近白色,就看不清了,所以需要限定范围
        int red = random.nextInt(150);
        //0-190
        int green = random.nextInt(150);
        //0-190
        int blue = random.nextInt(150);
        //使用rgb混合生成一种新的颜色,Color.rgb生成的是一个int数
        return Color.rgb(red, green, blue);
    }

    /**
     * Tab colors
     */
    public static final int[] TAB_COLORS = new int[]{
            Color.parseColor("#90C5F0"),
            Color.parseColor("#91CED5"),
            Color.parseColor("#F88F55"),
            Color.parseColor("#C0AFD0"),
            Color.parseColor("#E78F8F"),
            Color.parseColor("#67CCB7"),
            Color.parseColor("#F6BC7E")
    };

    public static int randomTagColor() {
        int randomNum = new Random().nextInt();
        int position = randomNum % TAB_COLORS.length;
        if (position < 0) {
            position = -position;
        }
        return TAB_COLORS[position];
    }

    /**
     * 密码加密
     *
     * @param pwd
     * @return
     */
    public static String encodePwd(String pwd) {
        String result = pwd;
        if (ObjectUtils.isNotEmpty(pwd)) {
            String pwdAES = Base64.encodeToString(EncryptUtils.encryptAES(pwd.getBytes(), AESKEY.getBytes(),
                    algorithmStr, null), Base64.DEFAULT);
            result = URLEncoder.encode(pwdAES);
        }
        return result;
    }

    /**
     * ================================================DialogUtil==============================================
     * 日期选择对话框， 格式：XX年XX月XX日
     *
     * @param context
     * @param tv
     */
    public static void showDatePickerDialog(Context context, final TextView tv) {
        String timeOld = tv.getText().toString();
        String str[] = timeOld.replace("年", "-").replace("月", "-").replace("日", "").split("-");

        int yearInt, monthInt, dayInt;
        if (str.length == 3) {
            yearInt = Integer.parseInt(str[0]);
            monthInt = Integer.parseInt(str[1]) - 1;
            dayInt = Integer.parseInt(str[2]);
        } else {
            Calendar c = Calendar.getInstance();
            yearInt = c.get(Calendar.YEAR);
            monthInt = c.get(Calendar.MONTH);
            dayInt = c.get(Calendar.DATE);
        }

        final DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                (DatePicker view, int year, int month, int day) -> {
                    month++;
                    String monthStr = (month > 9 ? "" : "0") + month;
                    String dayStr = (day > 9 ? "" : "0") + day;

                    String timeStart = year + "年" + monthStr + "月" + dayStr + "日";
                    ViewUtil.setViewText(tv, timeStart);
                }, yearInt, monthInt, dayInt);
        Window window = datePickerDialog.getWindow();
        window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.AnimationBottomInOut); // 添加动画
        datePickerDialog.show();
    }

    /**
     * 单一选择对话框
     */
    public static void showSinglePickerDialog(Context context, final TextView tv, PickerValue pickerValue) {
        final PickerDialog.Builder pickerBuilder = new PickerDialog.Builder(context);
        pickerBuilder.setBtnOk(pickerValue, "bottom", (NameValue nv1, NameValue nv2, NameValue nv3) -> {
            tv.setText(nv1.name);
            tv.setTag(nv1.value);
        }).setPickedData(new PickerItem(new NameValue(tv.getText().toString(), "")));

        if (pickerValue != null && pickerValue.list1.size() == 0) {
            Toast.makeText(context, "暂无数据", Toast.LENGTH_SHORT).show();
        } else {
            CU.showAnimatDialog(pickerBuilder.create());
        }
    }

    /**
     * 选择对话框
     */
    public static void showPickerDialog(Context context, PickerDialog.IPickerDialogOkCallBack listener, PickerValue
            pickerValue, NameValue defaultNv) {
        final PickerDialog.Builder pickerBuilder = new PickerDialog.Builder(context);
        pickerBuilder.setBtnOk(pickerValue, "bottom", listener)
                .setPickedData(new PickerItem(defaultNv));

        if (pickerValue != null && pickerValue.list1.size() == 0) {
            Toast.makeText(context, "暂无数据", Toast.LENGTH_SHORT).show();
        } else {
            CU.showAnimatDialog(pickerBuilder.create());
        }
    }

    /**
     * ================================================FileUtil==============================================
     * 随机生成文件名
     */
    public static String randomFileName(String postfix) {
        return getFormatTime("yyyy_MM_dd_", 0) + (int) (Math.random() * 100000) + postfix;
    }

    // 计算缓存大小
    public static String getCacheSize() {
        long fileSize = 0;
        try {
            fileSize += Long.parseLong(FileUtils.getSize(App.getInstance().getFilesDir()));
            fileSize += Long.parseLong(FileUtils.getSize(App.getInstance().getCacheDir()));
            fileSize += Long.parseLong(FileUtils.getSize(App.getInstance().getExternalCacheDir()));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return fileSize > 0 ? formatFileSize(fileSize) : "0KB";
    }

    /**
     * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache)
     */
    public static void cleanInternalCache() {
        FileUtils.delete(App.getInstance().getCacheDir());
    }

    /**
     * 清除/data/data/com.xxx.xxx/files下的内容
     */
    public static void cleanFiles() {
        FileUtils.delete(App.getInstance().getFilesDir());
    }

    /**
     * * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache)
     */
    public static void cleanExternalCache() {
        FileUtils.delete(App.getInstance().getExternalCacheDir());
    }


    /**
     * ================================================FormatUtil==============================================
     * 转换文件大小
     *
     * @return B/KB/MB/GB
     */
    public static String formatFileSize(long fileS) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
        String fileSizeString;
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * 判断字符串是否含有数字
     *
     * @param content
     * @return
     */
    public static boolean hasDigit(String content) {
        if (TextUtils.isEmpty(content)) {
            return false;
        }
        boolean result = false;
        Pattern p = Pattern.compile(".*\\d+.*");
        Matcher m = p.matcher(content);
        if (m.matches())
            result = true;
        return result;
    }

    /**
     * 判断字符串是否全为数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        if (null == str) {
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]+");
        return pattern.matcher(str).matches();
    }

    /**
     * 以date时间转换成format格式输出
     *
     * @param timeInMillis 0 表示返回当前时间
     * @param format       yyyyMMddkkmm、yyyy-MM-dd"、MM月dd日、yyyyMMddHHmmss、yyyyMMddHHmm
     * @return
     */
    public static String getFormatTime(String format, long timeInMillis) {
        return DateFormat.format(format, timeInMillis <= 0 ? System.currentTimeMillis() : timeInMillis).toString();
    }

    /**
     * 时间格式转换
     *
     * @param timeIn
     * @param formatIn
     * @param formatOut
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String transferTime(String timeIn, String formatIn, String formatOut) {
        String timeOut = "";
        if (TextUtils.isEmpty(formatIn) || TextUtils.isEmpty(formatOut)) {
            return timeOut;
        }
        try {
            Date dateIn = new SimpleDateFormat(formatIn).parse(timeIn);
            timeOut = new SimpleDateFormat(formatOut).format(dateIn);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeOut;
    }

    public static String formatTime(long timeMs) {
        String formated;
        StringBuilder sFormatBuilder = new StringBuilder();
        Formatter sFormatter = new Formatter(sFormatBuilder, Locale.getDefault());

        int totalSeconds = (int) (timeMs / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        sFormatBuilder.setLength(0);
        if (hours > 0) {
            formated = sFormatter.format("%02d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            formated = sFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
        sFormatter.close();
        return formated;
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
                    if (ObjectUtils.isNotEmpty(name) && ObjectUtils.isNotEmpty(data1)) {
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

    /**
     * ================================================ImageUtil==============================================
     */

    public static String FILE_PATH = getBaseImgPath(); // 图片存储路径

    /**
     * 获取图片下载存放目录
     */
    public static String getBaseImgPath() {
        String filePath = CommonData.IMAGE_DIR_SD;
        FileUtils.createOrExistsDir(filePath);
        return filePath;
    }

    /**
     * 根据iv大小返回压缩后的图片
     *
     * @param bitmap
     * @param iv
     * @return
     */
    public static Bitmap compressBitmap(Bitmap bitmap, ImageView iv) {
        if (bitmap == null || iv == null) return null;
        float widthScale = (float) iv.getWidth() / bitmap.getWidth();
        float heightScale = (float) iv.getHeight() / bitmap.getHeight();
        float scale = widthScale < heightScale ? widthScale : heightScale;
        return ImageUtils.scale(bitmap, scale, scale, true);
    }

    /**
     * 把图片存储到指定路径，当图片过大时，会降低分辨率存储
     *
     * @param pathSrc
     * @param fileName
     * @param pathDest
     * @return 存储后文件名
     */
    public static String bitmap2File(String pathSrc, String pathDest, String fileName) {
        Bitmap bitmap = ImageUtils.compressBySampleSize(ImageUtils.getBitmap(pathSrc), 1000, 1000, true);
//        int degree = ImageUtils.getRotateDegree(pathSrc);
//        bitmap = ImageUtils.rotate(bitmap, degree, 0, 0, true);

        String filePath = pathDest + (pathDest.endsWith(File.separator) ? "" : File.separator) + fileName;
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        if (fileName.endsWith("png")) format = Bitmap.CompressFormat.PNG;
        if (ImageUtils.save(bitmap, filePath, format, true)) return filePath;
        return "";
    }

    /**
     * 选取图库的图片
     *
     * @param data
     * @return
     */
    public static String getImagePathFromGallery(Intent data) {
        String sdcardPath = "";
        Cursor cursor;
        try {
            Uri picUri = data.getData();// 获得图片的uri=content://media/external/images/media/75920
            String[] proj = {MediaStore.MediaColumns.DATA};
            cursor = App.getInstance().getContentResolver().query(picUri, proj, null, null, null);
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                sdcardPath = cursor.getString(column_index); // /storage/sdcard1/Images/照相摄像/辅材表.jpg
            } else {
                sdcardPath = picUri.getPath();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sdcardPath;
    }

    /**
     * 动态设置TextView(EditText, Button)的drawable图标, 可设置图标大小
     *
     * @param tv
     * @param resId
     * @param dpW       单位dp, -1 表示使用原始宽度
     * @param dpH       单位dp, -1 表示使用原始高度
     * @param dpPadding 图标文字间隔
     */
    public static void setTVDrawableLeft(TextView tv, int resId, int dpW, int dpH, int dpPadding) {
        Drawable[] ds = tv.getCompoundDrawables();
        tv.setCompoundDrawables(res2drawable(tv.getContext(), resId, dpW, dpH), ds[1], ds[2], ds[3]);
        tv.setCompoundDrawablePadding(ConvertUtils.dp2px(dpPadding));
    }

    public static void setTVDrawableTop(TextView tv, int resId, int dpW, int dpH, int dpPadding) {
        Drawable[] ds = tv.getCompoundDrawables();
        tv.setCompoundDrawables(ds[0], res2drawable(tv.getContext(), resId, dpW, dpH), ds[2], ds[3]);
        tv.setCompoundDrawablePadding(ConvertUtils.dp2px(dpPadding));
    }

    public static void setTVDrawableRight(TextView tv, int resId, int dpW, int dpH, int dpPadding) {
        Drawable[] ds = tv.getCompoundDrawables();
        tv.setCompoundDrawables(ds[0], ds[1], res2drawable(tv.getContext(), resId, dpW, dpH), ds[3]);
        tv.setCompoundDrawablePadding(ConvertUtils.dp2px(dpPadding));
    }

    public static void setTVDrawableBottom(TextView tv, int resId, int dpW, int dpH, int dpPadding) {
        Drawable[] ds = tv.getCompoundDrawables();
        tv.setCompoundDrawables(ds[0], ds[1], ds[2], res2drawable(tv.getContext(), resId, dpW, dpH));
        tv.setCompoundDrawablePadding(ConvertUtils.dp2px(dpPadding));
    }

    /**
     * @param dpW 单位dp, -1 表示使用原始宽度
     * @param dpH 单位dp, -1 表示使用原始高度
     */
    public static Drawable res2drawable(Context context, int resId, int dpW, int dpH) {
        if (resId < 0) {
            return null;
        }
        Drawable drawable = context.getResources().getDrawable(resId);
        int width = dpW < 0 ? drawable.getMinimumWidth() : ConvertUtils.dp2px(dpW);
        int height = dpH < 0 ? drawable.getMinimumHeight() : ConvertUtils.dp2px(dpH);
        drawable.setBounds(0, 0, width, height); // 这一步必须要做,否则不会显示.
        return drawable;
    }

    //保存图片到指定路径并刷新相册
    public static boolean saveBmp2Gallery(Bitmap bmp) {
        boolean isSuccess = false;
        String fileName = randomFileName(".jpg");
        File file = new File(getBaseImgPath(), fileName);
        try {
            isSuccess = ImageUtils.save(bmp, file, Bitmap.CompressFormat.JPEG, true);

            //把文件插入到系统图库
            MediaStore.Images.Media.insertImage(App.getInstance().getContentResolver(), file.getAbsolutePath(),
                    fileName, null);

            //保存图片后发送广播通知更新数据库
            App.getInstance().sendBroadcast(
                    new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, UriUtils.file2Uri(file)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    /**
     * 给WebView同步Cookie
     *
     * @param context 上下文
     * @param url     可以使用[domain][host]
     */
    public static void synCookies(Context context, String url) {
        List<Cookie> cookies = OkHttpUtil.getInstance().getCookies(null);

        CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(context);
        cookieSyncManager.sync();
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        for (Cookie cookie : cookies) {
            cookieManager.setCookie(url, cookie.toString());
        }
        CookieSyncManager.getInstance().sync();
    }

    /**
     * 调用本地分享文本
     */
    public static void showLocationShare(Activity activity, String shareContent) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareContent);//注意：这里只是分享文本内容
        sendIntent.setType("text/plain");
        activity.startActivity(sendIntent);
    }

    /**
     * 在X,Y方向上缩放
     */
    public static void scaleXY(View view, long duration, ICallBack icb, float... values) {
        commonFloatAnim(view, "scaleX", duration, null, icb, values);
        commonFloatAnim(view, "scaleY", duration, null, icb, values);
    }


    public static void clickScale(View v, MotionEvent event, float scale) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            scaleXY(v, 100, null, 1f, scale);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            scaleXY(v, 100, null, scale, 1f);
        }
    }

    /**
     * 通用属性动画
     *
     * @param icb 动画完成后回调
     */
    public static void commonFloatAnim(View view, String propertyName, long duration,
                                       TimeInterpolator interpolator, ICallBack icb, float... values) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(view, propertyName, values).setDuration(duration);
        if (interpolator != null) anim.setInterpolator(new AccelerateInterpolator());
        anim.start();
        if (icb != null) {
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    icb.dataCallback(null);
                    super.onAnimationEnd(animation);
                }
            });
        }
    }

    /**
     * 设置图片圆角
     *
     * @param bitmap      数据源
     * @param innerCorner 圆角显示的位置 8个圆角半径值 分别对就左上、右上、右下、左下四个点<br/>
     *                    float inner[] = new float[] {20, 20, 0, 0, 20, 20 ,0, 0 };
     */
    public static Bitmap drawCorner(Bitmap bitmap, float[] innerCorner) {
        if (bitmap == null) {
            return null;
        }
        Bitmap output = null;
        try {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError e) {
            if (output != null && !output.isRecycled()) {
                output.recycle();
                output = null;
            }
            return null;
        }
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        Path path = new Path();
        path.addRoundRect(rectF, innerCorner, Path.Direction.CW);
        canvas.drawPath(path, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static double parserDouble(String value) {
        if (ObjectUtils.isEmpty(value)) return 0d;
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0d;
        }
    }

    public static int parserInt(String value) {
        return (int) parserDouble(value);
    }

    public static long parserLong(String value) {
        return (long) parserDouble(value);
    }

    public static int getRandomInt(int max) {
        return max > 0 ? new Random().nextInt(max) : 0;
    }

    public static byte[] getBytes(final String data, final String charset) {
        try {
            return data.getBytes(charset);
        } catch (final UnsupportedEncodingException e) {
            return data.getBytes();
        }
    }

    /**
     * 初始化沉浸式状态栏，个性化请重载
     *
     * @param barColorResId 状态栏颜色（0：default, -1:不设置)
     * @param isFit         是否留出状态栏高度
     */
    public static void setImmersionBar(BaseActivity activity, int barColorResId, boolean isFit) {
        ImmersionBar bar = ImmersionBar.with(activity)
                .statusBarDarkFont(true) //状态栏字体是深色，不写默认为亮色
                .fitsSystemWindows(isFit);//解决状态栏和布局重叠问题，默认false，为true时要指定statusBarColor()，不然状态栏为透明色
        if (barColorResId >= 0) bar.statusBarColor(barColorResId > 0 ? barColorResId : R.color.title_bg_color);
        bar.init(); //必须调用方可应用以上所配置的参数
    }

    /**
     * 两个长的数字字符串做和
     *
     * @param first
     * @param second
     * @return
     */
    public static String addTwoNumberOfString(String first, String second) {
        if (!isNumeric(first) || !isNumeric(second)) {
            return "0";
        }

        String integerF = "";
        String decimalF = "";
        String integerS = "";
        String decimalS = "";
        String sumInteger = "";
        String sumDecimal = "";

        int firstIndex = first.indexOf(".");
        if (firstIndex == -1) {
            integerF = first;
        } else {
            integerF = first.substring(0, firstIndex);
            decimalF = first.substring(firstIndex + 1);
        }

        int secondIndex = second.indexOf(".");
        if (secondIndex == -1) {
            integerS = second;
        } else {
            integerS = second.substring(0, secondIndex);
            decimalS = second.substring(secondIndex + 1);
        }

        // 字符串反转
        integerF = new StringBuffer(integerF).reverse().toString();
        integerS = new StringBuffer(integerS).reverse().toString();
        decimalF = new StringBuffer(decimalF).reverse().toString();
        decimalS = new StringBuffer(decimalS).reverse().toString();

        int sumc = 0;
        String cf = "";
        String cs = "";

        // 裁剪为等长
        if (decimalF.length() > decimalS.length()) {
            int offsetLen = decimalF.length() - decimalS.length();
            sumDecimal = decimalF.substring(0, offsetLen);
            decimalF = decimalF.substring(offsetLen);
        } else {
            int offsetLen = decimalS.length() - decimalF.length();
            sumDecimal = decimalS.substring(0, offsetLen);
            decimalS = decimalS.substring(offsetLen);
        }
        // 计算小数部分
        for (int i = 0; i < decimalF.length(); i++) {
            cf = decimalF.substring(i, i + 1);
            cs = decimalS.substring(i, i + 1);
            sumc += Integer.parseInt(cf) + Integer.parseInt(cs);
            sumDecimal += sumc % 10;
            sumc = sumc / 10;
        }
        sumDecimal = new StringBuffer(sumDecimal).reverse().toString();

        // 计算整数部分
        int integerMaxLen = integerF.length() > integerS.length() ? integerF.length() : integerS.length();
        for (int i = 0; i < integerMaxLen; i++) {
            cf = i < integerF.length() ? integerF.substring(i, i + 1) : "0";
            cs = i < integerS.length() ? integerS.substring(i, i + 1) : "0";
            sumc += Integer.parseInt(cf) + Integer.parseInt(cs);
            sumInteger += sumc % 10;
            sumc = sumc / 10;
        }
        if (sumc > 0) {
            sumInteger += sumc;
        }
        sumInteger = new StringBuffer(sumInteger).reverse().toString();

        if (TextUtils.isEmpty(sumDecimal)) {
            return sumInteger;
        } else {
            return sumInteger + "." + sumDecimal;
        }
    }
}
