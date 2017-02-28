package kpi.ipt.labs.distributed.twophasecommit.service.impl.jdbc.postgres;

import kpi.ipt.labs.distributed.twophasecommit.ConnectionInfo;
import kpi.ipt.labs.distributed.twophasecommit.domain.FlyBooking;
import kpi.ipt.labs.distributed.twophasecommit.service.FlyBookingService;
import kpi.ipt.labs.distributed.twophasecommit.service.impl.jdbc.Utils;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PgFlyBookingService extends AbstractPgBookingService<FlyBooking> implements FlyBookingService {

    public static final String INSERT_FLY = "INSERT " +
            "INTO bookings (client_name, fly_number, \"from\", \"to\", date)" +
            "VALUES (?, ?, ?, ?, ?)";

    public PgFlyBookingService(ConnectionInfo connInfo) {
        super(connInfo);
    }

    @Override
    public int bookFlight(FlyBooking flyBooking) {
        try {
            return super.doBooking(flyBooking);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected String getInsertQuery() {
        return INSERT_FLY;
    }

    @Override
    protected String getIdColumnName() {
        return "id";
    }

    @Override
    protected void setParameters(PreparedStatement insertStmt, FlyBooking flyBooking) throws SQLException {
        insertStmt.setString(1, flyBooking.getClientName());
        insertStmt.setString(2, flyBooking.getFlyNumber());
        insertStmt.setString(3, flyBooking.getFrom());
        insertStmt.setString(4, flyBooking.getTo());
        insertStmt.setDate(5, Utils.toSqlDate(flyBooking.getDate()));
    }
}
