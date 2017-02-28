package kpi.ipt.labs.distributed.twophasecommit.service.impl.jdbc.postgres;

import kpi.ipt.labs.distributed.twophasecommit.ConnectionInfo;
import kpi.ipt.labs.distributed.twophasecommit.service.impl.jdbc.TransactionalJdbcOperations;
import kpi.ipt.labs.distributed.twophasecommit.service.impl.jdbc.Utils;
import kpi.ipt.labs.distributed.twophasecommit.transaction.jdbc.PgConnectionResource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

abstract class AbstractPgBookingService<T> extends TransactionalJdbcOperations<PgConnectionResource> {

    AbstractPgBookingService(ConnectionInfo connInfo) {
        super(connInfo, PgConnectionResource::new);
    }

    //TODO: checkup all exceptions
    int doBooking(T bookingEntity) throws SQLException {
        return withTransactionalConnection(connection -> bookWithinTransaction(connection, bookingEntity));
    }

    private int bookWithinTransaction(Connection connection, T bookingEntity) throws SQLException {
        try (PreparedStatement statement = Utils.withAutoGeneratedKeys(connection, getInsertQuery())) {
            setParameters(statement, bookingEntity);

            statement.executeUpdate();

            return Utils.extractGeneratedId(getIdColumnName(), statement);
        }
    }

    protected abstract String getInsertQuery();

    protected abstract String getIdColumnName();

    protected abstract void setParameters(PreparedStatement insertStmt, T bookingEntity) throws SQLException;
}