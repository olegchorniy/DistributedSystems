package kpi.ipt.labs.distributed.twophasecommit;

import kpi.ipt.labs.distributed.twophasecommit.domain.FlyBooking;
import kpi.ipt.labs.distributed.twophasecommit.domain.HotelBooking;
import kpi.ipt.labs.distributed.twophasecommit.service.BookingService;
import kpi.ipt.labs.distributed.twophasecommit.service.FlyBookingService;
import kpi.ipt.labs.distributed.twophasecommit.service.HotelBookingService;
import kpi.ipt.labs.distributed.twophasecommit.service.impl.PgFlyBookingService;
import kpi.ipt.labs.distributed.twophasecommit.service.impl.PgHotelBookingService;

import java.util.Date;

public class Main {

    private static final ConnectionInfo flyConnectionInfo = new ConnectionInfo(
            "jdbc:postgresql://localhost:5432/fly_booking",
            "fly_admin",
            "fly_password"
    );

    private static final ConnectionInfo hotelConnectionInfo = new ConnectionInfo(
            "jdbc:postgresql://localhost:5432/hotel_booking",
            "hotel_admin",
            "hotel_password"
    );

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Postgres driver not found in classpath");
        }
    }

    public static void main(String[] args) {
        FlyBookingService flyBookingService = new PgFlyBookingService(flyConnectionInfo);
        HotelBookingService hotelBookingService = new PgHotelBookingService(hotelConnectionInfo);

        BookingService bookingService = new BookingService(flyBookingService, hotelBookingService);

        successfulCase(bookingService);
        //failureAtFirstTransaction(bookingService);
        //failureAtSecondTransaction(bookingService);
    }

    private static void successfulCase(BookingService bookingService) {
        FlyBooking flyBooking = getValidFlyBookingInstance();
        HotelBooking hotelBooking = getValidHotelBookingInstance();

        bookingService.bookFlyAndHotel(flyBooking, hotelBooking);

        System.out.format("Fly booking id = %d, hotel booking id = %d%n", flyBooking.getId(), hotelBooking.getId());
    }

    private static void failureAtSecondTransaction(BookingService bookingService) {
        FlyBooking flyBooking = getValidFlyBookingInstance();

        bookingService.bookFlyAndHotel(flyBooking, new HotelBooking());
    }

    private static void failureAtFirstTransaction(BookingService bookingService) {
        HotelBooking hotelBooking = getValidHotelBookingInstance();

        bookingService.bookFlyAndHotel(new FlyBooking(), hotelBooking);
    }

    private static FlyBooking getValidFlyBookingInstance() {
        FlyBooking flyBooking = new FlyBooking();
        flyBooking.setClientName("Oleg");
        flyBooking.setFlyNumber("1111");
        flyBooking.setFrom("Kiev");
        flyBooking.setTo("Odessa");
        flyBooking.setDate(new Date());

        return flyBooking;
    }

    private static HotelBooking getValidHotelBookingInstance() {
        HotelBooking hotelBooking = new HotelBooking();
        hotelBooking.setClientName("Oleg");
        hotelBooking.setHotelName("2222");
        hotelBooking.setArrival(new Date());
        hotelBooking.setDeparture(new Date(System.currentTimeMillis() + 1000 * 3600 * 24 * 5));

        return hotelBooking;
    }
}
