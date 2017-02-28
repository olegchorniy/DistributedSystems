package kpi.ipt.labs.distributed.twophasecommit.service.impl.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionConsumer<T> {

    T consume(Connection connection) throws SQLException;
}
