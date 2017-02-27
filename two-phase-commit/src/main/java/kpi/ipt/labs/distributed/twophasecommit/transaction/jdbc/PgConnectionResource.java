package kpi.ipt.labs.distributed.twophasecommit.transaction.jdbc;

import kpi.ipt.labs.distributed.twophasecommit.transaction.TransactionalResource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class PgConnectionResource implements TransactionalResource {

    private final Connection connection;
    private String transactionId;

    public PgConnectionResource(Connection connection) throws SQLException {
        this.connection = connection;
        this.connection.setAutoCommit(false);
    }

    @Override
    public void prepare(String transactionId) {
        try {
            prepareTransaction(this.connection, Objects.requireNonNull(transactionId));

            //store into local field only after successful preparation
            this.transactionId = transactionId;
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
            if (this.transactionId == null) {
                this.connection.rollback();
            } else {
                rollbackPrepared(this.connection, this.transactionId);
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
