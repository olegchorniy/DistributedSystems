package kpi.ipt.labs.distributed.twophasecommit;

import kpi.ipt.labs.distributed.twophasecommit.db.BookingTransactionManager;
import kpi.ipt.labs.distributed.twophasecommit.db.ConnectionInfo;
import kpi.ipt.labs.distributed.twophasecommit.domain.FlyBooking;
import kpi.ipt.labs.distributed.twophasecommit.domain.HotelBooking;

import java.util.Date;

public class Main {

    public static void main(String[] args) {
        ConnectionInfo flyConnInfo = new ConnectionInfo(
                "jdbc:postgresql://localhost:5432/fly_booking",
                "fly_admin",
                "fly_password"
        );
        ConnectionInfo hotelConnInfo = new ConnectionInfo(
                "jdbc:postgresql://localhost:5432/hotel_booking",
                "hotel_admin",
                "hotel_password"
        );

        BookingTransactionManager manager = new BookingTransactionManager("org.postgresql.Driver", flyConnInfo, hotelConnInfo);
        failureCase(manager);
    }


    private static void successfulCase(BookingTransactionManager manager) {
        FlyBooking flyBooking = new FlyBooking();
        flyBooking.setClientName("Oleg");
        flyBooking.setFlyNumber("1111");
        flyBooking.setFrom("Kiev");
        flyBooking.setTo("Odessa");
        flyBooking.setDate(new Date());

        HotelBooking hotelBooking = new HotelBooking();
        hotelBooking.setClientName("Oleg");
        hotelBooking.setHotelName("2222");
        hotelBooking.setArrival(new Date());
        hotelBooking.setDeparture(new Date(System.currentTimeMillis() + 1000 * 3600 * 24 * 5));

        manager.bookFlyAndHotel(flyBooking, hotelBooking);
    }

    private static void failureCase(BookingTransactionManager manager) {
        FlyBooking flyBooking = new FlyBooking();
        flyBooking.setClientName("Oleg");
        flyBooking.setFlyNumber("1111");
        flyBooking.setFrom("Kiev");
        flyBooking.setTo("Odessa");
        flyBooking.setDate(new Date());

        manager.bookFlyAndHotel(flyBooking, new HotelBooking());
    }
}
