package com.bupt.service.datasource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DynamicDataSource extends AbstractRoutingDataSource implements DisposableBean {
    private static final Logger LOGGER = Logger.getLogger(DynamicDataSource.class);
    
    private static final String TEST_SQL = "select 1";
    private DataSourceRecoverHeartBeat recoverHeartBeat;
    private final Map<String, DataSource> failedMap = new ConcurrentHashMap<String, DataSource>();
    private ExecutorService exe;
    private LBStrategy<String> strategy;
    private static final ThreadLocal<String> SELECTED_KEY = new ThreadLocal<String>();
    
    private String validSql = TEST_SQL;
    private long validInterval = 1000L; // in milliseconds
    private boolean enableValid = true;
    
    public void setValidInterval(long validInterval) {
        this.validInterval = validInterval;
    }
    
    public void setValidSql(String validSql) {
        this.validSql = validSql;
    }
    
    public LBStrategy<String> getStrategy() {
        return strategy;
    }
    
    public void setStrategy(LBStrategy<String> strategy) {
        this.strategy = strategy;
    }
    
    @Override
    protected Object determineCurrentLookupKey() {
        String key = SELECTED_KEY.get();
        return key;
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        return getConnectionFromDataSource(null, null);
    }
    
    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return getConnectionFromDataSource(username, password);
    }
    
    private String electKey() {
        String key = strategy.elect(null);
        SELECTED_KEY.set(key);
        return key;
    }
    
    private void clearKey() {
        SELECTED_KEY.remove();
    }
    
    private Connection getConnectionFromDataSource(String username, String password)
            throws SQLException {
        
        String key = electKey();
        if (key == null) {
            throw new SQLException("no more datasource available.  elect key is null.");
        }
        DataSource ds;
        try {
            ds = determineTargetDataSource();
            
            Connection con = null;
            try {
                if (username == null && password == null) {
                    con = ds.getConnection();
                } else {
                    con = ds.getConnection(username, password);
                }
                validateConnection(con);
            } catch (SQLException e) {
                strategy.removeTarget(key);
                failedMap.put(key, ds);
                executeHeartBeat();
                return getConnectionFromDataSource(username, password);
            }
            return con;
            
        } finally {
            clearKey();
        }
    }
    
    private void validateConnection(Connection con) throws SQLException {
        if (!enableValid) {
            return;
        }
        PreparedStatement stmt = con.prepareStatement(validSql);
        stmt.execute();
        stmt.close();
    }
    
    private synchronized void executeHeartBeat() {
        if (recoverHeartBeat == null) {
            recoverHeartBeat = new DataSourceRecoverHeartBeat(this);
            if (exe == null) {
                exe = Executors.newFixedThreadPool(1);
            }
            exe.execute(recoverHeartBeat);
        } else {
            if (!recoverHeartBeat.isRuning()) {
                if (exe == null) {
                    exe = Executors.newFixedThreadPool(1);
                }
                exe.execute(recoverHeartBeat);
            }
        }
    }
    
    private synchronized void shutdownHeartBean() {
        if (recoverHeartBeat != null) {
            recoverHeartBeat.close();
        }
        if (exe != null) {
            exe.shutdown();
        }
    }
    
    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Property 'enableValid' value is " + enableValid);
        }
    }
    
    private static class DataSourceRecoverHeartBeat implements Runnable {
        private static final Logger LOGGER = Logger.getLogger(DataSourceRecoverHeartBeat.class);
        
        private DynamicDataSource dynamicDataSource;
        private boolean runing;
        
        private boolean close = false;
        
        public boolean isRuning() {
            return runing;
        }
        
        public void close() {
            close = true;
        }
        
        public DataSourceRecoverHeartBeat(DynamicDataSource dynamicDataSource) {
            this.dynamicDataSource = dynamicDataSource;
        }
        
        public void run() {
            runing = true;
            while (!dynamicDataSource.failedMap.isEmpty() && !close) {
                Map<String, DataSource> dataSourceMapCopy;
                dataSourceMapCopy = new HashMap<String, DataSource>(dynamicDataSource.failedMap);
                
                Iterator<Entry<String, DataSource>> iter;
                iter = dataSourceMapCopy.entrySet().iterator();
                while (iter.hasNext()) {
                    Entry<String, DataSource> next = iter.next();
                    DataSource ds = next.getValue();
                    Connection con = null;
                    String key = next.getKey();
                    try {
                        con = ds.getConnection();
                        dynamicDataSource.validateConnection(con);
                        LOGGER.info("Datasource key='" + key + "' valid ok.");
                        dynamicDataSource.strategy.recoverTarget(key);
                        dynamicDataSource.failedMap.remove(key);
                    } catch (SQLException e) {
                        LOGGER.warn("Datasource key='" + key + "' valid failed. due to:"
                                + e.getMessage());
                    } finally {
                        if (con != null) {
                            try {
                                con.close();
                            } catch (SQLException e) {
                                if (LOGGER.isDebugEnabled()) {
                                    LOGGER.debug(e.getMessage(), e);
                                }
                            }
                        }
                    }
                    
                }
                
                try {
                    Thread.sleep(dynamicDataSource.validInterval);
                } catch (Exception e) {
                    LOGGER.error("{}", e);
                }
            }
            runing = false;
        }
    }
    
    public void destroy() throws Exception {
        shutdownHeartBean();
    }
}
