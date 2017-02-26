package kpi.ipt.labs.distributed.twophasecommit.db;

import kpi.ipt.labs.distributed.twophasecommit.domain.FlyBooking;
import kpi.ipt.labs.distributed.twophasecommit.domain.HotelBooking;

import java.sql.*;
import java.sql.Date;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.Map.Entry;

public class BookingTransactionManager {

    private static final String INSERT_FLY_QUERY = "INSERT " +
            "INTO bookings (client_name, fly_number, \"from\", \"to\", date)" +
            "VALUES (?, ?, ?, ?, ?)";

    private static final String INSERT_HOTEL_QUERY = "INSERT " +
            "INTO bookings (client_name, hotel_name, arrival, departure)" +
            "VALUES (?, ?, ?, ?)";

    private final ConnectionInfo flyConnInfo;
    private final ConnectionInfo hotelConnInfo;

    public BookingTransactionManager(String driverName, ConnectionInfo flyConnInfo, ConnectionInfo hotelConnInfo) {
        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        this.flyConnInfo = flyConnInfo;
        this.hotelConnInfo = hotelConnInfo;
    }

    public void bookFlyAndHotel(FlyBooking flyBooking, HotelBooking hotelBooking) {
        try {
            Connection flyConn = getConnection(flyConnInfo);
            Connection hotelConn = getConnection(hotelConnInfo);

            //TODO: rewrite this shit. Support smart rollback (depending on the state of transaction - prepared/not prepared).
            insertFlyBooking(flyConn, flyBooking);
            insertHotelBooking(hotelConn, hotelBooking);

            Queue<Connection> preparationQueue = new LinkedList<>();
            preparationQueue.add(flyConn);
            preparationQueue.add(hotelConn);

            List<Entry<Connection, String>> prepared = new ArrayList<>();
            boolean rollback = false;

            SQLException reThrow = null;
            while (!preparationQueue.isEmpty()) {
                Connection connection = preparationQueue.poll();

                try {
                    prepared.add(new SimpleEntry<>(connection, prepareTransaction(connection)));
                } catch (SQLException e) {
                    //return to unprocessed
                    preparationQueue.add(connection);

                    rollback = true;
                    reThrow = e;
                    break;
                }
            }

            if (rollback) {
                //1. simple rollback for connections from the queue
                //2. ROLLBACK prepared for connections from the processed list
            } else {

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void insertFlyBooking(Connection connection, FlyBooking flyBooking) throws SQLException {
        try (PreparedStatement insertStmt = connection.prepareStatement(INSERT_FLY_QUERY)) {

            insertStmt.setString(1, flyBooking.getClientName());
            insertStmt.setString(2, flyBooking.getFlyNumber());
            insertStmt.setString(3, flyBooking.getFrom());
            insertStmt.setString(4, flyBooking.getTo());
            insertStmt.setDate(5, toSqlDate(flyBooking.getDate()));

            insertStmt.executeUpdate();
        }
    }

    private void insertHotelBooking(Connection connection, HotelBooking hotelBooking) throws SQLException {
        try (PreparedStatement insertStmt = connection.prepareStatement(INSERT_HOTEL_QUERY)) {

            insertStmt.setString(1, hotelBooking.getClientName());
            insertStmt.setString(2, hotelBooking.getHotelName());
            insertStmt.setDate(3, toSqlDate(hotelBooking.getArrival()));
            insertStmt.setDate(4, toSqlDate(hotelBooking.getDeparture()));

            insertStmt.executeUpdate();
        }
    }

    private static String prepareTransaction(Connection connection) throws SQLException {
        String transactionId = generateTransactionId();

        try (Statement prepareTransactionStmt = connection.createStatement()) {
            prepareTransactionStmt.execute("PREPARE TRANSACTION '" + transactionId + "'");
        }

        return transactionId;
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

    private static Connection getConnection(ConnectionInfo connInfo) throws SQLException {
        Connection conn = DriverManager.getConnection(
                connInfo.getUrl(),
                connInfo.getUsername(),
                connInfo.getPassword()
        );
        conn.setAutoCommit(false);

        return conn;
    }

    private static Date toSqlDate(java.util.Date date) {
        if (date == null) {
            return null;
        }

        return new Date(date.getTime());
    }

    private static String generateTransactionId() {
        return UUID.randomUUID().toString();
    }
}
