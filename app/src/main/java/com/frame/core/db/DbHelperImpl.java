package com.frame.core.db;

import com.frame.application.AndApp;
import com.frame.core.dao.DaoSession;
import com.frame.core.dao.HistoryData;
import com.frame.core.dao.HistoryDataDao;

import java.util.Iterator;
import java.util.List;


/**
 * 对外隐藏操作数据库的实现细节
 *
 */
public class DbHelperImpl implements IDbHelper {

    private static final int HISTORY_LIST_SIZE = 20;

    private DaoSession daoSession;

    public DbHelperImpl() {
        daoSession = AndApp.getInstance().getDaoSession();
    }

    private HistoryDataDao getHistoryDataDao() {
        return daoSession.getHistoryDataDao();
    }

    @Override
    public List<HistoryData> addHistoryData(String data) {
        List<HistoryData> historyList = getHistoryDataDao().loadAll();
        HistoryData historyData = createHistoryData(data);
        if (historyDataForward(historyList, historyData)) {
            return historyList;
        }

        if (historyList.size() < HISTORY_LIST_SIZE) {
            getHistoryDataDao().insert(historyData);
            historyList.add(historyData);
        } else {
            historyList.remove(0);
            historyList.add(historyData);
            getHistoryDataDao().deleteAll();
            getHistoryDataDao().insertInTx(historyList);
        }
        return historyList;
    }

    @Override
    public void clearHistoryData() {
        daoSession.getHistoryDataDao().deleteAll();
    }

    @Override
    public List<HistoryData> loadAllHistoryData() {
        return daoSession.getHistoryDataDao().loadAll();
    }

    /**
     * 历史数据前移
     *
     * @return 返回true表示查询的数据已存在，只需将其前移到第一项历史记录，否则需要增加新的历史记录
     */
    private boolean historyDataForward(List<HistoryData> historyList, HistoryData historyData) {
        //重复搜索时进行历史记录前移
        Iterator<HistoryData> iterator = historyList.iterator();
        //不要在foreach循环中进行元素的remove、add操作，使用Iterator模式
        while (iterator.hasNext()) {
            HistoryData item = iterator.next();
            if (item.getData().equals(historyData.getData())) {
                historyList.remove(item);
                historyList.add(historyData);
                getHistoryDataDao().deleteAll();
                getHistoryDataDao().insertInTx(historyList);
                return true;
            }
        }
        return false;
    }

    private HistoryData createHistoryData(String data) {
        HistoryData historyData = new HistoryData();
        historyData.setDate(System.currentTimeMillis());
        historyData.setData(data);
        return historyData;
    }

}
