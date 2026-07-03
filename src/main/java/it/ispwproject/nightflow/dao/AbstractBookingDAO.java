package it.ispwproject.nightflow.dao;

import it.ispwproject.nightflow.model.Booking;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBookingDAO implements BookingDAO {
    protected final List<Booking> identityMap = new ArrayList<>();

    protected Booking findInCache(int id) {
        return identityMap.stream().filter(b -> b.getId() == id).findFirst().orElse(null);
    }

    protected List<Booking> findInCacheByClient(int clientId) {
        return identityMap.stream().filter(b -> b.getClient() != null && b.getClient().getId() == clientId).toList();
    }

    protected void addToCache(Booking booking) {
        if (findInCache(booking.getId()) == null) identityMap.add(booking);
    }

    protected void updateInCache(int bookingId) {
        Booking cached = findInCache(bookingId);
        if (cached != null) cached.cancel();
    }
}