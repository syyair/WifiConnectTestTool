package greendao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import greendao.WifiLog;

import greendao.WifiLogDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig wifiLogDaoConfig;

    private final WifiLogDao wifiLogDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        wifiLogDaoConfig = daoConfigMap.get(WifiLogDao.class).clone();
        wifiLogDaoConfig.initIdentityScope(type);

        wifiLogDao = new WifiLogDao(wifiLogDaoConfig, this);

        registerDao(WifiLog.class, wifiLogDao);
    }
    
    public void clear() {
        wifiLogDaoConfig.getIdentityScope().clear();
    }

    public WifiLogDao getWifiLogDao() {
        return wifiLogDao;
    }

}
