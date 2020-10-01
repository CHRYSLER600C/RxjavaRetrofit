package com.frame.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

@SuppressWarnings("rawtypes")
public class CommonAdapter extends BaseAdapter {

    private List mList = null;
    private int mLayoutId;
    private LayoutInflater mInflater;
    private IItemCallback mHandler = null;

    /**
     * 初始化通用 Adapter
     *
     * @param context  上下文
     * @param list     自定义类型的数据列表
     * @param layoutId 布局文件的 ID
     * @param handle   回调函数处理getView的每一项数据
     */
    public CommonAdapter(Context context, List list, int layoutId, IItemCallback handle) {
        this.mList = list;
        this.mLayoutId = layoutId;
        this.mHandler = handle;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List getList() {
        return mList;
    }

    public void removeItem(int position) {
        mList.remove(position);
        notifyDataSetChanged();
    }

    public void removeItemAll() {
        mList.clear();
        notifyDataSetChanged();
    }

    @SuppressWarnings("unchecked")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        Object holder = null;
//        if (null == convertView) {
//            convertView = mInflater.inflate(mLayoutId, null);
//            try {
//                holder = mClazz.getConstructor(View.class).newInstance(convertView);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            convertView.setTag(holder);
//        } else {
//            holder = convertView.getTag();
//        }
        CommVHolder commVHolder;
        if (convertView == null) {
            commVHolder = CommVHolder.get(null, mInflater.inflate(mLayoutId, null));
        } else { // When convertView != null, parent must be an AbsListView.
            commVHolder =  CommVHolder.get(convertView, null);
        }
        mHandler.handleItem(position, parent, commVHolder, mList);
        return commVHolder.itemView;
    }

    // 回调接口
    public interface IItemCallback {
        /**
         * 处理每一个item
         *
         * @param position    位置
         * @param parent      父窗口
         * @param h           CommVHolder
         * @param list        回调对应position的数据项
         */
        void handleItem(int position, ViewGroup parent, CommVHolder h, List list);
    }
}
