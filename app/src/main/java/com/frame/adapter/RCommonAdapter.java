package com.frame.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by min on 2017/1/13.
 */

@SuppressWarnings(value = { "rawtypes", "unchecked" })
public class RCommonAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {

	private Object mObject = null; // 调用者实例对象
	private List mList = null;
	private int mLayoutId;
	private Class mClazz;
	private IItemCallBack mHandler = null;

	/**
	 * 初始化通用 Adapter
	 *
	 * @param object
	 *            activity对象
	 * @param list
	 *            自定义类型的数据列表
	 * @param layoutId
	 *            布局文件的 ID
	 * @param clazz
	 *            布局文件对应的ViewHolder
	 * @param handle
	 *            回调函数处理每一项数据
	 */
	public RCommonAdapter(Object object, List list, int layoutId, Class clazz, IItemCallBack handle) {
		this.mObject = object;
		this.mList = list;
		this.mLayoutId = layoutId;
		this.mClazz = clazz;
		this.mHandler = handle;
	}

	@Override
	public T onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
		Object holder = null;
		try {
			holder = mClazz.getConstructor(View.class).newInstance(v);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (T) holder;
	}

	@Override
	public void onBindViewHolder(T holder, int position) {
		mHandler.handleItem(mObject, holder, mList, position);
	}

	@Override
	public int getItemCount() {
		return mList.size();
	}

	public void addItem(int position, Object item) {
		mList.add(position, item);
		notifyItemInserted(position);
	}

	public void removeItem(int position) {
		mList.remove(position);
		notifyItemRemoved(position);
	}

	// 回调接口
	public interface IItemCallBack {
		/**
		 * 处理每一个item
		 *
		 * @param object
		 *            调用者实例对象
		 * @param holder
		 *            ViewHolder
		 * @param position
		 *            位置
		 */
		void handleItem(Object object, Object holder, List list, int position);
	}
}
