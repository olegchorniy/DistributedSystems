package kpi.ipt.labs.distributed.twophasecommit;

public final class BookingConnections {
    private BookingConnections() {
    }

    public static final ConnectionInfo flyConnectionInfo = new ConnectionInfo(
            "jdbc:postgresql://localhost:5432/fly_booking",
            "fly_admin",
            "fly_password"
    );

    public static final ConnectionInfo hotelConnectionInfo = new ConnectionInfo(
            "jdbc:postgresql://localhost:5432/hotel_booking",
            "hotel_admin",
            "hotel_password"
    );


    public static final ConnectionInfo mysqlHotelConnectionInfo = new ConnectionInfo("jdbc:mysql://localhost:3306/test?" +
            "useUnicode=true&" +
            "useJDBCCompliantTimezoneShift=true&" +
            "useLegacyDatetimeCode=false&" +
            "serverTimezone=UTC&" +
            "useSSL=false",
            "root", "password"
    );
}
