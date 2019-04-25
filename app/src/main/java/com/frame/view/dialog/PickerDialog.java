package com.frame.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.frame.R;
import com.frame.dataclass.bean.NameValue;
import com.frame.dataclass.bean.PickerItem;
import com.frame.dataclass.bean.PickerValue;
import com.frame.view.pickerview.PickerView;

public class PickerDialog extends Dialog {

    protected PickerDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public PickerDialog(Context context, int arg1) {
        super(context, arg1);
    }

    public PickerDialog(Context context) {
        super(context);
    }

    public static class Builder {
        private Context context;
        private NameValue nameValue1;
        private NameValue nameValue2;
        private NameValue nameValue3;
        private String place;

        private PickerValue mPickerValue;
        private PickerItem mPickerItem; // 默认值
        private IPickerDialogOkCallBack okBtnListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setBtnOk(PickerValue pickerValue, String place, IPickerDialogOkCallBack
                listener) {
            this.mPickerValue = pickerValue;
            this.place = place;
            this.okBtnListener = listener;
            return this;
        }

        // 设置已选择的数据
        public void setPickedData(PickerItem pickerItem) {
            this.mPickerItem = pickerItem;
        }

        public PickerDialog create() {
            final PickerDialog pickerDialog = new PickerDialog(context, R.style.PickerDialog);

            switch (place) {
                case "bottom":
                    pickerDialog.getWindow().setGravity(Gravity.BOTTOM);
                    break;
                case "middle":
                    pickerDialog.getWindow().setGravity(Gravity.CENTER);
                    break;
            }
            View view = LayoutInflater.from(context).inflate(R.layout.common_picker_dialog, null);
            final PickerView pickerView = (PickerView) view.findViewById(R.id.pickerView);
            final TextView tvOk = (TextView) view.findViewById(R.id.tvPickerDlgOk);
            final TextView tvCancel = (TextView) view.findViewById(R.id.tvPickerDlgCancel);

            pickerView.setPickerData(mPickerItem, mPickerValue);
            pickerView.setOnSelectingListener(new PickerView.OnPickerSelectingListener() {

                @Override
                public void selected(boolean selected) {
                    nameValue1 = pickerView.getSelectedNameValue1();
                    nameValue2 = pickerView.getSelectedNameValue2();
                    nameValue3 = pickerView.getSelectedNameValue3();
                }
            });
            tvOk.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (okBtnListener != null) {
                        if (nameValue1 == null) { // 未滑动时为空
                            nameValue1 = pickerView.getSelectedNameValue1();
                            nameValue2 = pickerView.getSelectedNameValue2();
                            nameValue3 = pickerView.getSelectedNameValue3();
                        }
                        okBtnListener.handleBtnOk(nameValue1, nameValue2, nameValue3);
                    }
                    pickerDialog.dismiss();
                }
            });
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pickerDialog.dismiss();
                }
            });
            pickerDialog.setContentView(view);
            return pickerDialog;

        }
    }

    /***
     * 确定按钮回调方法
     **/
    public interface IPickerDialogOkCallBack {
        public abstract void handleBtnOk(NameValue nv1, NameValue nv2, NameValue nv3);
    }

    ;

}
