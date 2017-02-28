package kpi.ipt.labs.distributed.twophasecommit.transaction.jdbc;

import kpi.ipt.labs.distributed.twophasecommit.transaction.TransactionResource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class MysqlConnectionResource implements TransactionResource {

    private final Connection connection;
    private String transactionId;

    public MysqlConnectionResource(Connection connection) throws SQLException {
        this.connection = connection;
        this.connection.setAutoCommit(false);
    }

    @Override
    public void begin(String transactionId) {
        this.transactionId = Objects.requireNonNull(transactionId);

        try {
            executeQuery(this.connection, "XA START '" + this.transactionId + "'");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void end() {
        try {
            executeQuery(this.connection, "XA END '" + this.transactionId + "'");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void prepare() {
        try {
            executeQuery(this.connection, "XA PREPARE '" + this.transactionId + "'");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void commit() {
        try {
            executeQuery(this.connection, "XA COMMIT '" + this.transactionId + "'");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void rollback() {
        try {
            executeQuery(this.connection, "XA ROLLBACK '" + this.transactionId + "'");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        this.connection.close();
    }

    private static void executeQuery(Connection connection, String query) throws SQLException {
        try (Statement prepareTransactionStmt = connection.createStatement()) {
            prepareTransactionStmt.execute(query);
        }
    }
}
