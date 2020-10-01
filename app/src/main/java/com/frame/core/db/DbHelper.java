package com.frame.core.db;

import com.frame.application.App;
import com.frame.core.dao.DaoSession;
import com.frame.core.dao.HistoryData;
import com.frame.core.dao.HistoryDataDao;

import java.util.List;


/**
 * 对外隐藏操作数据库的实现细节
 */
public class DbHelper implements IDbHelper {
    private static final int HISTORY_LIST_SIZE = 20;

    private DaoSession daoSession;

    private DbHelper() {
        daoSession = App.getInstance().getDaoSession();
    }

    private static class SingletonHolder {
        private static final DbHelper INSTANCE = new DbHelper();
    }

    public static DbHelper getInstance() {
        return SingletonHolder.INSTANCE;
    }

    // get_dao
    private HistoryDataDao getHistoryDataDao() {
        return daoSession.getHistoryDataDao();
    }

    @Override
    public List<HistoryData> addHistoryData(String data) {
        List<HistoryData> historyList = getHistoryDataDao().loadAll();
        HistoryData historyData = DbUtil.createHistoryData(data);
        if (DbUtil.ItemBackward(getHistoryDataDao(), historyList, historyData)) {
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
}
