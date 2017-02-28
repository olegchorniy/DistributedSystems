package kpi.ipt.labs.distributed.twophasecommit.service.impl.jdbc;

import kpi.ipt.labs.distributed.twophasecommit.transaction.TransactionResource;

import java.sql.Connection;
import java.sql.SQLException;

public interface TransactionResourceMapper<T extends TransactionResource> {

    T getResource(Connection connection) throws SQLException;
}
