package kpi.ipt.labs.distributed.twophasecommit.service.impl.jdbc.mysql;

import kpi.ipt.labs.distributed.twophasecommit.ConnectionInfo;
import kpi.ipt.labs.distributed.twophasecommit.domain.HotelBooking;
import kpi.ipt.labs.distributed.twophasecommit.service.HotelBookingService;
import kpi.ipt.labs.distributed.twophasecommit.service.impl.jdbc.TransactionalJdbcOperations;
import kpi.ipt.labs.distributed.twophasecommit.service.impl.jdbc.Utils;
import kpi.ipt.labs.distributed.twophasecommit.transaction.jdbc.MysqlConnectionResource;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MysqlHotelBookingService
        extends TransactionalJdbcOperations<MysqlConnectionResource>
        implements HotelBookingService {

    public MysqlHotelBookingService(ConnectionInfo connectionInfo) {
        super(connectionInfo, MysqlConnectionResource::new);
    }

    @Override
    public int bookHotel(HotelBooking hotelBooking) {
        try {
            return withTransactionalConnection(connection -> {
                try (PreparedStatement statement = Utils.withAutoGeneratedKeys(connection, "INSERT " +
                        "INTO hotel_bookings (client_name, hotel_name, arrival, departure)" +
                        "VALUES (?, ?, ?, ?)")) {
                    statement.setString(1, hotelBooking.getClientName());
                    statement.setString(2, hotelBooking.getHotelName());
                    statement.setDate(3, Utils.toSqlDate(hotelBooking.getArrival()));
                    statement.setDate(4, Utils.toSqlDate(hotelBooking.getDeparture()));

                    statement.executeUpdate();

                    return Utils.extractGeneratedId(1, statement);
                }
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}