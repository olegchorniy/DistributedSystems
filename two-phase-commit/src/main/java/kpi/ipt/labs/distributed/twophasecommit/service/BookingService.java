package kpi.ipt.labs.distributed.twophasecommit.service;

import kpi.ipt.labs.distributed.twophasecommit.domain.FlyBooking;
import kpi.ipt.labs.distributed.twophasecommit.domain.HotelBooking;
import kpi.ipt.labs.distributed.twophasecommit.transaction.Transaction;
import kpi.ipt.labs.distributed.twophasecommit.transaction.TransactionHolder;

public class BookingService {

    private final FlyBookingService flyBookingService;
    private final HotelBookingService hotelBookingService;

    public BookingService(FlyBookingService flyBookingService, HotelBookingService hotelBookingService) {
        this.flyBookingService = flyBookingService;
        this.hotelBookingService = hotelBookingService;
    }

    public void bookFlyAndHotel(FlyBooking flyBooking, HotelBooking hotelBooking) {
        Transaction tx = TransactionHolder.beginTransaction();

        try {
            int hotelBookingId = hotelBookingService.bookHotel(hotelBooking);
            int flyBookingId = flyBookingService.bookFlight(flyBooking);

            flyBooking.setId(flyBookingId);
            hotelBooking.setId(hotelBookingId);
        } catch (Exception ex) {
            tx.rollback();
            throw new RuntimeException(ex);
        }

        //if everything is OK
        tx.commit();
    }
}
