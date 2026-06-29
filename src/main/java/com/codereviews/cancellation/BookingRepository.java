package com.codereviews.cancellation;

import java.util.Optional;

public interface BookingRepository {
    Optional<Booking> findById(String bookingId);

    void save(Booking booking);
}
