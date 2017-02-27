package kpi.ipt.labs.distributed.twophasecommit.service;

import kpi.ipt.labs.distributed.twophasecommit.domain.HotelBooking;

public interface HotelBookingService {

    int bookHotel(HotelBooking hotelBooking);
}
