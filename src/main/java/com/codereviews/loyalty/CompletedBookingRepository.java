package com.codereviews.loyalty;

import java.util.Optional;

public interface CompletedBookingRepository {
    Optional<CompletedBooking> findByBookingId(String bookingId);
}
