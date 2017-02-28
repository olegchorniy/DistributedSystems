package kpi.ipt.labs.distributed.twophasecommit;

import kpi.ipt.labs.distributed.twophasecommit.service.impl.jdbc.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class MySqlTest {

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void main(String[] args) throws SQLException {
        ConnectionInfo connInf = new ConnectionInfo("jdbc:mysql://localhost:3306/test?" +
                "useUnicode=true&" +
                "useJDBCCompliantTimezoneShift=true&" +
                "useLegacyDatetimeCode=false&" +
                "serverTimezone=UTC&" +
                "useSSL=false",
                "root", "password"
        );

        Connection connection = Utils.getConnection(connInf);
        connection.setAutoCommit(false);

        issueCommand(connection, "XA START 'xatest'");

        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO key_value(value) VALUES (?)")) {
            statement.setString(1, "value_1");
            statement.executeUpdate();
        }

        issueCommand(connection, "XA END 'xatest'");
        issueCommand(connection, "XA PREPARE 'xatest'");
        issueCommand(connection, "XA COMMIT 'xatest'");
        issueCommand(connection, "XA ROLLBACK 'xatest'");

        connection.close();
    }

    private static void issueCommand(Connection connection, String command) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(command);
        }
    }
}
