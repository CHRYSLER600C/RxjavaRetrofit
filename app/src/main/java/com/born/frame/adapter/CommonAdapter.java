package com.born.frame.adapter;

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
    private Class mClazz;
    private LayoutInflater mInflater;
    private IItemCallback mHandler = null;

    /**
     * 初始化通用 Adapter
     *
     * @param context  上下文
     * @param list     自定义类型的数据列表
     * @param layoutId 布局文件的 ID
     * @param clazz    布局文件对应的ViewHolder
     * @param handle   回调函数处理getView的每一项数据
     */
    public CommonAdapter(Context context, List list, int layoutId, Class clazz, IItemCallback handle) {
        this.mList = list;
        this.mLayoutId = layoutId;
        this.mClazz = clazz;
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
        Object holder = null;
        if (null == convertView) {
            convertView = mInflater.inflate(mLayoutId, null);
            try {
                holder = mClazz.getConstructor(View.class).newInstance(convertView);
            } catch (Exception e) {
                e.printStackTrace();
            }
            convertView.setTag(holder);
        } else {
            holder = convertView.getTag();
        }
        mHandler.handleItem(position, convertView, parent, holder, mList);
        return convertView;
    }

    // 回调接口
    public interface IItemCallback {
        /**
         * 处理每一个item
         *
         * @param position    位置
         * @param convertView 视图项
         * @param parent      父窗口
         * @param holder      ViewHolder
         * @param list        回调对应position的数据项
         */
        void handleItem(int position, View convertView, ViewGroup parent, Object holder, List list);
    }
}
