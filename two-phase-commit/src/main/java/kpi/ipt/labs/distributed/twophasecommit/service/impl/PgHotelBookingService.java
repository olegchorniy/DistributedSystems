package kpi.ipt.labs.distributed.twophasecommit.service.impl;

import kpi.ipt.labs.distributed.twophasecommit.ConnectionInfo;
import kpi.ipt.labs.distributed.twophasecommit.domain.HotelBooking;
import kpi.ipt.labs.distributed.twophasecommit.service.HotelBookingService;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PgHotelBookingService extends AbstractPgBookingService<HotelBooking> implements HotelBookingService {

    public static final String INSERT_HOTEL = "INSERT " +
            "INTO bookings (client_name, hotel_name, arrival, departure)" +
            "VALUES (?, ?, ?, ?)";

    public PgHotelBookingService(ConnectionInfo connInfo) {
        super(connInfo);
    }

    @Override
    public int bookHotel(HotelBooking hotelBooking) {
        try {
            return doBooking(hotelBooking);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected String getInsertQuery() {
        return INSERT_HOTEL;
    }

    @Override
    protected String getIdColumnName() {
        return "id";
    }

    @Override
    protected void setParameters(PreparedStatement insertStmt, HotelBooking hotelBooking) throws SQLException {
        insertStmt.setString(1, hotelBooking.getClientName());
        insertStmt.setString(2, hotelBooking.getHotelName());
        insertStmt.setDate(3, Utils.toSqlDate(hotelBooking.getArrival()));
        insertStmt.setDate(4, Utils.toSqlDate(hotelBooking.getDeparture()));
    }
}
