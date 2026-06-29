package com.codereviews.hotel;

import java.math.BigDecimal;
import java.time.LocalDate;

public record HotelQuoteRequest(
        LocalDate checkIn,
        LocalDate checkOut,
        int rooms,
        int guests,
        BigDecimal nightlyRate,
        String currency,
        String loyaltyTier
) {
    public HotelQuoteRequest {
        if (currency == null || currency.isBlank()) {
            currency = "USD";
        }
        if (loyaltyTier == null || loyaltyTier.isBlank()) {
            loyaltyTier = "none";
        }
    }
}
