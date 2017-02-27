package kpi.ipt.labs.distributed.twophasecommit.service;

import kpi.ipt.labs.distributed.twophasecommit.domain.FlyBooking;

public interface FlyBookingService {

    int bookFlight(FlyBooking flyBooking);
}
