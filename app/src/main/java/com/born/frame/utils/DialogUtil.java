package com.born.frame.utils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.born.frame.R;
import com.born.frame.dataclass.bean.NameValue;
import com.born.frame.dataclass.bean.PickerItem;
import com.born.frame.dataclass.bean.PickerValue;
import com.born.frame.view.dialog.PickerDialog;

import java.util.Calendar;


/**
 * Created by min on 2016/12/9.
 */

public class DialogUtil {

    /**
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
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month++;
                        String monthStr = (month > 9 ? "" : "0") + month;
                        String dayStr = (day > 9 ? "" : "0") + day;

                        String timeStart = year + "年" + monthStr + "月" + dayStr + "日";
                        ViewUtil.setViewText(tv, timeStart);
                    }
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
        pickerBuilder.setBtnOk(pickerValue, "bottom", new PickerDialog.IPickerDialogOkCallBack() {
            @Override
            public void handleBtnOk(NameValue nv1, NameValue nv2, NameValue nv3) {
                tv.setText(nv1.name);
                tv.setTag(nv1.value);
            }
        }).setPickedData(new PickerItem(new NameValue(tv.getText().toString(), "")));

        if (pickerValue != null && pickerValue.list1.size() == 0) {
            Toast.makeText(context, "暂无数据", Toast.LENGTH_SHORT).show();
        } else {
            DisplayUtil.showAnimatDialog(pickerBuilder.create());
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
            DisplayUtil.showAnimatDialog(pickerBuilder.create());
        }
    }
}
