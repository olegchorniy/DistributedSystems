package kpi.ipt.labs.distributed.twophasecommit.service.impl.jdbc;

import kpi.ipt.labs.distributed.twophasecommit.ConnectionInfo;
import kpi.ipt.labs.distributed.twophasecommit.transaction.TransactionHolder;
import kpi.ipt.labs.distributed.twophasecommit.transaction.TransactionResource;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class TransactionalJdbcOperations<T extends TransactionResource> {

    private final ConnectionInfo connectionInfo;
    private final TransactionResourceMapper<T> resourceMapper;

    public TransactionalJdbcOperations(ConnectionInfo connectionInfo, TransactionResourceMapper<T> resourceMapper) {
        this.connectionInfo = connectionInfo;
        this.resourceMapper = resourceMapper;
    }

    protected <U> U withTransactionalConnection(ConnectionConsumer<U> consumer) throws SQLException {
        Connection connection = Utils.getConnection(connectionInfo);
        TransactionResource resource = resourceMapper.getResource(connection);

        TransactionHolder.currentTransaction().enlistResource(resource);

        try {
            return consumer.consume(connection);
        } finally {
            TransactionHolder.currentTransaction().delistResource(resource);
        }
    }
}
