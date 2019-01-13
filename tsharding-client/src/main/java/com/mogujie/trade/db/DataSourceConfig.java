package com.mogujie.trade.db;

/**
 * @author by jiuru on 16/7/14.
 */
public class DataSourceConfig {

    static final int DEFAULT_MIN_POOL_SIZE = 0;
    static final int DEFAULT_MAX_POOL_SIZE = 10;
    static final int DEFAULT_INI_POOL_SIZE = 0;

    private String url;

    private String username;

    private String password;

    private int minPoolSize;

    private int maxPoolSize;

    private int initialPoolSize;
    /**
     * 新增驱动类
     */
    private String driver;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getMinPoolSize() {
        return minPoolSize;
    }

    public void setMinPoolSize(int minPoolSize) {
        this.minPoolSize = minPoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public int getInitialPoolSize() {
        return initialPoolSize;
    }

    public void setInitialPoolSize(int initialPoolSize) {
        this.initialPoolSize = initialPoolSize;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

}