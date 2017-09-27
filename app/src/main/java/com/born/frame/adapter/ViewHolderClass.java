package com.born.frame.adapter;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.born.frame.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ViewHolderClass {

    // ================================ ItemListAdapter ViewHolders ================================
    public static class LineViewHolder {
        @Bind(R.id.tvItemLine)
        public TextView tvItemLine;

        public LineViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public static class TextEditTextViewHolder {
        @Bind(R.id.tvItemNameTet)
        public TextView tvItemNameTet;
        @Bind(R.id.etItemContentTet)
        public EditText etItemContentTet;
        @Bind(R.id.tvItemUnitTet)
        public TextView tvItemUnitTet;

        public TextEditTextViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public static class SelectViewHolder {
        @Bind(R.id.tvSelectName)
        public TextView tvSelectName;

        @Bind(R.id.llSelectContent)
        public LinearLayout llSelectContent;
        @Bind(R.id.tvSelectContent1)
        public TextView tvSelectContent1;
        @Bind(R.id.tvSelectContent2)
        public TextView tvSelectContent2;
        @Bind(R.id.tvSelectContent3)
        public TextView tvSelectContent3;
        @Bind(R.id.tvSelectUnit)
        public TextView tvSelectUnit;

        public SelectViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public static class SelectTimeViewHolder {
        @Bind(R.id.tvSelectTimeName)
        public TextView tvSelectTimeName;
        @Bind(R.id.tvSelectTimeContent)
        public TextView tvSelectTimeContent;

        public SelectTimeViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public static class TextTextViewHolder {
        @Bind(R.id.tvItemNameTt)
        public TextView tvItemNameTt;
        @Bind(R.id.tvItemContentTt)
        public TextView tvItemContentTt;

        public TextTextViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public static class ButtonViewHolder {
        @Bind(R.id.btnCommon)
        public Button btnCommon;

        public ButtonViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public static class TextEditButtonViewHolder {
        @Bind(R.id.tvItemNameTeb)
        public TextView tvItemNameTeb;
        @Bind(R.id.etItemContentTeb)
        public EditText etItemContentTeb;
        @Bind(R.id.btnItemTeb)
        public TextView btnItemTeb;

        public TextEditButtonViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    // ================================ ItemListAdapter ViewHolders ================================

}
