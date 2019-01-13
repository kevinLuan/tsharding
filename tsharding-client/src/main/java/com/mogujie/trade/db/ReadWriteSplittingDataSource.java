package com.mogujie.trade.db;

import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.util.Assert;

import javax.sql.DataSource;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author by jiuru on 16/7/14.
 */
public class ReadWriteSplittingDataSource extends AbstractDataSource implements DataSource, Closeable {

    private final String name;

    private final DataSource masterDataSource;

    private final DataSource slaveDataSource;

    public ReadWriteSplittingDataSource(String name, DataSource masterDataSource, DataSource slaveDataSource) {
        this.name = name;
        this.masterDataSource = masterDataSource;
        this.slaveDataSource = slaveDataSource;
        Assert.isTrue(masterDataSource != slaveDataSource || masterDataSource != null,
                "masterDataSource and slaveDataSource can't be both null!");
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.determineTargetDataSource().getConnection();
    }

    public DataSource getSlaveDataSource() {
        return slaveDataSource;
    }

    private DataSource determineTargetDataSource() {
        if (ReadWriteSplittingContext.isMaster() || this.isInTransaction()) {
            if (masterDataSource == null) {
                throw new NotFoundDataSource("Not found master datasource:`" + name + "`");
            } else {
                return masterDataSource;
            }
        } else {
            if (slaveDataSource == null) {
                throw new NotFoundDataSource("Not found slave datasource:`" + name + "`");
            } else {
                return slaveDataSource;
            }
        }
    }

    private boolean isInTransaction() {
        return RoutingDataSourceTransactionContext.getCurTransactionDataSource() != null;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return this.determineTargetDataSource().getConnection(username, password);
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ReadWriteSplittingDataSource [");
        if (name != null) {
            builder.append("name=").append(name).append(", ");
        }
        if (masterDataSource != null) {
            builder.append("masterDataSource=").append(masterDataSource).append(", ");
        }
        if (slaveDataSource != null) {
            builder.append("slaveDataSource=").append(slaveDataSource);
        }
        builder.append("]");
        return builder.toString();
    }

    @Override
    public void close() throws IOException {
        if (this.masterDataSource != null) {
            if (masterDataSource instanceof AutoCloseable) {
                try {
                    ((AutoCloseable) masterDataSource).close();
                } catch (Exception ignore) {
                }
            }
        }

        if (this.slaveDataSource != null) {
            if (slaveDataSource instanceof AutoCloseable) {
                try {
                    ((AutoCloseable) slaveDataSource).close();
                } catch (Exception ignore) {
                }
            }
        }
    }

    public static class NotFoundDataSource extends RuntimeException {
        private static final long serialVersionUID = -4911020211387862197L;

        public NotFoundDataSource(String msg) {
            super(msg);
        }
    }
}
