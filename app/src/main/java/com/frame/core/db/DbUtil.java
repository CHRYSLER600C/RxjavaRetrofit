package com.frame.core.db;

import com.blankj.utilcode.util.ObjectUtils;
import com.frame.core.dao.HistoryData;
import com.frame.core.dao.HistoryDataDao;

import java.util.Iterator;
import java.util.List;

public class DbUtil {

    public static <T> List<T> reverseList(List<T> list) {
        if (ObjectUtils.isEmpty(list) || list.size() < 2) return list;
        int frontPoint = 0;
        int lastPoint = list.size() - 1;
        while (frontPoint < lastPoint) {
            T temp = list.get(frontPoint);
            list.set(frontPoint, list.get(lastPoint));
            list.set(lastPoint, temp);
            frontPoint++;
            lastPoint--;
        }
        return list;
    }

    /**
     * 历史数据后移
     *
     * @return 返回true表示查询的数据已存在，只需将其置后，否则需要增加新的历史记录
     */
    public static boolean ItemBackward(HistoryDataDao dao, List<HistoryData> list, HistoryData data) {
        Iterator<HistoryData> iterator = list.iterator();
        //不要在foreach循环中进行元素的remove、add操作，使用Iterator模式
        while (iterator.hasNext()) {
            HistoryData item = iterator.next();
            if (item.getData().equals(data.getData())) {
                dao.deleteByKey(item.getId());
                dao.insert(data);
                return true;
            }
        }
        return false;
    }

    public static HistoryData createHistoryData(String data) {
        HistoryData historyData = new HistoryData();
        historyData.setDate(System.currentTimeMillis());
        historyData.setData(data);
        return historyData;
    }

}
