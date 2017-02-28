package kpi.ipt.labs.distributed.twophasecommit.transaction.jdbc;

import kpi.ipt.labs.distributed.twophasecommit.transaction.TransactionResource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class PgConnectionResource implements TransactionResource {

    private final Connection connection;
    private String transactionId;
    private boolean prepared = false;

    public PgConnectionResource(Connection connection) throws SQLException {
        this.connection = connection;
        this.connection.setAutoCommit(false);
    }

    @Override
    public void begin(String transactionId) {
        this.transactionId = Objects.requireNonNull(transactionId);
    }

    @Override
    public void end() {
        //do nothing
    }

    @Override
    public void prepare() {
        try {
            prepareTransaction(this.connection, this.transactionId);

            this.prepared = true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void commit() {
        try {
            Objects.requireNonNull(this.transactionId);

            commitPrepared(this.connection, this.transactionId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void rollback() {
        try {
            if (prepared) {
                rollbackPrepared(this.connection, this.transactionId);
            } else {
                this.connection.rollback();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        this.connection.close();
    }

    private static void prepareTransaction(Connection connection, String transactionId) throws SQLException {
        try (Statement prepareTransactionStmt = connection.createStatement()) {
            prepareTransactionStmt.execute("PREPARE TRANSACTION '" + transactionId + "'");
        }
    }

    private static void commitPrepared(Connection connection, String transactionId) throws SQLException {
        connection.setAutoCommit(true);

        try (Statement prepareTransactionStmt = connection.createStatement()) {
            prepareTransactionStmt.execute("COMMIT PREPARED '" + transactionId + "'");
        }
    }

    private static void rollbackPrepared(Connection connection, String transactionId) throws SQLException {
        connection.setAutoCommit(true);

        try (Statement prepareTransactionStmt = connection.createStatement()) {
            prepareTransactionStmt.execute("ROLLBACK PREPARED '" + transactionId + "'");
        }
    }
}
