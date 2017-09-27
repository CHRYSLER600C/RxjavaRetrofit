package com.born.frame.utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.TextView;

import com.born.frame.common.CommonData;

import org.apache.http.util.EncodingUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class ImageUtil {

    public static String FILE_PATH = getBaseFilePath(); // 图片存储路径

    /**
     * 获取图片下载存放目录
     */
    public static String getBaseFilePath() {
        String filePath = "";
        if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState())) {
            filePath = CommonData.IMAMGE_DIR_SD + "/";
        } else {
            filePath = CommonData.IMAMGE_DIR_RAM + "/";
        }

        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return filePath;
    }

    /**
     * 旋转图片为正方向
     *
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        // 旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    public static Bitmap getSimplifyBitmap(String path) {
        int side = 960;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int dWidth = options.outWidth / side;
        int dHeight = options.outHeight / side;
        if (dWidth < dHeight) {
            options.inSampleSize = dHeight;
        } else {
            options.inSampleSize = dWidth;
        }
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);

        return bitmap;
    }

    public static String bitmap2File(String pathSrc, String name) {
        return bitmap2File(pathSrc, name, FILE_PATH);
    }

    /**
     * 把图片存储到指定路径，当图片过大时，会降低分辨率存储
     *
     * @param pathSrc
     * @param name
     * @param pathDest
     * @return 存储后文件名
     */
    public static String bitmap2File(String pathSrc, String name, String pathDest) {
        File file = null;
        Bitmap bitmap = getSimplifyBitmap(pathSrc);
        int degree = readPictureDegree(pathSrc);
        bitmap = rotaingImageView(degree, bitmap);
        if (null == bitmap) {
            return null;
        }
        FileOutputStream fileOut = null;
        try {
            File pathFile = new File(pathDest);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            file = new File(pathDest, name);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            fileOut = new FileOutputStream(file);
            int size = 100;
            if (bitmap.getHeight() > 1000 || bitmap.getWidth() > 1000) {
                size = 80;
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, size, fileOut);
            fileOut.flush();
            fileOut.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (fileOut != null) {
                try {
                    fileOut.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return file.getAbsolutePath();
    }

    /**
     * 将byte数组无损的写到硬盘上（图片或者文件)
     *
     * @param data
     * @param path
     */
    public static String byte2File(byte[] data, String path) {
        if (data.length < 3 || path.equals("")) {//判断输入的byte是否为空
            return "";
        }
        try {
            FileOutputStream imageOutput = new FileOutputStream(new File(path));//打开输入流
            imageOutput.write(data, 0, data.length);//将byte写入硬盘
            imageOutput.close();
            System.out.println("Make Picture success,Please find image in " + path);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return path;
    }

    public static Bitmap drawableToBitmap(Drawable d) {
        int w = d.getIntrinsicWidth();
        int h = d.getIntrinsicWidth();
        Config config = d.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        Canvas c = new Canvas(bitmap);
        d.setBounds(0, 0, w, h);
        d.draw(c);
        return bitmap;
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static String getCameraPath() {
        StringBuffer sb = new StringBuffer();
        sb.append(Environment.getExternalStorageDirectory().getPath());
        sb.append("/DCIM/Camera/");
        File file = new File(sb.toString());
        if (!file.exists()) {
            file.mkdirs();
        }
        return sb.toString();
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
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
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
        path.addRoundRect(rectF, innerCorner, Direction.CW);
        canvas.drawPath(path, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static String compressFile(String filePathSrc) {
        String name = "temp_" + System.currentTimeMillis() + ".jpg";
        return bitmap2File(filePathSrc, name, FILE_PATH);
    }

    /**
     * 选取图库的图片
     *
     * @param ctx
     * @param data
     * @return
     */
    public static String getImagePathFromGallery(Context ctx, Intent data) {
        String sdcardPath = "";
        Cursor cursor = null;
        try {
            Uri picUri = data.getData();// 获得图片的uri=content://media/external/images/media/75920
            String[] proj = {MediaStore.MediaColumns.DATA};
            cursor = ctx.getContentResolver().query(picUri, proj, null, null, null);
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                sdcardPath = cursor.getString(column_index); // /storage/sdcard1/Images/照相摄像/辅材表.jpg
            } else {
                sdcardPath = picUri.getPath();
            }
            sdcardPath = EncodingUtils.getString(sdcardPath.getBytes(), "utf-8");

            if (Build.VERSION.SDK_INT < 14) {// 4.0以上的版本会自动关闭(4.0--14;4.0.3--15)
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (cursor != null) {
                if (Build.VERSION.SDK_INT < 14) {// 4.0以上的版本会自动关闭(4.0--14;4.0.3--15)
                    cursor.close();
                }
            }
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
    public static void setTextViewDrawableLeft(TextView tv, int resId, int dpW, int dpH, int dpPadding) {
        Drawable[] ds = tv.getCompoundDrawables();
        tv.setCompoundDrawables(res2drawable(tv.getContext(), resId, dpW, dpH), ds[1], ds[2], ds[3]);
        tv.setCompoundDrawablePadding(DisplayUtil.dip2px(tv.getContext(), dpPadding));
    }

    public static void setTextViewDrawableTop(TextView tv, int resId, int dpW, int dpH, int dpPadding) {
        Drawable[] ds = tv.getCompoundDrawables();
        tv.setCompoundDrawables(ds[0], res2drawable(tv.getContext(), resId, dpW, dpH), ds[2], ds[3]);
        tv.setCompoundDrawablePadding(DisplayUtil.dip2px(tv.getContext(), dpPadding));
    }

    public static void setTextViewDrawableRight(TextView tv, int resId, int dpW, int dpH, int dpPadding) {
        Drawable[] ds = tv.getCompoundDrawables();
        tv.setCompoundDrawables(ds[0], ds[1], res2drawable(tv.getContext(), resId, dpW, dpH), ds[3]);
        tv.setCompoundDrawablePadding(DisplayUtil.dip2px(tv.getContext(), dpPadding));
    }

    public static void setTextViewDrawableBottom(TextView tv, int resId, int dpW, int dpH, int dpPadding) {
        Drawable[] ds = tv.getCompoundDrawables();
        tv.setCompoundDrawables(ds[0], ds[1], ds[2], res2drawable(tv.getContext(), resId, dpW, dpH));
        tv.setCompoundDrawablePadding(DisplayUtil.dip2px(tv.getContext(), dpPadding));
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
        int width = dpW < 0 ? drawable.getMinimumWidth() : DisplayUtil.dip2px(context, dpW);
        int height = dpH < 0 ? drawable.getMinimumHeight() : DisplayUtil.dip2px(context, dpH);
        drawable.setBounds(0, 0, width, height); // 这一步必须要做,否则不会显示.
        return drawable;
    }
}